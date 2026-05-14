package conne.connect.connect.Models;

import conne.connect.connect.Enums.DificuldadeTarefa;
import conne.connect.connect.Enums.PrioridadeTarefa;
import conne.connect.connect.Enums.StatusTarefa;
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
@Table(name = "tarefa")
@Getter
@Setter
@NoArgsConstructor
public class TarefaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idTarefa;

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "projeto_id", nullable = false)
    private Long idProjeto;

    @Column(name = "responsavel_usuario_empresa_id")
    private Long idResponsavelUsuarioEmpresa;

    @Column(name = "titulo", nullable = false, length = 130)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 20)
    private PrioridadeTarefa prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "dificuldade", nullable = false, length = 20)
    private DificuldadeTarefa dificuldade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusTarefa status;

    @Column(name = "horas_estimadas")
    private Integer horasEstimadas;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "prazo")
    private LocalDateTime prazo;

    @Column(name = "concluida_em")
    private LocalDateTime concluidaEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        if (status == null) {
            status = StatusTarefa.pendente;
        }

        if (prioridade == null) {
            prioridade = PrioridadeTarefa.media;
        }

        if (dificuldade == null) {
            dificuldade = DificuldadeTarefa.medio;
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