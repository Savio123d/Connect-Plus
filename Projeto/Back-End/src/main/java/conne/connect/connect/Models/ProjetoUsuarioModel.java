package conne.connect.connect.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "projeto_usuario")
@Getter
@Setter
@NoArgsConstructor
public class ProjetoUsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idProjetoUsuario;

    @Column(name = "empresa_id", nullable = false)
    private Long idEmpresa;

    @Column(name = "projeto_id", nullable = false)
    private Long idProjeto;

    @Column(name = "usuario_empresa_id", nullable = false)
    private Long idUsuarioEmpresa;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}
