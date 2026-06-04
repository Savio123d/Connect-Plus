package conne.connect.connect.Controllers;

import conne.connect.connect.Models.LojaModel;
import conne.connect.connect.Services.LojaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequestMapping(path = "/api/lojas")
@RestController
public class LojaController {

    @Autowired
    private LojaService lojaService;

    @GetMapping
    public ResponseEntity<List<LojaModel>> findAll(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false, defaultValue = "true") Boolean somenteAtivas
    ) {
        List<LojaModel> lojas;

        if (empresaId != null) {
            lojas = lojaService.listarPorEmpresa(empresaId, somenteAtivas);
        } else if (Boolean.TRUE.equals(somenteAtivas)) {
            lojas = lojaService.listarAtivas();
        } else {
            lojas = lojaService.findAll();
        }

        return ResponseEntity.ok(lojas);
    }

    @PostMapping
    public ResponseEntity<LojaModel> criarLoja(@RequestBody LojaModel lojaModel) {
        LojaModel loja = lojaService.criarLoja(lojaModel);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(loja.getIdLoja())
                .toUri();

        return ResponseEntity.created(uri).body(loja);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaModel> buscarPorId(@PathVariable("id") Long idLoja) {
        return ResponseEntity.ok(lojaService.buscarPorId(idLoja));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaModel> atualizar(
            @PathVariable("id") Long idLoja,
            @RequestBody LojaModel lojaModel
    ) {
        return ResponseEntity.ok(lojaService.atualizarLoja(idLoja, lojaModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long idLoja) {
        lojaService.excluirLoja(idLoja);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/esgotar")
    public ResponseEntity<LojaModel> esgotar(@PathVariable("id") Long idLoja) {
        return ResponseEntity.ok(lojaService.esgotarLoja(idLoja));
    }

    @PatchMapping("/{id}/repor")
    public ResponseEntity<LojaModel> repor(@PathVariable("id") Long idLoja) {
        return ResponseEntity.ok(lojaService.reporLoja(idLoja));
    }

    @PatchMapping("/{id}/repor/{quantidade}")
    public ResponseEntity<LojaModel> reporComQuantidade(
            @PathVariable("id") Long idLoja,
            @PathVariable("quantidade") Integer quantidade
    ) {

        return ResponseEntity.ok(lojaService.reporLoja(idLoja));
    }

    @PostMapping("/{id}/resgatar")
    public ResponseEntity<LojaModel> resgatar(@PathVariable("id") Long idLoja) {
        return ResponseEntity.ok(lojaService.resgatarLoja(idLoja));
    }
}