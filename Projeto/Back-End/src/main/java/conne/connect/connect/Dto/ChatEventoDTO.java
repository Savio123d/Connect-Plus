package conne.connect.connect.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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