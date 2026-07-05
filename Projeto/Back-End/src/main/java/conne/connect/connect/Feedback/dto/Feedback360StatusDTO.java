package conne.connect.connect.Feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feedback360StatusDTO {
    private Boolean bloqueiaSistema;
    private Long rodadaId;
    private Long projetoId;
    private String projetoNome;
    private Boolean obrigatoria;
    private Long pendentes;
}
