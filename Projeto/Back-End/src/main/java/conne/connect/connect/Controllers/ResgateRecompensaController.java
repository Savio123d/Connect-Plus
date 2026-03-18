package conne.connect.connect.Controllers;

import conne.connect.connect.Models.ResgateRecompensaModel;
import conne.connect.connect.Services.ResgateRecompensaService;
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

@RequestMapping(path = "/api/resgates-recompensa")
@RestController
public class ResgateRecompensaController {

    @Autowired
    private ResgateRecompensaService resgateRecompensaService;

    @GetMapping
    public ResponseEntity<List<ResgateRecompensaModel>> findAll() {
        List<ResgateRecompensaModel> resgatesRecompensa = resgateRecompensaService.findAll();
        return ResponseEntity.ok(resgatesRecompensa);
    }

    @PostMapping
    public ResponseEntity<ResgateRecompensaModel> criarResgateRecompensa(@RequestBody ResgateRecompensaModel resgateRecompensaModel) {
        ResgateRecompensaModel resgateRecompensa = resgateRecompensaService.criarResgateRecompensa(resgateRecompensaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resgateRecompensa.getIdResgateRecompensa()).toUri();
        return ResponseEntity.created(uri).body(resgateRecompensa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idResgateRecompensa) {
        resgateRecompensaService.excluirResgateRecompensa(idResgateRecompensa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ResgateRecompensaModel>> buscarPorId(@PathVariable("id") Long idResgateRecompensa) {
        return ResponseEntity.ok(resgateRecompensaService.buscarPorId(idResgateRecompensa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResgateRecompensaModel> atualizar(@PathVariable("id") Long idResgateRecompensa, @RequestBody ResgateRecompensaModel resgateRecompensaModel) {
        return ResponseEntity.ok(resgateRecompensaService.atualizarResgateRecompensa(idResgateRecompensa, resgateRecompensaModel));
    }
}
