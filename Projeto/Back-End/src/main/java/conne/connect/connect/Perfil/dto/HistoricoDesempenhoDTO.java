package conne.connect.connect.Perfil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoDesempenhoDTO {

    private String mes;
    private int tarefasConcluidas;
    private int xpGanho;
}
