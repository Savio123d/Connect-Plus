package conne.connect.connect.Services;

import conne.connect.connect.Models.FeedbackModel;
import conne.connect.connect.Repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<FeedbackModel> findAll() {
        return feedbackRepository.findAll();
    }

    public FeedbackModel criarFeedback(FeedbackModel feedbackModel) {
        return feedbackRepository.save(feedbackModel);
    }

    public Optional<FeedbackModel> buscarPorId(Long idFeedback) {
        return feedbackRepository.findById(idFeedback);
    }

    public FeedbackModel atualizarFeedback(Long idFeedback, FeedbackModel feedbackModel) {
        FeedbackModel feedback = feedbackRepository.findById(idFeedback).get();
        feedback.setIdEmpresa(feedbackModel.getIdEmpresa());
        feedback.setIdAutorUsuarioEmpresa(feedbackModel.getIdAutorUsuarioEmpresa());
        feedback.setIdProjeto(feedbackModel.getIdProjeto());
        feedback.setIdTarefa(feedbackModel.getIdTarefa());
        feedback.setNota(feedbackModel.getNota());
        feedback.setComentario(feedbackModel.getComentario());
        return feedbackRepository.save(feedback);
    }

    public void excluirFeedback(Long idFeedback) {
        feedbackRepository.deleteById(idFeedback);
    }
}
