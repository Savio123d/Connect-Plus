package conne.connect.connect.Dto;

import conne.connect.connect.Enums.DificuldadeTarefa;
import conne.connect.connect.Enums.PrioridadeTarefa;
import conne.connect.connect.Enums.StatusTarefa;
import conne.connect.connect.Models.TarefaModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
        this.idEmpresa = tarefa.getIdEmpresa();
        this.idProjeto = tarefa.getIdProjeto();
        this.idResponsavelUsuarioEmpresa = tarefa.getIdResponsavelUsuarioEmpresa();
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