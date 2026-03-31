package conne.connect.connect.Services;

import conne.connect.connect.Models.ComentarioTarefaModel;
import conne.connect.connect.Repositories.ComentarioTarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioTarefaService {

    @Autowired
    private ComentarioTarefaRepository comentarioTarefaRepository;

    public List<ComentarioTarefaModel> findAll() {
        return comentarioTarefaRepository.findAll();
    }

    public ComentarioTarefaModel criarComentarioTarefa(ComentarioTarefaModel comentarioTarefaModel) {
        return comentarioTarefaRepository.save(comentarioTarefaModel);
    }

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
