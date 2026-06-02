package conne.connect.connect.Services;

import conne.connect.connect.Dto.TarefaRequestDTO;
import conne.connect.connect.Enums.StatusTarefa;
import conne.connect.connect.Models.TarefaModel;
import conne.connect.connect.Repositories.TarefaRepository;
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

    public List<TarefaModel> findAll() {
        return tarefaRepository.findAll();
    }

    public TarefaModel buscarPorId(Long idTarefa) {
        return tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    public TarefaModel criarTarefa(TarefaRequestDTO dto) {
        TarefaModel tarefa = new TarefaModel();

        tarefa.setIdEmpresa(dto.getIdEmpresa());
        tarefa.setIdProjeto(dto.getIdProjeto());
        tarefa.setIdResponsavelUsuarioEmpresa(dto.getIdResponsavelUsuarioEmpresa());
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setPrioridade(dto.getPrioridade());
        tarefa.setDificuldade(dto.getDificuldade());
        tarefa.setHorasEstimadas(dto.getHorasEstimadas());
        tarefa.setPrazo(dto.getPrazo());

        tarefa.setStatus(StatusTarefa.pendente);

        return tarefaRepository.save(tarefa);
    }

    public TarefaModel atualizarTarefa(Long idTarefa, TarefaRequestDTO dto) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        tarefa.setIdEmpresa(dto.getIdEmpresa());
        tarefa.setIdProjeto(dto.getIdProjeto());
        tarefa.setIdResponsavelUsuarioEmpresa(dto.getIdResponsavelUsuarioEmpresa());
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setPrioridade(dto.getPrioridade());
        tarefa.setDificuldade(dto.getDificuldade());
        tarefa.setHorasEstimadas(dto.getHorasEstimadas());
        tarefa.setPrazo(dto.getPrazo());

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
}