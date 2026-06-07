package conne.connect.connect.Repositories;

import conne.connect.connect.Models.ProjetoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM projeto
        WHERE empresa_id = :empresaId
        AND status IN ('planejamento', 'em_andamento')
        AND excluido IS NULL
    """, nativeQuery = true)
    Long countProjetosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}
