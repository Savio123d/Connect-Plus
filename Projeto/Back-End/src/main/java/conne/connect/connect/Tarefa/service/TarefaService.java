package conne.connect.connect.Tarefa.service;

import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import conne.connect.connect.Recompensa.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Projeto.enums.MarcoStatusProjetoTela;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Tarefa.dto.TarefaRequestDTO;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProjetoTelaRepository projetoRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Transactional(readOnly = true)
    public List<TarefaModel> findAll() {
        return tarefaRepository.findAll();
    }

    @Cacheable(value = "tarefasPorEmpresa", key = "#empresaId")
    @Transactional(readOnly = true)
    public List<TarefaModel> listarPorEmpresa(Long empresaId) {
        return tarefaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(empresaId);
    }

    @Cacheable(value = "tarefaPorId", key = "#idTarefa")
    @Transactional(readOnly = true)
    public TarefaModel buscarPorId(Long idTarefa) {
        return tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa nao encontrada"));
    }

    // Nova tarefa muda a listagem do quadro e o resumo/listagem dos projetos.
    @Caching(evict = {
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel criarTarefa(TarefaRequestDTO dto) {
        TarefaModel tarefa = new TarefaModel();
        preencherTarefaComDto(tarefa, dto);
        tarefa.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusTarefa.pendente);
        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "#idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel atualizarTarefa(Long idTarefa, TarefaRequestDTO dto) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        preencherTarefaComDto(tarefa, dto);
        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "#idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel atualizarStatus(Long idTarefa, StatusTarefa novoStatus) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        tarefa.setStatus(novoStatus);

        if (novoStatus == StatusTarefa.concluida) {
            tarefa.setConcluidaEm(LocalDateTime.now());
        } else {
            tarefa.setConcluidaEm(null);
        }

        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "#idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public void excluirTarefa(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        ProjetoTelaModel projeto = tarefa.getIdProjeto();
        tarefaRepository.delete(tarefa);
        recalcularProgressoProjeto(projeto);
    }

    private void preencherTarefaComDto(TarefaModel tarefa, TarefaRequestDTO dto) {
        EmpresaModel empresa = buscarEmpresa(dto.getIdEmpresa());
        ProjetoTelaModel projeto = buscarProjeto(dto.getIdProjeto());
        validarProjetoDaEmpresa(projeto, empresa);
        UsuarioEmpresaModel responsavel = buscarResponsavel(dto.getIdResponsavelUsuarioEmpresa(), empresa.getIdEmpresa());

        tarefa.setIdEmpresa(empresa);
        tarefa.setIdProjeto(projeto);
        tarefa.setIdResponsavelUsuarioEmpresa(responsavel);
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setPrioridade(dto.getPrioridade());
        tarefa.setDificuldade(dto.getDificuldade());
        tarefa.setHorasEstimadas(dto.getHorasEstimadas() != null ? dto.getHorasEstimadas() : 0);
        tarefa.setPrazo(dto.getPrazo());
    }

    private EmpresaModel buscarEmpresa(Long idEmpresa) {
        if (idEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da empresa e obrigatorio");
        }

        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa nao encontrada"));
    }

    private ProjetoTelaModel buscarProjeto(Long idProjeto) {
        if (idProjeto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do projeto e obrigatorio");
        }

        return projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto nao encontrado"));
    }

    private UsuarioEmpresaModel buscarResponsavel(Long idResponsavelUsuarioEmpresa, Long idEmpresa) {
        if (idResponsavelUsuarioEmpresa == null) {
            return null;
        }

        UsuarioEmpresaModel responsavel = usuarioEmpresaRepository.findById(idResponsavelUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsavel nao encontrado"));

        if (responsavel.getIdEmpresa() == null
                || responsavel.getIdEmpresa().getIdEmpresa() == null
                || !responsavel.getIdEmpresa().getIdEmpresa().equals(idEmpresa)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsavel nao pertence a empresa da tarefa");
        }

        if (!Boolean.TRUE.equals(responsavel.getAtivo()) || responsavel.getExcluido() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsavel inativo ou excluido");
        }

        return responsavel;
    }

    private void validarProjetoDaEmpresa(ProjetoTelaModel projeto, EmpresaModel empresa) {
        if (projeto.getEmpresa() == null
                || projeto.getEmpresa().getIdEmpresa() == null
                || !projeto.getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto nao pertence a empresa da tarefa");
        }
    }

    private void recalcularProgressoProjeto(ProjetoTelaModel projeto) {
        if (projeto == null || projeto.getIdProjeto() == null) {
            return;
        }

        List<TarefaModel> tarefas = tarefaRepository
                .findByIdProjeto_IdProjetoAndExcluidoIsNullOrderByIdTarefaAsc(projeto.getIdProjeto());

        int totalTarefas = tarefas.size();
        int tarefasConcluidas = (int) tarefas.stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.concluida)
                .count();

        int totalMarcos = projeto.getMarcos().size();
        int marcosConcluidos = (int) projeto.getMarcos().stream()
                .filter(marco -> marco.getStatus() == MarcoStatusProjetoTela.CONCLUIDO)
                .count();

        int totalItens = totalTarefas + totalMarcos;
        int totalConcluidos = tarefasConcluidas + marcosConcluidos;

        projeto.setProgresso(totalItens > 0
                ? (int) Math.round((totalConcluidos * 100.0) / totalItens)
                : 0);

        projetoRepository.save(projeto);
    }
}
