package conne.connect.connect.Services;

import conne.connect.connect.Models.NotificacaoModel;
import conne.connect.connect.Repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public List<NotificacaoModel> findAll() {
        return notificacaoRepository.findAll();
    }

    public NotificacaoModel criarNotificacao(NotificacaoModel notificacaoModel) {
        return notificacaoRepository.save(notificacaoModel);
    }

    public Optional<NotificacaoModel> buscarPorId(Long idNotificacao) {
        return notificacaoRepository.findById(idNotificacao);
    }

    public NotificacaoModel atualizarNotificacao(Long idNotificacao, NotificacaoModel notificacaoModel) {
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao).get();
        notificacao.setIdEmpresa(notificacaoModel.getIdEmpresa());
        notificacao.setIdUsuarioEmpresa(notificacaoModel.getIdUsuarioEmpresa());
        notificacao.setTipo(notificacaoModel.getTipo());
        notificacao.setTitulo(notificacaoModel.getTitulo());
        notificacao.setMensagem(notificacaoModel.getMensagem());
        notificacao.setLida(notificacaoModel.getLida());
        return notificacaoRepository.save(notificacao);
    }

    public void excluirNotificacao(Long idNotificacao) {
        notificacaoRepository.deleteById(idNotificacao);
    }
}
