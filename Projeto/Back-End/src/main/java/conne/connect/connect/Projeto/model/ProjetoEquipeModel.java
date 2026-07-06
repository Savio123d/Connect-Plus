package conne.connect.connect.Projeto.model;

import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import conne.connect.connect.Equipe.model.EquipeModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "projeto_equipe",
        uniqueConstraints = @UniqueConstraint(name = "uk_projeto_equipe_projeto_equipe", columnNames = {"projeto_id", "equipe_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ProjetoEquipeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idProjetoEquipe;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private ProjetoModel idProjeto;

    @ManyToOne
    @JoinColumn(name = "equipe_id", nullable = false)
    private EquipeModel idEquipe;

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
    }
}
