package conne.connect.connect.Controllers;

import conne.connect.connect.Models.PedidoResgateModel;
import conne.connect.connect.Services.PedidoResgateService;
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
@RequestMapping(path = "/api/pedidos-resgate")
@RestController
public class PedidoResgateController {

    @Autowired
    private PedidoResgateService pedidoResgateService;

    @GetMapping
    public ResponseEntity<List<PedidoResgateModel>> findAll() {
        List<PedidoResgateModel> pedidosResgate = pedidoResgateService.findAll();
        return ResponseEntity.ok(pedidosResgate);
    }

    @PostMapping
    public ResponseEntity<PedidoResgateModel> criarPedidoResgate(@RequestBody PedidoResgateModel pedidoResgateModel) {
        PedidoResgateModel pedidoResgate = pedidoResgateService.criarPedidoResgate(pedidoResgateModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pedidoResgate.getIdPedidoResgate()).toUri();
        return ResponseEntity.created(uri).body(pedidoResgate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idPedidoResgate) {
        pedidoResgateService.excluirPedidoResgate(idPedidoResgate);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PedidoResgateModel>> buscarPorId(@PathVariable("id") Long idPedidoResgate) {
        return ResponseEntity.ok(pedidoResgateService.buscarPorId(idPedidoResgate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResgateModel> atualizar(@PathVariable("id") Long idPedidoResgate, @RequestBody PedidoResgateModel pedidoResgateModel) {
        return ResponseEntity.ok(pedidoResgateService.atualizarPedidoResgate(idPedidoResgate, pedidoResgateModel));
    }
}
