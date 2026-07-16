package conne.connect.connect.Conversa.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Conversa.model.ConversaModel;
import conne.connect.connect.Conversa.model.MensagemModel;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import conne.connect.connect.Conversa.repository.ConversaRepository;
import conne.connect.connect.Conversa.repository.MensagemRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class MensageriaAcessoService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final AutorizacaoService autorizacaoService;

    public MensageriaAcessoService(
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            ConversaParticipanteRepository conversaParticipanteRepository,
            AutorizacaoService autorizacaoService
    ) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.conversaParticipanteRepository = conversaParticipanteRepository;
        this.autorizacaoService = autorizacaoService;
    }

    public UsuarioEmpresaModel buscarUsuarioEmpresaAtivoDoToken(Long idUsuarioEmpresa) {
        autorizacaoService.validarVinculoAtual(idUsuarioEmpresa);
        return buscarUsuarioEmpresaAtivo(idUsuarioEmpresa);
    }

    public UsuarioEmpresaModel buscarUsuarioEmpresaAtivo(Long idUsuarioEmpresa) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário da empresa não encontrado."
                ));

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário da empresa está inativo."
            );
        }

        return usuarioEmpresa;
    }

    public UsuarioEmpresaModel buscarParticipanteDaMesmaEmpresa(
            Long idUsuarioEmpresa,
            UsuarioEmpresaModel usuarioLogado
    ) {
        UsuarioEmpresaModel participante = buscarUsuarioEmpresaAtivo(idUsuarioEmpresa);
        validarMesmaEmpresa(usuarioLogado, participante);
        return participante;
    }

    public void validarConversaPrivada(UsuarioEmpresaModel usuarioLogado, UsuarioEmpresaModel destinatario) {
        validarMesmaEmpresa(usuarioLogado, destinatario);

        if (usuarioLogado.getIdUsuarioEmpresa().equals(destinatario.getIdUsuarioEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é permitido criar conversa privada com o próprio usuário."
            );
        }
    }

    public ConversaModel buscarConversaComAcesso(Long idConversa, UsuarioEmpresaModel usuarioLogado) {
        ConversaModel conversa = conversaRepository.findByIdConversaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
                        idConversa,
                        usuarioLogado.getIdEmpresa().getIdEmpresa()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversa não encontrada."
                ));

        validarParticipacaoAtiva(usuarioLogado.getIdUsuarioEmpresa(), conversa.getIdConversa());
        return conversa;
    }

    public MensagemModel buscarMensagemComAcesso(Long idMensagem, UsuarioEmpresaModel usuarioLogado) {
        MensagemModel mensagem = mensagemRepository
                .findByIdMensagemAndIdEmpresa_IdEmpresaAndExcluidoIsNullAndExcluidaEmIsNull(
                        idMensagem,
                        usuarioLogado.getIdEmpresa().getIdEmpresa()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mensagem não encontrada."
                ));

        validarMesmaEmpresa(
                usuarioLogado.getIdEmpresa().getIdEmpresa(),
                mensagem.getIdEmpresa().getIdEmpresa(),
                "Você não pode acessar mensagens de outra empresa."
        );

        validarParticipacaoAtiva(
                usuarioLogado.getIdUsuarioEmpresa(),
                mensagem.getIdConversa().getIdConversa()
        );

        return mensagem;
    }

    private void validarParticipacaoAtiva(Long idUsuarioEmpresa, Long idConversa) {
        boolean participaDaConversa = conversaParticipanteRepository
                .existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(
                        idConversa,
                        idUsuarioEmpresa
                );

        if (!participaDaConversa) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não participa desta conversa."
            );
        }
    }

    private void validarMesmaEmpresa(UsuarioEmpresaModel usuarioA, UsuarioEmpresaModel usuarioB) {
        validarMesmaEmpresa(
                usuarioA.getIdEmpresa().getIdEmpresa(),
                usuarioB.getIdEmpresa().getIdEmpresa(),
                "Usuários de empresas diferentes não podem trocar mensagens."
        );
    }

    private void validarMesmaEmpresa(Long idEmpresaEsperada, Long idEmpresaAtual, String mensagemErro) {
        if (!idEmpresaEsperada.equals(idEmpresaAtual)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, mensagemErro);
        }
    }
}
