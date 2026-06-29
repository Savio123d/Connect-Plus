package conne.connect.connect.Conversa.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatEventoDTO {

    private String tipo;
    private Long idConversa;
    private Long idMensagem;
    private Long idUsuarioEmpresaOrigem;
    private LocalDateTime ocorridoEm;

    public static ChatEventoDTO criar(
            String tipo,
            Long idConversa,
            Long idMensagem,
            Long idUsuarioEmpresaOrigem
    ) {
        ChatEventoDTO evento = new ChatEventoDTO();
        evento.setTipo(tipo);
        evento.setIdConversa(idConversa);
        evento.setIdMensagem(idMensagem);
        evento.setIdUsuarioEmpresaOrigem(idUsuarioEmpresaOrigem);
        evento.setOcorridoEm(LocalDateTime.now());
        return evento;
    }
}
