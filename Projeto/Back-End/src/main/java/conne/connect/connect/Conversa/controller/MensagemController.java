package conne.connect.connect.Conversa.controller;

import conne.connect.connect.Conversa.dto.EnviarMensagemRequestDTO;
import conne.connect.connect.Conversa.dto.MensagemDTO;
import conne.connect.connect.Conversa.service.MensageriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private static final String HEADER_USUARIO_EMPRESA = "X-Usuario-Empresa-Id";

    private final MensageriaService mensageriaService;

    public MensagemController(MensageriaService mensageriaService) {
        this.mensageriaService = mensageriaService;
    }

    @PostMapping
    public ResponseEntity<MensagemDTO> enviarMensagem(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @Valid @RequestBody EnviarMensagemRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(mensageriaService.enviarMensagem(idUsuarioEmpresaLogado, requestDTO));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<MensagemDTO> marcarComoLida(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @PathVariable("id") Long idMensagem
    ) {
        return ResponseEntity.ok(mensageriaService.marcarMensagemComoLida(idUsuarioEmpresaLogado, idMensagem));
    }
}
