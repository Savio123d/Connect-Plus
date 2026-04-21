package conne.connect.connect.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "autor_usuario_empresa_id", nullable = false)
    private Long idAutorUsuarioEmpresa;

    @Column(name = "projeto_id")
    private Long idProjeto;

    @Column(name = "tarefa_id")
    private Long idTarefa;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
