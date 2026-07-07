package conne.connect.connect.Feedback.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
        name = "feedback_360_observacao_projeto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_360_observacao_avaliador_rodada",
                columnNames = {"rodada_id", "avaliador_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Feedback360ObservacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idObservacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rodada_id", nullable = false)
    private Feedback360RodadaModel rodada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private ProjetoTelaModel projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliador_id", nullable = false)
    private UsuarioEmpresaModel avaliador;

    @Column(name = "observacao", columnDefinition = "TEXT", nullable = false)
    private String observacao;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    public void prePersist() {
        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }
    }
}
