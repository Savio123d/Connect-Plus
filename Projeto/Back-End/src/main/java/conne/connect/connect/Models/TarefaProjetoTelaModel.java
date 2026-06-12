package conne.connect.connect.Models;

import conne.connect.connect.Enums.PrioridadeProjetoTela;
import conne.connect.connect.Enums.TarefaStatusProjetoTela;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarefa_projeto_tela")
public class TarefaProjetoTelaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarefa")
    private Long idTarefa;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 120)
    private String responsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeProjetoTela prioridade = PrioridadeProjetoTela.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TarefaStatusProjetoTela status = TarefaStatusProjetoTela.A_FAZER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto", nullable = false)
    private ProjetoTelaModel projeto;

    public Long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Long idTarefa) {
        this.idTarefa = idTarefa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public PrioridadeProjetoTela getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeProjetoTela prioridade) {
        this.prioridade = prioridade;
    }

    public TarefaStatusProjetoTela getStatus() {
        return status;
    }

    public void setStatus(TarefaStatusProjetoTela status) {
        this.status = status;
    }

    public ProjetoTelaModel getProjeto() {
        return projeto;
    }

    public void setProjeto(ProjetoTelaModel projeto) {
        this.projeto = projeto;
    }
}
