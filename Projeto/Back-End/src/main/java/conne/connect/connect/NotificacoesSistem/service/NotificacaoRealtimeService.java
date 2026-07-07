package conne.connect.connect.NotificacoesSistem.service;

import conne.connect.connect.NotificacoesSistem.dto.NotificacaoPushDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class NotificacaoRealtimeService {
    private static final String ATTR_USUARIO_EMPRESA_ID = "usuarioEmpresaId";

    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<Long, CopyOnWriteArraySet<WebSocketSession>> sessoesPorUsuario =
            new ConcurrentHashMap<>();

    public NotificacaoRealtimeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void registrarSessao(Long idUsuarioEmpresa, WebSocketSession session) {
        session.getAttributes().put(ATTR_USUARIO_EMPRESA_ID, idUsuarioEmpresa);

        sessoesPorUsuario
                .computeIfAbsent(idUsuarioEmpresa, chave -> new CopyOnWriteArraySet<>())
                .add(session);
    }

    public void removerSessao(WebSocketSession session) {
        Object valor = session.getAttributes().get(ATTR_USUARIO_EMPRESA_ID);

        if (!(valor instanceof Long idUsuarioEmpresa)) {
            return;
        }

        Set<WebSocketSession> sessoes = sessoesPorUsuario.get(idUsuarioEmpresa);

        if (sessoes == null) {
            return;
        }

        sessoes.remove(session);

        if (sessoes.isEmpty()) {
            sessoesPorUsuario.remove(idUsuarioEmpresa);
        }
    }

    public void enviarParaUsuario(Long idUsuarioEmpresa, NotificacaoPushDTO payload) {
        Set<WebSocketSession> sessoes = sessoesPorUsuario.get(idUsuarioEmpresa);

        if (sessoes == null || sessoes.isEmpty()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(payload);

            for (WebSocketSession session : sessoes) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                } else {
                    removerSessao(session);
                }
            }

        } catch (Exception erro) {
            throw new RuntimeException("Erro ao enviar notificação em tempo real", erro);
        }
    }
}
