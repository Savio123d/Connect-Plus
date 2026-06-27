package conne.connect.connect.Conversa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarConversaPrivadaRequestDTO {

    @NotNull(message = "O destinatario e obrigatorio.")
    private Long idDestinatarioUsuarioEmpresa;
}
