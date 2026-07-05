package conne.connect.connect.Feedback.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Feedback.dto.Feedback360GestorDTO;
import conne.connect.connect.Feedback.dto.Feedback360ObrigatorioDTO;
import conne.connect.connect.Feedback.dto.Feedback360ObservacaoRequestDTO;
import conne.connect.connect.Feedback.dto.Feedback360PendenteDTO;
import conne.connect.connect.Feedback.dto.Feedback360RequestDTO;
import conne.connect.connect.Feedback.dto.Feedback360StatusDTO;
import conne.connect.connect.Feedback.dto.Feedback360UsuarioDTO;
import conne.connect.connect.Feedback.dto.FeedbackRequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackResponseDTO;
import conne.connect.connect.Feedback.dto.FeedbackResumoDTO;
import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Feedback.model.Feedback360AvaliacaoModel;
import conne.connect.connect.Feedback.model.Feedback360ObservacaoModel;
import conne.connect.connect.Feedback.model.Feedback360RodadaModel;
import conne.connect.connect.Feedback.model.FeedbackModel;
import conne.connect.connect.Feedback.repository.Feedback360AvaliacaoRepository;
import conne.connect.connect.Feedback.repository.Feedback360ObservacaoRepository;
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
import conne.connect.connect.Usuario.enums.PapelEmpresa;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final Feedback360ObservacaoRepository feedback360ObservacaoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ProjetoTelaRepository projetoTelaRepository;
    private final TarefaRepository tarefaRepository;
    private final NotificacaoService notificacaoService;

    public FeedbackService(
            FeedbackRepository feedbackRepository,
            Feedback360RodadaRepository feedback360RodadaRepository,
            Feedback360AvaliacaoRepository feedback360AvaliacaoRepository,
            Feedback360ObservacaoRepository feedback360ObservacaoRepository,
            EmpresaRepository empresaRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ProjetoTelaRepository projetoTelaRepository,
            TarefaRepository tarefaRepository,
            NotificacaoService notificacaoService
    ) {
        this.feedbackRepository = feedbackRepository;
        this.feedback360RodadaRepository = feedback360RodadaRepository;
        this.feedback360AvaliacaoRepository = feedback360AvaliacaoRepository;
        this.feedback360ObservacaoRepository = feedback360ObservacaoRepository;
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

        Long positivos = feedbackRepository.countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
                empresaId,
                FeedbackClassificacao.POSITIVO
        );

        Long medianos = feedbackRepository.countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
                empresaId,
                FeedbackClassificacao.MEDIANO
        );

        Long negativos = feedbackRepository.countByIdEmpresa_IdEmpresaAndClassificacaoAndExcluidoIsNull(
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
                        .comparing(Feedback360PendenteDTO::getObrigatoria, Comparator.reverseOrder())
                        .thenComparing(Feedback360PendenteDTO::getProjetoNome, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Feedback360PendenteDTO::getOrdem)
                        .thenComparing(Feedback360PendenteDTO::getDestinatarioNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public Feedback360StatusDTO buscarStatus360(Long empresaId, Long usuarioEmpresaId) {
        validarEmpresaId(empresaId);

        UsuarioEmpresaModel usuarioEmpresa = buscarUsuarioEmpresaDaMesmaEmpresa(
                usuarioEmpresaId,
                empresaId,
                "Usuário não pertence à empresa informada."
        );

        List<Feedback360AvaliacaoModel> pendentes = feedback360AvaliacaoRepository
                .buscarPendentesObrigatoriasDoAvaliador(
                        empresaId,
                        usuarioEmpresa.getIdUsuarioEmpresa()
                );

        if (pendentes.isEmpty()) {
            return new Feedback360StatusDTO(
                    false,
                    null,
                    null,
                    null,
                    false,
                    0L
            );
        }

        Feedback360AvaliacaoModel primeira = pendentes.get(0);

        return new Feedback360StatusDTO(
                true,
                primeira.getRodada().getIdRodada(),
                primeira.getProjeto().getIdProjeto(),
                primeira.getProjeto().getNome(),
                true,
                (long) pendentes.size()
        );
    }

    @Transactional
    public void definirObrigatoriedadeProjeto360(Long projetoId, Feedback360ObrigatorioDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da obrigatoriedade não enviados.");
        }

        validarEmpresaId(dto.getEmpresaId());

        UsuarioEmpresaModel gestor = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getGestorUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Gestor não pertence à empresa informada."
        );

        validarGestor(gestor);

        ProjetoTelaModel projeto = buscarProjetoDaEmpresa(projetoId, dto.getEmpresaId());
        boolean obrigatoria = Boolean.TRUE.equals(dto.getObrigatoria());

        projeto.setAvaliacao360Obrigatoria(obrigatoria);
        projetoTelaRepository.save(projeto);

        feedback360RodadaRepository
                .findByEmpresa_IdEmpresaAndProjeto_IdProjeto(dto.getEmpresaId(), projetoId)
                .ifPresent(rodada -> {
                    rodada.setObrigatoria(obrigatoria);
                    feedback360RodadaRepository.save(rodada);
                });
    }

    @Transactional
    public void salvarObservacaoProjeto360(Long rodadaId, Feedback360ObservacaoRequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da observação não enviados.");
        }

        validarEmpresaId(dto.getEmpresaId());

        if (dto.getUsuarioEmpresaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário é obrigatório.");
        }

        if (dto.getObservacao() == null || dto.getObservacao().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Observação do projeto é obrigatória.");
        }

        UsuarioEmpresaModel avaliador = buscarUsuarioEmpresaDaMesmaEmpresa(
                dto.getUsuarioEmpresaId(),
                dto.getEmpresaId(),
                "Usuário não pertence à empresa informada."
        );

        Feedback360RodadaModel rodada = feedback360RodadaRepository
                .findByEmpresa_IdEmpresaAndIdRodada(dto.getEmpresaId(), rodadaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rodada 360° não encontrada."
                ));

        long pendentesNaRodada = feedback360AvaliacaoRepository
                .countByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresaAndRespondidaFalse(
                        rodada.getIdRodada(),
                        avaliador.getIdUsuarioEmpresa()
                );

        if (pendentesNaRodada > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Conclua todas as avaliações individuais antes de enviar a observação do projeto."
            );
        }

        Feedback360ObservacaoModel observacao = feedback360ObservacaoRepository
                .findByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresa(
                        rodadaId,
                        avaliador.getIdUsuarioEmpresa()
                )
                .orElseGet(Feedback360ObservacaoModel::new);

        observacao.setRodada(rodada);
        observacao.setEmpresa(rodada.getEmpresa());
        observacao.setProjeto(rodada.getProjeto());
        observacao.setAvaliador(avaliador);
        observacao.setObservacao(dto.getObservacao().trim());

        feedback360ObservacaoRepository.save(observacao);
    }

    @Transactional(readOnly = true)
    public List<Feedback360UsuarioDTO> listarCardsUsuario360(
            Long empresaId,
            Long usuarioEmpresaId
    ) {
        validarEmpresaId(empresaId);

        UsuarioEmpresaModel usuarioEmpresa = buscarUsuarioEmpresaDaMesmaEmpresa(
                usuarioEmpresaId,
                empresaId,
                "Usuário não pertence à empresa informada."
        );

        List<Feedback360AvaliacaoModel> avaliacoes = feedback360AvaliacaoRepository
                .buscarPorAvaliador(
                        empresaId,
                        usuarioEmpresa.getIdUsuarioEmpresa()
                );

        Map<Long, List<Feedback360AvaliacaoModel>> porRodada = new LinkedHashMap<>();

        for (Feedback360AvaliacaoModel avaliacao : avaliacoes) {
            Long rodadaId = avaliacao.getRodada().getIdRodada();
            porRodada.computeIfAbsent(rodadaId, id -> new ArrayList<>()).add(avaliacao);
        }

        List<Feedback360UsuarioDTO> cards = new ArrayList<>();

        for (List<Feedback360AvaliacaoModel> lista : porRodada.values()) {
            boolean usuarioConcluiu = lista.stream()
                    .allMatch(avaliacao -> Boolean.TRUE.equals(avaliacao.getRespondida()));

            if (!usuarioConcluiu) {
                continue;
            }

            Feedback360AvaliacaoModel base = lista.get(0);

            cards.add(new Feedback360UsuarioDTO(
                    base.getRodada().getIdRodada(),
                    base.getProjeto().getIdProjeto(),
                    base.getProjeto().getNome(),
                    Boolean.TRUE.equals(base.getRodada().getObrigatoria()),
                    true,
                    base.getRodada().getAbertaEm()
            ));
        }

        return cards;
    }

    @Transactional(readOnly = true)
    public List<Feedback360GestorDTO> listarResumoGestor360(
            Long empresaId,
            Long gestorUsuarioEmpresaId
    ) {
        validarEmpresaId(empresaId);

        UsuarioEmpresaModel gestor = buscarUsuarioEmpresaDaMesmaEmpresa(
                gestorUsuarioEmpresaId,
                empresaId,
                "Gestor não pertence à empresa informada."
        );

        validarGestor(gestor);

        List<Feedback360AvaliacaoModel> avaliacoes =
                feedback360AvaliacaoRepository.buscarRespondidasPorEmpresa(empresaId);

        List<Feedback360ObservacaoModel> observacoes =
                feedback360ObservacaoRepository.findByEmpresa_IdEmpresaOrderByCriadaEmDesc(empresaId);

        Map<String, List<Feedback360AvaliacaoModel>> grupo = new LinkedHashMap<>();

        for (Feedback360AvaliacaoModel avaliacao : avaliacoes) {
            String chave = avaliacao.getProjeto().getIdProjeto()
                    + "-"
                    + avaliacao.getAvaliado().getIdUsuarioEmpresa();

            grupo.computeIfAbsent(chave, id -> new ArrayList<>()).add(avaliacao);
        }

        List<Feedback360GestorDTO> resposta = new ArrayList<>();

        for (List<Feedback360AvaliacaoModel> lista : grupo.values()) {
            Feedback360AvaliacaoModel base = lista.get(0);
            Long projetoId = base.getProjeto().getIdProjeto();

            List<String> comentarios = lista.stream()
                    .map(Feedback360AvaliacaoModel::getComentario)
                    .filter(texto -> texto != null && !texto.isBlank())
                    .toList();

            List<String> observacoesProjeto = observacoes.stream()
                    .filter(obs -> obs.getProjeto() != null
                            && projetoId.equals(obs.getProjeto().getIdProjeto()))
                    .map(Feedback360ObservacaoModel::getObservacao)
                    .filter(texto -> texto != null && !texto.isBlank())
                    .distinct()
                    .toList();

            resposta.add(new Feedback360GestorDTO(
                    projetoId,
                    base.getProjeto().getNome(),
                    base.getAvaliado().getIdUsuarioEmpresa(),
                    nomeDoUsuarioEmpresa(base.getAvaliado()),
                    mediaCampo(lista, "nota"),
                    mediaCampo(lista, "assiduidade"),
                    mediaCampo(lista, "nivelEntregas"),
                    mediaCampo(lista, "comunicacao"),
                    mediaCampo(lista, "colaboracao"),
                    mediaCampo(lista, "comprometimento"),
                    (long) lista.size(),
                    comentarios,
                    observacoesProjeto
            ));
        }

        return resposta;
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

        ProjetoTelaModel projetoCompleto = projetoTelaRepository
                .findById(projeto.getIdProjeto())
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
            int ordem = 1;

            for (UsuarioEmpresaModel avaliado : participantes.values()) {
                if (avaliador.getIdUsuarioEmpresa().equals(avaliado.getIdUsuarioEmpresa())) {
                    ordem++;
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
                    avaliacao.setOrdem(ordem);

                    feedback360AvaliacaoRepository.save(avaliacao);
                    avaliacoesCriadas++;
                }

                ordem++;
            }
        }

        if (avaliacoesCriadas > 0) {
            notificarAberturaRodada360(projetoCompleto, participantes, rodada);
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

        if (Boolean.TRUE.equals(avaliacaoPendente.getRespondida())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já realizou esta avaliação 360°.");
        }

        validarAutorDestinatarioDiferentes(
                avaliacaoPendente.getAvaliador(),
                avaliacaoPendente.getAvaliado()
        );

        Integer nota = obterNota360(dto);
        String comentario = dto.getComentario() != null ? dto.getComentario().trim() : "";

        FeedbackModel feedback = new FeedbackModel();
        feedback.setIdEmpresa(avaliacaoPendente.getEmpresa());
        feedback.setIdAutorUsuarioEmpresa(avaliacaoPendente.getAvaliador());
        feedback.setIdDestinatarioUsuarioEmpresa(avaliacaoPendente.getAvaliado());
        feedback.setIdProjeto(avaliacaoPendente.getProjeto());
        feedback.setNota(nota);
        feedback.setClassificacao(null);
        feedback.setCategoria("Avaliação 360°");
        feedback.setComentario(comentario);
        feedback.setAvaliacao360(true);
        feedback.setNivelEntregas(dto.getNivelEntregas());
        feedback.setComunicacao(dto.getComunicacao());
        feedback.setColaboracao(dto.getColaboracao());
        feedback.setComprometimento(dto.getComprometimento());
        feedback.setAssiduidade(dto.getAssiduidade());

        FeedbackModel feedbackSalvo = feedbackRepository.save(feedback);

        avaliacaoPendente.setNota(nota);
        avaliacaoPendente.setAssiduidade(dto.getAssiduidade());
        avaliacaoPendente.setNivelEntregas(dto.getNivelEntregas());
        avaliacaoPendente.setComunicacao(dto.getComunicacao());
        avaliacaoPendente.setColaboracao(dto.getColaboracao());
        avaliacaoPendente.setComprometimento(dto.getComprometimento());
        avaliacaoPendente.setComentario(comentario);
        avaliacaoPendente.setRespondida(true);
        avaliacaoPendente.setFeedback(feedbackSalvo);

        feedback360AvaliacaoRepository.save(avaliacaoPendente);

        atualizarRodadaSeTodasAvaliacoesForamRespondidas(avaliacaoPendente.getRodada());

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
                avaliacao.getRodada() != null && Boolean.TRUE.equals(avaliacao.getRodada().getObrigatoria()),
                avaliacao.getOrdem(),
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
        rodada.setObrigatoria(Boolean.TRUE.equals(projeto.getAvaliacao360Obrigatoria()));
        rodada.setAtiva(true);
        rodada.setConcluida(false);

        return feedback360RodadaRepository.save(rodada);
    }

    private void notificarAberturaRodada360(
            ProjetoTelaModel projeto,
            Map<Long, UsuarioEmpresaModel> participantes,
            Feedback360RodadaModel rodada
    ) {
        boolean obrigatoria = Boolean.TRUE.equals(rodada.getObrigatoria());

        participantes.values().forEach(usuarioEmpresa -> {
            NotificacaoModel notificacao = new NotificacaoModel();
            notificacao.setIdEmpresa(projeto.getEmpresa());
            notificacao.setIdUsuarioEmpresa(usuarioEmpresa);
            notificacao.setTipo(TipoNotificacao.feedback);
            notificacao.setTitulo(obrigatoria ? "Avaliação 360° obrigatória" : "Avaliação 360° disponível");
            notificacao.setMensagem(obrigatoria
                    ? "O projeto \"" + projeto.getNome() + "\" foi concluído. Realize a avaliação 360° para continuar usando o sistema."
                    : "O projeto \"" + projeto.getNome() + "\" foi concluído. A avaliação 360° está disponível."
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
                            "Avaliação 360° pendente não encontrada."
                    ));
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
                        "Avaliação 360° pendente não encontrada."
                ));
    }

    private void atualizarRodadaSeTodasAvaliacoesForamRespondidas(Feedback360RodadaModel rodada) {
        if (rodada == null || rodada.getIdRodada() == null) {
            return;
        }

        long pendentes = feedback360AvaliacaoRepository
                .countByRodada_IdRodadaAndRespondidaFalse(rodada.getIdRodada());

        if (pendentes == 0) {
            rodada.setConcluida(true);
            rodada.setAtiva(false);
            rodada.setConcluidaEm(LocalDateTime.now());

            feedback360RodadaRepository.save(rodada);
        }
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

        validarCriterio360(dto.getAssiduidade(), "Assiduidade");
        validarCriterio360(dto.getNivelEntregas(), "Nível de entregas");
        validarCriterio360(dto.getComunicacao(), "Comunicação");
        validarCriterio360(dto.getColaboracao(), "Colaboração em equipe");
        validarCriterio360(dto.getComprometimento(), "Comprometimento");
    }

    private void validarCriterio360(Integer nota, String campo) {
        if (nota == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " é obrigatório.");
        }

        if (nota < 1 || nota > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " deve ser de 1 a 5 estrelas.");
        }
    }

    private Integer obterNota360(Feedback360RequestDTO dto) {
        return (int) Math.round((
                dto.getAssiduidade()
                        + dto.getNivelEntregas()
                        + dto.getComunicacao()
                        + dto.getColaboracao()
                        + dto.getComprometimento()
        ) / 5.0);
    }

    private void validarAutorDestinatarioDiferentes(
            UsuarioEmpresaModel autor,
            UsuarioEmpresaModel destinatario
    ) {
        if (autor == null
                || destinatario == null
                || autor.getIdUsuarioEmpresa() == null
                || destinatario.getIdUsuarioEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autor ou destinatário inválido.");
        }

        if (autor.getIdUsuarioEmpresa().equals(destinatario.getIdUsuarioEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário não pode enviar feedback para si mesmo."
            );
        }
    }

    private Map<Long, UsuarioEmpresaModel> obterParticipantesAtivos(ProjetoTelaModel projeto) {
        Map<Long, UsuarioEmpresaModel> participantes = new LinkedHashMap<>();

        adicionarParticipante360(participantes, projeto.getLider());

        if (projeto.getMembros() != null) {
            projeto.getMembros().forEach(membro -> adicionarParticipante360(participantes, membro));
        }

        return participantes;
    }

    private void adicionarParticipante360(
            Map<Long, UsuarioEmpresaModel> participantes,
            PessoaProjetoTelaModel pessoa
    ) {
        if (pessoa == null
                || pessoa.getUsuarioEmpresa() == null
                || pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa() == null) {
            return;
        }

        UsuarioEmpresaModel usuarioEmpresa = pessoa.getUsuarioEmpresa();

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo()) || usuarioEmpresa.getExcluido() != null) {
            return;
        }

        participantes.put(usuarioEmpresa.getIdUsuarioEmpresa(), usuarioEmpresa);
    }

    private String nomeDoUsuarioEmpresa(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa == null || usuarioEmpresa.getIdUsuario() == null) {
            return "Usuário";
        }

        return usuarioEmpresa.getIdUsuario().getNome() != null
                ? usuarioEmpresa.getIdUsuario().getNome()
                : "Usuário";
    }

    private Integer notaPorClassificacao(FeedbackClassificacao classificacao) {
        return switch (classificacao) {
            case POSITIVO -> 5;
            case MEDIANO -> 3;
            case NEGATIVO -> 1;
        };
    }

    private String gerarIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "U";
        }

        String[] partes = nome.trim().split("\\s+");
        String primeira = partes[0].substring(0, 1);

        if (partes.length == 1) {
            return primeira.toUpperCase();
        }

        String ultima = partes[partes.length - 1].substring(0, 1);

        return (primeira + ultima).toUpperCase();
    }

    private void validarEmpresaId(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Empresa do usuário logado não encontrada."
            );
        }
    }

    private void validarGestor(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa == null || usuarioEmpresa.getPapel() != PapelEmpresa.gestor) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas gestor pode acessar este recurso."
            );
        }
    }

    private Double mediaCampo(List<Feedback360AvaliacaoModel> avaliacoes, String campo) {
        return avaliacoes.stream()
                .map(avaliacao -> valorCampo(avaliacao, campo))
                .filter(valor -> valor != null)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private Integer valorCampo(Feedback360AvaliacaoModel avaliacao, String campo) {
        return switch (campo) {
            case "assiduidade" -> avaliacao.getAssiduidade();
            case "nivelEntregas" -> avaliacao.getNivelEntregas();
            case "comunicacao" -> avaliacao.getComunicacao();
            case "colaboracao" -> avaliacao.getColaboracao();
            case "comprometimento" -> avaliacao.getComprometimento();
            default -> avaliacao.getNota();
        };
    }
}
