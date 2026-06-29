package conne.connect.connect.Resgate.model;

import conne.connect.connect.Recompensa.model.RecompensaModel;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_resgate_item")
@Getter
@Setter
@NoArgsConstructor
public class PedidoResgateItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idPedidoResgateItem;

    @ManyToOne
    @JoinColumn(name = "resgate_id", nullable = false)
    private PedidoResgateModel idPedidoResgate;

    @ManyToOne
    @JoinColumn(name = "recompensa_id", nullable = false)
    private RecompensaModel idRecompensa;

    @Column(name = "xp_unit", nullable = false)
    private Integer xpUnitario;

    @Column(name = "qtd", nullable = false)
    private Integer quantidade;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (quantidade == null) {
            quantidade = 1;
        }
    }
}
