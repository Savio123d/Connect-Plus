package conne.connect.connect.Recompensa.controller;

import conne.connect.connect.Recompensa.dto.LojaItemDTO;
import conne.connect.connect.Recompensa.dto.LojaItemRequestDTO;
import conne.connect.connect.Recompensa.dto.LojaResgateRequestDTO;
import conne.connect.connect.Recompensa.service.RecompensaService;
import java.net.URI;
import java.util.List;
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

@RequestMapping(path = "/api/lojas")
@RestController
public class LojaController {

    private final RecompensaService recompensaService;

    public LojaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    @GetMapping
    public ResponseEntity<List<LojaItemDTO>> listar(
            @RequestParam("empresaId") Long empresaId,
            @RequestParam(value = "usuarioEmpresaId", required = false) Long usuarioEmpresaId,
            @RequestParam(value = "somenteAtivas", defaultValue = "false") boolean somenteAtivas
    ) {
        return ResponseEntity.ok(recompensaService.listarItensLoja(empresaId, usuarioEmpresaId, somenteAtivas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaItemDTO> buscar(
            @PathVariable("id") Long id,
            @RequestParam("empresaId") Long empresaId,
            @RequestParam(value = "usuarioEmpresaId", required = false) Long usuarioEmpresaId
    ) {
        return ResponseEntity.ok(recompensaService.buscarItemLoja(id, empresaId, usuarioEmpresaId));
    }

    @PostMapping
    public ResponseEntity<LojaItemDTO> criar(@RequestBody LojaItemRequestDTO request) {
        LojaItemDTO item = recompensaService.criarItemLoja(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(item.getId()).toUri();
        return ResponseEntity.created(uri).body(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaItemDTO> atualizar(@PathVariable("id") Long id, @RequestBody LojaItemRequestDTO request) {
        return ResponseEntity.ok(recompensaService.atualizarItemLoja(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable("id") Long id,
            @RequestParam("empresaId") Long empresaId
    ) {
        recompensaService.excluirItemLoja(id, empresaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/esgotar")
    public ResponseEntity<LojaItemDTO> esgotar(
            @PathVariable("id") Long id,
            @RequestParam("empresaId") Long empresaId
    ) {
        return ResponseEntity.ok(recompensaService.esgotarItemLoja(id, empresaId));
    }

    @PatchMapping("/{id}/repor/{quantidade}")
    public ResponseEntity<LojaItemDTO> repor(
            @PathVariable("id") Long id,
            @PathVariable("quantidade") Integer quantidade,
            @RequestParam("empresaId") Long empresaId
    ) {
        return ResponseEntity.ok(recompensaService.reporItemLoja(id, empresaId, quantidade));
    }

    @PostMapping("/{id}/resgatar")
    public ResponseEntity<LojaItemDTO> resgatar(@PathVariable("id") Long id, @RequestBody LojaResgateRequestDTO request) {
        return ResponseEntity.ok(recompensaService.resgatarItemLoja(id, request));
    }
}
