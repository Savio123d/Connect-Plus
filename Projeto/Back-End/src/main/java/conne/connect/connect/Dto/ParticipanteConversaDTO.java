package conne.connect.connect.Dto;

import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Models.UsuarioModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipanteConversaDTO {

    private Long idUsuarioEmpresa;
    private Long idUsuario;
    private Long idEmpresa;
    private String nome;
    private String email;

    public static ParticipanteConversaDTO fromModel(UsuarioEmpresaModel usuarioEmpresaModel) {
        ParticipanteConversaDTO dto = new ParticipanteConversaDTO();
        dto.setIdUsuarioEmpresa(usuarioEmpresaModel.getIdUsuarioEmpresa());

        if (usuarioEmpresaModel.getIdEmpresa() != null) {
            dto.setIdEmpresa(usuarioEmpresaModel.getIdEmpresa().getIdEmpresa());
        }

        UsuarioModel usuario = usuarioEmpresaModel.getIdUsuario();
        if (usuario != null) {
            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNome(usuario.getNome());
            dto.setEmail(usuario.getEmail());
        }

        return dto;
    }
}