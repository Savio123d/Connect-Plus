package conne.connect.connect.Controllers;

import conne.connect.connect.Models.TarefaModel;
import conne.connect.connect.Services.TarefaService;
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

@RequestMapping(path = "/api/tarefas")
@RestController
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public ResponseEntity<List<TarefaModel>> findAll() {
        List<TarefaModel> tarefas = tarefaService.findAll();
        return ResponseEntity.ok(tarefas);
    }

    @PostMapping
    public ResponseEntity<TarefaModel> criarTarefa(@RequestBody TarefaModel tarefaModel) {
        TarefaModel tarefa = tarefaService.criarTarefa(tarefaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tarefa.getIdTarefa()).toUri();
        return ResponseEntity.created(uri).body(tarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idTarefa) {
        tarefaService.excluirTarefa(idTarefa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TarefaModel>> buscarPorId(@PathVariable("id") Long idTarefa) {
        return ResponseEntity.ok(tarefaService.buscarPorId(idTarefa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaModel> atualizar(@PathVariable("id") Long idTarefa, @RequestBody TarefaModel tarefaModel) {
        return ResponseEntity.ok(tarefaService.atualizarTarefa(idTarefa, tarefaModel));
    }
}
