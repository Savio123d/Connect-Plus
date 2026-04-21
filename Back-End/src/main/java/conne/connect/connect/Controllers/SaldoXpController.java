package conne.connect.connect.Controllers;

import conne.connect.connect.Models.SaldoXpModel;
import conne.connect.connect.Services.SaldoXpService;
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

@RequestMapping(path = "/api/saldos-xp")
@RestController
public class SaldoXpController {

    @Autowired
    private SaldoXpService saldoXpService;

    @GetMapping
    public ResponseEntity<List<SaldoXpModel>> findAll() {
        List<SaldoXpModel> saldosXp = saldoXpService.findAll();
        return ResponseEntity.ok(saldosXp);
    }

    @PostMapping
    public ResponseEntity<SaldoXpModel> criarSaldoXp(@RequestBody SaldoXpModel saldoXpModel) {
        SaldoXpModel saldoXp = saldoXpService.criarSaldoXp(saldoXpModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saldoXp.getIdSaldoXp()).toUri();
        return ResponseEntity.created(uri).body(saldoXp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idSaldoXp) {
        saldoXpService.excluirSaldoXp(idSaldoXp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<SaldoXpModel>> buscarPorId(@PathVariable("id") Long idSaldoXp) {
        return ResponseEntity.ok(saldoXpService.buscarPorId(idSaldoXp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaldoXpModel> atualizar(@PathVariable("id") Long idSaldoXp, @RequestBody SaldoXpModel saldoXpModel) {
        return ResponseEntity.ok(saldoXpService.atualizarSaldoXp(idSaldoXp, saldoXpModel));
    }
}
