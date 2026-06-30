package conne.connect.connect.Perfil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuarioDTO {

    private Long idUsuario;
    private Long idUsuarioEmpresa;
    private String nome;
    private String email;
    private String cargo;
    private String departamento;
    private Integer nivel;
    private Integer xpAtual;
    private Integer xpProximoNivel;
}
