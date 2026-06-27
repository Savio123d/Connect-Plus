package conne.connect.connect.Conversa.service;

import conne.connect.connect.Conversa.dto.ConversaResumoDTO;
import conne.connect.connect.Conversa.dto.MensagemDTO;
import conne.connect.connect.Conversa.dto.ParticipanteConversaDTO;
import conne.connect.connect.Conversa.enums.TipoConversa;
import conne.connect.connect.Conversa.enums.TipoMensagem;
import conne.connect.connect.Conversa.model.ConversaModel;
import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import conne.connect.connect.Conversa.model.MensagemModel;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Conversa.repository.ConversaRepository;
import conne.connect.connect.Conversa.repository.MensagemRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConversaService {

    private final ConversaRepository conversaRepository;
    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final MensagemRepository mensagemRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public ConversaService(ConversaRepository conversaRepository,
                           ConversaParticipanteRepository conversaParticipanteRepository,
                           MensagemRepository mensagemRepository,
                           UsuarioEmpresaRepository usuarioEmpresaRepository) {
        this.conversaRepository = conversaRepository;
        this.conversaParticipanteRepository = conversaParticipanteRepository;
        this.mensagemRepository = mensagemRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    public List<ConversaResumoDTO> listarConversas(Long idUsuarioEmpresaLogado) {
        validarUsuarioEmpresa(idUsuarioEmpresaLogado);

        return conversaParticipanteRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresaLogado)
                .stream()
                .map(ConversaParticipanteModel::getIdConversa)
                .filter(conversa -> conversa.getExcluido() == null)
                .map(conversa -> montarResumo(conversa, idUsuarioEmpresaLogado))
                .sorted(this::compararPorUltimaAtividade)
                .toList();
    }

    public List<MensagemDTO> listarMensagens(Long idConversa, Long idUsuarioEmpresaLogado) {
        validarParticipanteDaConversa(idConversa, idUsuarioEmpresaLogado);

        return mensagemRepository
                .findByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmAsc(idConversa)
                .stream()
                .map(this::montarMensagem)
                .toList();
    }

    public MensagemDTO enviarMensagem(Long idConversa, Long idUsuarioEmpresaLogado, String conteudo) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem vazia.");
        }

        UsuarioEmpresaModel remetente = validarParticipanteDaConversa(idConversa, idUsuarioEmpresaLogado);
        ConversaModel conversa = buscarConversa(idConversa);

        MensagemModel mensagem = new MensagemModel();
        mensagem.setIdEmpresa(conversa.getIdEmpresa());
        mensagem.setIdConversa(conversa);
        mensagem.setIdRemetente(remetente);
        mensagem.setTipo(TipoMensagem.texto);
        mensagem.setConteudo(conteudo.trim());

        conversa.setDataAtualizacao(java.time.LocalDateTime.now());
        conversaRepository.save(conversa);

        return montarMensagem(mensagemRepository.save(mensagem));
    }

    public ConversaResumoDTO criarOuBuscarConversaPrivada(
            Long idUsuarioEmpresaLogado,
            Long idDestinatarioUsuarioEmpresa
    ) {
        if (idDestinatarioUsuarioEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destinatario obrigatorio.");
        }

        if (idUsuarioEmpresaLogado.equals(idDestinatarioUsuarioEmpresa)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e possivel conversar consigo mesmo.");
        }

        UsuarioEmpresaModel usuarioLogado = validarUsuarioEmpresa(idUsuarioEmpresaLogado);
        UsuarioEmpresaModel destinatario = validarUsuarioEmpresa(idDestinatarioUsuarioEmpresa);

        if (!usuarioLogado.getIdEmpresa().getIdEmpresa().equals(destinatario.getIdEmpresa().getIdEmpresa())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuarios de empresas diferentes.");
        }

        ConversaModel conversaExistente = buscarConversaPrivadaExistente(
                idUsuarioEmpresaLogado,
                idDestinatarioUsuarioEmpresa
        );

        if (conversaExistente != null) {
            return montarResumo(conversaExistente, idUsuarioEmpresaLogado);
        }

        ConversaModel conversa = new ConversaModel();
        conversa.setIdEmpresa(usuarioLogado.getIdEmpresa());
        conversa.setTipo(TipoConversa.privada);
        conversa.setNome(destinatario.getIdUsuario().getNome());
        conversa.setIdCriador(usuarioLogado);
        ConversaModel conversaSalva = conversaRepository.save(conversa);

        adicionarParticipante(conversaSalva, usuarioLogado);
        adicionarParticipante(conversaSalva, destinatario);

        return montarResumo(conversaSalva, idUsuarioEmpresaLogado);
    }

    private ConversaModel buscarConversaPrivadaExistente(Long idUsuarioEmpresaLogado, Long idDestinatario) {
        return conversaParticipanteRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresaLogado)
                .stream()
                .map(ConversaParticipanteModel::getIdConversa)
                .filter(conversa -> conversa.getTipo() == TipoConversa.privada)
                .filter(conversa -> conversaParticipanteRepository
                        .existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(
                                conversa.getIdConversa(),
                                idDestinatario
                        ))
                .findFirst()
                .orElse(null);
    }

    private void adicionarParticipante(ConversaModel conversa, UsuarioEmpresaModel usuarioEmpresa) {
        ConversaParticipanteModel participante = new ConversaParticipanteModel();
        participante.setIdConversa(conversa);
        participante.setIdUsuarioEmpresa(usuarioEmpresa);
        participante.setAtivo(true);
        conversaParticipanteRepository.save(participante);
    }

    private UsuarioEmpresaModel validarParticipanteDaConversa(Long idConversa, Long idUsuarioEmpresa) {
        UsuarioEmpresaModel usuarioEmpresa = validarUsuarioEmpresa(idUsuarioEmpresa);

        boolean participa = conversaParticipanteRepository
                .existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(
                        idConversa,
                        idUsuarioEmpresa
                );

        if (!participa) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario nao participa da conversa.");
        }

        return usuarioEmpresa;
    }

    private UsuarioEmpresaModel validarUsuarioEmpresa(Long idUsuarioEmpresa) {
        return usuarioEmpresaRepository.findById(idUsuarioEmpresa)
                .filter(vinculo -> Boolean.TRUE.equals(vinculo.getAtivo()))
                .filter(vinculo -> vinculo.getExcluido() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vinculo usuario-empresa nao encontrado."));
    }

    private ConversaModel buscarConversa(Long idConversa) {
        return conversaRepository.findById(idConversa)
                .filter(conversa -> conversa.getExcluido() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa nao encontrada."));
    }

    private ConversaResumoDTO montarResumo(ConversaModel conversa, Long idUsuarioEmpresaLogado) {
        List<ParticipanteConversaDTO> participantes = conversaParticipanteRepository
                .findByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNull(conversa.getIdConversa())
                .stream()
                .map(this::montarParticipante)
                .toList();

        MensagemDTO ultimaMensagem = mensagemRepository
                .findTopByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmDesc(conversa.getIdConversa())
                .map(this::montarMensagem)
                .orElse(null);

        ConversaResumoDTO dto = new ConversaResumoDTO();
        dto.setId(conversa.getIdConversa());
        dto.setTipo(conversa.getTipo());
        dto.setNome(nomeDaConversa(conversa, participantes, idUsuarioEmpresaLogado));
        dto.setCriadoEm(conversa.getDataCriacao());
        dto.setAtualizadoEm(conversa.getDataAtualizacao());
        dto.setUltimaMensagem(ultimaMensagem);
        dto.setParticipantes(participantes);
        return dto;
    }

    private String nomeDaConversa(
            ConversaModel conversa,
            List<ParticipanteConversaDTO> participantes,
            Long idUsuarioEmpresaLogado
    ) {
        if (conversa.getTipo() == TipoConversa.privada) {
            return participantes.stream()
                    .filter(participante -> !participante.getIdUsuarioEmpresa().equals(idUsuarioEmpresaLogado))
                    .map(ParticipanteConversaDTO::getNome)
                    .findFirst()
                    .orElse("Conversa");
        }

        if (conversa.getNome() != null && !conversa.getNome().trim().isEmpty()) {
            return conversa.getNome();
        }

        if (participantes.isEmpty()) {
            return "Conversa";
        }

        return participantes.get(0).getNome();
    }

    private MensagemDTO montarMensagem(MensagemModel mensagem) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(mensagem.getIdMensagem());
        dto.setRemetente(montarParticipante(mensagem.getIdRemetente()));
        dto.setTipo(mensagem.getTipo());
        dto.setConteudo(mensagem.getConteudo());
        dto.setEnviadaEm(mensagem.getEnviadaEm());
        dto.setEditadaEm(mensagem.getEditadaEm());
        return dto;
    }

    private ParticipanteConversaDTO montarParticipante(ConversaParticipanteModel participante) {
        return montarParticipante(participante.getIdUsuarioEmpresa());
    }

    private ParticipanteConversaDTO montarParticipante(UsuarioEmpresaModel usuarioEmpresa) {
        return ParticipanteConversaDTO.fromModel(usuarioEmpresa);
    }

    private int compararPorUltimaAtividade(ConversaResumoDTO a, ConversaResumoDTO b) {
        java.time.LocalDateTime dataA = a.getUltimaMensagem() != null
                ? a.getUltimaMensagem().getEnviadaEm()
                : a.getAtualizadoEm();
        java.time.LocalDateTime dataB = b.getUltimaMensagem() != null
                ? b.getUltimaMensagem().getEnviadaEm()
                : b.getAtualizadoEm();

        if (dataA == null && dataB == null) {
            return 0;
        }

        if (dataA == null) {
            return 1;
        }

        if (dataB == null) {
            return -1;
        }

        return dataB.compareTo(dataA);
    }
}
