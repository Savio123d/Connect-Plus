package conne.connect.connect.Config;

import conne.connect.connect.Conversa.service.MensageriaAcessoService;
import conne.connect.connect.Conversa.service.MensageriaRealtimeService;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MensageriaAcessoService mensageriaAcessoService;
    private final MensageriaRealtimeService mensageriaRealtimeService;

    public ChatWebSocketHandler(
            MensageriaAcessoService mensageriaAcessoService,
            MensageriaRealtimeService mensageriaRealtimeService
    ) {
        this.mensageriaAcessoService = mensageriaAcessoService;
        this.mensageriaRealtimeService = mensageriaRealtimeService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long idUsuarioEmpresa = extrairIdUsuarioEmpresa(session);
        if (idUsuarioEmpresa == null) {
            encerrarSessao(session, CloseStatus.BAD_DATA.withReason("Usuario invalido"));
            return;
        }

        try {
            mensageriaAcessoService.buscarUsuarioEmpresaAtivo(idUsuarioEmpresa);
            mensageriaRealtimeService.registrarSessao(idUsuarioEmpresa, session);
        } catch (ResponseStatusException erro) {
            CloseStatus status = erro.getStatusCode().value() == HttpStatus.NOT_FOUND.value()
                    ? CloseStatus.POLICY_VIOLATION.withReason("Usuario nao encontrado")
                    : CloseStatus.POLICY_VIOLATION.withReason("Acesso negado");
            encerrarSessao(session, status);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        mensageriaRealtimeService.removerSessao(session);
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        mensageriaRealtimeService.removerSessao(session);
        super.handleTransportError(session, exception);
    }

    private Long extrairIdUsuarioEmpresa(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }

        String valor = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("idUsuarioEmpresa");

        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            long idUsuarioEmpresa = Long.parseLong(valor);
            return idUsuarioEmpresa > 0 ? idUsuarioEmpresa : null;
        } catch (NumberFormatException erro) {
            return null;
        }
    }

    private void encerrarSessao(WebSocketSession session, CloseStatus status) throws IOException {
        mensageriaRealtimeService.removerSessao(session);

        if (session.isOpen()) {
            session.close(status);
        }
    }
}
