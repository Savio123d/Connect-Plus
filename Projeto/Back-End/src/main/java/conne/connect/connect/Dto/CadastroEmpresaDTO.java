package conne.connect.connect.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CadastroEmpresaDTO {

    @NotBlank
    private String razaoSocial;

    private String nomeFantasia;

    @NotBlank
    private String cnpj;

    private String cidade;

    private String uf;

    @NotBlank
    private String nomeAdmin;

    @NotBlank
    @Email
    private String emailAdmin;

    @NotBlank
    @Size(min = 8)
    private String senhaAdmin;
}