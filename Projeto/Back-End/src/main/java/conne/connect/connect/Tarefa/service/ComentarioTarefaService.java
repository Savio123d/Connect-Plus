package conne.connect.connect.Tarefa.service;

import conne.connect.connect.Tarefa.model.ComentarioTarefaModel;
import conne.connect.connect.Tarefa.repository.ComentarioTarefaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComentarioTarefaService {

    @Autowired
    private ComentarioTarefaRepository comentarioTarefaRepository;

    @Transactional(readOnly = true)
    public List<ComentarioTarefaModel> findAll() {
        return comentarioTarefaRepository.findAll();
    }

    public ComentarioTarefaModel criarComentarioTarefa(ComentarioTarefaModel comentarioTarefaModel) {
        return comentarioTarefaRepository.save(comentarioTarefaModel);
    }

    @Transactional(readOnly = true)
    public Optional<ComentarioTarefaModel> buscarPorId(Long idComentarioTarefa) {
        return comentarioTarefaRepository.findById(idComentarioTarefa);
    }

    public ComentarioTarefaModel atualizarComentarioTarefa(Long idComentarioTarefa, ComentarioTarefaModel comentarioTarefaModel) {
        ComentarioTarefaModel comentarioTarefa = comentarioTarefaRepository.findById(idComentarioTarefa).get();
        comentarioTarefa.setIdEmpresa(comentarioTarefaModel.getIdEmpresa());
        comentarioTarefa.setIdTarefa(comentarioTarefaModel.getIdTarefa());
        comentarioTarefa.setIdAutorUsuarioEmpresa(comentarioTarefaModel.getIdAutorUsuarioEmpresa());
        comentarioTarefa.setConteudo(comentarioTarefaModel.getConteudo());
        return comentarioTarefaRepository.save(comentarioTarefa);
    }

    public void excluirComentarioTarefa(Long idComentarioTarefa) {
        comentarioTarefaRepository.deleteById(idComentarioTarefa);
    }
}
