package conne.connect.connect.Tarefa.repository;

import conne.connect.connect.Dashboard.projection.DesempenhoEquipeProjection;
import conne.connect.connect.Tarefa.model.TarefaModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TarefaRepository extends JpaRepository<TarefaModel, Long> {

    List<TarefaModel> findByIdEmpresa_IdEmpresaAndExcluidoIsNull(Long idEmpresa);

    @Query(value = """
            select cast(extract(month from concluida_em) as int) as mes,
                   count(*) as total
            from tarefa
            where empresa_id = :empresaId
              and extract(year from concluida_em) = :ano
              and status = 'concluida'
              and excluido is null
            group by cast(extract(month from concluida_em) as int)
            order by mes
            """, nativeQuery = true)
    List<DesempenhoEquipeProjection> countDesempenhoEquipePorMes(
            @Param("empresaId") Long empresaId,
            @Param("ano") Integer ano
    );

    @Query(value = """
            select count(*)
            from tarefa
            where empresa_id = :empresaId
              and status = 'concluida'
              and excluido is null
            """, nativeQuery = true)
    Long countTarefasConcluidasPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
            select count(*)
            from tarefa
            where empresa_id = :empresaId
              and status = 'em_andamento'
              and excluido is null
            """, nativeQuery = true)
    Long countTarefasEmAndamentoPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
            select count(*)
            from tarefa
            where empresa_id = :empresaId
              and status = 'pendente'
              and excluido is null
            """, nativeQuery = true)
    Long countTarefasPendentesPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
            select count(*)
            from tarefa
            where empresa_id = :empresaId
              and prazo < current_timestamp
              and status not in ('concluida', 'cancelada', 'arquivada')
              and excluido is null
            """, nativeQuery = true)
    Long countTarefasAtrasadasPorEmpresa(@Param("empresaId") Long empresaId);
}
