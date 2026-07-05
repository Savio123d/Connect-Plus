package conne.connect.connect.Feedback.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "feedback_360_avaliacao",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_360_avaliacao_par",
                columnNames = {"rodada_id", "avaliador_id", "avaliado_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Feedback360AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idAvaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rodada_id", nullable = false)
    private Feedback360RodadaModel rodada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "projeto_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private ProjetoTelaModel projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliador_id", nullable = false)
    private UsuarioEmpresaModel avaliador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliado_id", nullable = false)
    private UsuarioEmpresaModel avaliado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private FeedbackModel feedback;

    @Column(name = "nota")
    private Integer nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "respondida", nullable = false)
    private Boolean respondida;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @Column(name = "respondida_em")
    private LocalDateTime respondidaEm;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

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

    @PrePersist
    public void prePersist() {
        if (respondida == null) {
            respondida = false;
        }

        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }

        if (ordem == null) {
            ordem = 0;
        }

    }

    @PreUpdate
    public void preUpdate() {
        if (Boolean.TRUE.equals(respondida) && respondidaEm == null) {
            respondidaEm = LocalDateTime.now();
        }
    }
}
