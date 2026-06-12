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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "msg_anexo")
@Getter
@Setter
@NoArgsConstructor
public class MsgAnexoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idMsgAnexo;

    @ManyToOne
    @JoinColumn(name = "mensagem_id", nullable = false)
    private MensagemModel idMensagem;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "tamanho")
    private Integer tamanho;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (enviadoEm == null) {
            enviadoEm = LocalDateTime.now();
        }
    }
}
