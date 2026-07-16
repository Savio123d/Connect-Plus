package conne.connect.connect.Security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Origens liberadas para CORS, configuráveis via app.cors.allowed-origins
    // (ou variável de ambiente APP_CORS_ALLOWED_ORIGINS). Aceita lista separada
    // por vírgula e padrões com curinga, ex.: https://*.vercel.app
    private final String[] allowedOrigins;
    private final String segredoToken;
    private final TokenAuthenticationConverter tokenAuthenticationConverter;

    public SecurityConfig(
            @Value("${app.cors.allowed-origins}") String[] allowedOrigins,
            @Value("${app.security.token.secret}") String segredoToken,
            TokenAuthenticationConverter tokenAuthenticationConverter
    ) {
        this.allowedOrigins = allowedOrigins;
        this.segredoToken = segredoToken;
        this.tokenAuthenticationConverter = tokenAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey chave = new SecretKeySpec(segredoToken.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer("connect-plus"));
        return decoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Rotas públicas: login, cadastro de empresa (signup) e webhook externo.
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/empresas").permitAll()
                        .requestMatchers("/api/mercado-pago/webhook").permitAll()

                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Handshake dos WebSockets (chat, notificações e sinalização de chamadas).
                        .requestMatchers("/ws/**", "/sinalizacao").permitAll()

                        // Troca da própria senha: regra fina no @PreAuthorize do controller.
                        .requestMatchers(HttpMethod.PATCH, "/api/usuarios/*/senha").authenticated()

                        // Consultas pessoais devem preceder as regras administrativas mais amplas.
                        .requestMatchers(HttpMethod.GET, "/api/saldos-xp/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/perfil/**").authenticated()

                        // Recursos internos de leitura não são expostos ao papel cliente.
                        .requestMatchers(HttpMethod.GET,
                                "/api/equipes/**", "/api/equipes-membro/**",
                                "/api/projetos-equipe/**", "/api/setores/**",
                                "/api/lojas/**", "/api/recompensas/**",
                                "/api/feedbacks/**").hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.GET,
                                "/api/saldos-xp", "/api/saldos-xp/*",
                                "/api/transacoes-xp/**", "/api/pedidos-resgate/**",
                                "/api/pedidos-resgate-itens/**", "/api/resgates-recompensa/**")
                                .hasRole("GESTOR")

                        // Chat, anexos, chamadas e escrita de feedback são operacionais.
                        .requestMatchers("/api/conversas/**", "/api/mensagens/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.POST, "/api/imagens/chat", "/api/transmissoes/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/feedbacks/360/projeto/*/obrigatoriedade")
                                .hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/feedbacks/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/feedbacks/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/feedbacks/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/feedbacks/**")
                                .hasAnyRole("GESTOR", "COLABORADOR")

                        // Gestão de usuários, vínculos e empresa: exclusivo do gestor.
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("GESTOR")
                        .requestMatchers("/api/usuarios-empresa/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/empresas/*/usuarios").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/empresas/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/empresas/**").hasRole("GESTOR")

                        // Estrutura organizacional (equipes, setores, vínculos de projeto): gestor.
                        .requestMatchers(HttpMethod.POST,
                                "/api/equipes/**", "/api/equipes-membro/**",
                                "/api/projetos-equipe/**", "/api/setores/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/equipes/**", "/api/equipes-membro/**",
                                "/api/projetos-equipe/**", "/api/setores/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/equipes/**", "/api/equipes-membro/**",
                                "/api/projetos-equipe/**", "/api/setores/**").hasRole("GESTOR")

                        // Loja: resgatar é do dia a dia (gestor/colaborador); administrar itens é do gestor.
                        .requestMatchers(HttpMethod.POST, "/api/lojas/*/resgatar").hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.POST, "/api/lojas/**", "/api/recompensas/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/lojas/**", "/api/recompensas/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/lojas/**", "/api/recompensas/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/lojas/**", "/api/recompensas/**").hasRole("GESTOR")

                        // CRUD administrativo de XP e resgates: gestor.
                        .requestMatchers(HttpMethod.POST,
                                "/api/saldos-xp/**", "/api/transacoes-xp/**",
                                "/api/pedidos-resgate/**", "/api/pedidos-resgate-itens/**",
                                "/api/resgates-recompensa/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/saldos-xp/**", "/api/transacoes-xp/**",
                                "/api/pedidos-resgate/**", "/api/pedidos-resgate-itens/**",
                                "/api/resgates-recompensa/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/saldos-xp/**", "/api/transacoes-xp/**",
                                "/api/pedidos-resgate/**", "/api/pedidos-resgate-itens/**",
                                "/api/resgates-recompensa/**").hasRole("GESTOR")

                        // Dia a dia (tarefas, comentários, projetos): gestor e colaborador
                        // escrevem; cliente fica só com leitura (GET cai no authenticated).
                        .requestMatchers(HttpMethod.POST,
                                "/api/tarefas/**", "/api/comentarios-tarefa/**",
                                "/api/projetos/**").hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/tarefas/**", "/api/comentarios-tarefa/**",
                                "/api/projetos/**").hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/tarefas/**", "/api/comentarios-tarefa/**",
                                "/api/projetos/**").hasAnyRole("GESTOR", "COLABORADOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/tarefas/**", "/api/comentarios-tarefa/**",
                                "/api/projetos/**").hasAnyRole("GESTOR", "COLABORADOR")

                        .requestMatchers(HttpMethod.GET, "/api/notificacoes").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/notificacoes/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/notificacoes/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/notificacoes/**").hasRole("GESTOR")

                        // Demais rotas de leitura exigem autenticação; o escopo de empresa
                        // continua sendo validado pelos serviços de domínio.
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(tokenAuthenticationConverter))
                        .authenticationEntryPoint(naoAutenticadoEntryPoint())
                        .accessDeniedHandler(acessoNegadoHandler())
                )

                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint(naoAutenticadoEntryPoint())
                        .accessDeniedHandler(acessoNegadoHandler())
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                .build();
    }


    private AuthenticationEntryPoint naoAutenticadoEntryPoint() {
        return (request, response, excecao) -> escreverErro(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Autenticação necessária. Faça login para continuar."
        );
    }

    private AccessDeniedHandler acessoNegadoHandler() {
        return (request, response, excecao) -> escreverErro(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Você não tem permissão para executar esta ação."
        );
    }

    private void escreverErro(HttpServletResponse response, int status, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origensPermitidas = limparOrigensPermitidas();

        config.setAllowedOriginPatterns(origensPermitidas);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    private List<String> limparOrigensPermitidas() {
        return Arrays.stream(allowedOrigins)
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toList();
    }
}
