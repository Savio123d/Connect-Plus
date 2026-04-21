package conne.connect.connect.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private long usuariosAtivos;
    private long empresasCadastradas;
    private long projetosEmAndamento;
    private long tarefasPendentes;
    private long tarefasEmAndamento;
    private long tarefasConcluidas;
}