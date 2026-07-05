package conne.connect.connect.NotificacoesSistem.config;

import conne.connect.connect.NotificacoesSistem.websockt.NotificacaoWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class NotificacaoWebSocketConfig implements WebSocketConfigurer {

    private final NotificacaoWebSocketHandler notificacaoWebSocketHandler;

    public NotificacaoWebSocketConfig(NotificacaoWebSocketHandler notificacaoWebSocketHandler) {
        this.notificacaoWebSocketHandler = notificacaoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificacaoWebSocketHandler, "/ws/notificacoes")
                .setAllowedOriginPatterns(
                        "http://localhost:4200",
                        "https://*.vercel.app"
                );
    }
}
