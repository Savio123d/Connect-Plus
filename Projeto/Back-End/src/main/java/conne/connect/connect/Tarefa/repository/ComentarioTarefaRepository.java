package conne.connect.connect.Tarefa.repository;

import conne.connect.connect.Tarefa.model.ComentarioTarefaModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioTarefaRepository extends JpaRepository<ComentarioTarefaModel, Long> {

    List<ComentarioTarefaModel> findByExcluidoIsNull();

    List<ComentarioTarefaModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<ComentarioTarefaModel> findByIdComentarioTarefaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idComentarioTarefa, Long idEmpresa);
}
