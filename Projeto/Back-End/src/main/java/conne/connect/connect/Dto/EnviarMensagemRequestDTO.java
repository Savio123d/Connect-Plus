package conne.connect.connect.Dto;

import conne.connect.connect.Enums.TipoMensagem;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnviarMensagemRequestDTO {

    private Long idConversa;

    private String conteudo;

    private TipoMensagem tipo;

    @Valid
    private MensagemAnexoDTO anexo;
}