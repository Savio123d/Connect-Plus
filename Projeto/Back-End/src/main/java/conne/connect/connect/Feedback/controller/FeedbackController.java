package conne.connect.connect.Feedback.controller;

import conne.connect.connect.Feedback.dto.Feedback360PendenteDTO;
import conne.connect.connect.Feedback.dto.Feedback360RequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackRequestDTO;
import conne.connect.connect.Feedback.dto.FeedbackResponseDTO;
import conne.connect.connect.Feedback.dto.FeedbackResumoDTO;
import conne.connect.connect.Feedback.service.FeedbackService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "http://localhost:4200")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<FeedbackResponseDTO>> listar(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "todos") String filtro
    ) {
        return ResponseEntity.ok(
                feedbackService.listarPorEmpresa(empresaId, filtro)
        );
    }

    @GetMapping("/empresa/{empresaId}/resumo")
    public ResponseEntity<FeedbackResumoDTO> buscarResumo(
            @PathVariable Long empresaId
    ) {
        return ResponseEntity.ok(
                feedbackService.buscarResumo(empresaId)
        );
    }

    @GetMapping("/empresa/{empresaId}/360/pendentes")
    public ResponseEntity<List<Feedback360PendenteDTO>> listarPendentes360(
            @PathVariable Long empresaId,
            @RequestParam Long autorUsuarioEmpresaId
    ) {
        return ResponseEntity.ok(
                feedbackService.listarAvaliacoes360Pendentes(empresaId, autorUsuarioEmpresaId)
        );
    }

    @GetMapping("/empresa/{empresaId}/{idFeedback}")
    public ResponseEntity<FeedbackResponseDTO> buscarPorId(
            @PathVariable Long empresaId,
            @PathVariable Long idFeedback
    ) {
        return ResponseEntity.ok(
                feedbackService.buscarPorId(empresaId, idFeedback)
        );
    }

    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> criarFeedback(
            @RequestBody FeedbackRequestDTO dto
    ) {
        return ResponseEntity.ok(
                feedbackService.criarFeedback(dto)
        );
    }

    @PostMapping("/360")
    public ResponseEntity<FeedbackResponseDTO> criarAvaliacao360(
            @RequestBody Feedback360RequestDTO dto
    ) {
        return ResponseEntity.ok(
                feedbackService.criarAvaliacao360(dto)
        );
    }

    @PutMapping("/{idFeedback}")
    public ResponseEntity<FeedbackResponseDTO> atualizarFeedback(
            @PathVariable Long idFeedback,
            @RequestBody FeedbackRequestDTO dto
    ) {
        return ResponseEntity.ok(
                feedbackService.atualizarFeedback(idFeedback, dto)
        );
    }

    @DeleteMapping("/empresa/{empresaId}/{idFeedback}")
    public ResponseEntity<Void> excluirFeedback(
            @PathVariable Long empresaId,
            @PathVariable Long idFeedback
    ) {
        feedbackService.excluirFeedback(empresaId, idFeedback);
        return ResponseEntity.noContent().build();
    }
}