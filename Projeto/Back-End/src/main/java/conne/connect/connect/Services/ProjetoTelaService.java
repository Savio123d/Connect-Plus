package conne.connect.connect.Services;

import conne.connect.connect.Dto.ProjetoRequestDTO;
import conne.connect.connect.Dto.ProjetoResponseDTO;
import conne.connect.connect.Enums.MarcoStatusProjetoTela;
import conne.connect.connect.Enums.PrioridadeProjetoTela;
import conne.connect.connect.Enums.ProjetoStatusTela;
import conne.connect.connect.Enums.TarefaStatusProjetoTela;
import conne.connect.connect.Models.MarcoProjetoTelaModel;
import conne.connect.connect.Models.PessoaProjetoTelaModel;
import conne.connect.connect.Models.ProjetoTelaModel;
import conne.connect.connect.Models.TarefaProjetoTelaModel;
import conne.connect.connect.Repositories.PessoaProjetoTelaRepository;
import conne.connect.connect.Repositories.ProjetoTelaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjetoTelaService {

    private final ProjetoTelaRepository projetoRepository;
    private final PessoaProjetoTelaRepository pessoaRepository;
    private final ProjetoTelaMapper mapper;

    public ProjetoTelaService(
        ProjetoTelaRepository projetoRepository,
        PessoaProjetoTelaRepository pessoaRepository,
        ProjetoTelaMapper mapper
    ) {
        this.projetoRepository = projetoRepository;
        this.pessoaRepository = pessoaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listar() {
        return mapper.toResponseList(projetoRepository.findAllByOrderByIdProjetoDesc());
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarProjeto(id));
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO.PessoaDTO> listarUsuariosDisponiveis() {
        return pessoaRepository.findByAtivoTrueOrderByNomeAsc()
            .stream()
            .map(mapper::toPessoaResponse)
            .toList();
    }

    @Transactional
    public ProjetoResponseDTO criar(ProjetoRequestDTO request) {
        validarCriacao(request);

        PessoaProjetoTelaModel lider = buscarPessoa(request.liderId());

        Set<PessoaProjetoTelaModel> membros = new LinkedHashSet<>();
        membros.add(lider);

        if (request.membrosIds() != null && !request.membrosIds().isEmpty()) {
            pessoaRepository.findAllById(request.membrosIds()).forEach(membros::add);
        }

        ProjetoTelaModel projeto = new ProjetoTelaModel();
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
            throw new IllegalArgumentException("Status do projeto é obrigatório.");
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
            throw new IllegalArgumentException("Projeto não encontrado.");
        }

        projetoRepository.deleteById(id);
    }

    @Transactional
    public ProjetoResponseDTO adicionarMembro(Long projetoId, Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);
        PessoaProjetoTelaModel pessoa = buscarPessoa(usuarioId);

        boolean jaEstaNoProjeto = projeto.getMembros().stream()
            .anyMatch(membro -> membro.getIdPessoa().equals(usuarioId));

        if (!jaEstaNoProjeto) {
            projeto.getMembros().add(pessoa);
        }

        return mapper.toResponse(projetoRepository.save(projeto));
    }

    @Transactional
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
    public ProjetoResponseDTO adicionarTarefa(Long projetoId, ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da tarefa não enviados.");
        }

        ProjetoTelaModel projeto = buscarProjeto(projetoId);

        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new IllegalArgumentException("Título da tarefa é obrigatório.");
        }
        if (request.responsavel() == null || request.responsavel().isBlank()) {
            throw new IllegalArgumentException("Responsável da tarefa é obrigatório.");
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

    private ProjetoTelaModel buscarProjeto(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado."));
    }

    private PessoaProjetoTelaModel buscarPessoa(Long id) {
        return pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    private void validarCriacao(ProjetoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do projeto não enviados.");
        }
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
