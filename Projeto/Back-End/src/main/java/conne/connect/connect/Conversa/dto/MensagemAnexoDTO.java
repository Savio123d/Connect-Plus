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

    @NotBlank(message = "O nome do arquivo e obrigatorio.")
    private String filename;

    @NotBlank(message = "Os dados do anexo sao obrigatorios.")
    private String data;

    private String tipoMime;

    @PositiveOrZero(message = "O tamanho do anexo nao pode ser negativo.")
    private Integer tamanho;
}
