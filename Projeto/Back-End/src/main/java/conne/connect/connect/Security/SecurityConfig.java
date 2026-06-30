package conne.connect.connect.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    // Origens liberadas para CORS, configuráveis via app.cors.allowed-origins
    // (ou variável de ambiente APP_CORS_ALLOWED_ORIGINS). Aceita lista separada
    // por vírgula e padrões com curinga, ex.: https://*.vercel.app
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/**").permitAll()
                        .requestMatchers("/api/usuario-empresa/**").permitAll()
                        .requestMatchers("/api/usuarioempresas/**").permitAll()
                        .requestMatchers("/api/empresas/**").permitAll()
                        .requestMatchers("/api/projetos/**").permitAll()
                        .requestMatchers("/api/tarefas/**").permitAll()
                        .requestMatchers("/api/imagens/**").permitAll()
                        .requestMatchers("/api/lojas/**").permitAll()
                        .requestMatchers("/api/recompensas/**").permitAll()
                        .requestMatchers("/api/saldos-xp/**").permitAll()
                        .requestMatchers("/api/saldo-xp/**").permitAll()
                        .requestMatchers("/api/perfil/**").permitAll()
                        .requestMatchers("/api/pedidos-resgate/**").permitAll()
                        .requestMatchers("/api/notificacoes/**").permitAll()

                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        .anyRequest().permitAll()
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                .build();
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
                .collect(Collectors.toList());
    }
}
