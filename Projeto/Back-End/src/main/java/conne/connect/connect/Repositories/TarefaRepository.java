package conne.connect.connect.Repositories;

import conne.connect.connect.Models.TarefaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<TarefaModel, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM tarefa
        WHERE empresa_id = :empresaId
        AND status = 'concluida'
    """, nativeQuery = true)
    Long countTarefasConcluidasPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
        SELECT COUNT(*)
        FROM tarefa
        WHERE empresa_id = :empresaId
        AND status = 'em_andamento'
    """, nativeQuery = true)
    Long countTarefasEmAndamentoPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
        SELECT COUNT(*)
        FROM tarefa
        WHERE empresa_id = :empresaId
        AND status = 'pendente'
    """, nativeQuery = true)
    Long countTarefasPendentesPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
        SELECT COUNT(*)
        FROM tarefa
        WHERE empresa_id = :empresaId
        AND prazo < NOW()
        AND status IN ('pendente', 'em_andamento', 'em_revisao')
    """, nativeQuery = true)
    Long countTarefasAtrasadasPorEmpresa(@Param("empresaId") Long empresaId);

    @Query(value = """
        SELECT 
            CAST(EXTRACT(MONTH FROM concluida_em) AS INTEGER) AS mes,
            COUNT(*) AS total
        FROM tarefa
        WHERE empresa_id = :empresaId
        AND status = 'concluida'
        AND concluida_em IS NOT NULL
        AND EXTRACT(YEAR FROM concluida_em) = :ano
        GROUP BY EXTRACT(MONTH FROM concluida_em)
        ORDER BY EXTRACT(MONTH FROM concluida_em)
    """, nativeQuery = true)
    List<DesempenhoEquipeProjection> countDesempenhoEquipePorMes(
            @Param("empresaId") Long empresaId,
            @Param("ano") Integer ano
    );
}