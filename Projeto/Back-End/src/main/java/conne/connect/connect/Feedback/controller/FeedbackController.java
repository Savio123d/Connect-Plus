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
import conne.connect.connect.Feedback.dto.Feedback360GestorDTO;
import conne.connect.connect.Feedback.dto.Feedback360ObrigatorioDTO;
import conne.connect.connect.Feedback.dto.Feedback360ObservacaoRequestDTO;
import conne.connect.connect.Feedback.dto.Feedback360StatusDTO;
import conne.connect.connect.Feedback.dto.Feedback360UsuarioDTO;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    @GetMapping("/360/status")
    public ResponseEntity<Feedback360StatusDTO> buscarStatus360(
            @RequestParam Long empresaId,
            @RequestParam Long usuarioEmpresaId
    ) {
        return ResponseEntity.ok(feedbackService.buscarStatus360(empresaId, usuarioEmpresaId));
    }

    @PatchMapping("/360/projeto/{projetoId}/obrigatoriedade")
    public ResponseEntity<Void> definirObrigatoriedade360(
            @PathVariable Long projetoId,
            @RequestBody Feedback360ObrigatorioDTO dto
    ) {
        feedbackService.definirObrigatoriedadeProjeto360(projetoId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/360/rodadas/{rodadaId}/observacao")
    public ResponseEntity<Void> salvarObservacaoProjeto360(
            @PathVariable Long rodadaId,
            @RequestBody Feedback360ObservacaoRequestDTO dto
    ) {
        feedbackService.salvarObservacaoProjeto360(rodadaId, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/360/usuario/cards")
    public ResponseEntity<List<Feedback360UsuarioDTO>> listarCardsUsuario360(
            @RequestParam Long empresaId,
            @RequestParam Long usuarioEmpresaId
    ) {
        return ResponseEntity.ok(feedbackService.listarCardsUsuario360(empresaId, usuarioEmpresaId));
    }

    @GetMapping("/360/gestor")
    public ResponseEntity<List<Feedback360GestorDTO>> listarResumoGestor360(
            @RequestParam Long empresaId,
            @RequestParam Long gestorUsuarioEmpresaId
    ) {
        return ResponseEntity.ok(feedbackService.listarResumoGestor360(empresaId, gestorUsuarioEmpresaId));
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


    @GetMapping("/empresa/{empresaId}/destinatario/{destinatarioUsuarioEmpresaId}")
    public ResponseEntity<List<FeedbackResponseDTO>> listarPorDestinatario(
            @PathVariable Long empresaId,
            @PathVariable Long destinatarioUsuarioEmpresaId,
            @RequestParam(defaultValue = "todos") String filtro
    ) {
        return ResponseEntity.ok(
                feedbackService.listarPorDestinatario(
                        empresaId,
                        destinatarioUsuarioEmpresaId,
                        filtro
                )
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
