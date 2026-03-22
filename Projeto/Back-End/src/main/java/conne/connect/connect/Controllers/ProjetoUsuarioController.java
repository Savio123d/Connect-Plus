package conne.connect.connect.Controllers;

import conne.connect.connect.Models.ProjetoUsuarioModel;
import conne.connect.connect.Services.ProjetoUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class ProjetoUsuarioController {
    @Autowired
    private ProjetoUsuarioService projetoUsuarioService;

    @GetMapping
    public ResponseEntity<List<ProjetoUsuarioModel>> findAll() {
        List<ProjetoUsuarioModel> projetosUsuarios = projetoUsuarioService.findAll();
        return ResponseEntity.ok(projetosUsuarios);
    }

    @PostMapping
    public ResponseEntity<ProjetoUsuarioModel> criarProjetoUsuario(@RequestBody ProjetoUsuarioModel projetoUsuarioModel) {
        ProjetoUsuarioModel projetoUsuario = projetoUsuarioService.criarProjetoUsuario(projetoUsuarioModel);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projetoUsuario.getIdProjetoUsuario())
                .toUri();

        return ResponseEntity.created(uri).body(projetoUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long idProjetoUsuario) {
        projetoUsuarioService.excluirProjetoUsuario(idProjetoUsuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProjetoUsuarioModel>> buscarPorId(@PathVariable("id") Long idProjetoUsuario) {
        return ResponseEntity.ok(projetoUsuarioService.buscarPorId(idProjetoUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoUsuarioModel> atualizar(
            @PathVariable("id") Long idProjetoUsuario,
            @RequestBody ProjetoUsuarioModel projetoUsuarioModel
    ) {
        return ResponseEntity.ok(
                projetoUsuarioService.atualizarProjetoUsuario(idProjetoUsuario, projetoUsuarioModel)
        );
    }
}
