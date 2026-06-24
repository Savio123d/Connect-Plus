package conne.connect.connect.Repositories;

import conne.connect.connect.Models.ProjetoTelaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetoTelaRepository extends JpaRepository<ProjetoTelaModel, Long> {
    List<ProjetoTelaModel> findAllByOrderByIdProjetoDesc();
}
