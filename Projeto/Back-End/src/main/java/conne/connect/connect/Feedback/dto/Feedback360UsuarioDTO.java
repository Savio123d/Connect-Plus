package conne.connect.connect.Feedback.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feedback360UsuarioDTO {
    private Long rodadaId;
    private Long projetoId;
    private String projetoNome;
    private Boolean obrigatoria;
    private Boolean concluidaPeloUsuario;
    private LocalDateTime abertaEm;
}
