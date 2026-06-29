package conne.connect.connect.Auth.dto;

public class LoginResponseDTO {

    private Long idUsuario;
    private String nome;
    private String email;
    private Long empresaId;
    private Long usuarioEmpresaId;
    private String papel;
    private String status;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(
            Long idUsuario,
            String nome,
            String email,
            Long empresaId,
            Long usuarioEmpresaId,
            String papel,
            String status
    ) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.empresaId = empresaId;
        this.usuarioEmpresaId = usuarioEmpresaId;
        this.papel = papel;
        this.status = status;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getUsuarioEmpresaId() {
        return usuarioEmpresaId;
    }

    public void setUsuarioEmpresaId(Long usuarioEmpresaId) {
        this.usuarioEmpresaId = usuarioEmpresaId;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
