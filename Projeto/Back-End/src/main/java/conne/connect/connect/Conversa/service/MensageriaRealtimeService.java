package conne.connect.connect.Conversa.service;

import conne.connect.connect.Conversa.dto.ChatEventoDTO;
import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import conne.connect.connect.Conversa.repository.ConversaParticipanteRepository;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class MensageriaRealtimeService {

    public static final String EVENTO_CONVERSA_CRIADA = "CONVERSA_CRIADA";
    public static final String EVENTO_MENSAGEM_ENVIADA = "MENSAGEM_ENVIADA";
    public static final String EVENTO_MENSAGEM_LIDA = "MENSAGEM_LIDA";

    private static final int TEMPO_ENVIO_MILLIS = 5000;
    private static final int LIMITE_BUFFER_BYTES = 512 * 1024;

    private final ConversaParticipanteRepository conversaParticipanteRepository;
    private final Map<String, SessaoChatConectada> sessoesPorId = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> sessoesPorUsuario = new ConcurrentHashMap<>();

    public MensageriaRealtimeService(ConversaParticipanteRepository conversaParticipanteRepository) {
        this.conversaParticipanteRepository = conversaParticipanteRepository;
    }

    public void registrarSessao(Long idUsuarioEmpresa, WebSocketSession sessaoOriginal) {
        WebSocketSession sessaoDecorada = new ConcurrentWebSocketSessionDecorator(
                sessaoOriginal,
                TEMPO_ENVIO_MILLIS,
                LIMITE_BUFFER_BYTES
        );

        sessoesPorId.put(sessaoOriginal.getId(), new SessaoChatConectada(idUsuarioEmpresa, sessaoDecorada));
        sessoesPorUsuario
                .computeIfAbsent(idUsuarioEmpresa, ignored -> ConcurrentHashMap.newKeySet())
                .add(sessaoOriginal.getId());
    }

    public void removerSessao(WebSocketSession sessao) {
        if (sessao == null) {
            return;
        }

        removerSessaoPorId(sessao.getId());
    }

    public void notificarConversaAposCommit(
            Long idConversa,
            String tipoEvento,
            Long idMensagem,
            Long idUsuarioEmpresaOrigem
    ) {
        ChatEventoDTO evento = ChatEventoDTO.criar(
                tipoEvento,
                idConversa,
                idMensagem,
                idUsuarioEmpresaOrigem
        );

        executarAposCommit(() -> notificarParticipantesDaConversa(idConversa, evento));
    }

    private void notificarParticipantesDaConversa(Long idConversa, ChatEventoDTO evento) {
        List<Long> idsParticipantes = conversaParticipanteRepository
                .findByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNullOrderByEntrouEmAsc(idConversa)
                .stream()
                .map(ConversaParticipanteModel::getIdUsuarioEmpresa)
                .map(usuarioEmpresa -> usuarioEmpresa.getIdUsuarioEmpresa())
                .distinct()
                .toList();

        if (idsParticipantes.isEmpty()) {
            return;
        }

        String payload = serializarEvento(evento);
        if (payload == null) {
            return;
        }

        for (Long idParticipante : idsParticipantes) {
            enviarParaUsuario(idParticipante, payload);
        }
    }

    private void enviarParaUsuario(Long idUsuarioEmpresa, String payload) {
        Set<String> idsSessoes = sessoesPorUsuario.get(idUsuarioEmpresa);
        if (idsSessoes == null || idsSessoes.isEmpty()) {
            return;
        }

        for (String idSessao : List.copyOf(idsSessoes)) {
            SessaoChatConectada sessaoChat = sessoesPorId.get(idSessao);
            if (sessaoChat == null) {
                removerSessaoPorId(idSessao);
                continue;
            }

            try {
                if (!sessaoChat.sessao().isOpen()) {
                    removerSessaoPorId(idSessao);
                    continue;
                }

                sessaoChat.sessao().sendMessage(new TextMessage(payload));
            } catch (IOException erro) {
                encerrarSessaoComFalha(idSessao, sessaoChat.sessao());
            }
        }
    }

    private void executarAposCommit(Runnable acao) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            acao.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                acao.run();
            }
        });
    }

    private String serializarEvento(ChatEventoDTO evento) {
        return "{"
                + "\"tipo\":\"" + escaparJson(evento.getTipo()) + "\","
                + "\"idConversa\":" + numeroOuNull(evento.getIdConversa()) + ","
                + "\"idMensagem\":" + numeroOuNull(evento.getIdMensagem()) + ","
                + "\"idUsuarioEmpresaOrigem\":" + numeroOuNull(evento.getIdUsuarioEmpresaOrigem()) + ","
                + "\"ocorridoEm\":" + textoOuNull(evento.getOcorridoEm() != null ? evento.getOcorridoEm().toString() : null)
                + "}";
    }

    private void encerrarSessaoComFalha(String idSessao, WebSocketSession sessao) {
        removerSessaoPorId(idSessao);

        try {
            if (sessao.isOpen()) {
                sessao.close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException ignored) {
        }
    }

    private void removerSessaoPorId(String idSessao) {
        SessaoChatConectada sessaoRemovida = sessoesPorId.remove(idSessao);
        if (sessaoRemovida == null) {
            return;
        }

        Set<String> idsSessoes = sessoesPorUsuario.get(sessaoRemovida.idUsuarioEmpresa());
        if (idsSessoes == null) {
            return;
        }

        idsSessoes.remove(idSessao);
        if (idsSessoes.isEmpty()) {
            sessoesPorUsuario.remove(sessaoRemovida.idUsuarioEmpresa());
        }
    }

    private String numeroOuNull(Long valor) {
        return valor != null ? valor.toString() : "null";
    }

    private String textoOuNull(String valor) {
        return valor != null ? "\"" + escaparJson(valor) + "\"" : "null";
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private record SessaoChatConectada(Long idUsuarioEmpresa, WebSocketSession sessao) {
    }
}
