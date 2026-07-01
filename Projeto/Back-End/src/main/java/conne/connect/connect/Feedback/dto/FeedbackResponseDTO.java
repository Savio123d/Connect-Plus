package conne.connect.connect.Feedback.dto;

import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Feedback.model.FeedbackModel;

import java.time.LocalDateTime;

public class FeedbackResponseDTO {

    private Long idFeedback;

    private Long empresaId;

    private Long autorUsuarioEmpresaId;
    private String autorNome;
    private String autorEmail;

    private Long destinatarioUsuarioEmpresaId;
    private String destinatarioNome;
    private String destinatarioEmail;

    private Long projetoId;
    private String projetoNome;

    private Long tarefaId;
    private String tarefaTitulo;

    private Integer nota;
    private String comentario;
    private FeedbackClassificacao classificacao;
    private String categoria;

    private Boolean avaliacao360;
    private Integer comprometimento;
    private Integer nivelEntregas;
    private Integer colaboracao;
    private Integer comunicacao;

    private LocalDateTime dataCriacao;

    public static FeedbackResponseDTO fromModel(FeedbackModel feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();

        dto.setIdFeedback(feedback.getIdFeedback());

        if (feedback.getIdEmpresa() != null) {
            dto.setEmpresaId(feedback.getIdEmpresa().getIdEmpresa());
        }

        if (feedback.getIdAutorUsuarioEmpresa() != null) {
            dto.setAutorUsuarioEmpresaId(feedback.getIdAutorUsuarioEmpresa().getIdUsuarioEmpresa());

            if (feedback.getIdAutorUsuarioEmpresa().getIdUsuario() != null) {
                dto.setAutorNome(feedback.getIdAutorUsuarioEmpresa().getIdUsuario().getNome());
                dto.setAutorEmail(feedback.getIdAutorUsuarioEmpresa().getIdUsuario().getEmail());
            }
        }

        if (feedback.getIdDestinatarioUsuarioEmpresa() != null) {
            dto.setDestinatarioUsuarioEmpresaId(
                    feedback.getIdDestinatarioUsuarioEmpresa().getIdUsuarioEmpresa()
            );

            if (feedback.getIdDestinatarioUsuarioEmpresa().getIdUsuario() != null) {
                dto.setDestinatarioNome(feedback.getIdDestinatarioUsuarioEmpresa().getIdUsuario().getNome());
                dto.setDestinatarioEmail(feedback.getIdDestinatarioUsuarioEmpresa().getIdUsuario().getEmail());
            }
        }

        if (feedback.getIdProjeto() != null) {
            dto.setProjetoId(feedback.getIdProjeto().getIdProjeto());
            dto.setProjetoNome(feedback.getIdProjeto().getNome());
        }

        if (feedback.getIdTarefa() != null) {
            dto.setTarefaId(feedback.getIdTarefa().getIdTarefa());
            dto.setTarefaTitulo(feedback.getIdTarefa().getTitulo());
        }

        dto.setNota(feedback.getNota());
        dto.setComentario(feedback.getComentario());
        dto.setClassificacao(feedback.getClassificacao());
        dto.setCategoria(feedback.getCategoria());
        dto.setAvaliacao360(feedback.getAvaliacao360());
        dto.setComprometimento(feedback.getComprometimento());
        dto.setNivelEntregas(feedback.getNivelEntregas());
        dto.setColaboracao(feedback.getColaboracao());
        dto.setComunicacao(feedback.getComunicacao());
        dto.setDataCriacao(feedback.getDataCriacao());

        return dto;
    }

    public Long getIdFeedback() {
        return idFeedback;
    }

    public void setIdFeedback(Long idFeedback) {
        this.idFeedback = idFeedback;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getAutorUsuarioEmpresaId() {
        return autorUsuarioEmpresaId;
    }

    public void setAutorUsuarioEmpresaId(Long autorUsuarioEmpresaId) {
        this.autorUsuarioEmpresaId = autorUsuarioEmpresaId;
    }

    public String getAutorNome() {
        return autorNome;
    }

    public void setAutorNome(String autorNome) {
        this.autorNome = autorNome;
    }

    public String getAutorEmail() {
        return autorEmail;
    }

    public void setAutorEmail(String autorEmail) {
        this.autorEmail = autorEmail;
    }

    public Long getDestinatarioUsuarioEmpresaId() {
        return destinatarioUsuarioEmpresaId;
    }

    public void setDestinatarioUsuarioEmpresaId(Long destinatarioUsuarioEmpresaId) {
        this.destinatarioUsuarioEmpresaId = destinatarioUsuarioEmpresaId;
    }

    public String getDestinatarioNome() {
        return destinatarioNome;
    }

    public void setDestinatarioNome(String destinatarioNome) {
        this.destinatarioNome = destinatarioNome;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public void setDestinatarioEmail(String destinatarioEmail) {
        this.destinatarioEmail = destinatarioEmail;
    }

    public Long getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(Long projetoId) {
        this.projetoId = projetoId;
    }

    public String getProjetoNome() {
        return projetoNome;
    }

    public void setProjetoNome(String projetoNome) {
        this.projetoNome = projetoNome;
    }

    public Long getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(Long tarefaId) {
        this.tarefaId = tarefaId;
    }

    public String getTarefaTitulo() {
        return tarefaTitulo;
    }

    public void setTarefaTitulo(String tarefaTitulo) {
        this.tarefaTitulo = tarefaTitulo;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public FeedbackClassificacao getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(FeedbackClassificacao classificacao) {
        this.classificacao = classificacao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getAvaliacao360() {
        return avaliacao360;
    }

    public void setAvaliacao360(Boolean avaliacao360) {
        this.avaliacao360 = avaliacao360;
    }

    public Integer getComprometimento() {
        return comprometimento;
    }

    public void setComprometimento(Integer comprometimento) {
        this.comprometimento = comprometimento;
    }

    public Integer getNivelEntregas() {
        return nivelEntregas;
    }

    public void setNivelEntregas(Integer nivelEntregas) {
        this.nivelEntregas = nivelEntregas;
    }

    public Integer getColaboracao() {
        return colaboracao;
    }

    public void setColaboracao(Integer colaboracao) {
        this.colaboracao = colaboracao;
    }

    public Integer getComunicacao() {
        return comunicacao;
    }

    public void setComunicacao(Integer comunicacao) {
        this.comunicacao = comunicacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}