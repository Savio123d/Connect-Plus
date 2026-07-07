package conne.connect.connect.Feedback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback360ObservacaoRequestDTO {
    private Long empresaId;
    private Long usuarioEmpresaId;
    private String observacao;
}
