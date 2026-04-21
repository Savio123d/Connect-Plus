package conne.connect.connect.Controllers;

import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Services.UsuarioEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/usuarios-empresa")
@RestController
public class UsuarioEmpresaController {

    @Autowired
    private UsuarioEmpresaService usuarioEmpresaService;

    @GetMapping
    public ResponseEntity<List<UsuarioEmpresaModel>> findAll() {
        List<UsuarioEmpresaModel> usuariosEmpresa = usuarioEmpresaService.findAll();
        return ResponseEntity.ok(usuariosEmpresa);
    }

    @PostMapping
    public ResponseEntity<UsuarioEmpresaModel> criarUsuarioEmpresa(@RequestBody UsuarioEmpresaModel usuarioEmpresaModel) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaService.criarUsuarioEmpresa(usuarioEmpresaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuarioEmpresa.getIdUsuarioEmpresa()).toUri();
        return ResponseEntity.created(uri).body(usuarioEmpresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idUsuarioEmpresa) {
        usuarioEmpresaService.excluirUsuarioEmpresa(idUsuarioEmpresa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UsuarioEmpresaModel>> buscarPorId(@PathVariable("id") Long idUsuarioEmpresa) {
        return ResponseEntity.ok(usuarioEmpresaService.buscarPorId(idUsuarioEmpresa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEmpresaModel> atualizar(@PathVariable("id") Long idUsuarioEmpresa, @RequestBody UsuarioEmpresaModel usuarioEmpresaModel) {
        return ResponseEntity.ok(usuarioEmpresaService.atualizarUsuarioEmpresa(idUsuarioEmpresa, usuarioEmpresaModel));
    }
}
