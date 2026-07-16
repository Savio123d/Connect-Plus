package conne.connect.connect.Usuario.controller;

import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.service.UsuarioEmpresaService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping(path = "/api/usuarios-empresa")
@RestController
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    public UsuarioEmpresaController(UsuarioEmpresaService usuarioEmpresaService) {
        this.usuarioEmpresaService = usuarioEmpresaService;
    }

    @PreAuthorize("@autorizacao.ehGestor()")
    @GetMapping
    public ResponseEntity<List<UsuarioEmpresaModel>> findAll() {
        List<UsuarioEmpresaModel> usuariosEmpresa = usuarioEmpresaService.findAll();
        return ResponseEntity.ok(usuariosEmpresa);
    }

    @PreAuthorize("@autorizacao.ehGestor()")
    @PostMapping
    public ResponseEntity<UsuarioEmpresaModel> criarUsuarioEmpresa(@RequestBody UsuarioEmpresaModel usuarioEmpresaModel) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaService.criarUsuarioEmpresa(usuarioEmpresaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuarioEmpresa.getIdUsuarioEmpresa()).toUri();
        return ResponseEntity.created(uri).body(usuarioEmpresa);
    }

    @PreAuthorize("@autorizacao.gestorDoVinculo(#idUsuarioEmpresa)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idUsuarioEmpresa) {
        usuarioEmpresaService.excluirUsuarioEmpresa(idUsuarioEmpresa);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@autorizacao.gestorDoVinculo(#idUsuarioEmpresa)")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEmpresaModel> buscarPorId(@PathVariable("id") Long idUsuarioEmpresa) {
        return ResponseEntity.of(usuarioEmpresaService.buscarPorId(idUsuarioEmpresa));
    }

    @PreAuthorize("@autorizacao.gestorDoVinculo(#idUsuarioEmpresa)")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEmpresaModel> atualizar(@PathVariable("id") Long idUsuarioEmpresa, @RequestBody UsuarioEmpresaModel usuarioEmpresaModel) {
        return ResponseEntity.ok(usuarioEmpresaService.atualizarUsuarioEmpresa(idUsuarioEmpresa, usuarioEmpresaModel));
    }
}
