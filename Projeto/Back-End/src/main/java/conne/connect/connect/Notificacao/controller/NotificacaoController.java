package conne.connect.connect.Notificacao.controller;

import conne.connect.connect.Notificacao.dto.NotificacaoResponseDTO;
import conne.connect.connect.Notificacao.model.NotificacaoModel;
import conne.connect.connect.Notificacao.service.NotificacaoService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<NotificacaoModel> criarNotificacao(
            @RequestBody NotificacaoModel notificacaoModel
    ) {
        NotificacaoModel notificacao = notificacaoService.criarNotificacao(notificacaoModel);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(notificacao.getIdNotificacao())
                .toUri();

        return ResponseEntity.created(uri).body(notificacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long idNotificacao) {
        notificacaoService.excluirNotificacao(idNotificacao);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<NotificacaoModel>> buscarPorId(
            @PathVariable("id") Long idNotificacao
    ) {
        return ResponseEntity.ok(notificacaoService.buscarPorId(idNotificacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacaoModel> atualizar(
            @PathVariable("id") Long idNotificacao,
            @RequestBody NotificacaoModel notificacaoModel
    ) {
        return ResponseEntity.ok(
                notificacaoService.atualizarNotificacao(idNotificacao, notificacaoModel)
        );
    }

    @GetMapping("/usuario-empresa/{idUsuarioEmpresa}")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarPorUsuarioEmpresa(
            @PathVariable Long idUsuarioEmpresa
    ) {
        return ResponseEntity.ok(
                notificacaoService.buscarPorUsuarioEmpresa(idUsuarioEmpresa)
        );
    }

    @GetMapping("/usuario-empresa/{idUsuarioEmpresa}/ultimas")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarUltimasPorUsuarioEmpresa(
            @PathVariable Long idUsuarioEmpresa
    ) {
        return ResponseEntity.ok(
                notificacaoService.buscarUltimasPorUsuarioEmpresa(idUsuarioEmpresa)
        );
    }

    @GetMapping("/usuario-empresa/{idUsuarioEmpresa}/nao-lidas/quantidade")
    public ResponseEntity<Map<String, Long>> contarNaoLidasPorUsuarioEmpresa(
            @PathVariable Long idUsuarioEmpresa
    ) {
        long quantidade = notificacaoService.contarNaoLidasPorUsuarioEmpresa(idUsuarioEmpresa);

        return ResponseEntity.ok(
                Map.of("quantidade", quantidade)
        );
    }

    @PatchMapping("/{id}/marcar-como-lida")
    public ResponseEntity<NotificacaoResponseDTO> marcarComoLida(
            @PathVariable("id") Long idNotificacao
    ) {
        return ResponseEntity.ok(
                notificacaoService.marcarComoLida(idNotificacao)
        );
    }
}
