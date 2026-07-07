package conne.connect.connect.Feedback.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Feedback.dto.Feedback360PendenteDTO;
import conne.connect.connect.Feedback.dto.Feedback360RequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackRequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackResponseDTO;
import conne.connect.connect.Feedback.dto.FeedbackResumoDTO;
import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Feedback.model.FeedbackModel;
import conne.connect.connect.Feedback.repository.FeedbackRepository;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FeedbackService {

    private static final int PRAZO_AVALIACAO_360_DIAS = 5;

    private final FeedbackRepository feedbackRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ProjetoTelaRepository projetoTelaRepository;
    private final TarefaRepository tarefaRepository;

    public FeedbackService(
            FeedbackRepository feedbackRepository,
            EmpresaRepository empresaRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ProjetoTelaRepository projetoTelaRepository,
            TarefaRepository tarefaRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.projetoTelaRepository = projetoTelaRepository;
        this.tarefaRepository = tarefaRepository;
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> listarPorEmpresa(Long empresaId, String filtro) {
        validarEmpresaId(empresaId);

        List<FeedbackModel> feedbacks;

        String filtroNormalizado = filtro == null ? "todos" : filtro.toLowerCase();

        switch (filtroNormalizado) {
            case "avaliacao360" -> feedbacks =
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

        UsuarioEmpresaModel autor = buscarUsuarioEmpresaDaMesmaEmpresa(
                autorUsuarioEmpresaId,
                empresaId,
                "Autor não pertence à empresa informada."
        );

        return projetoTelaRepository
                .findByEmpresa_IdEmpresaAndStatusAndConcluidoEmIsNotNullOrderByConcluidoEmDesc(
                        empresaId,
                        ProjetoStatusTela.concluido
                )
                .stream()
                .filter(projeto -> usuarioParticipaDoProjeto(projeto, autor.getIdUsuarioEmpresa()))
                .flatMap(projeto -> projeto.getMembros().stream()
                        .filter(membro -> Boolean.TRUE.equals(membro.getAtivo()))
                        .filter(membro -> obterUsuarioEmpresaId(membro) != null)
                        .filter(membro -> !autor.getIdUsuarioEmpresa().equals(obterUsuarioEmpresaId(membro)))
                        .filter(membro -> !avaliacao360JaRespondida(
                                empresaId,
                                projeto.getIdProjeto(),
                                autor.getIdUsuarioEmpresa(),
                                obterUsuarioEmpresaId(membro)
                        ))
                        .map(membro -> montarPendente360(projeto, membro))
                )
                .sorted(Comparator
                        .comparing(Feedback360PendenteDTO::getVencido)
                        .thenComparing(Feedback360PendenteDTO::getPrazoLimite)
                        .thenComparing(Feedback360PendenteDTO::getProjetoNome)
                        .thenComparing(Feedback360PendenteDTO::getDestinatarioNome))
                .toList();
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

        ProjetoTelaModel projeto = buscarProjetoDaEmpresa(dto.getProjetoId(), dto.getEmpresaId());

        if (projeto.getStatus() != ProjetoStatusTela.concluido) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A avaliação 360° só pode ser feita após a conclusão do projeto."
            );
        }

        if (projeto.getConcluidoEm() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Projeto concluído sem data de conclusão registrada. Conclua o projeto novamente."
            );
        }

        if (LocalDate.now().isAfter(prazoLimite360(projeto))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O prazo de 5 dias para responder esta avaliação 360° foi encerrado."
            );
        }

        if (!usuarioParticipaDoProjeto(projeto, autor.getIdUsuarioEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Autor não faz parte do projeto informado."
            );
        }

        if (!usuarioParticipaDoProjeto(projeto, destinatario.getIdUsuarioEmpresa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destinatário não faz parte do projeto informado."
            );
        }

        if (avaliacao360JaRespondida(
                dto.getEmpresaId(),
                projeto.getIdProjeto(),
                autor.getIdUsuarioEmpresa(),
                destinatario.getIdUsuarioEmpresa()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já realizou esta avaliação 360°."
            );
        }

        double media = calcularMedia360(dto);
        FeedbackClassificacao classificacao = classificacaoPorMedia(media);

        FeedbackModel feedback = new FeedbackModel();
        feedback.setIdEmpresa(empresa);
        feedback.setIdAutorUsuarioEmpresa(autor);
        feedback.setIdDestinatarioUsuarioEmpresa(destinatario);
        feedback.setIdProjeto(projeto);
        feedback.setClassificacao(classificacao);
        feedback.setNota((int) Math.round(media));
        feedback.setCategoria("Avaliação 360°");
        feedback.setComentario(dto.getComentario() != null ? dto.getComentario().trim() : "");
        feedback.setAvaliacao360(true);
        feedback.setComprometimento(dto.getComprometimento());
        feedback.setNivelEntregas(dto.getNivelEntregas());
        feedback.setColaboracao(dto.getColaboracao());
        feedback.setComunicacao(dto.getComunicacao());

        FeedbackModel salvo = feedbackRepository.save(feedback);
        return FeedbackResponseDTO.fromModel(salvo);
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

    private Feedback360PendenteDTO montarPendente360(
            ProjetoTelaModel projeto,
            PessoaProjetoTelaModel destinatario
    ) {
        LocalDate prazoLimite = prazoLimite360(projeto);
        LocalDate hoje = LocalDate.now();
        boolean vencido = hoje.isAfter(prazoLimite);
        long diasRestantes = Math.max(ChronoUnit.DAYS.between(hoje, prazoLimite), 0);

        return new Feedback360PendenteDTO(
                projeto.getIdProjeto(),
                projeto.getNome(),
                obterUsuarioEmpresaId(destinatario),
                destinatario.getNome(),
                iniciaisDoDestinatario(destinatario),
                projeto.getConcluidoEm(),
                prazoLimite,
                diasRestantes,
                vencido
        );
    }

    private LocalDate prazoLimite360(ProjetoTelaModel projeto) {
        return projeto.getConcluidoEm().plusDays(PRAZO_AVALIACAO_360_DIAS);
    }

    private boolean avaliacao360JaRespondida(
            Long empresaId,
            Long projetoId,
            Long autorUsuarioEmpresaId,
            Long destinatarioUsuarioEmpresaId
    ) {
        return feedbackRepository
                .existsByIdEmpresa_IdEmpresaAndIdProjeto_IdProjetoAndIdAutorUsuarioEmpresa_IdUsuarioEmpresaAndIdDestinatarioUsuarioEmpresa_IdUsuarioEmpresaAndAvaliacao360TrueAndExcluidoIsNull(
                        empresaId,
                        projetoId,
                        autorUsuarioEmpresaId,
                        destinatarioUsuarioEmpresaId
                );
    }

    private boolean usuarioParticipaDoProjeto(ProjetoTelaModel projeto, Long usuarioEmpresaId) {
        if (usuarioEmpresaId == null || projeto == null) {
            return false;
        }

        Long liderId = obterUsuarioEmpresaId(projeto.getLider());

        if (usuarioEmpresaId.equals(liderId)) {
            return true;
        }

        if (projeto.getMembros() == null) {
            return false;
        }

        return projeto.getMembros().stream()
                .map(this::obterUsuarioEmpresaId)
                .anyMatch(usuarioEmpresaId::equals);
    }

    private Long obterUsuarioEmpresaId(PessoaProjetoTelaModel pessoa) {
        if (pessoa == null) {
            return null;
        }

        if (pessoa.getUsuarioEmpresa() != null
                && pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa() != null) {
            return pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa();
        }

        return null;
    }

    private String iniciaisDoDestinatario(PessoaProjetoTelaModel pessoa) {
        if (pessoa.getIniciais() != null && !pessoa.getIniciais().isBlank()) {
            return pessoa.getIniciais();
        }

        return gerarIniciais(pessoa.getNome());
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Autor é obrigatório.");
        }

        if (dto.getDestinatarioUsuarioEmpresaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destinatário é obrigatório.");
        }

        if (dto.getProjetoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto é obrigatório para avaliação 360°.");
        }

        validarNota360(dto.getComprometimento(), "Comprometimento");
        validarNota360(dto.getNivelEntregas(), "Nível de entregas");
        validarNota360(dto.getColaboracao(), "Colaboração");
        validarNota360(dto.getComunicacao(), "Comunicação");
    }

    private void validarNota360(Integer nota, String criterio) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    criterio + " deve ser avaliado de 1 a 5."
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

    private double calcularMedia360(Feedback360RequestDTO dto) {
        return (
                dto.getComprometimento()
                        + dto.getNivelEntregas()
                        + dto.getColaboracao()
                        + dto.getComunicacao()
        ) / 4.0;
    }

    private FeedbackClassificacao classificacaoPorMedia(double media) {
        if (media >= 4.0) {
            return FeedbackClassificacao.POSITIVO;
        }

        if (media >= 3.0) {
            return FeedbackClassificacao.MEDIANO;
        }

        return FeedbackClassificacao.NEGATIVO;
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