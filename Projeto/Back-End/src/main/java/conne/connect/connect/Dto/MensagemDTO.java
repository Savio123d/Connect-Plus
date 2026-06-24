package conne.connect.connect.Dto;

import conne.connect.connect.Enums.TipoMensagem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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