package conne.connect.connect.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario_tarefa")
@Getter
@Setter
@NoArgsConstructor
public class ComentarioTarefaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idComentarioTarefa;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel idEmpresa;

    @Column(name = "tarefa_id", nullable = false)
    private Long idTarefa;

    @Column(name = "autor_usuario_empresa_id", nullable = false)
    private Long idAutorUsuarioEmpresa;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "editado_em")
    private LocalDateTime dataEdicao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        dataEdicao = LocalDateTime.now();
    }
}
