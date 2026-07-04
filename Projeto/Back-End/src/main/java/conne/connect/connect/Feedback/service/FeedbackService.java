package conne.connect.connect.Feedback.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Feedback.dto.Feedback360PendenteDTO;
import conne.connect.connect.Feedback.dto.Feedback360RequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackRequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackResponseDTO;
import conne.connect.connect.Feedback.dto.FeedbackResumoDTO;
import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Feedback.model.Feedback360AvaliacaoModel;
import conne.connect.connect.Feedback.model.Feedback360RodadaModel;
import conne.connect.connect.Feedback.model.FeedbackModel;
import conne.connect.connect.Feedback.repository.Feedback360AvaliacaoRepository;
import conne.connect.connect.Feedback.repository.Feedback360RodadaRepository;
import conne.connect.connect.Feedback.repository.FeedbackRepository;
import conne.connect.connect.Notificacao.enums.TipoNotificacao;
import conne.connect.connect.Notificacao.model.NotificacaoModel;
import conne.connect.connect.Notificacao.service.NotificacaoService;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final Feedback360RodadaRepository feedback360RodadaRepository;
    private final Feedback360AvaliacaoRepository feedback360AvaliacaoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ProjetoTelaRepository projetoTelaRepository;
    private final TarefaRepository tarefaRepository;
    private final NotificacaoService notificacaoService;

    public FeedbackService(
            FeedbackRepository feedbackRepository,
            Feedback360RodadaRepository feedback360RodadaRepository,
            Feedback360AvaliacaoRepository feedback360AvaliacaoRepository,
            EmpresaRepository empresaRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ProjetoTelaRepository projetoTelaRepository,
            TarefaRepository tarefaRepository,
            NotificacaoService notificacaoService
    ) {
        this.feedbackRepository = feedbackRepository;
        this.feedback360RodadaRepository = feedback360RodadaRepository;
        this.feedback360AvaliacaoRepository = feedback360AvaliacaoRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.projetoTelaRepository = projetoTelaRepository;
        this.tarefaRepository = tarefaRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> listarPorEmpresa(Long empresaId, String filtro) {
        validarEmpresaId(empresaId);

        List<FeedbackModel> feedbacks;
        String filtroNormalizado = filtro == null ? "todos" : filtro.toLowerCase();

        switch (filtroNormalizado) {
            case "avaliacao360", "360" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndAvaliacao360TrueAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId
                    );

            case "positivos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            FeedbackClassificacao.POSITIVO
                    );

            case "medianos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            FeedbackClassificacao.MEDIANO
                    );

            case "negativos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            FeedbackClassificacao.NEGATIVO
                    );

            default -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId
                    );
        }

        return feedbacks.stream()
                .map(FeedbackResponseDTO::fromModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> listarPorDestinatario(
            Long empresaId,
            Long destinatarioUsuarioEmpresaId,
            String filtro
    ) {
        validarEmpresaId(empresaId);
        buscarUsuarioEmpresaDaMesmaEmpresa(
                destinatarioUsuarioEmpresaId,
                empresaId,
                "Usuário avaliado não pertence à empresa informada."
        );

        List<FeedbackModel> feedbacks;
        String filtroNormalizado = filtro == null ? "todos" : filtro.toLowerCase();

        switch (filtroNormalizado) {
            case "avaliacao360", "360" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndAvaliacao360IsTrueAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            destinatarioUsuarioEmpresaId
                    );

            case "positivos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            destinatarioUsuarioEmpresaId,
                            FeedbackClassificacao.POSITIVO
                    );

            case "medianos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            destinatarioUsuarioEmpresaId,
                            FeedbackClassificacao.MEDIANO
                    );

            case "negativos" -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndClassificacaoAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            destinatarioUsuarioEmpresaId,
                            FeedbackClassificacao.NEGATIVO
                    );

            default -> feedbacks =
                    feedbackRepository.findByIdEmpresa_IdEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNullOrderByDataCriacaoDesc(
                            empresaId,
                            destinatarioUsuarioEmpresaId
                    );
        }

        return feedbacks.stream()
                .map(FeedbackResponseDTO::fromModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackResumoDTO buscarResumo(Long empresaId) {
        validarEmpresaId(empresaId);

        Long positivos = feedbackRepository
                .countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
                        empresaId,
                        FeedbackClassificacao.POSITIVO
                );

        Long medianos = feedbackRepository
                .countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
                        empresaId,
                        FeedbackClassificacao.MEDIANO
                );

        Long negativos = feedbackRepository
                .countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
                        empresaId,
                        FeedbackClassificacao.NEGATIVO
                );

        return new FeedbackResumoDTO(positivos, medianos, negativos);
    }

    @Transactional(readOnly = true)
    public FeedbackResponseDTO buscarPorId(Long empresaId, Long idFeedback) {
        validarEmpresaId(empresaId);

        FeedbackModel feedback = feedbackRepository
                .findByIdFeedbackAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idFeedback, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Feedback não encontrado para esta empresa."
                ));

        return FeedbackResponseDTO.fromModel(feedback);
    }

    @Transactional(readOnly = true)
    public List<Feedback360PendenteDTO> listarAvaliacoes360Pendentes(
            Long empresaId,
            Long autorUsuarioEmpresaId
    ) {
        validarEmpresaId(empresaId);

        UsuarioEmpresaModel avaliador = buscarUsuarioEmpresaDaMesmaEmpresa(
                autorUsuarioEmpresaId,
                empresaId,
                "Avaliador não pertence à empresa informada."
        );

        return feedback360AvaliacaoRepository
                .buscarPendentesDoAvaliador(empresaId, avaliador.getIdUsuarioEmpresa())
                .stream()
                .map(this::montarPendente360)
                .sorted(Comparator
                        .comparing(Feedback360PendenteDTO::getProjetoNome, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Feedback360PendenteDTO::getDestinatarioNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public int abrirRodada360ParaProjetoConcluido(ProjetoTelaModel projeto) {
        if (projeto == null
                || projeto.getIdProjeto() == null
                || projeto.getEmpresa() == null
                || projeto.getEmpresa().getIdEmpresa() == null
                || projeto.getStatus() != ProjetoStatusTela.concluido) {
            return 0;
        }

        ProjetoTelaModel projetoCompleto = projetoTelaRepository.findById(projeto.getIdProjeto())
                .orElse(projeto);

        Map<Long, UsuarioEmpresaModel> participantes = obterParticipantesAtivos(projetoCompleto);

        if (participantes.size() < 2) {
            return 0;
        }

        Long empresaId = projetoCompleto.getEmpresa().getIdEmpresa();
        Long projetoId = projetoCompleto.getIdProjeto();

        Feedback360RodadaModel rodada = feedback360RodadaRepository
                .findByEmpresa_IdEmpresaAndProjeto_IdProjeto(empresaId, projetoId)
                .orElseGet(() -> criarRodada360(projetoCompleto));

        int avaliacoesCriadas = 0;

        for (UsuarioEmpresaModel avaliador : participantes.values()) {
            for (UsuarioEmpresaModel avaliado : participantes.values()) {
                if (avaliador.getIdUsuarioEmpresa().equals(avaliado.getIdUsuarioEmpresa())) {
                    continue;
                }

                boolean jaExiste = feedback360AvaliacaoRepository
                        .existsByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresaAndAvaliado_IdUsuarioEmpresa(
                                rodada.getIdRodada(),
                                avaliador.getIdUsuarioEmpresa(),
                                avaliado.getIdUsuarioEmpresa()
                        );

                if (!jaExiste) {
                    Feedback360AvaliacaoModel avaliacao = new Feedback360AvaliacaoModel();
                    avaliacao.setRodada(rodada);
                    avaliacao.setEmpresa(projetoCompleto.getEmpresa());
                    avaliacao.setProjeto(projetoCompleto);
                    avaliacao.setAvaliador(avaliador);
                    avaliacao.setAvaliado(avaliado);
                    avaliacao.setRespondida(false);

                    feedback360AvaliacaoRepository.save(avaliacao);
                    avaliacoesCriadas++;
                }
            }
        }

        if (avaliacoesCriadas > 0) {
            notificarAberturaRodada360(projetoCompleto, participantes);
        }

        return avaliacoesCriadas;
    }

    @Transactional
    public FeedbackResponseDTO criarFeedback(FeedbackRequestDTO dto) {
        validarFeedbackManual(dto);

        EmpresaModel empresa = buscarEmpresa(dto.getEmpresaId());

        UsuarioEmpresaModel autor = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getAutorUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Autor não pertence à empresa informada."
        );

        UsuarioEmpresaModel destinatario = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getDestinatarioUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Destinatário não pertence à empresa informada."
        );

        validarAutorDestinatarioDiferentes(autor, destinatario);

        FeedbackModel feedback = new FeedbackModel();
        feedback.setIdEmpresa(empresa);
        feedback.setIdAutorUsuarioEmpresa(autor);
        feedback.setIdDestinatarioUsuarioEmpresa(destinatario);
        feedback.setClassificacao(dto.getClassificacao());
        feedback.setNota(notaPorClassificacao(dto.getClassificacao()));
        feedback.setCategoria(dto.getCategoria().trim());
        feedback.setComentario(dto.getComentario().trim());
        feedback.setAvaliacao360(false);

        if (dto.getProjetoId() != null) {
            ProjetoTelaModel projeto = buscarProjetoDaEmpresa(dto.getProjetoId(), dto.getEmpresaId());
            feedback.setIdProjeto(projeto);
        }

        if (dto.getTarefaId() != null) {
            TarefaModel tarefa = buscarTarefaDaEmpresa(dto.getTarefaId(), dto.getEmpresaId());
            feedback.setIdTarefa(tarefa);
        }

        FeedbackModel salvo = feedbackRepository.save(feedback);
        return FeedbackResponseDTO.fromModel(salvo);
    }

    @Transactional
    public FeedbackResponseDTO criarAvaliacao360(Feedback360RequestDTO dto) {
        validarAvaliacao360(dto);

        Feedback360AvaliacaoModel avaliacaoPendente = buscarAvaliacaoPendente(dto);
        Integer nota = obterNota360(dto);

        if (Boolean.TRUE.equals(avaliacaoPendente.getRespondida())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já realizou esta avaliação 360°."
            );
        }

        validarAutorDestinatarioDiferentes(
                avaliacaoPendente.getAvaliador(),
                avaliacaoPendente.getAvaliado()
        );

        FeedbackModel feedback = new FeedbackModel();
        feedback.setIdEmpresa(avaliacaoPendente.getEmpresa());
        feedback.setIdAutorUsuarioEmpresa(avaliacaoPendente.getAvaliador());
        feedback.setIdDestinatarioUsuarioEmpresa(avaliacaoPendente.getAvaliado());
        feedback.setIdProjeto(avaliacaoPendente.getProjeto());
        feedback.setNota(nota);
        feedback.setClassificacao(classificacaoPorNota(nota));
        feedback.setCategoria("Avaliação 360°");
        feedback.setComentario(dto.getComentario() != null ? dto.getComentario().trim() : "");
        feedback.setAvaliacao360(true);
        feedback.setComprometimento(dto.getComprometimento());
        feedback.setNivelEntregas(dto.getNivelEntregas());
        feedback.setColaboracao(dto.getColaboracao());
        feedback.setComunicacao(dto.getComunicacao());

        FeedbackModel feedbackSalvo = feedbackRepository.save(feedback);

        avaliacaoPendente.setNota(nota);
        avaliacaoPendente.setComentario(feedback.getComentario());
        avaliacaoPendente.setRespondida(true);
        avaliacaoPendente.setFeedback(feedbackSalvo);
        feedback360AvaliacaoRepository.save(avaliacaoPendente);

        return FeedbackResponseDTO.fromModel(feedbackSalvo);
    }

    @Transactional
    public FeedbackResponseDTO atualizarFeedback(Long idFeedback, FeedbackRequestDTO dto) {
        validarFeedbackManual(dto);

        FeedbackModel feedback = feedbackRepository
                .findByIdFeedbackAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idFeedback, dto.getEmpresaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Feedback não encontrado para esta empresa."
                ));

        if (Boolean.TRUE.equals(feedback.getAvaliacao360())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Avaliação 360° não pode ser alterada por este endpoint."
            );
        }

        UsuarioEmpresaModel autor = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getAutorUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Autor não pertence à empresa informada."
        );

        UsuarioEmpresaModel destinatario = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getDestinatarioUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Destinatário não pertence à empresa informada."
        );

        validarAutorDestinatarioDiferentes(autor, destinatario);

        feedback.setIdAutorUsuarioEmpresa(autor);
        feedback.setIdDestinatarioUsuarioEmpresa(destinatario);
        feedback.setClassificacao(dto.getClassificacao());
        feedback.setNota(notaPorClassificacao(dto.getClassificacao()));
        feedback.setCategoria(dto.getCategoria().trim());
        feedback.setComentario(dto.getComentario().trim());

        FeedbackModel atualizado = feedbackRepository.save(feedback);
        return FeedbackResponseDTO.fromModel(atualizado);
    }

    @Transactional
    public void excluirFeedback(Long empresaId, Long idFeedback) {
        validarEmpresaId(empresaId);

        FeedbackModel feedback = feedbackRepository
                .findByIdFeedbackAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idFeedback, empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Feedback não encontrado para esta empresa."
                ));

        feedback.setExcluido(LocalDate.now());
        feedbackRepository.save(feedback);
    }

    private Feedback360PendenteDTO montarPendente360(Feedback360AvaliacaoModel avaliacao) {
        ProjetoTelaModel projeto = avaliacao.getProjeto();
        UsuarioEmpresaModel avaliado = avaliacao.getAvaliado();
        String nomeAvaliado = nomeDoUsuarioEmpresa(avaliado);

        return new Feedback360PendenteDTO(
                avaliacao.getIdAvaliacao(),
                avaliacao.getRodada() != null ? avaliacao.getRodada().getIdRodada() : null,
                projeto != null ? projeto.getIdProjeto() : null,
                projeto != null ? projeto.getNome() : "Projeto",
                avaliado != null ? avaliado.getIdUsuarioEmpresa() : null,
                nomeAvaliado,
                gerarIniciais(nomeAvaliado),
                projeto != null ? projeto.getConcluidoEm() : null,
                null,
                null,
                false
        );
    }

    private Feedback360RodadaModel criarRodada360(ProjetoTelaModel projeto) {
        Feedback360RodadaModel rodada = new Feedback360RodadaModel();
        rodada.setEmpresa(projeto.getEmpresa());
        rodada.setProjeto(projeto);
        rodada.setAtiva(true);
        return feedback360RodadaRepository.save(rodada);
    }

    private Map<Long, UsuarioEmpresaModel> obterParticipantesAtivos(ProjetoTelaModel projeto) {
        Map<Long, UsuarioEmpresaModel> participantes = new LinkedHashMap<>();

        adicionarParticipante(participantes, projeto.getLider(), projeto.getEmpresa().getIdEmpresa());

        if (projeto.getMembros() != null) {
            projeto.getMembros().forEach(membro -> adicionarParticipante(
                    participantes,
                    membro,
                    projeto.getEmpresa().getIdEmpresa()
            ));
        }

        return participantes;
    }

    private void adicionarParticipante(
            Map<Long, UsuarioEmpresaModel> participantes,
            PessoaProjetoTelaModel pessoa,
            Long empresaId
    ) {
        if (pessoa == null || !Boolean.TRUE.equals(pessoa.getAtivo())) {
            return;
        }

        UsuarioEmpresaModel usuarioEmpresa = pessoa.getUsuarioEmpresa();

        if (usuarioEmpresa == null
                || usuarioEmpresa.getIdUsuarioEmpresa() == null
                || usuarioEmpresa.getIdEmpresa() == null
                || !empresaId.equals(usuarioEmpresa.getIdEmpresa().getIdEmpresa())
                || !Boolean.TRUE.equals(usuarioEmpresa.getAtivo())
                || usuarioEmpresa.getExcluido() != null) {
            return;
        }

        participantes.put(usuarioEmpresa.getIdUsuarioEmpresa(), usuarioEmpresa);
    }

    private void notificarAberturaRodada360(
            ProjetoTelaModel projeto,
            Map<Long, UsuarioEmpresaModel> participantes
    ) {
        participantes.values().forEach(usuarioEmpresa -> {
            NotificacaoModel notificacao = new NotificacaoModel();
            notificacao.setIdEmpresa(projeto.getEmpresa());
            notificacao.setIdUsuarioEmpresa(usuarioEmpresa);
            notificacao.setTipo(TipoNotificacao.feedback);
            notificacao.setTitulo("Avaliação 360° disponível");
            notificacao.setMensagem(
                    "O projeto \"" + projeto.getNome()
                            + "\" foi concluído. Avalie os colegas com quem você trabalhou."
            );
            notificacao.setLida(false);

            notificacaoService.criarNotificacao(notificacao);
        });
    }

    private Feedback360AvaliacaoModel buscarAvaliacaoPendente(Feedback360RequestDTO dto) {
        if (dto.getAvaliacaoId() != null) {
            return feedback360AvaliacaoRepository
                    .buscarPendentePorId(
                            dto.getAvaliacaoId(),
                            dto.getEmpresaId(),
                            dto.getAutorUsuarioEmpresaId()
                    )
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Avaliação 360° pendente não encontrada para este usuário."
                    ));
        }

        if (feedback360AvaliacaoRepository
                .existsByEmpresa_IdEmpresaAndProjeto_IdProjetoAndAvaliador_IdUsuarioEmpresaAndAvaliado_IdUsuarioEmpresaAndRespondidaTrue(
                        dto.getEmpresaId(),
                        dto.getProjetoId(),
                        dto.getAutorUsuarioEmpresaId(),
                        dto.getDestinatarioUsuarioEmpresaId()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já realizou esta avaliação 360°."
            );
        }

        return feedback360AvaliacaoRepository
                .buscarPendentePorPar(
                        dto.getEmpresaId(),
                        dto.getProjetoId(),
                        dto.getAutorUsuarioEmpresaId(),
                        dto.getDestinatarioUsuarioEmpresaId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avaliação 360° pendente não encontrada para este par."
                ));
    }

    private EmpresaModel buscarEmpresa(Long empresaId) {
        validarEmpresaId(empresaId);

        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada."
                ));
    }

    private ProjetoTelaModel buscarProjetoDaEmpresa(Long projetoId, Long empresaId) {
        if (projetoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto é obrigatório.");
        }

        ProjetoTelaModel projeto = projetoTelaRepository.findById(projetoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Projeto não encontrado."
                ));

        if (projeto.getEmpresa() == null
                || !empresaId.equals(projeto.getEmpresa().getIdEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Projeto não pertence à empresa informada."
            );
        }

        return projeto;
    }

    private TarefaModel buscarTarefaDaEmpresa(Long tarefaId, Long empresaId) {
        TarefaModel tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tarefa não encontrada."
                ));

        if (tarefa.getIdEmpresa() == null
                || !empresaId.equals(tarefa.getIdEmpresa().getIdEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tarefa não pertence à empresa informada."
            );
        }

        return tarefa;
    }

    private UsuarioEmpresaModel buscarUsuarioEmpresaDaMesmaEmpresa(
            Long usuarioEmpresaId,
            Long empresaId,
            String mensagemErro
    ) {
        if (usuarioEmpresaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro);
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository.findById(usuarioEmpresaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        mensagemErro
                ));

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo())
                || usuarioEmpresa.getExcluido() != null
                || usuarioEmpresa.getIdEmpresa() == null
                || !empresaId.equals(usuarioEmpresa.getIdEmpresa().getIdEmpresa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagemErro);
        }

        return usuarioEmpresa;
    }

    private void validarAutorDestinatarioDiferentes(
            UsuarioEmpresaModel autor,
            UsuarioEmpresaModel destinatario
    ) {
        if (autor.getIdUsuarioEmpresa().equals(destinatario.getIdUsuarioEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário não pode enviar feedback para si mesmo."
            );
        }
    }

    private void validarFeedbackManual(FeedbackRequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do feedback não enviados.");
        }

        validarEmpresaId(dto.getEmpresaId());

        if (dto.getAutorUsuarioEmpresaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autor é obrigatório.");
        }

        if (dto.getDestinatarioUsuarioEmpresaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destinatário é obrigatório.");
        }

        if (dto.getClassificacao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Classificação é obrigatória.");
        }

        if (dto.getCategoria() == null || dto.getCategoria().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria é obrigatória.");
        }

        if (dto.getComentario() == null || dto.getComentario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentário é obrigatório.");
        }
    }

    private void validarAvaliacao360(Feedback360RequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da avaliação 360° não enviados.");
        }

        validarEmpresaId(dto.getEmpresaId());

        if (dto.getAutorUsuarioEmpresaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avaliador é obrigatório.");
        }

        if (dto.getAvaliacaoId() == null) {
            if (dto.getProjetoId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto é obrigatório para avaliação 360°.");
            }

            if (dto.getDestinatarioUsuarioEmpresaId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário avaliado é obrigatório.");
            }
        }

        validarNota360(obterNota360(dto));
    }

    private Integer obterNota360(Feedback360RequestDTO dto) {
        if (dto.getNota() != null) {
            return dto.getNota();
        }

        if (dto.getComprometimento() != null
                && dto.getNivelEntregas() != null
                && dto.getColaboracao() != null
                && dto.getComunicacao() != null) {
            return (int) Math.round((
                    dto.getComprometimento()
                            + dto.getNivelEntregas()
                            + dto.getColaboracao()
                            + dto.getComunicacao()
            ) / 4.0);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota é obrigatória.");
    }

    private void validarNota360(Integer nota) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nota deve ser um valor de 1 a 5."
            );
        }
    }

    private void validarEmpresaId(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Empresa do usuário logado não encontrada."
            );
        }
    }

    private Integer notaPorClassificacao(FeedbackClassificacao classificacao) {
        return switch (classificacao) {
            case POSITIVO -> 5;
            case MEDIANO -> 3;
            case NEGATIVO -> 1;
        };
    }

    private FeedbackClassificacao classificacaoPorNota(Integer nota) {
        if (nota >= 4) {
            return FeedbackClassificacao.POSITIVO;
        }

        if (nota >= 3) {
            return FeedbackClassificacao.MEDIANO;
        }

        return FeedbackClassificacao.NEGATIVO;
    }

    private String nomeDoUsuarioEmpresa(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa == null) {
            return "Usuário";
        }

        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();

        if (usuario == null || usuario.getNome() == null || usuario.getNome().isBlank()) {
            return "Usuário " + usuarioEmpresa.getIdUsuarioEmpresa();
        }

        return usuario.getNome();
    }

    private String gerarIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "US";
        }

        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0]
                    .substring(0, Math.min(2, partes[0].length()))
                    .toUpperCase();
        }

        return (partes[0].substring(0, 1)
                + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }
}
