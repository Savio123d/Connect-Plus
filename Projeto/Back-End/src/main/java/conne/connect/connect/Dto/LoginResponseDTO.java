package conne.connect.connect.Dto;

import conne.connect.connect.Dto.UsuarioDTO;

public class LoginResponseDTO {

    private String mensagem;
    private UsuarioDTO usuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String mensagem, UsuarioDTO usuario) {
        this.mensagem = mensagem;
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }
}