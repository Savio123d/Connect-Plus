package conne.connect.connect.Models;

import conne.connect.connect.Enums.TipoTransacaoXp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacao_xp")
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

    @Column(name = "usuario_empresa_id", nullable = false)
    private Long idUsuarioEmpresa;

    @Column(name = "tarefa_id")
    private Long idTarefa;

    @Column(name = "recompensa_id")
    private Long idRecompensa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoTransacaoXp tipo;

    @Column(name = "valor", nullable = false)
    private Integer valor;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
