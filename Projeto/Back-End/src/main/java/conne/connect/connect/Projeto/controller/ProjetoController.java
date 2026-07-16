package conne.connect.connect.Projeto.controller;

import conne.connect.connect.Projeto.dto.ProjetoRequestDTO;
import conne.connect.connect.Projeto.dto.ProjetoResponseDTO;
import conne.connect.connect.Projeto.dto.ProjetoResumoDTO;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.service.ProjetoTelaService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoTelaService projetoTelaService;

    public ProjetoController(ProjetoTelaService projetoTelaService) {
        this.projetoTelaService = projetoTelaService;
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar(@RequestParam Long empresaId) {
        return ResponseEntity.ok(projetoTelaService.listar(empresaId));
    }

    @GetMapping("/resumos")
    public ResponseEntity<List<ProjetoResumoDTO>> listarResumos(@RequestParam Long empresaId) {
        return ResponseEntity.ok(projetoTelaService.listarResumos(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(
        @PathVariable Long id,
        @RequestParam Long empresaId
    ) {
        return ResponseEntity.ok(projetoTelaService.buscarPorId(id, empresaId));
    }

    @GetMapping("/usuarios-disponiveis")
    public ResponseEntity<List<ProjetoResponseDTO.PessoaDTO>> listarUsuariosDisponiveis(@RequestParam Long empresaId) {
        return ResponseEntity.ok(projetoTelaService.listarUsuariosDisponiveis(empresaId));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criar(@RequestBody ProjetoRequestDTO request) {
        ProjetoResponseDTO projetoCriado = projetoTelaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoCriado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjetoResponseDTO> atualizarStatus(
        @PathVariable Long id,
        @RequestBody ProjetoRequestDTO request
    ) {
        return ResponseEntity.ok(projetoTelaService.atualizarStatus(id, request.status()));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ProjetoResponseDTO> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(projetoTelaService.atualizarStatus(id, ProjetoStatusTela.concluido.getValor()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        projetoTelaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membros")
    public ResponseEntity<ProjetoResponseDTO> adicionarMembro(
        @PathVariable Long id,
        @RequestBody ProjetoRequestDTO request
    ) {
        return ResponseEntity.ok(projetoTelaService.adicionarMembro(id, request.usuarioId()));
    }

    @PostMapping("/{id}/marcos")
    public ResponseEntity<ProjetoResponseDTO> adicionarMarco(
        @PathVariable Long id,
        @RequestBody ProjetoRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoTelaService.adicionarMarco(id, request));
    }

    @PostMapping("/{id}/tarefas")
    public ResponseEntity<ProjetoResponseDTO> adicionarTarefa(
        @PathVariable Long id,
        @RequestBody ProjetoRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoTelaService.adicionarTarefa(id, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarErroRegra(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}
