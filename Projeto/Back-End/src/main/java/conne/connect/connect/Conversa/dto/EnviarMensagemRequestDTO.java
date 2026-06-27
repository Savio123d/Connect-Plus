package conne.connect.connect.Conversa.dto;

import conne.connect.connect.Conversa.enums.TipoMensagem;
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
