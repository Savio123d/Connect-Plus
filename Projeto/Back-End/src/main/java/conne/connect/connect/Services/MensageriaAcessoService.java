package conne.connect.connect.Services;

import conne.connect.connect.Models.ConversaModel;
import conne.connect.connect.Models.MensagemModel;
import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Repositories.ConversaParticipanteRepository;
import conne.connect.connect.Repositories.ConversaRepository;
import conne.connect.connect.Repositories.MensagemRepository;
import conne.connect.connect.Repositories.UsuarioEmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MensageriaAcessoService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final ConversaParticipanteRepository conversaParticipanteRepository;

    public MensageriaAcessoService(
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ConversaRepository conversaRepository,
            MensagemRepository mensagemRepository,
            ConversaParticipanteRepository conversaParticipanteRepository
    ) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.conversaParticipanteRepository = conversaParticipanteRepository;
    }

    public UsuarioEmpresaModel buscarUsuarioEmpresaAtivo(Long idUsuarioEmpresa) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository.findById(idUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario da empresa nao encontrado."
                ));

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuario da empresa esta inativo."
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
                    "Nao e permitido criar conversa privada com o proprio usuario."
            );
        }
    }

    public ConversaModel buscarConversaComAcesso(Long idConversa, UsuarioEmpresaModel usuarioLogado) {
        ConversaModel conversa = conversaRepository.findByIdConversaAndIdEmpresa_IdEmpresa(
                        idConversa,
                        usuarioLogado.getIdEmpresa().getIdEmpresa()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversa nao encontrada."
                ));

        validarParticipacaoAtiva(usuarioLogado.getIdUsuarioEmpresa(), conversa.getIdConversa());
        return conversa;
    }

    public MensagemModel buscarMensagemComAcesso(Long idMensagem, UsuarioEmpresaModel usuarioLogado) {
        MensagemModel mensagem = mensagemRepository.findById(idMensagem)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mensagem nao encontrada."
                ));

        validarMesmaEmpresa(
                usuarioLogado.getIdEmpresa().getIdEmpresa(),
                mensagem.getIdEmpresa().getIdEmpresa(),
                "Voce nao pode acessar mensagens de outra empresa."
        );

        validarParticipacaoAtiva(
                usuarioLogado.getIdUsuarioEmpresa(),
                mensagem.getIdConversa().getIdConversa()
        );

        return mensagem;
    }

    private void validarParticipacaoAtiva(Long idUsuarioEmpresa, Long idConversa) {
        boolean participaDaConversa = conversaParticipanteRepository
                .existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrue(
                        idConversa,
                        idUsuarioEmpresa
                );

        if (!participaDaConversa) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Voce nao participa desta conversa."
            );
        }
    }

    private void validarMesmaEmpresa(UsuarioEmpresaModel usuarioA, UsuarioEmpresaModel usuarioB) {
        validarMesmaEmpresa(
                usuarioA.getIdEmpresa().getIdEmpresa(),
                usuarioB.getIdEmpresa().getIdEmpresa(),
                "Usuarios de empresas diferentes nao podem trocar mensagens."
        );
    }

    private void validarMesmaEmpresa(Long idEmpresaEsperada, Long idEmpresaAtual, String mensagemErro) {
        if (!idEmpresaEsperada.equals(idEmpresaAtual)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, mensagemErro);
        }
    }
}