package conne.connect.connect.Perfil.dto;

public record PerfilUsuarioDTO(
        Long idUsuario,
        Long idUsuarioEmpresa,
        String nome,
        String email,
        String cargo,
        String departamento,
        Integer nivel,
        Integer xpAtual,
        Integer xpProximoNivel
) {
}