package conne.connect.connect.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();


        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(500)
                .recordStats());


        manager.registerCustomCache("tarefaPorId",
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .recordStats()
                        .build());

        manager.registerCustomCache("tarefasPorEmpresa",
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(200)
                        .recordStats()
                        .build());


        manager.registerCustomCache("dashboardResumo",
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(200)
                        .recordStats()
                        .build());

        manager.registerCustomCache("usuariosPorEmpresa",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(200)
                        .recordStats()
                        .build());

        manager.registerCustomCache("projetosPorEmpresa",
                Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .maximumSize(200)
                        .recordStats()
                        .build());


        manager.registerCustomCache("notificacoesNaoLidas",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .recordStats()
                        .build());
        manager.registerCustomCache("notificacoesUltimas",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .recordStats()
                        .build());

        // Validação do token por request: TTL curto para revogar acesso
        // (usuário desativado / papel alterado) em no máximo 60s.
        manager.registerCustomCache("vinculoAutenticacao",
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(5000)
                        .recordStats()
                        .build());

        return manager;
    }
}
