package conne.connect.connect.Tarefa.dto;

import conne.connect.connect.Tarefa.enums.DificuldadeTarefa;
import conne.connect.connect.Tarefa.enums.PrioridadeTarefa;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaRequestDTO {

    private Long idEmpresa;

    private Long idProjeto;

    private Long idResponsavelUsuarioEmpresa;

    private String titulo;

    private String descricao;

    private PrioridadeTarefa prioridade;

    private DificuldadeTarefa dificuldade;

    private Integer horasEstimadas;

    private LocalDateTime prazo;
}
