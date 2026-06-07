package conne.connect.connect.Dto;

import lombok.Builder;

@Builder
public record DesempenhoEquipeDTO(
        Integer mes,
        Long total
) {}
