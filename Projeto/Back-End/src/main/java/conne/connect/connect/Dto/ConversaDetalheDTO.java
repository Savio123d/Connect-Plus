package conne.connect.connect.Dto;

import conne.connect.connect.Enums.TipoConversa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConversaDetalheDTO {

    private Long id;
    private TipoConversa tipo;
    private String nome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<ParticipanteConversaDTO> participantes = new ArrayList<>();
    private List<MensagemDTO> mensagens = new ArrayList<>();
}