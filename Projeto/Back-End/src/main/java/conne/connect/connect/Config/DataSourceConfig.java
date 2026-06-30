package conne.connect.connect.Config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String writeUrl;

    @Value("${spring.datasource.username}")
    private String writeUsername;

    @Value("${spring.datasource.password}")
    private String writePassword;

    @Value("${spring.datasource.read-url:}")
    private String readUrl;

    @Value("${spring.datasource.read-username:}")
    private String readUsername;

    @Value("${spring.datasource.read-password:}")
    private String readPassword;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    @Bean
    public DataSource writeDataSource() {
        return criarDataSource(writeUrl, writeUsername, writePassword, "connect-write-pool");
    }

    @Bean
    public DataSource readDataSource() {
        return criarDataSource(
                valorOuPadrao(readUrl, writeUrl),
                valorOuPadrao(readUsername, writeUsername),
                valorOuPadrao(readPassword, writePassword),
                "connect-read-pool"
        );
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource
    ) {
        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(ReadWriteRoutingDataSource.ESCRITA, writeDataSource);
        dataSources.put(ReadWriteRoutingDataSource.LEITURA, readDataSource);

        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    private HikariDataSource criarDataSource(
            String url,
            String username,
            String password,
            String poolName
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setPoolName(poolName);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setMaxLifetime(maxLifetime);
        return dataSource;
    }

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
