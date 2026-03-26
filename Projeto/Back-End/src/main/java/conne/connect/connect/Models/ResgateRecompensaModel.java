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

import java.time.LocalDateTime;

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

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "usuario_empresa_id", nullable = false)
    private Long idUsuarioEmpresa;

    @Column(name = "recompensa_id", nullable = false)
    private Long idRecompensa;

    @Column(name = "transacao_xp_id")
    private Long idTransacaoXp;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "xp_gasto", nullable = false)
    private Integer xpGasto;

    @Column(name = "resgatado_em", nullable = false)
    private LocalDateTime dataResgate;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

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
