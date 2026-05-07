package conne.connect.connect.Models;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversa_participante",
        uniqueConstraints = @UniqueConstraint(name = "uk_conversa_participante_conversa_usu_emp", columnNames = {"conversa_id", "usu_emp_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class ConversaParticipanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idConversaParticipante;

    @ManyToOne
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaModel idConversa;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @Column(name = "entrou_em", nullable = false)
    private LocalDateTime entrouEm;

    @Column(name = "saiu_em")
    private LocalDateTime saiuEm;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (ativo == null) {
            ativo = true;
        }

        if (entrouEm == null) {
            entrouEm = LocalDateTime.now();
        }
    }
}
