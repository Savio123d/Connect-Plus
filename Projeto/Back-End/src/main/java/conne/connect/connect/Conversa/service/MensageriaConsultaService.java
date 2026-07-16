package conne.connect.connect.Conversa.service;

import conne.connect.connect.Conversa.dto.ConversaDetalheDTO;
import conne.connect.connect.Conversa.dto.ConversaResumoDTO;
import conne.connect.connect.Conversa.dto.MensagemAnexoDTO;
import conne.connect.connect.Conversa.dto.MensagemDTO;
import conne.connect.connect.Conversa.dto.ParticipanteConversaDTO;
import conne.connect.connect.Conversa.enums.TipoConversa;
import conne.connect.connect.Conversa.model.*;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Conversa.repository.MensagemRepository;
import conne.connect.connect.Conversa.repository.MsgAnexoRepository;
import conne.connect.connect.Conversa.repository.MsgLeituraRepository;
import conne.connect.connect.Imagem.service.ImagemSistemaService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MensageriaConsultaService {

    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final MensagemRepository mensagemRepository;
    private final MsgLeituraRepository msgLeituraRepository;
    private final MsgAnexoRepository msgAnexoRepository;
    private final ImagemSistemaService imagemSistemaService;

    public MensageriaConsultaService(
            ConversaParticipanteRepository conversaParticipanteRepository,
            MensagemRepository mensagemRepository,
            MsgLeituraRepository msgLeituraRepository,
            MsgAnexoRepository msgAnexoRepository,
            ImagemSistemaService imagemSistemaService
    ) {
        this.conversaParticipanteRepository = conversaParticipanteRepository;
        this.mensagemRepository = mensagemRepository;
        this.msgLeituraRepository = msgLeituraRepository;
        this.msgAnexoRepository = msgAnexoRepository;
        this.imagemSistemaService = imagemSistemaService;
    }

    public List<ConversaResumoDTO> listarConversas(UsuarioEmpresaModel usuarioLogado, TipoConversa tipo) {
        List<ConversaParticipanteModel> participacoes = conversaParticipanteRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(usuarioLogado.getIdUsuarioEmpresa());

        Map<Long, ConversaModel> conversasPorId = new LinkedHashMap<>();
        for (ConversaParticipanteModel participacao : participacoes) {
            ConversaModel conversa = participacao.getIdConversa();

            if (conversa == null || conversa.getExcluido() != null) {
                continue;
            }

            if (!conversa.getIdEmpresa().getIdEmpresa().equals(usuarioLogado.getIdEmpresa().getIdEmpresa())) {
                continue;
            }

            if (tipo != null && !tipo.equals(conversa.getTipo())) {
                continue;
            }

            conversasPorId.put(conversa.getIdConversa(), conversa);
        }

        return conversasPorId.values()
                .stream()
                .sorted(Comparator.comparing(
                        ConversaModel::getDataAtualizacao,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(conversa -> montarConversaResumo(conversa, usuarioLogado.getIdUsuarioEmpresa()))
                .toList();
    }

    public ConversaDetalheDTO montarConversaDetalhe(ConversaModel conversa, Long idUsuarioEmpresaLogado) {
        List<ParticipanteConversaDTO> participantes = listarParticipantes(conversa.getIdConversa());

        ConversaDetalheDTO dto = new ConversaDetalheDTO();
        dto.setId(conversa.getIdConversa());
        dto.setTipo(conversa.getTipo());
        dto.setNome(resolverNomeConversa(conversa, participantes, idUsuarioEmpresaLogado));
        dto.setCriadoEm(conversa.getDataCriacao());
        dto.setAtualizadoEm(conversa.getDataAtualizacao());
        dto.setParticipantes(participantes);
        dto.setMensagens(listarMensagens(conversa.getIdConversa(), idUsuarioEmpresaLogado));
        return dto;
    }

    public List<MensagemDTO> listarMensagens(Long idConversa, Long idUsuarioEmpresaLogado) {
        List<MensagemModel> mensagens = mensagemRepository
                .findByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmAsc(idConversa);

        if (mensagens.isEmpty()) {
            return List.of();
        }

        // Leituras, anexos e total de participantes carregados em lote:
        // evita 3-4 consultas por mensagem ao abrir a conversa (N+1).
        List<Long> idsMensagens = mensagens.stream()
                .map(MensagemModel::getIdMensagem)
                .toList();

        Map<Long, Long> leiturasPorMensagem = new HashMap<>();
        Set<Long> lidasPeloUsuario = new HashSet<>();
        for (MsgLeituraModel leitura : msgLeituraRepository.findByIdMensagem_IdMensagemInAndExcluidoIsNull(idsMensagens)) {
            Long idMensagem = leitura.getIdMensagem().getIdMensagem();
            leiturasPorMensagem.merge(idMensagem, 1L, Long::sum);

            if (leitura.getIdUsuarioEmpresa() != null
                    && idUsuarioEmpresaLogado.equals(leitura.getIdUsuarioEmpresa().getIdUsuarioEmpresa())) {
                lidasPeloUsuario.add(idMensagem);
            }
        }

        Map<Long, MsgAnexoModel> anexoPorMensagem = new HashMap<>();
        for (MsgAnexoModel anexo : msgAnexoRepository.findByIdMensagem_IdMensagemInAndExcluidoIsNull(idsMensagens)) {
            anexoPorMensagem.putIfAbsent(anexo.getIdMensagem().getIdMensagem(), anexo);
        }

        long totalParticipantes = conversaParticipanteRepository
                .countByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNull(idConversa);

        return mensagens.stream()
                .map(mensagem -> montarMensagemDTO(
                        mensagem,
                        idUsuarioEmpresaLogado,
                        totalParticipantes,
                        leiturasPorMensagem.getOrDefault(mensagem.getIdMensagem(), 0L),
                        lidasPeloUsuario.contains(mensagem.getIdMensagem()),
                        anexoPorMensagem.get(mensagem.getIdMensagem())))
                .toList();
    }

    public MensagemDTO montarMensagemDTO(MensagemModel mensagem, Long idUsuarioEmpresaLogado) {
        long totalParticipantes = conversaParticipanteRepository.countByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNull(
                mensagem.getIdConversa().getIdConversa()
        );
        long quantidadeLeituras = msgLeituraRepository.countByIdMensagem_IdMensagemAndExcluidoIsNull(mensagem.getIdMensagem());
        boolean lidaPeloUsuario = msgLeituraRepository.existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNull(
                mensagem.getIdMensagem(),
                idUsuarioEmpresaLogado
        );

        return montarMensagemDTO(
                mensagem,
                idUsuarioEmpresaLogado,
                totalParticipantes,
                quantidadeLeituras,
                lidaPeloUsuario,
                primeiroAnexo(mensagem.getIdMensagem())
        );
    }

    private MensagemDTO montarMensagemDTO(
            MensagemModel mensagem,
            Long idUsuarioEmpresaLogado,
            long totalParticipantes,
            long quantidadeLeituras,
            boolean lidaPeloUsuarioLogado,
            MsgAnexoModel anexo
    ) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(mensagem.getIdMensagem());
        dto.setRemetente(ParticipanteConversaDTO.fromModel(mensagem.getIdRemetente()));
        dto.setTipo(mensagem.getTipo());
        dto.setConteudo(mensagem.getConteudo());
        dto.setEnviadaEm(mensagem.getEnviadaEm());
        dto.setEditadaEm(mensagem.getEditadaEm());
        dto.setEnviadaPeloUsuarioLogado(
                mensagem.getIdRemetente().getIdUsuarioEmpresa().equals(idUsuarioEmpresaLogado)
        );
        dto.setLidaPeloUsuarioLogado(dto.isEnviadaPeloUsuarioLogado() || lidaPeloUsuarioLogado);
        dto.setQuantidadeLeituras(quantidadeLeituras);
        dto.setTotalParticipantes(totalParticipantes);
        dto.setAnexo(montarAnexoDTO(anexo));
        return dto;
    }

    private ConversaResumoDTO montarConversaResumo(ConversaModel conversa, Long idUsuarioEmpresaLogado) {
        List<ParticipanteConversaDTO> participantes = listarParticipantes(conversa.getIdConversa());

        ConversaResumoDTO dto = new ConversaResumoDTO();
        dto.setId(conversa.getIdConversa());
        dto.setTipo(conversa.getTipo());
        dto.setNome(resolverNomeConversa(conversa, participantes, idUsuarioEmpresaLogado));
        dto.setCriadoEm(conversa.getDataCriacao());
        dto.setAtualizadoEm(conversa.getDataAtualizacao());
        dto.setParticipantes(participantes);

        // Reaproveita a lista de participantes ja carregada em vez de repetir o COUNT no banco.
        mensagemRepository.findTopByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmDesc(conversa.getIdConversa())
                .ifPresent(mensagem -> dto.setUltimaMensagem(montarMensagemDTO(
                        mensagem,
                        idUsuarioEmpresaLogado,
                        participantes.size(),
                        msgLeituraRepository.countByIdMensagem_IdMensagemAndExcluidoIsNull(mensagem.getIdMensagem()),
                        msgLeituraRepository.existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNull(
                                mensagem.getIdMensagem(),
                                idUsuarioEmpresaLogado),
                        primeiroAnexo(mensagem.getIdMensagem()))));

        return dto;
    }

    private List<ParticipanteConversaDTO> listarParticipantes(Long idConversa) {
        return conversaParticipanteRepository.findByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNullOrderByEntrouEmAsc(idConversa)
                .stream()
                .map(ConversaParticipanteModel::getIdUsuarioEmpresa)
                .map(ParticipanteConversaDTO::fromModel)
                .toList();
    }

    private String resolverNomeConversa(
            ConversaModel conversa,
            List<ParticipanteConversaDTO> participantes,
            Long idUsuarioEmpresaLogado
    ) {
        if (conversa.getNome() != null && !conversa.getNome().isBlank()) {
            return conversa.getNome().trim();
        }

        List<String> nomes = participantes.stream()
                .filter(participante -> !participante.getIdUsuarioEmpresa().equals(idUsuarioEmpresaLogado))
                .map(ParticipanteConversaDTO::getNome)
                .filter(nome -> nome != null && !nome.isBlank())
                .toList();

        if (nomes.isEmpty()) {
            return "Conversa sem nome";
        }

        return String.join(", ", nomes);
    }

    private MsgAnexoModel primeiroAnexo(Long idMensagem) {
        List<MsgAnexoModel> anexos = msgAnexoRepository.findByIdMensagem_IdMensagemAndExcluidoIsNull(idMensagem);
        return anexos.isEmpty() ? null : anexos.get(0);
    }

    private MensagemAnexoDTO montarAnexoDTO(MsgAnexoModel anexo) {
        if (anexo == null) {
            return null;
        }

        MensagemAnexoDTO dto = new MensagemAnexoDTO();
        dto.setId(anexo.getIdMsgAnexo());
        dto.setFilename(anexo.getNome());
        dto.setData(imagemSistemaService.urlPublica(anexo.getUrl()));
        dto.setTipoMime(anexo.getTipo());
        dto.setTamanho(anexo.getTamanho());
        return dto;
    }
}
