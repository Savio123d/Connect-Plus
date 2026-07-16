package conne.connect.connect.Transmissao.handler;

import conne.connect.connect.Security.TokenWebSocketHandshakeInterceptor;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SinalizacaoWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessoes = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long idUsuarioEmpresa = extrairIdUsuarioEmpresa(session);
        Long idAutenticado = (Long) session.getAttributes().get(
                TokenWebSocketHandshakeInterceptor.ATRIBUTO_USUARIO_EMPRESA
        );

        if (idUsuarioEmpresa == null || !idUsuarioEmpresa.equals(idAutenticado)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        sessoes.put(idAutenticado, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        if (!json.has("to")) {
            return;
        }

        Long idOrigem = (Long) session.getAttributes().get(
                TokenWebSocketHandshakeInterceptor.ATRIBUTO_USUARIO_EMPRESA
        );
        Long idEmpresaOrigem = (Long) session.getAttributes().get(
                TokenWebSocketHandshakeInterceptor.ATRIBUTO_EMPRESA
        );
        long idDestino = json.getLong("to");
        WebSocketSession sessaoDestino = sessoes.get(idDestino);

        if (idOrigem == null || idEmpresaOrigem == null || sessaoDestino == null) {
            return;
        }

        Long idEmpresaDestino = (Long) sessaoDestino.getAttributes().get(
                TokenWebSocketHandshakeInterceptor.ATRIBUTO_EMPRESA
        );
        if (!idEmpresaOrigem.equals(idEmpresaDestino) || !sessaoDestino.isOpen()) {
            return;
        }

        json.put("from", idOrigem);
        sessaoDestino.sendMessage(new TextMessage(json.toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessoes.values().remove(session);
    }

    private Long extrairIdUsuarioEmpresa(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }

        String valor = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("userId");

        try {
            long idUsuarioEmpresa = Long.parseLong(valor);
            return idUsuarioEmpresa > 0 ? idUsuarioEmpresa : null;
        } catch (NumberFormatException | NullPointerException erro) {
            return null;
        }
    }
}
