package conne.connect.connect.NotificacoesSistem.config;

import conne.connect.connect.NotificacoesSistem.websockt.NotificacaoWebSocketHandler;
import conne.connect.connect.Security.TokenWebSocketHandshakeInterceptor;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class NotificacaoWebSocketConfig implements WebSocketConfigurer {

    private final NotificacaoWebSocketHandler notificacaoWebSocketHandler;
    private final TokenWebSocketHandshakeInterceptor tokenWebSocketHandshakeInterceptor;
    private final String[] allowedOrigins;

    public NotificacaoWebSocketConfig(
            NotificacaoWebSocketHandler notificacaoWebSocketHandler,
            TokenWebSocketHandshakeInterceptor tokenWebSocketHandshakeInterceptor,
            @Value("${app.cors.allowed-origins}") String[] allowedOrigins
    ) {
        this.notificacaoWebSocketHandler = notificacaoWebSocketHandler;
        this.tokenWebSocketHandshakeInterceptor = tokenWebSocketHandshakeInterceptor;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificacaoWebSocketHandler, "/ws/notificacoes")
                .addInterceptors(tokenWebSocketHandshakeInterceptor)
                .setAllowedOriginPatterns(
                        Arrays.stream(allowedOrigins)
                                .map(String::trim)
                                .filter(origem -> !origem.isEmpty())
                                .toArray(String[]::new)
                );
    }
}
