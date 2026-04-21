package conne.connect.connect.Controllers;

import conne.connect.connect.Models.TransacaoXpModel;
import conne.connect.connect.Services.TransacaoXpService;
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

@RequestMapping(path = "/api/transacoes-xp")
@RestController
public class TransacaoXpController {

    @Autowired
    private TransacaoXpService transacaoXpService;

    @GetMapping
    public ResponseEntity<List<TransacaoXpModel>> findAll() {
        List<TransacaoXpModel> transacoesXp = transacaoXpService.findAll();
        return ResponseEntity.ok(transacoesXp);
    }

    @PostMapping
    public ResponseEntity<TransacaoXpModel> criarTransacaoXp(@RequestBody TransacaoXpModel transacaoXpModel) {
        TransacaoXpModel transacaoXp = transacaoXpService.criarTransacaoXp(transacaoXpModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transacaoXp.getIdTransacaoXp()).toUri();
        return ResponseEntity.created(uri).body(transacaoXp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idTransacaoXp) {
        transacaoXpService.excluirTransacaoXp(idTransacaoXp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TransacaoXpModel>> buscarPorId(@PathVariable("id") Long idTransacaoXp) {
        return ResponseEntity.ok(transacaoXpService.buscarPorId(idTransacaoXp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoXpModel> atualizar(@PathVariable("id") Long idTransacaoXp, @RequestBody TransacaoXpModel transacaoXpModel) {
        return ResponseEntity.ok(transacaoXpService.atualizarTransacaoXp(idTransacaoXp, transacaoXpModel));
    }
}
