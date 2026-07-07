package conne.connect.connect.Equipe.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
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
        name = "equipe_membro",
        uniqueConstraints = @UniqueConstraint(name = "uk_equipe_membro_equipe_usu_emp", columnNames = {"equipe_id", "usu_emp_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class EquipeMembroModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idEquipeMembro;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "equipe_id", nullable = false)
    private EquipeModel idEquipe;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

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
