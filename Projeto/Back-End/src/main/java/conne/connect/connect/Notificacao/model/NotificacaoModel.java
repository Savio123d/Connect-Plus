package conne.connect.connect.Notificacao.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Notificacao.enums.TipoNotificacao;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "notificacao",
        indexes = @Index(name = "idx_notificacao_usu_emp", columnList = "usu_emp_id")
)
@Getter
@Setter
@NoArgsConstructor
public class NotificacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idNotificacao;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoNotificacao tipo;

    @Column(name = "titulo", length = 120)
    private String titulo;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "lida", nullable = false)
    private Boolean lida;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "lida_em")
    private LocalDateTime dataLeitura;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (lida == null) {
            lida = false;
        }

        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (Boolean.TRUE.equals(lida) && dataLeitura == null) {
            dataLeitura = LocalDateTime.now();
        }

        if (Boolean.FALSE.equals(lida)) {
            dataLeitura = null;
        }
    }
}
