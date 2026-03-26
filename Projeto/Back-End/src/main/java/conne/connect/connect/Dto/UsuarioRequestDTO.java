package conne.connect.connect.Dto;

import conne.connect.connect.Models.UsuarioModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatorio.")
    private String nome;

    @NotBlank(message = "O email é obrigatorio.")
    @Email(message = "Informe um email valido.")
    private String email;

    @NotBlank(message = "A senha é obrigatoria.")
    private String senha;

    private String avatar;
    private String temaPerfil;

    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String nome, String email, String senha, String avatar, String temaPerfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.avatar = avatar;
        this.temaPerfil = temaPerfil;
    }

    public UsuarioModel toModel() {
        UsuarioModel usuarioModel = new UsuarioModel();
        applyToModel(usuarioModel);
        return usuarioModel;
    }

    public void applyToModel(UsuarioModel usuarioModel) {
        usuarioModel.setNome(nome);
        usuarioModel.setEmail(email);
        usuarioModel.setSenha(senha);
        usuarioModel.setAvatar(avatar);
        usuarioModel.setTemaPerfil(temaPerfil);
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    @Override
    public String toString() {
        return "UsuarioRequestDTO{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", temaPerfil='" + temaPerfil + '\'' +
                '}';
    }
}
