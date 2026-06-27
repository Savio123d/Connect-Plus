package conne.connect.connect.Dashboard.dto;

import java.util.List;
import lombok.Builder;

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
