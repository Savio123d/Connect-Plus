package conne.connect.connect.Controllers;

import conne.connect.connect.Models.ComentarioTarefaModel;
import conne.connect.connect.Services.ComentarioTarefaService;
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

@RequestMapping(path = "/api/comentarios-tarefa")
@RestController
public class ComentarioTarefaController {

    @Autowired
    private ComentarioTarefaService comentarioTarefaService;

    @GetMapping
    public ResponseEntity<List<ComentarioTarefaModel>> findAll() {
        List<ComentarioTarefaModel> comentariosTarefa = comentarioTarefaService.findAll();
        return ResponseEntity.ok(comentariosTarefa);
    }

    @PostMapping
    public ResponseEntity<ComentarioTarefaModel> criarComentarioTarefa(@RequestBody ComentarioTarefaModel comentarioTarefaModel) {
        ComentarioTarefaModel comentarioTarefa = comentarioTarefaService.criarComentarioTarefa(comentarioTarefaModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(comentarioTarefa.getIdComentarioTarefa()).toUri();
        return ResponseEntity.created(uri).body(comentarioTarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idComentarioTarefa) {
        comentarioTarefaService.excluirComentarioTarefa(idComentarioTarefa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ComentarioTarefaModel>> buscarPorId(@PathVariable("id") Long idComentarioTarefa) {
        return ResponseEntity.ok(comentarioTarefaService.buscarPorId(idComentarioTarefa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioTarefaModel> atualizar(@PathVariable("id") Long idComentarioTarefa, @RequestBody ComentarioTarefaModel comentarioTarefaModel) {
        return ResponseEntity.ok(comentarioTarefaService.atualizarComentarioTarefa(idComentarioTarefa, comentarioTarefaModel));
    }
}
