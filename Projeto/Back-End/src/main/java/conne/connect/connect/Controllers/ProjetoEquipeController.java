package conne.connect.connect.Controllers;

import conne.connect.connect.Models.ProjetoEquipeModel;
import conne.connect.connect.Services.ProjetoEquipeService;
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

@RequestMapping(path = "/api/projetos-equipe")
@RestController
public class ProjetoEquipeController {

    @Autowired
    private ProjetoEquipeService projetoEquipeService;

    @GetMapping
    public ResponseEntity<List<ProjetoEquipeModel>> findAll() {
        List<ProjetoEquipeModel> projetosEquipe = projetoEquipeService.findAll();
        return ResponseEntity.ok(projetosEquipe);
    }

    @PostMapping
    public ResponseEntity<ProjetoEquipeModel> criarProjetoEquipe(@RequestBody ProjetoEquipeModel projetoEquipeModel) {
        ProjetoEquipeModel projetoEquipe = projetoEquipeService.criarProjetoEquipe(projetoEquipeModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(projetoEquipe.getIdProjetoEquipe()).toUri();
        return ResponseEntity.created(uri).body(projetoEquipe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idProjetoEquipe) {
        projetoEquipeService.excluirProjetoEquipe(idProjetoEquipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProjetoEquipeModel>> buscarPorId(@PathVariable("id") Long idProjetoEquipe) {
        return ResponseEntity.ok(projetoEquipeService.buscarPorId(idProjetoEquipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoEquipeModel> atualizar(@PathVariable("id") Long idProjetoEquipe, @RequestBody ProjetoEquipeModel projetoEquipeModel) {
        return ResponseEntity.ok(projetoEquipeService.atualizarProjetoEquipe(idProjetoEquipe, projetoEquipeModel));
    }
}
