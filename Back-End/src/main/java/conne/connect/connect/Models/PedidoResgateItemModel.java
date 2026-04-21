package conne.connect.connect.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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

    @Column(name = "pedido_resgate_id", nullable = false)
    private Long idPedidoResgate;

    @Column(name = "recompensa_id", nullable = false)
    private Long idRecompensa;

    @Column(name = "xp_unitario", nullable = false)
    private Integer xpUnitario;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @PrePersist
    public void prePersist() {
        if (quantidade == null) {
            quantidade = 1;
        }
    }
}
