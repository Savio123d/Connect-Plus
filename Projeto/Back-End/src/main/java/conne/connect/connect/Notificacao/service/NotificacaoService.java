package conne.connect.connect.Notificacao.service;

import conne.connect.connect.Notificacao.dto.NotificacaoResponseDTO;
import conne.connect.connect.Notificacao.model.NotificacaoModel;
import conne.connect.connect.Notificacao.repository.NotificacaoRepository;
import conne.connect.connect.NotificacoesSistem.dto.NotificacaoPushDTO;
import conne.connect.connect.NotificacoesSistem.service.NotificacaoRealtimeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private NotificacaoRealtimeService notificacaoRealtimeService;

    @Transactional(readOnly = true)
    public List<NotificacaoModel> findAll() {
        return notificacaoRepository.findAll();
    }

    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoModel criarNotificacao(NotificacaoModel notificacaoModel) {
        NotificacaoModel notificacaoSalva = notificacaoRepository.save(notificacaoModel);

        notificacaoRealtimeService.enviarParaUsuario(
                notificacaoSalva.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                paraPushDTO(notificacaoSalva, "NOVA_NOTIFICACAO")
        );

        return notificacaoSalva;
    }

    @Transactional(readOnly = true)
    public Optional<NotificacaoModel> buscarPorId(Long idNotificacao) {
        return notificacaoRepository.findById(idNotificacao);
    }

    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoModel atualizarNotificacao(
            Long idNotificacao,
            NotificacaoModel notificacaoModel
    ) {
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

    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public void excluirNotificacao(Long idNotificacao) {
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));

        notificacao.setExcluido(LocalDate.now());
        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
                        idUsuarioEmpresa
                )
                .stream()
                .map(NotificacaoResponseDTO::fromModel)
                .toList();
    }

    @Cacheable(value = "notificacoesUltimas", key = "#idUsuarioEmpresa")
    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarUltimasPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .findTop5ByIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
                        idUsuarioEmpresa
                )
                .stream()
                .map(NotificacaoResponseDTO::fromModel)
                .toList();
    }

    @Cacheable(value = "notificacoesNaoLidas", key = "#idUsuarioEmpresa")
    @Transactional(readOnly = true)
    public long contarNaoLidasPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        return notificacaoRepository
                .countByIdUsuarioEmpresa_IdUsuarioEmpresaAndLidaFalseAndExcluidoIsNull(
                        idUsuarioEmpresa
                );
    }

    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoResponseDTO marcarComoLida(Long idNotificacao) {
        NotificacaoModel notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));

        notificacao.setLida(true);

        NotificacaoModel notificacaoSalva = notificacaoRepository.save(notificacao);

        notificacaoRealtimeService.enviarParaUsuario(
                notificacaoSalva.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                paraPushDTO(notificacaoSalva, "NOTIFICACAO_LIDA")
        );

        return NotificacaoResponseDTO.fromModel(notificacaoSalva);
    }

    private NotificacaoPushDTO paraPushDTO(NotificacaoModel notificacao, String evento) {
        return new NotificacaoPushDTO(
                evento,
                notificacao.getIdNotificacao(),
                notificacao.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                notificacao.getIdEmpresa().getIdEmpresa(),
                notificacao.getTipo().name(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getLida(),
                notificacao.getDataCriacao()
        );
    }
}
