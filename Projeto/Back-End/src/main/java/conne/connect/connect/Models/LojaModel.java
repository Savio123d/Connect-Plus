package conne.connect.connect.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recompensa")
@Getter
@Setter
@NoArgsConstructor
public class LojaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idLoja;

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "nome", nullable = false, length = 70)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "xp_req", nullable = false)
    private Integer custoXp;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @Transient
    private Integer quantidadeDisponivel;

    @Transient
    private String categoria;

    @Transient
    private String icone;

    @Transient
    private String cor;

    @Transient
    private Boolean resgatada;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        if (idEmpresa == null) {
            idEmpresa = 1L;
        }

        if (ativa == null) {
            ativa = true;
        }

        if (dataCriacao == null) {
            dataCriacao = agora;
        }

        if (incluido == null) {
            incluido = LocalDate.now();
        }

        dataAtualizacao = agora;
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}