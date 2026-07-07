package conne.connect.connect.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    // Mesmas origens do CORS HTTP (app.cors.allowed-origins).
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns(limparOrigensPermitidas());
    }

    private String[] limparOrigensPermitidas() {
        List<String> origensPermitidas = Arrays.stream(allowedOrigins)
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .collect(Collectors.toList());

        return origensPermitidas.toArray(new String[0]);
    }
}
