package conne.connect.connect.NotificacoesSistem.dto;

import java.time.LocalDateTime;

public record NotificacaoPushDTO(
        String evento,
        Long idNotificacao,
        Long idUsuarioEmpresa,
        Long idEmpresa,
        String tipo,
        String titulo,
        String mensagem,
        Boolean lida,
        LocalDateTime criadaEm
) {
}
