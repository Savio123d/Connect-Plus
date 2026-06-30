package conne.connect.connect.Conversa.service;

import conne.connect.connect.Conversa.dto.ConversaDetalheDTO;
import conne.connect.connect.Conversa.dto.ConversaResumoDTO;
import conne.connect.connect.Conversa.dto.MensagemAnexoDTO;
import conne.connect.connect.Conversa.dto.MensagemDTO;
import conne.connect.connect.Conversa.dto.ParticipanteConversaDTO;
import conne.connect.connect.Conversa.enums.TipoConversa;
import conne.connect.connect.Conversa.model.ConversaModel;
import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import conne.connect.connect.Conversa.model.MensagemModel;
import conne.connect.connect.Conversa.model.MsgAnexoModel;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Conversa.repository.MensagemRepository;
import conne.connect.connect.Conversa.repository.MsgAnexoRepository;
import conne.connect.connect.Conversa.repository.MsgLeituraRepository;
import conne.connect.connect.Imagem.service.ImagemSistemaService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
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
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrue(usuarioLogado.getIdUsuarioEmpresa());

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
        return mensagemRepository.findByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmAsc(idConversa)
                .stream()
                .map(mensagem -> montarMensagemDTO(mensagem, idUsuarioEmpresaLogado))
                .toList();
    }

    public MensagemDTO montarMensagemDTO(MensagemModel mensagem, Long idUsuarioEmpresaLogado) {
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
        dto.setLidaPeloUsuarioLogado(
                dto.isEnviadaPeloUsuarioLogado()
                        || msgLeituraRepository.existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresa(
                        mensagem.getIdMensagem(),
                        idUsuarioEmpresaLogado
                )
        );
        dto.setQuantidadeLeituras(msgLeituraRepository.countByIdMensagem_IdMensagem(mensagem.getIdMensagem()));
        dto.setTotalParticipantes(
                conversaParticipanteRepository.countByIdConversa_IdConversaAndAtivoTrue(
                        mensagem.getIdConversa().getIdConversa()
                )
        );
        dto.setAnexo(buscarAnexo(mensagem.getIdMensagem()));
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

        mensagemRepository.findTopByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmDesc(conversa.getIdConversa())
                .ifPresent(mensagem -> dto.setUltimaMensagem(montarMensagemDTO(mensagem, idUsuarioEmpresaLogado)));

        return dto;
    }

    private List<ParticipanteConversaDTO> listarParticipantes(Long idConversa) {
        return conversaParticipanteRepository.findByIdConversa_IdConversaAndAtivoTrueOrderByEntrouEmAsc(idConversa)
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

    private MensagemAnexoDTO buscarAnexo(Long idMensagem) {
        List<MsgAnexoModel> anexos = msgAnexoRepository.findByIdMensagem_IdMensagem(idMensagem);
        if (anexos.isEmpty()) {
            return null;
        }

        MsgAnexoModel anexo = anexos.get(0);
        MensagemAnexoDTO dto = new MensagemAnexoDTO();
        dto.setId(anexo.getIdMsgAnexo());
        dto.setFilename(anexo.getNome());
        dto.setData(imagemSistemaService.urlPublica(anexo.getUrl()));
        dto.setTipoMime(anexo.getTipo());
        dto.setTamanho(anexo.getTamanho());
        return dto;
    }
}
