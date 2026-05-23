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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "sala_participante",
        uniqueConstraints = @UniqueConstraint(name = "uk_sala_participante_sala_usu_emp", columnNames = {"sala_id", "usu_emp_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class SalaParticipanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idSalaParticipante;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaTransmissaoModel idSala;

    @ManyToOne
    @JoinColumn(name = "usu_emp_id", nullable = false)
    private UsuarioEmpresaModel idUsuarioEmpresa;

    @Column(name = "entrou_em", nullable = false)
    private LocalDateTime entrouEm;

    @Column(name = "saiu_em")
    private LocalDateTime saiuEm;

    @Column(name = "compart_tela", nullable = false)
    private Boolean compartilhaTela;

    @Column(name = "audio_on", nullable = false)
    private Boolean audioOn;

    @Column(name = "video_on", nullable = false)
    private Boolean videoOn;

    @Column(name = "incluido", columnDefinition = "DATE")
    private LocalDate incluido;

    @Column(name = "excluido", columnDefinition = "DATE")
    private LocalDate excluido;

    @PrePersist
    public void prePersist() {
        if (entrouEm == null) {
            entrouEm = LocalDateTime.now();
        }

        if (compartilhaTela == null) {
            compartilhaTela = false;
        }

        if (audioOn == null) {
            audioOn = true;
        }

        if (videoOn == null) {
            videoOn = false;
        }
    }
}
