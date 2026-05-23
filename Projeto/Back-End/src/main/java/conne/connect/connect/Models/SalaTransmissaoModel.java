package conne.connect.connect.Models;

import conne.connect.connect.Enums.StatusSala;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "sala_transmissao",
        indexes = @Index(name = "idx_sala_transmissao_status", columnList = "status")
)
@Getter
@Setter
@NoArgsConstructor
public class SalaTransmissaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idSalaTransmissao;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "conversa_id")
    private ConversaModel idConversa;

    @Column(name = "nome", length = 120)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private UsuarioEmpresaModel idCriador;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusSala status;

    @Column(name = "inicio_em", nullable = false)
    private LocalDateTime inicioEm;

    @Column(name = "fim_em")
    private LocalDateTime fimEm;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = StatusSala.ativa;
        }

        if (inicioEm == null) {
            inicioEm = LocalDateTime.now();
        }
    }
}
