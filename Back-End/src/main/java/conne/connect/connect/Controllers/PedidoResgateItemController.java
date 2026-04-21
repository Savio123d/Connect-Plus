package conne.connect.connect.Controllers;

import conne.connect.connect.Models.PedidoResgateItemModel;
import conne.connect.connect.Services.PedidoResgateItemService;
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

@RequestMapping(path = "/api/pedidos-resgate-itens")
@RestController
public class PedidoResgateItemController {

    @Autowired
    private PedidoResgateItemService pedidoResgateItemService;

    @GetMapping
    public ResponseEntity<List<PedidoResgateItemModel>> findAll() {
        List<PedidoResgateItemModel> pedidosResgateItem = pedidoResgateItemService.findAll();
        return ResponseEntity.ok(pedidosResgateItem);
    }

    @PostMapping
    public ResponseEntity<PedidoResgateItemModel> criarPedidoResgateItem(@RequestBody PedidoResgateItemModel pedidoResgateItemModel) {
        PedidoResgateItemModel pedidoResgateItem = pedidoResgateItemService.criarPedidoResgateItem(pedidoResgateItemModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pedidoResgateItem.getIdPedidoResgateItem()).toUri();
        return ResponseEntity.created(uri).body(pedidoResgateItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idPedidoResgateItem) {
        pedidoResgateItemService.excluirPedidoResgateItem(idPedidoResgateItem);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PedidoResgateItemModel>> buscarPorId(@PathVariable("id") Long idPedidoResgateItem) {
        return ResponseEntity.ok(pedidoResgateItemService.buscarPorId(idPedidoResgateItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResgateItemModel> atualizar(@PathVariable("id") Long idPedidoResgateItem, @RequestBody PedidoResgateItemModel pedidoResgateItemModel) {
        return ResponseEntity.ok(pedidoResgateItemService.atualizarPedidoResgateItem(idPedidoResgateItem, pedidoResgateItemModel));
    }
}
