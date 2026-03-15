package conne.connect.connect.Controllers;
import conne.connect.connect.Models.UsuarioModel;
import conne.connect.connect.Services.UsuarioService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/usuarios")
@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> findAll() {
        List<UsuarioModel> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> criarUsuario(@RequestBody UsuarioModel usuarioModel) {
        UsuarioModel usuario = usuarioService.criarUsuario(usuarioModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuario.getIdUsuario()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idUsuario) {
        usuarioService.excluirUsuario(idUsuario);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UsuarioModel>> buscarid(@PathVariable("id") Long idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarPorId(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> atualizar(@PathVariable("id") Long idUsuario, @RequestBody UsuarioModel usuarioModel) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(idUsuario, usuarioModel));
    }


}
