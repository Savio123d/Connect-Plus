package conne.connect.connect.Transmissao.controller;

import conne.connect.connect.Transmissao.dto.TransmissaoTokenDTO;
import conne.connect.connect.Transmissao.service.TransmissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transmissoes")
public class TransmissaoController {

    private final TransmissaoService transmissaoService;

    public TransmissaoController(TransmissaoService transmissaoService) {
        this.transmissaoService = transmissaoService;
    }

    @PostMapping("/conversas/{idConversa}/entrar")
    public ResponseEntity<TransmissaoTokenDTO> entrarNaConversa(
            @PathVariable Long idConversa,
            @RequestHeader("X-Usuario-Empresa-Id") Long idUsuarioEmpresa
    ) {
        return ResponseEntity.ok(transmissaoService.entrarNaConversa(idConversa, idUsuarioEmpresa));
    }
}
