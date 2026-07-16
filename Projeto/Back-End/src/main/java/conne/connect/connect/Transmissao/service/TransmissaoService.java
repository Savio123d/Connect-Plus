package conne.connect.connect.Transmissao.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Transmissao.dto.TransmissaoTokenDTO;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransmissaoService {

    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final AutorizacaoService autorizacaoService;

    @Value("${livekit.url:ws://localhost:7880}")
    private String livekitUrl;

    @Value("${livekit.api-key:devkey}")
    private String livekitApiKey;

    @Value("${livekit.api-secret:secret}")
    private String livekitApiSecret;

    public TransmissaoService(ConversaParticipanteRepository conversaParticipanteRepository,
                              UsuarioEmpresaRepository usuarioEmpresaRepository,
                              AutorizacaoService autorizacaoService) {
        this.conversaParticipanteRepository = conversaParticipanteRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public TransmissaoTokenDTO entrarNaConversa(Long idConversa, Long idUsuarioEmpresa) {
        if (idUsuarioEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header X-Usuario-Empresa-Id obrigatório.");
        }

        autorizacaoService.validarVinculoAtual(idUsuarioEmpresa);

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository.findById(idUsuarioEmpresa)
                .filter(vinculo -> Boolean.TRUE.equals(vinculo.getAtivo()))
                .filter(vinculo -> vinculo.getExcluido() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário da empresa não encontrado."));

        boolean participaDaConversa = conversaParticipanteRepository
                .existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(
                        idConversa,
                        idUsuarioEmpresa
                );

        if (!participaDaConversa) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não participa dessa conversa.");
        }

        String sala = "conversa-" + idConversa;
        String identidade = "usuario-empresa-" + idUsuarioEmpresa + "-" + UUID.randomUUID();
        String nome = usuarioEmpresa.getIdUsuario() != null
                && usuarioEmpresa.getIdUsuario().getNome() != null
                && !usuarioEmpresa.getIdUsuario().getNome().isBlank()
                ? usuarioEmpresa.getIdUsuario().getNome()
                : "Usuário";

        return new TransmissaoTokenDTO(
                livekitUrl,
                gerarTokenLivekit(identidade, nome, sala),
                sala,
                identidade,
                nome
        );
    }

    private String gerarTokenLivekit(String identidade, String nome, String sala) {
        Instant agora = Instant.now();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> video = new LinkedHashMap<>();
        video.put("roomJoin", true);
        video.put("room", sala);
        video.put("canPublish", true);
        video.put("canSubscribe", true);
        video.put("canPublishData", true);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", livekitApiKey);
        payload.put("sub", identidade);
        payload.put("name", nome);
        payload.put("nbf", agora.minusSeconds(5).getEpochSecond());
        payload.put("exp", agora.plus(Duration.ofHours(6)).getEpochSecond());
        payload.put("video", video);

        try {
            String headerJson = codificarJson(header);
            String payloadJson = codificarJson(payload);
            String conteudo = headerJson + "." + payloadJson;
            return conteudo + "." + assinar(conteudo);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao gerar token LiveKit.", ex);
        }
    }

    private String codificarJson(Map<String, Object> dados) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(montarJson(dados).getBytes(StandardCharsets.UTF_8));
    }

    private String montarJson(Map<String, Object> dados) {
        StringBuilder json = new StringBuilder("{");
        boolean primeiro = true;

        for (Map.Entry<String, Object> entry : dados.entrySet()) {
            if (!primeiro) {
                json.append(",");
            }

            json.append("\"").append(escapar(entry.getKey())).append("\":");
            json.append(valorJson(entry.getValue()));
            primeiro = false;
        }

        return json.append("}").toString();
    }

    @SuppressWarnings("unchecked")
    private String valorJson(Object valor) {
        if (valor instanceof String texto) {
            return "\"" + escapar(texto) + "\"";
        }

        if (valor instanceof Number || valor instanceof Boolean) {
            return valor.toString();
        }

        if (valor instanceof Map<?, ?> mapa) {
            return montarJson((Map<String, Object>) mapa);
        }

        return "null";
    }

    private String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String assinar(String conteudo) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec chave = new SecretKeySpec(livekitApiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(chave);
        byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(assinatura);
    }
}
