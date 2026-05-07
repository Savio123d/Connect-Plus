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
        name = "msg_leitura",
        uniqueConstraints = @UniqueConstraint(name = "uk_msg_leitura_mensagem_usu_emp", columnNames = {"mensagem_id", "usu_emp_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class MsgLeituraModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idMsgLeitura;

    @ManyToOne
    @JoinColumn(name = "mensagem_id", nullable = false)
    private MensagemModel idMensagem;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @Column(name = "lido_em", nullable = false)
    private LocalDateTime lidoEm;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (lidoEm == null) {
            lidoEm = LocalDateTime.now();
        }
    }
}
