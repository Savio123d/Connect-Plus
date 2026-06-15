package conne.connect.connect.Services;

import conne.connect.connect.Dto.NotificacaoResponseDTO;
import conne.connect.connect.Models.NotificacaoModel;
import conne.connect.connect.Repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
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
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));

        notificacao.setIdEmpresa(notificacaoModel.getIdEmpresa());
        notificacao.setIdUsuarioEmpresa(notificacaoModel.getIdUsuarioEmpresa());
        notificacao.setTipo(notificacaoModel.getTipo());
        notificacao.setTitulo(notificacaoModel.getTitulo());
        notificacao.setMensagem(notificacaoModel.getMensagem());
        notificacao.setLida(notificacaoModel.getLida());

        return notificacaoRepository.save(notificacao);
    }

    public void excluirNotificacao(Long idNotificacao) {
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));

        notificacao.setExcluido(LocalDate.now());
        notificacaoRepository.save(notificacao);
    }

    public List<NotificacaoResponseDTO> buscarPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(idUsuarioEmpresa)
                .stream()
                .map(NotificacaoResponseDTO::fromModel)
                .toList();
    }

    public List<NotificacaoResponseDTO> buscarUltimasPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .findTop5ByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(idUsuarioEmpresa)
                .stream()
                .map(NotificacaoResponseDTO::fromModel)
                .toList();
    }

    public long contarNaoLidasPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .countByIdUsuarioEmpresa_IdUsuarioEmpresaAndLidaFalseAndExcluidoIsNull(idUsuarioEmpresa);
    }

    public NotificacaoResponseDTO marcarComoLida(Long idNotificacao) {
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));

        notificacao.setLida(true);

        NotificacaoModel notificacaoSalva = notificacaoRepository.save(notificacao);

        return NotificacaoResponseDTO.fromModel(notificacaoSalva);
    }
}