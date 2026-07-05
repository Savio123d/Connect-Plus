package conne.connect.connect.Feedback.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Feedback360GestorDTO {
    private Long projetoId;
    private String projetoNome;
    private Long avaliadoId;
    private String avaliadoNome;
    private Double mediaGeral;
    private Double mediaAssiduidade;
    private Double mediaNivelEntregas;
    private Double mediaComunicacao;
    private Double mediaColaboracao;
    private Double mediaComprometimento;
    private Long quantidadeAvaliacoes;
    private List<String> comentarios;
    private List<String> observacoesProjeto;
}
