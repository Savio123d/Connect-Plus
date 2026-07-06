package conne.connect.connect.Resgate.model;

import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import conne.connect.connect.Recompensa.model.RecompensaModel;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "resgate_recompensa")
@Getter
@Setter
@NoArgsConstructor
public class ResgateRecompensaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idResgateRecompensa;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(name = "recompensa_id", nullable = false)
    private RecompensaModel idRecompensa;

    @ManyToOne
    @JoinColumn(name = "transacao_id")
    private TransacaoXpModel idTransacaoXp;

    @Column(name = "qtd", nullable = false)
    private Integer quantidade;

    @Column(name = "xp_gasto", nullable = false)
    private Integer xpGasto;

    @Column(name = "resgatado_em", nullable = false)
    private LocalDateTime dataResgate;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (quantidade == null) {
            quantidade = 1;
        }

        if (status == null) {
            status = "resgatado";
        }

        if (dataResgate == null) {
            dataResgate = LocalDateTime.now();
        }
    }
}
