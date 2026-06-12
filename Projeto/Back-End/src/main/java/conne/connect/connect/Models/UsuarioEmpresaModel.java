package conne.connect.connect.Models;

import conne.connect.connect.Enums.PapelEmpresa;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuario_empresa",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_empresa_empresa_usuario", columnNames = {"empresa_id", "usuario_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEmpresaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel idUsuario;

    @ManyToOne
    @JoinColumn(name = "setor_id")
    private SetorModel idSetor;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 20)
    private PapelEmpresa papel;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        if (ativo == null) {
            ativo = true;
        }

        if (dataCriacao == null) {
            dataCriacao = agora;
        }

        dataAtualizacao = agora;
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
