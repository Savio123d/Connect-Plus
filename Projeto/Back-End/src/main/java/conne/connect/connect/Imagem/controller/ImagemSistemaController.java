package conne.connect.connect.Imagem.controller;

import conne.connect.connect.Imagem.dto.ImagemUploadResponseDTO;
import conne.connect.connect.Imagem.service.ImagemSistemaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imagens")
public class ImagemSistemaController {

    private static final String HEADER_USUARIO_EMPRESA = "X-Usuario-Empresa-Id";

    private final ImagemSistemaService imagemSistemaService;

    public ImagemSistemaController(ImagemSistemaService imagemSistemaService) {
        this.imagemSistemaService = imagemSistemaService;
    }

    @PostMapping(path = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemUploadResponseDTO> enviarImagemChat(
            @RequestHeader(HEADER_USUARIO_EMPRESA) Long idUsuarioEmpresaLogado,
            @RequestParam("empresaId") Long idEmpresa,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        return ResponseEntity.ok(imagemSistemaService.enviarImagemChat(arquivo, idEmpresa, idUsuarioEmpresaLogado));
    }
}
