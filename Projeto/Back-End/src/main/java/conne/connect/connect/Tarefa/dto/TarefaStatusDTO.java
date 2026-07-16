package conne.connect.connect.Tarefa.dto;

import conne.connect.connect.Tarefa.enums.StatusTarefa;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaStatusDTO {

    @NotNull(message = "O status é obrigatório.")
    private StatusTarefa status;
}
