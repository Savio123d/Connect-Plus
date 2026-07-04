package conne.connect.connect.Feedback.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feedback360PendenteDTO {

    private Long avaliacaoId;
    private Long rodadaId;
    private Long projetoId;
    private String projetoNome;
    private Long destinatarioUsuarioEmpresaId;
    private String destinatarioNome;
    private String destinatarioIniciais;
    private LocalDate concluidoEm;
    private LocalDate prazoLimite;
    private Long diasRestantes;
    private Boolean vencido;
}
