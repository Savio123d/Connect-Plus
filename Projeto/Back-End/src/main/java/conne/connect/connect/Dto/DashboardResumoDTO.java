package conne.connect.connect.Dto;

import lombok.Builder;
import java.util.List;

@Builder
public record DashboardResumoDTO(
        Long usuariosAtivos,
        Long projetosAtivos,
        Long tarefasConcluidas,
        Long tarefasEmAndamento,
        Long tarefasPendentes,
        Long tarefasAtrasadas,
        Long feedbacks,
        List<DesempenhoEquipeDTO> desempenhoEquipe
) {}