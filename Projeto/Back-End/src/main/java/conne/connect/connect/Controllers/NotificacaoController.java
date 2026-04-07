package conne.connect.connect.Controllers;

import conne.connect.connect.Models.NotificacaoModel;
import conne.connect.connect.Services.NotificacaoService;
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
@RequestMapping(path = "/api/notificacoes")
@RestController
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoModel>> findAll() {
        List<NotificacaoModel> notificacoes = notificacaoService.findAll();
        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping
    public ResponseEntity<NotificacaoModel> criarNotificacao(@RequestBody NotificacaoModel notificacaoModel) {
        NotificacaoModel notificacao = notificacaoService.criarNotificacao(notificacaoModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(notificacao.getIdNotificacao()).toUri();
        return ResponseEntity.created(uri).body(notificacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idNotificacao) {
        notificacaoService.excluirNotificacao(idNotificacao);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<NotificacaoModel>> buscarPorId(@PathVariable("id") Long idNotificacao) {
        return ResponseEntity.ok(notificacaoService.buscarPorId(idNotificacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacaoModel> atualizar(@PathVariable("id") Long idNotificacao, @RequestBody NotificacaoModel notificacaoModel) {
        return ResponseEntity.ok(notificacaoService.atualizarNotificacao(idNotificacao, notificacaoModel));
    }
}
