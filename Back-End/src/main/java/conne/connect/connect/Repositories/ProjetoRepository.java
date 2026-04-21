package conne.connect.connect.Repositories;

import conne.connect.connect.Models.ProjetoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import conne.connect.connect.Enums.StatusProjeto;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Long> {

    long countByStatus(StatusProjeto status);
}
