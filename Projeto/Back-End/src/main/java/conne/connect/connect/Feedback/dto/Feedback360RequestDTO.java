package conne.connect.connect.Feedback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback360RequestDTO {

    private Long empresaId;
    private Long autorUsuarioEmpresaId;
    private Long destinatarioUsuarioEmpresaId;
    private Long projetoId;

    private Integer comprometimento;
    private Integer nivelEntregas;
    private Integer colaboracao;
    private Integer comunicacao;

    private String comentario;
}
