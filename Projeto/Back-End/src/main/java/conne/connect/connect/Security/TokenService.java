package conne.connect.connect.Security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

/**
 * Emite o token de sessão (JWT assinado com HMAC-SHA256).
 * O token carrega o papel e a empresa do usuário para o controle de acesso.
 */
@Service
public class TokenService {

    public static final String CLAIM_EMPRESA = "empresaId";
    public static final String CLAIM_USUARIO_EMPRESA = "usuarioEmpresaId";
    public static final String CLAIM_PAPEL = "papel";

    private final JwtEncoder jwtEncoder;
    private final long validadeHoras;

    public TokenService(
            @Value("${app.security.token.secret}") String segredo,
            @Value("${app.security.token.validade-horas:12}") long validadeHoras
    ) {
        SecretKey chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(chave));
        this.validadeHoras = validadeHoras;
    }

    public String gerarToken(Long idUsuario, Long idEmpresa, Long idUsuarioEmpresa, String papel) {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("connect-plus")
                .subject(String.valueOf(idUsuario))
                .claim(CLAIM_EMPRESA, idEmpresa)
                .claim(CLAIM_USUARIO_EMPRESA, idUsuarioEmpresa)
                .claim(CLAIM_PAPEL, papel)
                .issuedAt(agora)
                .expiresAt(agora.plus(validadeHoras, ChronoUnit.HOURS))
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
