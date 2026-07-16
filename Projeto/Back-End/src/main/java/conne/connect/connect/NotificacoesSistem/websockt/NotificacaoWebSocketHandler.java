package conne.connect.connect.NotificacoesSistem.websockt;

import conne.connect.connect.Security.TokenWebSocketHandshakeInterceptor;
import conne.connect.connect.NotificacoesSistem.service.NotificacaoRealtimeService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class NotificacaoWebSocketHandler extends TextWebSocketHandler {

    private final NotificacaoRealtimeService notificacaoRealtimeService;

    public NotificacaoWebSocketHandler(NotificacaoRealtimeService notificacaoRealtimeService) {
        this.notificacaoRealtimeService = notificacaoRealtimeService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long idUsuarioEmpresa = extrairLongDaQuery(session, "usuarioEmpresaId");

        if (idUsuarioEmpresa == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Long idUsuarioEmpresaAutenticado = (Long) session.getAttributes().get(
                TokenWebSocketHandshakeInterceptor.ATRIBUTO_USUARIO_EMPRESA
        );
        if (!idUsuarioEmpresa.equals(idUsuarioEmpresaAutenticado)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        notificacaoRealtimeService.registrarSessao(idUsuarioEmpresa, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        notificacaoRealtimeService.removerSessao(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        notificacaoRealtimeService.removerSessao(session);

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private Long extrairLongDaQuery(WebSocketSession session, String nomeCampo) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return null;
        }

        String[] parametros = session.getUri().getQuery().split("&");

        for (String parametro : parametros) {
            String[] partes = parametro.split("=");

            if (partes.length != 2) {
                continue;
            }

            String chave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(partes[1], StandardCharsets.UTF_8);

            if (chave.equals(nomeCampo)) {
                try {
                    return Long.valueOf(valor);
                } catch (NumberFormatException erro) {
                    return null;
                }
            }
        }

        return null;
    }
}
