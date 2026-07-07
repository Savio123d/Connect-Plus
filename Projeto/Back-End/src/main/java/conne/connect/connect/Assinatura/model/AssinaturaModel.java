package conne.connect.connect.Assinatura.model;

import conne.connect.connect.Assinatura.enums.StatusAssinatura;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Plano.model.PlanoModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assinatura")
@Getter
@Setter
@NoArgsConstructor
public class AssinaturaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idAssinatura;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoModel idPlano;

    @Column(name = "qtd_usuarios", nullable = false)
    private Integer quantidadeUsuarios;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusAssinatura status;

    @Column(name = "inicio_em", nullable = false)
    private LocalDateTime inicioEm;

    @Column(name = "fim_em")
    private LocalDateTime fimEm;

    @Column(name = "mp_preapproval_id", unique = true, length = 80)
    private String mercadoPagoPreapprovalId;

    @Column(name = "mp_external_reference", unique = true, length = 120)
    private String mercadoPagoExternalReference;

    @Column(name = "mp_status", length = 40)
    private String mercadoPagoStatus;

    @Column(name = "mp_init_point", length = 500)
    private String mercadoPagoInitPoint;

    @Column(name = "mp_sandbox_init_point", length = 500)
    private String mercadoPagoSandboxInitPoint;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (quantidadeUsuarios == null) {
            quantidadeUsuarios = 1;
        }

        if (status == null) {
            status = StatusAssinatura.ativa;
        }

        if (inicioEm == null) {
            inicioEm = LocalDateTime.now();
        }
    }
}