package conne.connect.connect.Dashboard.dto;

import lombok.Builder;

@Builder
public record DesempenhoEquipeDTO(
        Integer mes,
        Long total
) {}
