package conne.connect.connect.Conversa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarConversaGrupoRequestDTO {

    @NotBlank(message = "O nome do grupo e obrigatorio.")
    private String nome;

    @NotEmpty(message = "Informe ao menos um participante para o grupo.")
    private List<Long> idsParticipantes = new ArrayList<>();
}
