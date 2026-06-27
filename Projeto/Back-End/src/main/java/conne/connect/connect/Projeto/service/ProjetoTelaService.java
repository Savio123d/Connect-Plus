package conne.connect.connect.Projeto.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Projeto.dto.ProjetoRequestDTO;
import conne.connect.connect.Projeto.dto.ProjetoResponseDTO;
import conne.connect.connect.Projeto.enums.MarcoStatusProjetoTela;
import conne.connect.connect.Projeto.enums.PrioridadeProjetoTela;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.enums.TarefaStatusProjetoTela;
import conne.connect.connect.Projeto.mapper.ProjetoTelaMapper;
import conne.connect.connect.Projeto.model.MarcoProjetoTelaModel;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.model.TarefaProjetoTelaModel;
import conne.connect.connect.Projeto.repository.PessoaProjetoTelaRepository;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjetoTelaService {

    private final ProjetoTelaRepository projetoRepository;
    private final PessoaProjetoTelaRepository pessoaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ProjetoTelaMapper mapper;

    public ProjetoTelaService(
        ProjetoTelaRepository projetoRepository,
        PessoaProjetoTelaRepository pessoaRepository,
        EmpresaRepository empresaRepository,
        UsuarioEmpresaRepository usuarioEmpresaRepository,
        ProjetoTelaMapper mapper
    ) {
        this.projetoRepository = projetoRepository;
        this.pessoaRepository = pessoaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listar(Long empresaId) {
        validarEmpresaId(empresaId);
        return mapper.toResponseList(projetoRepository.findByEmpresa_IdEmpresaOrderByIdProjetoDesc(empresaId));
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id, Long empresaId) {
        validarEmpresaId(empresaId);
        ProjetoTelaModel projeto = buscarProjeto(id);
        validarProjetoDaEmpresa(projeto, empresaId);
        return mapper.toResponse(projeto);
    }

    @Transactional
    public List<ProjetoResponseDTO.PessoaDTO> listarUsuariosDisponiveis(Long empresaId) {
        validarEmpresaId(empresaId);

        return usuarioEmpresaRepository.findByIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(empresaId)
            .stream()
            .sorted(Comparator.comparing(this::nomeDoUsuario, String.CASE_INSENSITIVE_ORDER))
            .map(this::sincronizarPessoaDoProjeto)
            .map(mapper::toPessoaResponse)
            .toList();
    }

    @Transactional
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
    public ProjetoResponseDTO atualizarStatus(Long id, String statusTexto) {
        if (statusTexto == null || statusTexto.isBlank()) {
            throw new IllegalArgumentException("Status do projeto e obrigatorio.");
        }

        ProjetoStatusTela status = ProjetoStatusTela.from(statusTexto);
        ProjetoTelaModel projeto = buscarProjeto(id);
        projeto.setStatus(status);

        if (status == ProjetoStatusTela.concluido) {
            projeto.setProgresso(100);
            projeto.setAtrasado(false);
        } else {
            projeto.setAtrasado(projeto.getPrazo() != null && projeto.getPrazo().isBefore(LocalDate.now()));
        }

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    public void excluir(Long id) {
        if (!projetoRepository.existsById(id)) {
            throw new IllegalArgumentException("Projeto nao encontrado.");
        }

        projetoRepository.deleteById(id);
    }

    @Transactional
    public ProjetoResponseDTO adicionarMembro(Long projetoId, Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario e obrigatorio.");
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
    public ProjetoResponseDTO adicionarMarco(Long projetoId, ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do marco nao enviados.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);

        if (request.titulo() == null || request.titulo().isBlank() || request.data() == null) {
            throw new IllegalArgumentException("Titulo e data do marco sao obrigatorios.");
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
    public ProjetoResponseDTO adicionarTarefa(Long projetoId, ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da tarefa nao enviados.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);

        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new IllegalArgumentException("Titulo da tarefa e obrigatorio.");
        }
        if (request.responsavel() == null || request.responsavel().isBlank()) {
            throw new IllegalArgumentException("Responsavel da tarefa e obrigatorio.");
        }

        TarefaProjetoTelaModel tarefa = new TarefaProjetoTelaModel();
        tarefa.setTitulo(request.titulo().trim());
        tarefa.setResponsavel(request.responsavel().trim());
        tarefa.setPrioridade(request.prioridade() != null ? request.prioridade() : PrioridadeProjetoTela.MEDIA);
        tarefa.setStatus(TarefaStatusProjetoTela.from(request.status()));
        tarefa.setProjeto(projeto);

        projeto.getTarefas().add(tarefa);
        recalcularProgresso(projeto);

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    private EmpresaModel buscarEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));
    }

    private ProjetoTelaModel buscarProjeto(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Projeto nao encontrado."));
    }

    private PessoaProjetoTelaModel buscarPessoaDaEmpresa(Long idUsuarioEmpresa, Long empresaId) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository.findById(idUsuarioEmpresa)
            .orElseThrow(() -> new IllegalArgumentException("Usuario da empresa nao encontrado."));

        if (!Boolean.TRUE.equals(usuarioEmpresa.getAtivo())
            || usuarioEmpresa.getExcluido() != null
            || usuarioEmpresa.getIdEmpresa() == null
            || !empresaId.equals(usuarioEmpresa.getIdEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException("Usuario nao pertence a empresa do projeto.");
        }

        return sincronizarPessoaDoProjeto(usuarioEmpresa);
    }

    private PessoaProjetoTelaModel sincronizarPessoaDoProjeto(UsuarioEmpresaModel usuarioEmpresa) {
        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        String nome = usuario != null ? usuario.getNome() : "Usuario";
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

    private void validarCriacao(ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do projeto nao enviados.");
        }
        validarEmpresaId(request.empresaId());
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do projeto e obrigatorio.");
        }
        if (request.descricao() == null || request.descricao().isBlank()) {
            throw new IllegalArgumentException("Descricao do projeto e obrigatoria.");
        }
        if (request.prazo() == null) {
            throw new IllegalArgumentException("Prazo do projeto e obrigatorio.");
        }
        if (request.liderId() == null) {
            throw new IllegalArgumentException("Lider do projeto e obrigatorio.");
        }
    }

    private void validarEmpresaId(Long empresaId) {
        if (empresaId == null || empresaId <= 0) {
            throw new IllegalArgumentException("Empresa do usuario logado nao encontrada.");
        }
    }

    private void validarProjetoDaEmpresa(ProjetoTelaModel projeto, Long empresaId) {
        Long empresaDoProjeto = obterEmpresaIdDoProjeto(projeto);

        if (!empresaId.equals(empresaDoProjeto)) {
            throw new IllegalArgumentException("Projeto nao pertence a empresa informada.");
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
        int totalTarefas = projeto.getTarefas().size();
        int tarefasConcluidas = (int) projeto.getTarefas().stream()
            .filter(tarefa -> tarefa.getStatus() == TarefaStatusProjetoTela.CONCLUIDO)
            .count();

        int totalMarcos = projeto.getMarcos().size();
        int marcosConcluidos = (int) projeto.getMarcos().stream()
            .filter(marco -> marco.getStatus() == MarcoStatusProjetoTela.CONCLUIDO)
            .count();

        int totalItens = totalTarefas + totalMarcos;
        int totalConcluidos = tarefasConcluidas + marcosConcluidos;

        if (totalItens > 0) {
            projeto.setProgresso((int) Math.round((totalConcluidos * 100.0) / totalItens));
        } else {
            projeto.setProgresso(0);
        }
    }
}
