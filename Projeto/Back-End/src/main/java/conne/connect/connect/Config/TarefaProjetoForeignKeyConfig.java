package conne.connect.connect.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class TarefaProjetoForeignKeyConfig {

    private static final Logger logger = LoggerFactory.getLogger(TarefaProjetoForeignKeyConfig.class);

    private final JdbcTemplate jdbcTemplate;

    public TarefaProjetoForeignKeyConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void corrigirForeignKeyTarefaProjeto() {
        try {
            if (!tabelaExiste("tarefa") || !tabelaExiste("projeto_tela")) {
                return;
            }

            jdbcTemplate.execute("ALTER TABLE tarefa DROP CONSTRAINT IF EXISTS tarefa_projeto_id_fkey");
            jdbcTemplate.execute("ALTER TABLE tarefa DROP CONSTRAINT IF EXISTS fk_tarefa_projeto_tela");
            jdbcTemplate.execute("""
                    ALTER TABLE tarefa
                    ADD CONSTRAINT fk_tarefa_projeto_tela
                    FOREIGN KEY (projeto_id)
                    REFERENCES projeto_tela (id_projeto)
                    NOT VALID
                    """);

            logger.info("Foreign key tarefa.projeto_id ajustada para projeto_tela.id_projeto.");
        } catch (DataAccessException erro) {
            logger.warn("Nao foi possivel ajustar a foreign key de tarefa/projeto automaticamente: {}",
                    erro.getMessage());
        }
    }

    private boolean tabelaExiste(String tabela) {
        Boolean existe = jdbcTemplate.queryForObject(
                "select to_regclass(?) is not null",
                Boolean.class,
                "public." + tabela
        );

        return Boolean.TRUE.equals(existe);
    }
}
