package conne.connect.connect.Tarefa.dto;

import conne.connect.connect.Tarefa.enums.DificuldadeTarefa;
import conne.connect.connect.Tarefa.enums.PrioridadeTarefa;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaRequestDTO {

    @NotNull(message = "A empresa é obrigatória.")
    private Long idEmpresa;

    @NotNull(message = "O projeto é obrigatório.")
    private Long idProjeto;

    private Long idResponsavelUsuarioEmpresa;

    @NotBlank(message = "O título é obrigatório.")
    private String titulo;

    private String descricao;

    private PrioridadeTarefa prioridade;

    private DificuldadeTarefa dificuldade;

    private StatusTarefa status;

    @PositiveOrZero(message = "As horas estimadas não podem ser negativas.")
    private Integer horasEstimadas;

    private LocalDateTime prazo;
}
