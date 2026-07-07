package conne.connect.connect.Trasmissão.handler;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class SinalizacaoWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        // Proteção contra URLs malformadas
        if (query != null && query.contains("=")) {
            String userId = query.split("=")[1];

            session.getAttributes().put("userId", userId);
            sessions.put(userId, session);

        } else {
            session.close(); // Rejeita conexões sem ID válido
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        if (!json.has("to")) {
            return;
        }
        json.put("from", session.getAttributes().get("userId"));
        String targetUserId = json.getString("to");

        if (sessions.containsKey(targetUserId)) {
            WebSocketSession targetSession = sessions.get(targetUserId);
            if (targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage((json.toString())));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
    }
}
