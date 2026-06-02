package conne.connect.connect.Controllers;

import conne.connect.connect.Models.RecompensaModel;
import conne.connect.connect.Services.RecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/recompensas")
@RestController
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    @GetMapping
    public ResponseEntity<List<RecompensaModel>> findAll() {
        List<RecompensaModel> recompensas = recompensaService.findAll();
        return ResponseEntity.ok(recompensas);
    }

    @PostMapping
    public ResponseEntity<RecompensaModel> criarRecompensa(@RequestBody RecompensaModel recompensaModel) {
        RecompensaModel recompensa = recompensaService.criarRecompensa(recompensaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(recompensa.getIdRecompensa()).toUri();
        return ResponseEntity.created(uri).body(recompensa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idRecompensa) {
        recompensaService.excluirRecompensa(idRecompensa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<RecompensaModel>> buscarPorId(@PathVariable("id") Long idRecompensa) {
        return ResponseEntity.ok(recompensaService.buscarPorId(idRecompensa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecompensaModel> atualizar(@PathVariable("id") Long idRecompensa, @RequestBody RecompensaModel recompensaModel) {
        return ResponseEntity.ok(recompensaService.atualizarRecompensa(idRecompensa, recompensaModel));
    }
}
