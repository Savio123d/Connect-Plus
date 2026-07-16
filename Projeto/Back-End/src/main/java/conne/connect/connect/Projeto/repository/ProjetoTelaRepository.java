package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoTelaRepository extends JpaRepository<ProjetoTelaModel, Long> {
    List<ProjetoTelaModel> findAllByOrderByIdProjetoDesc();

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaOrderByIdProjetoDesc(Long empresaId);

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaAndExcluidoIsNullOrderByIdProjetoDesc(Long empresaId);

    Optional<ProjetoTelaModel> findByIdProjetoAndEmpresa_IdEmpresaAndExcluidoIsNull(Long idProjeto, Long empresaId);

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaAndStatusAndConcluidoEmIsNotNullOrderByConcluidoEmDesc(
            Long empresaId,
            ProjetoStatusTela status
    );
}
