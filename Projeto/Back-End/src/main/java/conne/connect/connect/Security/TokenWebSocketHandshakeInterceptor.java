package conne.connect.connect.Security;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TokenWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATRIBUTO_EMPRESA = "empresaIdAutenticada";
    public static final String ATRIBUTO_USUARIO_EMPRESA = "usuarioEmpresaIdAutenticado";

    private final JwtDecoder jwtDecoder;
    private final TokenAuthenticationConverter tokenAuthenticationConverter;

    public TokenWebSocketHandshakeInterceptor(
            JwtDecoder jwtDecoder,
            TokenAuthenticationConverter tokenAuthenticationConverter
    ) {
        this.jwtDecoder = jwtDecoder;
        this.tokenAuthenticationConverter = tokenAuthenticationConverter;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            tokenAuthenticationConverter.convert(jwt);
            attributes.put(
                    ATRIBUTO_EMPRESA,
                    numero(jwt.getClaim(TokenService.CLAIM_EMPRESA))
            );
            attributes.put(
                    ATRIBUTO_USUARIO_EMPRESA,
                    numero(jwt.getClaim(TokenService.CLAIM_USUARIO_EMPRESA))
            );
            return true;
        } catch (JwtException | AuthenticationException | IllegalArgumentException erro) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }

    private Long numero(Object valor) {
        if (valor instanceof Number numero) {
            return numero.longValue();
        }

        return Long.valueOf(String.valueOf(valor));
    }
}
