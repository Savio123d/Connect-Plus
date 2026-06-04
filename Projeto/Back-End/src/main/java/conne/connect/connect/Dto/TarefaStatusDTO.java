package conne.connect.connect.Dto;

import conne.connect.connect.Enums.StatusTarefa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaStatusDTO {

    private StatusTarefa status;
}