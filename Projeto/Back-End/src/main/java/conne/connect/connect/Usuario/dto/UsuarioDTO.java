package conne.connect.connect.Usuario.dto;

import conne.connect.connect.Setor.model.SetorModel;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Xp.model.SaldoXpModel;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsuarioDTO {

    private Long idUsuario;
    private Long idUsuarioEmpresa;
    private Long idEmpresa;
    private Long idSetor;

    private String nome;
    private String email;
    private StatusUsuario status;

    private String cargo;
    private String departamento;
    private Integer xp;
    private Integer nivel;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    private String avatar;
    private String temaPerfil;
    private int nivelAtual;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long idUsuario, String nome, String email, StatusUsuario status,
                      LocalDateTime dataCriacao, LocalDateTime dataAtualizacao,
                      String avatar, String temaPerfil, int nivelAtual) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.avatar = avatar;
        this.temaPerfil = temaPerfil;
        this.nivelAtual = nivelAtual;
    }

    public static UsuarioDTO fromModel(UsuarioModel usuarioModel) {
        if (usuarioModel == null) {
            return null;
        }

        return new UsuarioDTO(
                usuarioModel.getIdUsuario(),
                usuarioModel.getNome(),
                usuarioModel.getEmail(),
                usuarioModel.getStatus(),
                usuarioModel.getDataCriacao(),
                usuarioModel.getDataAtualizacao(),
                usuarioModel.getAvatar(),
                usuarioModel.getTemaPerfil(),
                usuarioModel.getNivelAtual()
        );
    }

    public static UsuarioDTO fromUsuarioEmpresa(
            UsuarioEmpresaModel usuarioEmpresa,
            SaldoXpModel saldoXp
    ) {
        if (usuarioEmpresa == null || usuarioEmpresa.getIdUsuario() == null) {
            return null;
        }

        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        SetorModel setor = usuarioEmpresa.getIdSetor();

        int xpTotal = saldoXp != null && saldoXp.getXpTotal() != null
                ? saldoXp.getXpTotal()
                : 0;

        UsuarioDTO dto = fromModel(usuario);

        dto.setIdUsuarioEmpresa(usuarioEmpresa.getIdUsuarioEmpresa());

        if (usuarioEmpresa.getIdEmpresa() != null) {
            dto.setIdEmpresa(usuarioEmpresa.getIdEmpresa().getIdEmpresa());
        }

        if (setor != null) {
            dto.setIdSetor(setor.getIdSetor());
            dto.setDepartamento(setor.getNome());
        } else {
            dto.setDepartamento("Não informado");
        }

        dto.setCargo(usuarioEmpresa.getPapel() != null
                ? formatarCargo(usuarioEmpresa.getPapel().name())
                : "Colaborador"
        );

        dto.setStatus(Boolean.TRUE.equals(usuarioEmpresa.getAtivo())
                ? StatusUsuario.ativo
                : StatusUsuario.inativo
        );

        dto.setXp(xpTotal);
        dto.setNivel(calcularNivel(xpTotal));
        dto.setNivelAtual(calcularNivel(xpTotal));

        return dto;
    }

    private static String formatarCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            return "Colaborador";
        }

        String cargoFormatado = cargo.toLowerCase().replace("_", " ");
        return cargoFormatado.substring(0, 1).toUpperCase() + cargoFormatado.substring(1);
    }

    private static int calcularNivel(int xp) {
        return (xp / 500) + 1;
    }


    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", idUsuarioEmpresa=" + idUsuarioEmpresa +
                ", idEmpresa=" + idEmpresa +
                ", idSetor=" + idSetor +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", cargo='" + cargo + '\'' +
                ", departamento='" + departamento + '\'' +
                ", xp=" + xp +
                ", nivel=" + nivel +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                ", avatar='" + avatar + '\'' +
                ", temaPerfil='" + temaPerfil + '\'' +
                ", nivelAtual=" + nivelAtual +
                '}';
    }
}
