package conne.connect.connect.Feedback.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Feedback.enums.FeedbackClassificacao;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
public class FeedbackModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idFeedback;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioEmpresaModel idAutorUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private UsuarioEmpresaModel idDestinatarioUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(
            name = "projeto_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private ProjetoTelaModel idProjeto;

    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private TarefaModel idTarefa;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao", length = 20)
    private FeedbackClassificacao classificacao;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "avaliacao_360", nullable = false)
    private Boolean avaliacao360;

    @Column(name = "assiduidade")
    private Integer assiduidade;

    @Column(name = "nivel_entregas")
    private Integer nivelEntregas;

    @Column(name = "comunicacao")
    private Integer comunicacao;

    @Column(name = "colaboracao")
    private Integer colaboracao;

    @Column(name = "comprometimento")
    private Integer comprometimento;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }

        if (incluido == null) {
            incluido = LocalDate.now();
        }

        if (avaliacao360 == null) {
            avaliacao360 = false;
        }

        if (!Boolean.TRUE.equals(avaliacao360) && classificacao == null) {
            classificacao = FeedbackClassificacao.MEDIANO;
        }

        if (categoria == null || categoria.isBlank()) {
            categoria = Boolean.TRUE.equals(avaliacao360) ? "Avaliação 360°" : "Geral";
        }

        if (comentario == null) {
            comentario = "";
        }

        if (nota == null) {
            nota = notaPadraoPorClassificacao(classificacao);
        }
    }

    private Integer notaPadraoPorClassificacao(FeedbackClassificacao classificacao) {
        if (classificacao == null) {
            return 3;
        }

        return switch (classificacao) {
            case POSITIVO -> 5;
            case MEDIANO -> 3;
            case NEGATIVO -> 1;
        };
    }
}