package conne.connect.connect.Models;

import conne.connect.connect.Enums.TipoMensagem;
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
        name = "mensagem",
        indexes = @Index(name = "idx_mensagem_conversa_enviada", columnList = "conversa_id, enviada_em")
)
@Getter
@Setter
@NoArgsConstructor
public class MensagemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idMensagem;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaModel idConversa;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private UsuarioEmpresaModel idRemetente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMensagem tipo;

    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "enviada_em", nullable = false)
    private LocalDateTime enviadaEm;

    @Column(name = "editada_em")
    private LocalDateTime editadaEm;

    @Column(name = "excluida_em")
    private LocalDateTime excluidaEm;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (tipo == null) {
            tipo = TipoMensagem.texto;
        }

        if (enviadaEm == null) {
            enviadaEm = LocalDateTime.now();
        }
    }
}
