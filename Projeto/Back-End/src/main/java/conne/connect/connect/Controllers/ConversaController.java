package conne.connect.connect.Controllers;

import conne.connect.connect.Dto.ConversaDetalheDTO;
import conne.connect.connect.Dto.ConversaResumoDTO;
import conne.connect.connect.Dto.CriarConversaGrupoRequestDTO;
import conne.connect.connect.Dto.CriarConversaPrivadaRequestDTO;
import conne.connect.connect.Dto.EnviarMensagemRequestDTO;
import conne.connect.connect.Dto.MensagemDTO;
import conne.connect.connect.Enums.TipoConversa;
import conne.connect.connect.Services.MensageriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversas")
public class ConversaController {

    private static final String HEADER_USUARIO_EMPRESA = "X-Usuario-Empresa-Id";

    private final MensageriaService mensageriaService;

    public ConversaController(MensageriaService mensageriaService) {
        this.mensageriaService = mensageriaService;
    }

    @PostMapping("/privada")
    public ResponseEntity<ConversaDetalheDTO> criarConversaPrivada(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @Valid @RequestBody CriarConversaPrivadaRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                mensageriaService.criarOuBuscarConversaPrivada(idUsuarioEmpresaLogado, requestDTO)
        );
    }

    @PostMapping("/grupo")
    public ResponseEntity<ConversaDetalheDTO> criarConversaGrupo(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @Valid @RequestBody CriarConversaGrupoRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(
                mensageriaService.criarConversaGrupo(idUsuarioEmpresaLogado, requestDTO)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConversaResumoDTO>> listarConversas(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @RequestParam(value = "tipo", required = false) TipoConversa tipo
    ) {
        return ResponseEntity.ok(mensageriaService.listarConversas(idUsuarioEmpresaLogado, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversaDetalheDTO> detalharConversa(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @PathVariable("id") Long idConversa
    ) {
        return ResponseEntity.ok(mensageriaService.detalharConversa(idUsuarioEmpresaLogado, idConversa));
    }

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<List<MensagemDTO>> listarMensagens(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @PathVariable("id") Long idConversa
    ) {
        return ResponseEntity.ok(mensageriaService.listarMensagens(idUsuarioEmpresaLogado, idConversa));
    }

    @PostMapping("/{id}/mensagens")
    public ResponseEntity<MensagemDTO> enviarMensagemNaConversa(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @PathVariable("id") Long idConversa,
            @Valid @RequestBody EnviarMensagemRequestDTO requestDTO
    ) {
        requestDTO.setIdConversa(idConversa);
        return ResponseEntity.ok(mensageriaService.enviarMensagem(idUsuarioEmpresaLogado, requestDTO));
    }
}