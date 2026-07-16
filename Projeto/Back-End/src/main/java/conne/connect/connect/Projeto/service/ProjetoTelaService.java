package conne.connect.connect.Projeto.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Feedback.service.FeedbackService;
import conne.connect.connect.Projeto.dto.ProjetoRequestDTO;
import conne.connect.connect.Projeto.dto.ProjetoResponseDTO;
import conne.connect.connect.Projeto.dto.ProjetoResumoDTO;
import conne.connect.connect.Projeto.enums.MarcoStatusProjetoTela;
import conne.connect.connect.Projeto.enums.PrioridadeProjetoTela;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.enums.TarefaStatusProjetoTela;
import conne.connect.connect.Projeto.mapper.ProjetoTelaMapper;
import conne.connect.connect.Projeto.model.MarcoProjetoTelaModel;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.repository.PessoaProjetoTelaRepository;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Tarefa.dto.TarefaRequestDTO;
import conne.connect.connect.Tarefa.enums.DificuldadeTarefa;
import conne.connect.connect.Tarefa.enums.PrioridadeTarefa;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Tarefa.service.TarefaService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoTelaService {

    private final ProjetoTelaRepository projetoRepository;
    private final PessoaProjetoTelaRepository pessoaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final TarefaService tarefaService;
    private final TarefaRepository tarefaRepository;
    private final ProjetoTelaMapper mapper;
    private final FeedbackService feedbackService;
    private final AutorizacaoService autorizacaoService;

    public ProjetoTelaService(
        ProjetoTelaRepository projetoRepository,
        PessoaProjetoTelaRepository pessoaRepository,
        EmpresaRepository empresaRepository,
        UsuarioEmpresaRepository usuarioEmpresaRepository,
        TarefaService tarefaService,
        TarefaRepository tarefaRepository,
        ProjetoTelaMapper mapper,
        FeedbackService feedbackService,
        AutorizacaoService autorizacaoService
    ) {
        this.projetoRepository = projetoRepository;
        this.pessoaRepository = pessoaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.tarefaService = tarefaService;
        this.tarefaRepository = tarefaRepository;
        this.mapper = mapper;
        this.feedbackService = feedbackService;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listar(Long empresaId) {
        validarEmpresaId(empresaId);
        return mapper.toResponseList(
            projetoRepository.findByEmpresa_IdEmpresaAndExcluidoIsNullOrderByIdProjetoDesc(empresaId));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "projetosPorEmpresa", key = "@autorizacao.empresaAtual() + ':' + #empresaId")
    public List<ProjetoResumoDTO> listarResumos(Long empresaId) {
        validarEmpresaId(empresaId);
        return projetoRepository.listarResumosPorEmpresa(empresaId);
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id, Long empresaId) {
        validarEmpresaId(empresaId);
        ProjetoTelaModel projeto = buscarProjeto(id);
        validarProjetoDaEmpresa(projeto, empresaId);
        return mapper.toResponse(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO.PessoaDTO> listarUsuariosDisponiveis(Long empresaId) {
        validarEmpresaId(empresaId);

        return usuarioEmpresaRepository.findByIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(empresaId)
            .stream()
            .sorted(Comparator.comparing(this::nomeDoUsuario, String.CASE_INSENSITIVE_ORDER))
            .map(this::toPessoaDisponivel)
            .toList();
    }

    @Transactional
    @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    public ProjetoResponseDTO criar(ProjetoRequestDTO request) {
        validarCriacao(request);

        Long empresaId = request.empresaId();
        EmpresaModel empresa = buscarEmpresa(empresaId);
        PessoaProjetoTelaModel lider = buscarPessoaDaEmpresa(request.liderId(), empresaId);

        Set<PessoaProjetoTelaModel> membros = new LinkedHashSet<>();
        membros.add(lider);

        if (request.membrosIds() != null && !request.membrosIds().isEmpty()) {
            request.membrosIds()
                .forEach(idUsuarioEmpresa -> membros.add(buscarPessoaDaEmpresa(idUsuarioEmpresa, empresaId)));
        }

        ProjetoTelaModel projeto = new ProjetoTelaModel();
        projeto.setEmpresa(empresa);
        projeto.setNome(request.nome().trim());
        projeto.setDescricao(request.descricao().trim());
        projeto.setPrazo(request.prazo());
        projeto.setInicio(LocalDate.now());
        projeto.setStatus(ProjetoStatusTela.em_andamento);
        projeto.setPrioridade(PrioridadeProjetoTela.ALTA);
        projeto.setProgresso(0);
        projeto.setHorasTrabalhadas(0);
        projeto.setHorasEstimadas(240);
        projeto.setAtrasado(request.prazo().isBefore(LocalDate.now()));
        projeto.setLider(lider);
        projeto.setMembros(membros);

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    public ProjetoResponseDTO atualizarStatus(Long id, String statusTexto) {
        if (statusTexto == null || statusTexto.isBlank()) {
            throw new IllegalArgumentException("Status do projeto é obrigatório.");
        }

        ProjetoStatusTela status = ProjetoStatusTela.from(statusTexto);
        ProjetoTelaModel projeto = buscarProjeto(id);
        boolean concluindoAgora = projeto.getStatus() != ProjetoStatusTela.concluido
            && status == ProjetoStatusTela.concluido;

        projeto.setStatus(status);

        if (status == ProjetoStatusTela.concluido) {
            projeto.setProgresso(100);
            projeto.setAtrasado(false);

            if (projeto.getConcluidoEm() == null) {
                projeto.setConcluidoEm(LocalDate.now());
            }
        } else {
            projeto.setAtrasado(projeto.getPrazo() != null && projeto.getPrazo().isBefore(LocalDate.now()));
            projeto.setConcluidoEm(null);
        }

        ProjetoTelaModel projetoSalvo = projetoRepository.save(projeto);

        if (concluindoAgora) {
            feedbackService.abrirRodada360ParaProjetoConcluido(projetoSalvo);
        }

        return mapper.toResponse(projetoSalvo);
    }

    // Exclusao logica: o projeto e suas tarefas recebem a data em "excluido",
    // preservando o historico de XP ja concedido.
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "projetosPorEmpresa", allEntries = true),
        @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
        @CacheEvict(value = "tarefaPorId", allEntries = true)
    })
    public void excluir(Long id) {
        ProjetoTelaModel projeto = buscarProjeto(id);

        LocalDate hoje = LocalDate.now();
        List<TarefaModel> tarefas =
            tarefaRepository.findByIdProjeto_IdProjetoAndExcluidoIsNullOrderByIdTarefaAsc(id);

        for (TarefaModel tarefa : tarefas) {
            tarefa.setExcluido(hoje);
        }

        tarefaRepository.saveAll(tarefas);

        projeto.setExcluido(hoje);
        projetoRepository.save(projeto);
    }

    @Transactional
    @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    public ProjetoResponseDTO adicionarMembro(Long projetoId, Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);
        Long empresaId = obterEmpresaIdDoProjeto(projeto);
        PessoaProjetoTelaModel pessoa = buscarPessoaDaEmpresa(usuarioId, empresaId);

        boolean jaEstaNoProjeto = projeto.getMembros().stream()
            .anyMatch(membro -> obterUsuarioEmpresaId(membro).equals(usuarioId));

        if (!jaEstaNoProjeto) {
            projeto.getMembros().add(pessoa);
        }

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    public ProjetoResponseDTO adicionarMarco(Long projetoId, ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do marco não enviados.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);

        if (request.titulo() == null || request.titulo().isBlank() || request.data() == null) {
            throw new IllegalArgumentException("Título e data do marco são obrigatórios.");
        }

        MarcoProjetoTelaModel marco = new MarcoProjetoTelaModel();
        marco.setTitulo(request.titulo().trim());
        marco.setData(request.data());
        marco.setStatus(MarcoStatusProjetoTela.from(request.status()));
        marco.setProjeto(projeto);

        projeto.getMarcos().add(marco);
        recalcularProgresso(projeto);

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    public ProjetoResponseDTO adicionarTarefa(Long projetoId, ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da tarefa não enviados.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);

        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new IllegalArgumentException("Título da tarefa é obrigatório.");
        }

        Long empresaId = obterEmpresaIdDoProjeto(projeto);
        Long responsavelId = resolverResponsavelId(projeto, request);

        if (responsavelId == null) {
            throw new IllegalArgumentException("Responsável da tarefa é obrigatório.");
        }

        PessoaProjetoTelaModel responsavel = buscarPessoaDaEmpresa(responsavelId, empresaId);

        boolean responsavelJaEhMembro = projeto.getMembros().stream()
            .anyMatch(membro -> obterUsuarioEmpresaId(membro).equals(responsavelId));

        if (!responsavelJaEhMembro) {
            projeto.getMembros().add(responsavel);
        }

        TarefaRequestDTO tarefaRequest = new TarefaRequestDTO();
        tarefaRequest.setIdEmpresa(empresaId);
        tarefaRequest.setIdProjeto(projeto.getIdProjeto());
        tarefaRequest.setIdResponsavelUsuarioEmpresa(responsavelId);
        tarefaRequest.setTitulo(request.titulo().trim());
        tarefaRequest.setDescricao("");
        tarefaRequest.setPrioridade(prioridadeTarefa(request.prioridade()));
        tarefaRequest.setDificuldade(DificuldadeTarefa.medio);
        tarefaRequest.setStatus(statusTarefa(request.status()));
        tarefaRequest.setHorasEstimadas(request.horasEstimadas() != null ? request.horasEstimadas() : 8);

        tarefaService.criarTarefa(tarefaRequest);
        recalcularProgresso(projeto);

        return mapper.toResponse(projetoRepository.save(projeto));
    }


    private EmpresaModel buscarEmpresa(Long empresaId) {
        validarEmpresaId(empresaId);
        return empresaRepository.findById(empresaId)
            .filter(empresa -> empresa.getExcluido() == null)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada."));
    }

    private ProjetoTelaModel buscarProjeto(Long id) {
        return projetoRepository
            .findByIdProjetoAndEmpresa_IdEmpresaAndExcluidoIsNull(id, autorizacaoService.empresaAtual())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));
    }

    private PessoaProjetoTelaModel buscarPessoaDaEmpresa(Long idUsuarioEmpresa, Long empresaId) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
            .findByIdUsuarioEmpresaAndIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(
                idUsuarioEmpresa,
                empresaId
            )
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário da empresa não encontrado."));

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo())
            || usuarioEmpresa.getExcluido() != null
            || usuarioEmpresa.getIdEmpresa() == null
            || !empresaId.equals(usuarioEmpresa.getIdEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("Usuário não pertence à empresa do projeto.");
        }

        return sincronizarPessoaDoProjeto(usuarioEmpresa);
    }

    private PessoaProjetoTelaModel sincronizarPessoaDoProjeto(UsuarioEmpresaModel usuarioEmpresa) {
        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        String nome = usuario != null ? usuario.getNome() : "Usuário";
        String email = usuario != null ? usuario.getEmail() : "usuario-" + usuarioEmpresa.getIdUsuarioEmpresa() + "@connect.local";
        PessoaProjetoTelaModel pessoa = pessoaRepository
            .findByUsuarioEmpresa_IdUsuarioEmpresa(usuarioEmpresa.getIdUsuarioEmpresa())
            .or(() -> pessoaRepository.findByEmail(email))
            .orElseGet(PessoaProjetoTelaModel::new);

        pessoa.setUsuarioEmpresa(usuarioEmpresa);
        pessoa.setEmpresa(usuarioEmpresa.getIdEmpresa());
        pessoa.setNome(nome);
        pessoa.setEmail(email);
        pessoa.setCargo(formatarCargo(usuarioEmpresa.getPapel() != null ? usuarioEmpresa.getPapel().name() : null));
        pessoa.setIniciais(gerarIniciais(nome));
        pessoa.setAtivo(Boolean.TRUE.equals(usuarioEmpresa.getAtivo()) && usuarioEmpresa.getExcluido() == null);

        if (pessoa.getHorasTrabalhadas() == null) {
            pessoa.setHorasTrabalhadas(0);
        }

        return pessoaRepository.save(pessoa);
    }

    private ProjetoResponseDTO.PessoaDTO toPessoaDisponivel(UsuarioEmpresaModel usuarioEmpresa) {
        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        String nome = usuario != null ? usuario.getNome() : "Usuario";
        String email = usuario != null
            ? usuario.getEmail()
            : "usuario-" + usuarioEmpresa.getIdUsuarioEmpresa() + "@connect.local";

        return new ProjetoResponseDTO.PessoaDTO(
            usuarioEmpresa.getIdUsuarioEmpresa(),
            nome,
            formatarCargo(usuarioEmpresa.getPapel() != null ? usuarioEmpresa.getPapel().name() : null),
            email,
            gerarIniciais(nome),
            0,
            null,
            Boolean.TRUE.equals(usuarioEmpresa.getAtivo()) && usuarioEmpresa.getExcluido() == null
        );
    }

    private void validarCriacao(ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do projeto não enviados.");
        }
        validarEmpresaId(request.empresaId());
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do projeto é obrigatório.");
        }
        if (request.descricao() == null || request.descricao().isBlank()) {
            throw new IllegalArgumentException("Descrição do projeto é obrigatória.");
        }
        if (request.prazo() == null) {
            throw new IllegalArgumentException("Prazo do projeto é obrigatório.");
        }
        if (request.liderId() == null) {
            throw new IllegalArgumentException("Líder do projeto é obrigatório.");
        }
    }

    private void validarEmpresaId(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException("Empresa do usuário logado não encontrada.");
        }

        autorizacaoService.validarEmpresaAtual(empresaId);
    }

    private void validarProjetoDaEmpresa(ProjetoTelaModel projeto, Long empresaId) {
        Long empresaDoProjeto = obterEmpresaIdDoProjeto(projeto);

        if (!empresaId.equals(empresaDoProjeto)) {
            throw new IllegalArgumentException("Projeto não pertence à empresa informada.");
        }
    }

    private Long obterEmpresaIdDoProjeto(ProjetoTelaModel projeto) {
        if (projeto.getEmpresa() == null || projeto.getEmpresa().getIdEmpresa() == null) {
            throw new IllegalArgumentException("Projeto sem empresa vinculada.");
        }

        return projeto.getEmpresa().getIdEmpresa();
    }

    private Long obterUsuarioEmpresaId(PessoaProjetoTelaModel pessoa) {
        if (pessoa.getUsuarioEmpresa() != null && pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa() != null) {
            return pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa();
        }

        return pessoa.getIdPessoa();
    }

    private Long resolverResponsavelId(ProjetoTelaModel projeto, ProjetoRequestDTO request) {
        if (request.idResponsavelUsuarioEmpresa() != null) {
            return request.idResponsavelUsuarioEmpresa();
        }

        if (request.responsavelId() != null) {
            return request.responsavelId();
        }

        if (request.responsavel() == null || request.responsavel().isBlank()) {
            return null;
        }

        String nomeResponsavel = request.responsavel().trim();

        return projeto.getMembros().stream()
            .filter(membro -> membro.getNome() != null && membro.getNome().equalsIgnoreCase(nomeResponsavel))
            .map(this::obterUsuarioEmpresaId)
            .findFirst()
            .orElse(null);
    }

    private PrioridadeTarefa prioridadeTarefa(PrioridadeProjetoTela prioridade) {
        if (prioridade == null) {
            return PrioridadeTarefa.media;
        }

        return switch (prioridade) {
            case BAIXA -> PrioridadeTarefa.baixa;
            case MEDIA -> PrioridadeTarefa.media;
            case ALTA -> PrioridadeTarefa.alta;
        };
    }

    private StatusTarefa statusTarefa(String statusTexto) {
        TarefaStatusProjetoTela status = TarefaStatusProjetoTela.from(statusTexto);

        return switch (status) {
            case A_FAZER -> StatusTarefa.pendente;
            case EM_ANDAMENTO -> StatusTarefa.em_andamento;
            case CONCLUIDO -> StatusTarefa.concluida;
        };
    }

    private String nomeDoUsuario(UsuarioEmpresaModel usuarioEmpresa) {
        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        return usuario != null && usuario.getNome() != null ? usuario.getNome() : "";
    }

    private String formatarCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            return "Colaborador";
        }

        String cargoFormatado = cargo.toLowerCase().replace("_", " ");
        return cargoFormatado.substring(0, 1).toUpperCase() + cargoFormatado.substring(1);
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

    private void recalcularProgresso(ProjetoTelaModel projeto) {
        // Counts no banco evitam carregar todas as tarefas do projeto so para medir progresso.
        long totalTarefas = projeto.getIdProjeto() != null
            ? tarefaRepository.countByIdProjeto_IdProjetoAndExcluidoIsNull(projeto.getIdProjeto())
            : 0L;
        long tarefasConcluidas = projeto.getIdProjeto() != null
            ? tarefaRepository.countByIdProjeto_IdProjetoAndStatusAndExcluidoIsNull(
                projeto.getIdProjeto(), StatusTarefa.concluida)
            : 0L;

        long totalMarcos = projeto.getMarcos().size();
        long marcosConcluidos = projeto.getMarcos().stream()
            .filter(marco -> marco.getStatus() == MarcoStatusProjetoTela.CONCLUIDO)
            .count();

        long totalItens = totalTarefas + totalMarcos;
        long totalConcluidos = tarefasConcluidas + marcosConcluidos;

        if (totalItens > 0) {
            projeto.setProgresso((int) Math.round((totalConcluidos * 100.0) / totalItens));
        } else {
            projeto.setProgresso(0);
        }
    }
}
