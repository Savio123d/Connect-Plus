package conne.connect.connect.Dto;

import conne.connect.connect.Enums.DificuldadeTarefa;
import conne.connect.connect.Enums.PrioridadeTarefa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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