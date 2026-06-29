package conne.connect.connect.Recompensa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LojaResgateRequestDTO {

    private Long idEmpresa;
    private Long idUsuarioEmpresa;
    private Integer quantidade;
}
