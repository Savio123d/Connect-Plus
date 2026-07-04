package conne.connect.connect.Feedback.repository;

import conne.connect.connect.Feedback.model.Feedback360RodadaModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Feedback360RodadaRepository extends JpaRepository<Feedback360RodadaModel, Long> {

    Optional<Feedback360RodadaModel> findByEmpresa_IdEmpresaAndProjeto_IdProjeto(
            Long empresaId,
            Long projetoId
    );
}

