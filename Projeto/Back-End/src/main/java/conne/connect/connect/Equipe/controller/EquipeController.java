package conne.connect.connect.Equipe.controller;

import conne.connect.connect.Equipe.model.EquipeModel;
import conne.connect.connect.Equipe.service.EquipeService;
import java.net.URI;
import java.util.List;
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

@RequestMapping(path = "/api/equipes")
@RestController
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping
    public ResponseEntity<List<EquipeModel>> findAll() {
        List<EquipeModel> equipes = equipeService.findAll();
        return ResponseEntity.ok(equipes);
    }

    @PostMapping
    public ResponseEntity<EquipeModel> criarEquipe(@RequestBody EquipeModel equipeModel) {
        EquipeModel equipe = equipeService.criarEquipe(equipeModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(equipe.getIdEquipe()).toUri();
        return ResponseEntity.created(uri).body(equipe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idEquipe) {
        equipeService.excluirEquipe(idEquipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeModel> buscarPorId(@PathVariable("id") Long idEquipe) {
        return ResponseEntity.of(equipeService.buscarPorId(idEquipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeModel> atualizar(@PathVariable("id") Long idEquipe, @RequestBody EquipeModel equipeModel) {
        return ResponseEntity.ok(equipeService.atualizarEquipe(idEquipe, equipeModel));
    }
}
