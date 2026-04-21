package conne.connect.connect.Repositories;

import conne.connect.connect.Models.TarefaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import conne.connect.connect.Enums.StatusTarefa;

public interface TarefaRepository extends JpaRepository<TarefaModel, Long> {

    long countByStatus(StatusTarefa status);
}
