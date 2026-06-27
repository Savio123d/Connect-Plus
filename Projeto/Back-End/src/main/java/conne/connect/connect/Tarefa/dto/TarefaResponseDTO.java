package conne.connect.connect.Tarefa.dto;

import conne.connect.connect.Tarefa.enums.DificuldadeTarefa;
import conne.connect.connect.Tarefa.enums.PrioridadeTarefa;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TarefaResponseDTO {

    private Long idTarefa;

    private Long idEmpresa;

    private Long idProjeto;

    private Long idResponsavelUsuarioEmpresa;

    private String titulo;

    private String descricao;

    private PrioridadeTarefa prioridade;

    private DificuldadeTarefa dificuldade;

    private StatusTarefa status;

    private Integer horasEstimadas;

    private Integer xpRecompensa;

    private LocalDateTime dataCriacao;

    private LocalDateTime prazo;

    private LocalDateTime concluidaEm;

    private LocalDateTime dataAtualizacao;

    public TarefaResponseDTO(TarefaModel tarefa) {
        this.idTarefa = tarefa.getIdTarefa();

        this.idEmpresa = tarefa.getIdEmpresa() != null
                ? tarefa.getIdEmpresa().getIdEmpresa()
                : null;

        this.idProjeto = tarefa.getIdProjeto() != null
                ? tarefa.getIdProjeto().getIdProjeto()
                : null;

        this.idResponsavelUsuarioEmpresa = tarefa.getIdResponsavelUsuarioEmpresa() != null
                ? tarefa.getIdResponsavelUsuarioEmpresa().getIdUsuarioEmpresa()
                : null;

        this.titulo = tarefa.getTitulo();
        this.descricao = tarefa.getDescricao();
        this.prioridade = tarefa.getPrioridade();
        this.dificuldade = tarefa.getDificuldade();
        this.status = tarefa.getStatus();
        this.horasEstimadas = tarefa.getHorasEstimadas();
        this.xpRecompensa = calcularXp(tarefa.getDificuldade());
        this.dataCriacao = tarefa.getDataCriacao();
        this.prazo = tarefa.getPrazo();
        this.concluidaEm = tarefa.getConcluidaEm();
        this.dataAtualizacao = tarefa.getDataAtualizacao();
    }

    private Integer calcularXp(DificuldadeTarefa dificuldade) {
        if (dificuldade == null) {
            return 0;
        }

        return switch (dificuldade) {
            case facil -> 10;
            case medio -> 20;
            case dificil -> 50;
        };
    }
}
