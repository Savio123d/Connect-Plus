package conne.connect.connect.Services;

import conne.connect.connect.Models.TarefaModel;
import conne.connect.connect.Repositories.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<TarefaModel> findAll() {
        return tarefaRepository.findAll();
    }

    public TarefaModel criarTarefa(TarefaModel tarefaModel) {
        return tarefaRepository.save(tarefaModel);
    }

    public Optional<TarefaModel> buscarPorId(Long idTarefa) {
        return tarefaRepository.findById(idTarefa);
    }

    public TarefaModel atualizarTarefa(Long idTarefa, TarefaModel tarefaModel) {
        TarefaModel tarefa = tarefaRepository.findById(idTarefa).get();
        tarefa.setIdEmpresa(tarefaModel.getIdEmpresa());
        tarefa.setIdProjeto(tarefaModel.getIdProjeto());
        tarefa.setIdResponsavelUsuarioEmpresa(tarefaModel.getIdResponsavelUsuarioEmpresa());
        tarefa.setTitulo(tarefaModel.getTitulo());
        tarefa.setDescricao(tarefaModel.getDescricao());
        tarefa.setDificuldade(tarefaModel.getDificuldade());
        tarefa.setStatus(tarefaModel.getStatus());
        tarefa.setPrazo(tarefaModel.getPrazo());
        tarefa.setConcluidaEm(tarefaModel.getConcluidaEm());
        return tarefaRepository.save(tarefa);
    }

    public void excluirTarefa(Long idTarefa) {
        tarefaRepository.deleteById(idTarefa);
    }
}
