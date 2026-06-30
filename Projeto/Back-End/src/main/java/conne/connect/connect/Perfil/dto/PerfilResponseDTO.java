package conne.connect.connect.Perfil.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponseDTO {

    private PerfilUsuarioDTO usuario;
    private List<ConquistaPerfilDTO> conquistas;
    private List<HistoricoDesempenhoDTO> historico;
}
