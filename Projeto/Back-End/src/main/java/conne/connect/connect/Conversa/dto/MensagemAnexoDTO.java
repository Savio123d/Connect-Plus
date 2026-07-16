package conne.connect.connect.Conversa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MensagemAnexoDTO {

    private Long id;

    @NotBlank(message = "O nome do arquivo é obrigatório.")
    private String filename;

    @NotBlank(message = "Os dados do anexo são obrigatórios.")
    private String data;

    private String tipoMime;

    @PositiveOrZero(message = "O tamanho do anexo não pode ser negativo.")
    private Integer tamanho;
}
