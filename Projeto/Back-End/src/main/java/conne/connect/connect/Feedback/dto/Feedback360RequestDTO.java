package conne.connect.connect.Feedback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback360RequestDTO {

    private Long avaliacaoId;
    private Long empresaId;
    private Long autorUsuarioEmpresaId;
    private Long destinatarioUsuarioEmpresaId;
    private Long projetoId;
    private Integer nota;
    private String comentario;
    private Integer assiduidade;
    private Integer nivelEntregas;
    private Integer comunicacao;
    private Integer colaboracao;
    private Integer comprometimento;
}