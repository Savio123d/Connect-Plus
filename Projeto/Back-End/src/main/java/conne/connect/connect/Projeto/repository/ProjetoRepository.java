package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.model.ProjetoModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Long> {

    List<ProjetoModel> findByExcluidoIsNull();

    List<ProjetoModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    Optional<ProjetoModel> findByIdProjetoAndIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idProjeto, Long idEmpresa);

    @Query(value = """
            select count(*)
            from projeto
            where empresa_id = :empresaId
              and status in ('planejamento', 'em_andamento')
              and excluido is null
            """, nativeQuery = true)
    Long countProjetosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}
