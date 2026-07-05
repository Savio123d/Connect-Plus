package conne.connect.connect.Feedback.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "feedback_360_rodada",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_360_rodada_projeto",
                columnNames = {"projeto_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Feedback360RodadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idRodada;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel empresa;

    @ManyToOne
    @JoinColumn(
            name = "projeto_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private ProjetoTelaModel projeto;

    @Column(name = "aberta_em", nullable = false)
    private LocalDateTime abertaEm;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa;

    @Column(name = "obrigatoria", nullable = false)
    private Boolean obrigatoria;

    @Column(name = "concluida", nullable = false)
    private Boolean concluida;

    @Column(name = "concluida_em")
    private LocalDateTime concluidaEm;

    @PrePersist
    public void prePersist() {
        if (abertaEm == null) {
            abertaEm = LocalDateTime.now();
        }
        if (ativa == null) {
            ativa = true;
        }
        if (obrigatoria == null) {
            obrigatoria = false;
        }
        if (concluida == null) {
            concluida = false;
        }
    }
}
