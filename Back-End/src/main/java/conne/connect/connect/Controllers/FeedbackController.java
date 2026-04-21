package conne.connect.connect.Controllers;

import conne.connect.connect.Models.FeedbackModel;
import conne.connect.connect.Services.FeedbackService;
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

@RequestMapping(path = "/api/feedbacks")
@RestController
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<FeedbackModel>> findAll() {
        List<FeedbackModel> feedbacks = feedbackService.findAll();
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping
    public ResponseEntity<FeedbackModel> criarFeedback(@RequestBody FeedbackModel feedbackModel) {
        FeedbackModel feedback = feedbackService.criarFeedback(feedbackModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(feedback.getIdFeedback()).toUri();
        return ResponseEntity.created(uri).body(feedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idFeedback) {
        feedbackService.excluirFeedback(idFeedback);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<FeedbackModel>> buscarPorId(@PathVariable("id") Long idFeedback) {
        return ResponseEntity.ok(feedbackService.buscarPorId(idFeedback));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackModel> atualizar(@PathVariable("id") Long idFeedback, @RequestBody FeedbackModel feedbackModel) {
        return ResponseEntity.ok(feedbackService.atualizarFeedback(idFeedback, feedbackModel));
    }
}
