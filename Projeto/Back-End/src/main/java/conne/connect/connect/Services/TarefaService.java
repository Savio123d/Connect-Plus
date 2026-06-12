package conne.connect.connect.Services;

import conne.connect.connect.Dto.TarefaRequestDTO;
import conne.connect.connect.Enums.StatusTarefa;
import conne.connect.connect.Models.EmpresaModel;
import conne.connect.connect.Models.ProjetoModel;
import conne.connect.connect.Models.TarefaModel;
import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Repositories.EmpresaRepository;
import conne.connect.connect.Repositories.ProjetoRepository;
import conne.connect.connect.Repositories.TarefaRepository;
import conne.connect.connect.Repositories.UsuarioEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    public List<TarefaModel> findAll() {
        return tarefaRepository.findAll();
    }

    public TarefaModel buscarPorId(Long idTarefa) {
        return tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    public TarefaModel criarTarefa(TarefaRequestDTO dto) {
        TarefaModel tarefa = new TarefaModel();

        preencherTarefaComDto(tarefa, dto);

        tarefa.setStatus(StatusTarefa.pendente);

        return tarefaRepository.save(tarefa);
    }

    public TarefaModel atualizarTarefa(Long idTarefa, TarefaRequestDTO dto) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        preencherTarefaComDto(tarefa, dto);

        return tarefaRepository.save(tarefa);
    }

    public TarefaModel atualizarStatus(Long idTarefa, StatusTarefa novoStatus) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        tarefa.setStatus(novoStatus);

        if (novoStatus == StatusTarefa.concluida) {
            tarefa.setConcluidaEm(LocalDateTime.now());
        } else {
            tarefa.setConcluidaEm(null);
        }

        return tarefaRepository.save(tarefa);
    }

    public void excluirTarefa(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        tarefaRepository.delete(tarefa);
    }

    private void preencherTarefaComDto(TarefaModel tarefa, TarefaRequestDTO dto) {
        EmpresaModel empresa = buscarEmpresa(dto.getIdEmpresa());
        ProjetoModel projeto = buscarProjeto(dto.getIdProjeto());
        UsuarioEmpresaModel responsavel = buscarResponsavel(dto.getIdResponsavelUsuarioEmpresa());

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da empresa é obrigatório");
        }

        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }

    private ProjetoModel buscarProjeto(Long idProjeto) {
        if (idProjeto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do projeto é obrigatório");
        }

        return projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    private UsuarioEmpresaModel buscarResponsavel(Long idResponsavelUsuarioEmpresa) {
        if (idResponsavelUsuarioEmpresa == null) {
            return null;
        }

        return usuarioEmpresaRepository.findById(idResponsavelUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsável não encontrado"));
    }
}