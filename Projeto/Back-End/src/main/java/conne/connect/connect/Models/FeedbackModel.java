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
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
public class FeedbackModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idFeedback;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioEmpresaModel idAutorUsuarioEmpresa;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private ProjetoModel idProjeto;

    @ManyToOne
    @JoinColumn(name = "tarefa_id")
    private TarefaModel idTarefa;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
