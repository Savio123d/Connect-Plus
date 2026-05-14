package conne.connect.connect.Controllers;

import conne.connect.connect.Dto.TarefaRequestDTO;
import conne.connect.connect.Dto.TarefaResponseDTO;
import conne.connect.connect.Dto.TarefaStatusDTO;
import conne.connect.connect.Models.TarefaModel;
import conne.connect.connect.Services.TarefaService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequestMapping(path = "/api/tarefas")
@RestController
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> findAll() {
        List<TarefaResponseDTO> tarefas = tarefaService.findAll()
                .stream()
                .map(TarefaResponseDTO::new)
                .toList();

        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(@PathVariable("id") Long idTarefa) {
        TarefaModel tarefa = tarefaService.buscarPorId(idTarefa);
        return ResponseEntity.ok(new TarefaResponseDTO(tarefa));
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(@RequestBody TarefaRequestDTO tarefaRequestDTO) {
        TarefaModel tarefa = tarefaService.criarTarefa(tarefaRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarefa.getIdTarefa())
                .toUri();

        return ResponseEntity.created(uri).body(new TarefaResponseDTO(tarefa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable("id") Long idTarefa,
            @RequestBody TarefaRequestDTO tarefaRequestDTO
    ) {
        TarefaModel tarefa = tarefaService.atualizarTarefa(idTarefa, tarefaRequestDTO);
        return ResponseEntity.ok(new TarefaResponseDTO(tarefa));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaResponseDTO> atualizarStatus(
            @PathVariable("id") Long idTarefa,
            @RequestBody TarefaStatusDTO tarefaStatusDTO
    ) {
        TarefaModel tarefa = tarefaService.atualizarStatus(idTarefa, tarefaStatusDTO.getStatus());
        return ResponseEntity.ok(new TarefaResponseDTO(tarefa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long idTarefa) {
        tarefaService.excluirTarefa(idTarefa);
        return ResponseEntity.noContent().build();
    }
}