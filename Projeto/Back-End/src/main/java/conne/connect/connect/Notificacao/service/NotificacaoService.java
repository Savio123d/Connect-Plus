package conne.connect.connect.Notificacao.service;

import conne.connect.connect.Notificacao.dto.NotificacaoResponseDTO;
import conne.connect.connect.Notificacao.model.NotificacaoModel;
import conne.connect.connect.Notificacao.repository.NotificacaoRepository;
import conne.connect.connect.NotificacoesSistem.dto.NotificacaoPushDTO;
import conne.connect.connect.NotificacoesSistem.service.NotificacaoRealtimeService;
import conne.connect.connect.Security.AutorizacaoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoRealtimeService notificacaoRealtimeService;
    private final AutorizacaoService autorizacaoService;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            NotificacaoRealtimeService notificacaoRealtimeService,
            AutorizacaoService autorizacaoService
    ) {
        this.notificacaoRepository = notificacaoRepository;
        this.notificacaoRealtimeService = notificacaoRealtimeService;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoModel> findAll() {
        Long idEmpresa = autorizacaoService.empresaAtual();
        return idEmpresa == null
                ? List.of()
                : notificacaoRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(idEmpresa);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoModel criarNotificacao(NotificacaoModel notificacaoModel) {
        validarEscopoNotificacao(notificacaoModel);
        NotificacaoModel notificacaoSalva = notificacaoRepository.save(notificacaoModel);

        notificacaoRealtimeService.enviarParaUsuario(
                notificacaoSalva.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                paraPushDTO(notificacaoSalva, "NOVA_NOTIFICACAO")
        );

        return notificacaoSalva;
    }

    @Transactional(readOnly = true)
    public Optional<NotificacaoModel> buscarPorId(Long idNotificacao) {
        return notificacaoRepository.findByIdNotificacaoAndExcluidoIsNull(idNotificacao)
                .filter(this::podeAcessarNotificacao);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoModel atualizarNotificacao(
            Long idNotificacao,
            NotificacaoModel notificacaoModel
    ) {
        NotificacaoModel notificacao = buscarNotificacaoExistente(idNotificacao);
        validarEscopoNotificacao(notificacaoModel);

        notificacao.setIdEmpresa(notificacaoModel.getIdEmpresa());
        notificacao.setIdUsuarioEmpresa(notificacaoModel.getIdUsuarioEmpresa());
        notificacao.setTipo(notificacaoModel.getTipo());
        notificacao.setTitulo(notificacaoModel.getTitulo());
        notificacao.setMensagem(notificacaoModel.getMensagem());
        notificacao.setLida(notificacaoModel.getLida());

        return notificacaoRepository.save(notificacao);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public void excluirNotificacao(Long idNotificacao) {
        NotificacaoModel notificacao = buscarNotificacaoExistente(idNotificacao);
        notificacao.setExcluido(LocalDate.now());
        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarPorUsuarioEmpresa(Long idUsuarioEmpresa) {
        validarDestinatario(idUsuarioEmpresa);
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
        validarDestinatario(idUsuarioEmpresa);
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
        validarDestinatario(idUsuarioEmpresa);
        return notificacaoRepository
                .countByIdUsuarioEmpresa_IdUsuarioEmpresaAndLidaFalseAndExcluidoIsNull(
                        idUsuarioEmpresa
                );
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "notificacoesNaoLidas", allEntries = true),
            @CacheEvict(value = "notificacoesUltimas", allEntries = true)
    })
    public NotificacaoResponseDTO marcarComoLida(Long idNotificacao) {
        NotificacaoModel notificacao = buscarNotificacaoExistente(idNotificacao);
        notificacao.setLida(true);

        NotificacaoModel notificacaoSalva = notificacaoRepository.save(notificacao);

        notificacaoRealtimeService.enviarParaUsuario(
                notificacaoSalva.getIdUsuarioEmpresa().getIdUsuarioEmpresa(),
                paraPushDTO(notificacaoSalva, "NOTIFICACAO_LIDA")
        );

        return NotificacaoResponseDTO.fromModel(notificacaoSalva);
    }

    private NotificacaoModel buscarNotificacaoExistente(Long idNotificacao) {
        NotificacaoModel notificacao = notificacaoRepository
                .findByIdNotificacaoAndExcluidoIsNull(idNotificacao)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificação não encontrada."
                ));
        validarAcessoNotificacao(notificacao);
        return notificacao;
    }

    private void validarEscopoNotificacao(NotificacaoModel notificacao) {
        if (notificacao.getIdEmpresa() == null
                || !autorizacaoService.mesmaEmpresa(notificacao.getIdEmpresa().getIdEmpresa())
                || notificacao.getIdUsuarioEmpresa() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "A notificação deve pertencer à empresa autenticada."
            );
        }

        validarDestinatario(notificacao.getIdUsuarioEmpresa().getIdUsuarioEmpresa());
    }

    private void validarDestinatario(Long idUsuarioEmpresa) {
        if (!autorizacaoService.vinculoDaEmpresa(idUsuarioEmpresa)
                || (!autorizacaoService.proprioVinculo(idUsuarioEmpresa)
                && !autorizacaoService.ehGestor())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode acessar notificações de outro usuário."
            );
        }
    }

    private boolean podeAcessarNotificacao(NotificacaoModel notificacao) {
        return notificacao.getIdEmpresa() != null
                && autorizacaoService.mesmaEmpresa(notificacao.getIdEmpresa().getIdEmpresa())
                && notificacao.getIdUsuarioEmpresa() != null
                && (autorizacaoService.ehGestor()
                || autorizacaoService.proprioVinculo(
                        notificacao.getIdUsuarioEmpresa().getIdUsuarioEmpresa()));
    }

    private void validarAcessoNotificacao(NotificacaoModel notificacao) {
        if (!podeAcessarNotificacao(notificacao)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode acessar esta notificação."
            );
        }
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
