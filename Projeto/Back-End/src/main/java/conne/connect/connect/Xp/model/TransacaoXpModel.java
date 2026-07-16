package conne.connect.connect.Xp.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Recompensa.model.RecompensaModel;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Xp.enums.TipoTransacaoXp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
        name = "transacao_xp",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_transacao_xp_tarefa_empresa_usuario_tipo",
                columnNames = {"tarefa_id", "empresa_id", "usu_emp_id", "tipo"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class TransacaoXpModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idTransacaoXp;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private TarefaModel idTarefa;

    @ManyToOne
    @JoinColumn(name = "recompensa_id")
    private RecompensaModel idRecompensa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoTransacaoXp tipo;

    @Column(name = "valor", nullable = false)
    private Integer valor;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criada_em", nullable = false)
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
