package conne.connect.connect.Models;

import conne.connect.connect.Enums.TipoNotificacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
public class NotificacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idNotificacao;

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "usuario_empresa_id", nullable = false)
    private Long idUsuarioEmpresa;

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
