package conne.connect.connect.Tarefa.dto;

import conne.connect.connect.Tarefa.enums.StatusTarefa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaStatusDTO {

    private StatusTarefa status;
}
