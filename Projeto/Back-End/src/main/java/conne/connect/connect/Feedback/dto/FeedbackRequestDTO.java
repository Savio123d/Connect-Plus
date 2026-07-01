package conne.connect.connect.Feedback.dto;

import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDTO {

    private Long empresaId;
    private Long autorUsuarioEmpresaId;
    private Long destinatarioUsuarioEmpresaId;
    private Long projetoId;
    private Long tarefaId;

    private FeedbackClassificacao classificacao;
    private String categoria;
    private String comentario;
    private Boolean avaliacao360;
}
