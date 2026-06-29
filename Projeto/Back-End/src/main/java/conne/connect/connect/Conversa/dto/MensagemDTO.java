package conne.connect.connect.Conversa.dto;

import conne.connect.connect.Conversa.enums.TipoMensagem;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MensagemDTO {

    private Long id;
    private ParticipanteConversaDTO remetente;
    private TipoMensagem tipo;
    private String conteudo;
    private MensagemAnexoDTO anexo;
    private LocalDateTime enviadaEm;
    private LocalDateTime editadaEm;
    private boolean enviadaPeloUsuarioLogado;
    private boolean lidaPeloUsuarioLogado;
    private long quantidadeLeituras;
    private long totalParticipantes;
}
