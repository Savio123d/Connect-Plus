package conne.connect.connect.Config;

import conne.connect.connect.Security.TokenWebSocketHandshakeInterceptor;
import conne.connect.connect.Transmissao.handler.SinalizacaoWebSocketHandler;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final SinalizacaoWebSocketHandler sinalizacaoWebSocketHandler;
    private final TokenWebSocketHandshakeInterceptor tokenWebSocketHandshakeInterceptor;
    private final String[] allowedOrigins;

    public WebSocketConfig(
            ChatWebSocketHandler chatWebSocketHandler,
            SinalizacaoWebSocketHandler sinalizacaoWebSocketHandler,
            TokenWebSocketHandshakeInterceptor tokenWebSocketHandshakeInterceptor,
            @Value("${app.cors.allowed-origins}") String[] allowedOrigins
    ) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.sinalizacaoWebSocketHandler = sinalizacaoWebSocketHandler;
        this.tokenWebSocketHandshakeInterceptor = tokenWebSocketHandshakeInterceptor;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(tokenWebSocketHandshakeInterceptor)
                .setAllowedOriginPatterns(limparOrigensPermitidas());

        registry.addHandler(sinalizacaoWebSocketHandler, "/sinalizacao")
                .addInterceptors(tokenWebSocketHandshakeInterceptor)
                .setAllowedOriginPatterns(limparOrigensPermitidas());
    }

    private String[] limparOrigensPermitidas() {
        return Arrays.stream(allowedOrigins)
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toArray(String[]::new);
    }
}
