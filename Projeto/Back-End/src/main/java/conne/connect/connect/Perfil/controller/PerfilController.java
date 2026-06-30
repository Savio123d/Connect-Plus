package conne.connect.connect.Perfil.controller;

import conne.connect.connect.Perfil.dto.PerfilResponseDTO;
import conne.connect.connect.Perfil.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/api/perfil")
@RestController
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/{idUsuarioEmpresa}")
    public ResponseEntity<PerfilResponseDTO> buscarPerfil(
            @PathVariable Long idUsuarioEmpresa
    ) {
        return ResponseEntity.ok(perfilService.buscarPerfil(idUsuarioEmpresa));
    }
}
