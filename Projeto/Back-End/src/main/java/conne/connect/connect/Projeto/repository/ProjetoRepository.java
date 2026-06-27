package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.model.ProjetoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetoRepository extends JpaRepository<ProjetoModel, Long> {

    @Query(value = """
            select count(*)
            from projeto
            where empresa_id = :empresaId
              and status in ('planejamento', 'em_andamento')
              and excluido is null
            """, nativeQuery = true)
    Long countProjetosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}
