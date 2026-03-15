package conne.connect.connect.Dto;
import conne.connect.connect.Enums.StatusUsuario;
import conne.connect.connect.Models.UsuarioModel;
import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long idUsuario;
    private String nome;
    private String email;
    private StatusUsuario status;
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
        );
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTemaPerfil() {
        return temaPerfil;
    }

    public void setTemaPerfil(String temaPerfil) {
        this.temaPerfil = temaPerfil;
    }

    public int getNivelAtual() {
        return nivelAtual;
    }

    public void setNivelAtual(int nivelAtual) {
        this.nivelAtual = nivelAtual;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                ", avatar='" + avatar + '\'' +
                ", temaPerfil='" + temaPerfil + '\'' +
                ", nivelAtual=" + nivelAtual +
                '}';
    }
}
