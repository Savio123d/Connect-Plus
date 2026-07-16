package conne.connect.connect.Conversa.service;

import conne.connect.connect.Conversa.dto.ConversaDetalheDTO;
import conne.connect.connect.Conversa.dto.ConversaResumoDTO;
import conne.connect.connect.Conversa.dto.CriarConversaGrupoRequestDTO;
import conne.connect.connect.Conversa.dto.CriarConversaPrivadaRequestDTO;
import conne.connect.connect.Conversa.dto.EnviarMensagemRequestDTO;
import conne.connect.connect.Conversa.dto.MensagemAnexoDTO;
import conne.connect.connect.Conversa.dto.MensagemDTO;
import conne.connect.connect.Conversa.enums.TipoConversa;
import conne.connect.connect.Conversa.enums.TipoMensagem;
import conne.connect.connect.Conversa.model.ConversaModel;
import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import conne.connect.connect.Conversa.model.MensagemModel;
import conne.connect.connect.Conversa.model.MsgAnexoModel;
import conne.connect.connect.Conversa.model.MsgLeituraModel;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Conversa.repository.ConversaRepository;
import conne.connect.connect.Conversa.repository.MensagemRepository;
import conne.connect.connect.Conversa.repository.MsgAnexoRepository;
import conne.connect.connect.Conversa.repository.MsgLeituraRepository;
import conne.connect.connect.Imagem.service.ImagemSistemaService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MensageriaService {

    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final MsgLeituraRepository msgLeituraRepository;
    private final MsgAnexoRepository msgAnexoRepository;
    private final MensageriaAcessoService mensageriaAcessoService;
    private final MensageriaConsultaService mensageriaConsultaService;
    private final MensageriaRealtimeService mensageriaRealtimeService;
    private final ImagemSistemaService imagemSistemaService;

    public MensageriaService(
            ConversaParticipanteRepository conversaParticipanteRepository,
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            MsgLeituraRepository msgLeituraRepository,
            MsgAnexoRepository msgAnexoRepository,
            MensageriaAcessoService mensageriaAcessoService,
            MensageriaConsultaService mensageriaConsultaService,
            MensageriaRealtimeService mensageriaRealtimeService,
            ImagemSistemaService imagemSistemaService
    ) {
        this.conversaParticipanteRepository = conversaParticipanteRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.msgLeituraRepository = msgLeituraRepository;
        this.msgAnexoRepository = msgAnexoRepository;
        this.mensageriaAcessoService = mensageriaAcessoService;
        this.mensageriaConsultaService = mensageriaConsultaService;
        this.mensageriaRealtimeService = mensageriaRealtimeService;
        this.imagemSistemaService = imagemSistemaService;
    }

    @Transactional
    public ConversaDetalheDTO criarOuBuscarConversaPrivada(
            Long idUsuarioEmpresaLogado,
            CriarConversaPrivadaRequestDTO requestDTO
    ) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        UsuarioEmpresaModel destinatario = mensageriaAcessoService.buscarParticipanteDaMesmaEmpresa(
                requestDTO.getIdDestinatarioUsuarioEmpresa(),
                usuarioLogado
        );

        mensageriaAcessoService.validarConversaPrivada(usuarioLogado, destinatario);

        Optional<ConversaModel> conversaExistente = conversaRepository.findConversaPrivadaExistente(
                usuarioLogado.getIdEmpresa().getIdEmpresa(),
                TipoConversa.privada,
                usuarioLogado.getIdUsuarioEmpresa(),
                destinatario.getIdUsuarioEmpresa()
        );

        ConversaModel conversaPrivada = conversaExistente.orElse(null);
        if (conversaPrivada != null) {
            return mensageriaConsultaService.montarConversaDetalhe(
                    conversaPrivada,
                    idUsuarioEmpresaLogado
            );
        }

        ConversaModel conversaSalva = conversaRepository.save(criarConversa(TipoConversa.privada, null, usuarioLogado));
        adicionarParticipantes(conversaSalva, List.of(usuarioLogado, destinatario));
        mensageriaRealtimeService.notificarConversaAposCommit(
                conversaSalva.getIdConversa(),
                MensageriaRealtimeService.EVENTO_CONVERSA_CRIADA,
                null,
                idUsuarioEmpresaLogado
        );

        return mensageriaConsultaService.montarConversaDetalhe(conversaSalva, idUsuarioEmpresaLogado);
    }

    @Transactional
    public ConversaDetalheDTO criarConversaGrupo(
            Long idUsuarioEmpresaLogado,
            CriarConversaGrupoRequestDTO requestDTO
    ) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        Set<Long> idsParticipantes = normalizarIdsParticipantes(requestDTO.getIdsParticipantes(), idUsuarioEmpresaLogado);
        validarQuantidadeMinimaParticipantes(idsParticipantes);

        List<UsuarioEmpresaModel> participantes = carregarParticipantesDoGrupo(idsParticipantes, usuarioLogado);
        ConversaModel conversaSalva = conversaRepository.save(
                criarConversa(TipoConversa.grupo, requestDTO.getNome().trim(), usuarioLogado)
        );

        adicionarParticipantes(conversaSalva, participantes);
        mensageriaRealtimeService.notificarConversaAposCommit(
                conversaSalva.getIdConversa(),
                MensageriaRealtimeService.EVENTO_CONVERSA_CRIADA,
                null,
                idUsuarioEmpresaLogado
        );
        return mensageriaConsultaService.montarConversaDetalhe(conversaSalva, idUsuarioEmpresaLogado);
    }

    @Transactional(readOnly = true)
    public List<ConversaResumoDTO> listarConversas(Long idUsuarioEmpresaLogado, TipoConversa tipo) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        return mensageriaConsultaService.listarConversas(usuarioLogado, tipo);
    }

    @Transactional(readOnly = true)
    public ConversaDetalheDTO detalharConversa(Long idUsuarioEmpresaLogado, Long idConversa) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        ConversaModel conversa = mensageriaAcessoService.buscarConversaComAcesso(idConversa, usuarioLogado);
        return mensageriaConsultaService.montarConversaDetalhe(conversa, idUsuarioEmpresaLogado);
    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> listarMensagens(Long idUsuarioEmpresaLogado, Long idConversa) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        ConversaModel conversa = mensageriaAcessoService.buscarConversaComAcesso(idConversa, usuarioLogado);
        return mensageriaConsultaService.listarMensagens(conversa.getIdConversa(), idUsuarioEmpresaLogado);
    }

    @Transactional
    public MensagemDTO enviarMensagem(Long idUsuarioEmpresaLogado, EnviarMensagemRequestDTO requestDTO) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        validarMensagemParaEnvio(requestDTO);

        ConversaModel conversa = mensageriaAcessoService.buscarConversaComAcesso(
                requestDTO.getIdConversa(),
                usuarioLogado
        );

        MensagemModel mensagemSalva = mensagemRepository.save(criarMensagem(requestDTO, conversa, usuarioLogado));

        if (requestDTO.getAnexo() != null) {
            salvarAnexo(mensagemSalva, requestDTO.getAnexo());
        }

        atualizarDataConversa(conversa);
        mensageriaRealtimeService.notificarConversaAposCommit(
                conversa.getIdConversa(),
                MensageriaRealtimeService.EVENTO_MENSAGEM_ENVIADA,
                mensagemSalva.getIdMensagem(),
                idUsuarioEmpresaLogado
        );
        return mensageriaConsultaService.montarMensagemDTO(mensagemSalva, idUsuarioEmpresaLogado);
    }

    @Transactional
    public MensagemDTO marcarMensagemComoLida(Long idUsuarioEmpresaLogado, Long idMensagem) {
        UsuarioEmpresaModel usuarioLogado = mensageriaAcessoService.buscarUsuarioEmpresaAtivoDoToken(idUsuarioEmpresaLogado);
        MensagemModel mensagem = mensageriaAcessoService.buscarMensagemComAcesso(idMensagem, usuarioLogado);

        boolean jaLida = msgLeituraRepository.existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNull(
                idMensagem,
                idUsuarioEmpresaLogado
        );

        if (!jaLida) {
            MsgLeituraModel leitura = new MsgLeituraModel();
            leitura.setIdMensagem(mensagem);
            leitura.setIdUsuarioEmpresa(usuarioLogado);
            msgLeituraRepository.save(leitura);
            mensageriaRealtimeService.notificarConversaAposCommit(
                    mensagem.getIdConversa().getIdConversa(),
                    MensageriaRealtimeService.EVENTO_MENSAGEM_LIDA,
                    mensagem.getIdMensagem(),
                    idUsuarioEmpresaLogado
            );
        }

        return mensageriaConsultaService.montarMensagemDTO(mensagem, idUsuarioEmpresaLogado);
    }

    private Set<Long> normalizarIdsParticipantes(List<Long> idsParticipantes, Long idUsuarioEmpresaLogado) {
        Set<Long> participantes = new LinkedHashSet<>(idsParticipantes);
        participantes.add(idUsuarioEmpresaLogado);
        return participantes;
    }

    private void validarQuantidadeMinimaParticipantes(Set<Long> idsParticipantes) {
        if (idsParticipantes.size() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Um grupo precisa ter pelo menos dois participantes."
            );
        }
    }

    private List<UsuarioEmpresaModel> carregarParticipantesDoGrupo(
            Set<Long> idsParticipantes,
            UsuarioEmpresaModel usuarioLogado
    ) {
        return idsParticipantes.stream()
                .map(idParticipante -> mensageriaAcessoService.buscarParticipanteDaMesmaEmpresa(
                        idParticipante,
                        usuarioLogado
                ))
                .toList();
    }

    private ConversaModel criarConversa(TipoConversa tipoConversa, String nome, UsuarioEmpresaModel usuarioLogado) {
        ConversaModel conversa = new ConversaModel();
        conversa.setIdEmpresa(usuarioLogado.getIdEmpresa());
        conversa.setTipo(tipoConversa);
        conversa.setNome(nome);
        conversa.setIdCriador(usuarioLogado);
        return conversa;
    }

    private void adicionarParticipantes(ConversaModel conversa, List<UsuarioEmpresaModel> participantes) {
        for (UsuarioEmpresaModel participante : participantes) {
            salvarParticipante(conversa, participante);
        }
    }

    private MensagemModel criarMensagem(
            EnviarMensagemRequestDTO requestDTO,
            ConversaModel conversa,
            UsuarioEmpresaModel usuarioLogado
    ) {
        MensagemModel mensagem = new MensagemModel();
        mensagem.setIdEmpresa(usuarioLogado.getIdEmpresa());
        mensagem.setIdConversa(conversa);
        mensagem.setIdRemetente(usuarioLogado);
        mensagem.setConteudo(temConteudo(requestDTO.getConteudo()) ? requestDTO.getConteudo().trim() : null);
        mensagem.setTipo(definirTipoMensagem(requestDTO));
        return mensagem;
    }

    private void validarMensagemParaEnvio(EnviarMensagemRequestDTO requestDTO) {
        if (requestDTO.getIdConversa() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A conversa é obrigatória."
            );
        }

        if (!temConteudo(requestDTO.getConteudo()) && requestDTO.getAnexo() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o conteudo da mensagem ou um anexo."
            );
        }
    }

    private boolean temConteudo(String conteudo) {
        return conteudo != null && !conteudo.trim().isEmpty();
    }

    private TipoMensagem definirTipoMensagem(EnviarMensagemRequestDTO requestDTO) {
        if (requestDTO.getTipo() != null) {
            return requestDTO.getTipo();
        }

        if (requestDTO.getAnexo() != null && !temConteudo(requestDTO.getConteudo())) {
            return TipoMensagem.arquivo;
        }

        return TipoMensagem.texto;
    }

    private void salvarAnexo(MensagemModel mensagem, MensagemAnexoDTO anexoDTO) {
        MsgAnexoModel anexo = new MsgAnexoModel();
        anexo.setIdMensagem(mensagem);
        anexo.setNome(anexoDTO.getFilename().trim());
        // Guardamos apenas a CHAVE do S3 (ex.: "chat/6/uuid.png"), nunca a URL completa.
        String chave = imagemSistemaService.extrairChave(anexoDTO.getData());
        anexo.setUrl(chave != null ? chave : anexoDTO.getData().trim());
        anexo.setTipo(anexoDTO.getTipoMime());
        anexo.setTamanho(anexoDTO.getTamanho());
        msgAnexoRepository.save(anexo);
    }

    private void salvarParticipante(ConversaModel conversa, UsuarioEmpresaModel usuarioEmpresa) {
        ConversaParticipanteModel participante = new ConversaParticipanteModel();
        participante.setIdConversa(conversa);
        participante.setIdUsuarioEmpresa(usuarioEmpresa);
        participante.setAtivo(true);
        conversaParticipanteRepository.save(participante);
    }

    private void atualizarDataConversa(ConversaModel conversa) {
        conversa.setDataAtualizacao(LocalDateTime.now());
        conversaRepository.save(conversa);
    }
}
