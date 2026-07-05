package conne.connect.connect.Perfil.repository;

import conne.connect.connect.Perfil.dto.HistoricoDesempenhoDTO;
import conne.connect.connect.Perfil.dto.PerfilUsuarioDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PerfilRepository {

    private final JdbcTemplate jdbcTemplate;

    public PerfilRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PerfilUsuarioDTO buscarUsuarioPerfil(Long idUsuarioEmpresa) {
        String sql = """
                SELECT
                    u.id AS id_usuario,
                    ue.id AS id_usuario_empresa,
                    u.nome AS nome,
                    u.email AS email,
                    ue.papel AS cargo,
                    COALESCE(e.nome_fantasia, e.razao_social, 'Não informado') AS departamento
                FROM usuario_empresa ue
                INNER JOIN usuario u ON u.id = ue.usuario_id
                INNER JOIN empresa e ON e.id = ue.empresa_id
                WHERE ue.id = ?
                AND ue.ativo = true
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Long idUsuario = rs.getLong("id_usuario");
            Long idUsuarioEmpresaBanco = rs.getLong("id_usuario_empresa");
            String nome = rs.getString("nome");
            String email = rs.getString("email");
            String cargo = rs.getString("cargo");
            String departamento = rs.getString("departamento");

            Integer xpAtual = buscarXpAtual(idUsuarioEmpresaBanco);
            Integer xpProximoNivel = 5000;
            Integer nivel = calcularNivel(xpAtual);

            return new PerfilUsuarioDTO(
                    idUsuario,
                    idUsuarioEmpresaBanco,
                    nome,
                    email,
                    cargo,
                    departamento,
                    nivel,
                    xpAtual,
                    xpProximoNivel
            );
        }, idUsuarioEmpresa);
    }

    public Integer buscarXpAtual(Long idUsuarioEmpresa) {
        try {
            String sql = """
                    SELECT COALESCE(SUM(quantidade), 0)
                    FROM transacao_xp
                    WHERE usuario_empresa_id = ?
                    AND LOWER(CAST(tipo AS TEXT)) = 'ganho'
                    """;

            Integer xp = jdbcTemplate.queryForObject(sql, Integer.class, idUsuarioEmpresa);

            if (xp != null && xp > 0) {
                return xp;
            }
        } catch (Exception erro) {
            System.out.println("Não foi possível buscar XP em transacao_xp: " + erro.getMessage());
        }

        return calcularXpPorTarefasConcluidas(idUsuarioEmpresa);
    }

    public Integer calcularXpPorTarefasConcluidas(Long idUsuarioEmpresa) {
        try {
            String sql = """
                    SELECT COUNT(*) * 20
                    FROM tarefa
                    WHERE responsavel_usuario_empresa_id = ?
                    AND LOWER(CAST(status AS TEXT)) = 'concluida'
                    """;

            Integer xp = jdbcTemplate.queryForObject(sql, Integer.class, idUsuarioEmpresa);

            return xp != null ? xp : 0;
        } catch (Exception erro) {
            return 0;
        }
    }

    public Long contarTarefasConcluidas(Long idUsuarioEmpresa) {
        try {
            String sql = """
                    SELECT COUNT(*)
                    FROM tarefa
                    WHERE responsavel_usuario_empresa_id = ?
                    AND LOWER(CAST(status AS TEXT)) = 'concluida'
                    """;

            Long total = jdbcTemplate.queryForObject(sql, Long.class, idUsuarioEmpresa);

            return total != null ? total : 0L;
        } catch (Exception erro) {
            return 0L;
        }
    }

    public List<HistoricoDesempenhoDTO> buscarHistorico(Long idUsuarioEmpresa) {
        try {
            String sql = """
                    SELECT
                        INITCAP(TO_CHAR(concluida_em, 'TMMonth')) AS mes,
                        COUNT(*) AS tarefas_concluidas,
                        COUNT(*) * 20 AS xp_ganho
                    FROM tarefa
                    WHERE responsavel_usuario_empresa_id = ?
                    AND LOWER(CAST(status AS TEXT)) = 'concluida'
                    AND concluida_em IS NOT NULL
                    GROUP BY EXTRACT(MONTH FROM concluida_em), TO_CHAR(concluida_em, 'TMMonth')
                    ORDER BY EXTRACT(MONTH FROM concluida_em)
                    LIMIT 4
                    """;

            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    new HistoricoDesempenhoDTO(
                            rs.getString("mes"),
                            rs.getLong("tarefas_concluidas"),
                            rs.getLong("xp_ganho")
                    ), idUsuarioEmpresa
            );
        } catch (Exception erro) {
            return List.of();
        }
    }

    private Integer calcularNivel(Integer xpAtual) {
        if (xpAtual == null || xpAtual <= 0) {
            return 1;
        }

        return Math.max((xpAtual / 500) + 1, 1);
    }
}