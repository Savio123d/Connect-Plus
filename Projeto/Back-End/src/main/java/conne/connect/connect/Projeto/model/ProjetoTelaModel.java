package conne.connect.connect.Projeto.model;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Projeto.enums.PrioridadeProjetoTela;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projeto_tela")
public class ProjetoTelaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    private Long idProjeto;

    @Column(nullable = false, length = 140)
    private String nome;

    @Column(nullable = false, length = 700)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjetoStatusTela status = ProjetoStatusTela.em_andamento;

    @Column(nullable = false)
    private Boolean atrasado = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeProjetoTela prioridade = PrioridadeProjetoTela.ALTA;

    @Column(nullable = false)
    private LocalDate prazo;

    @Column(nullable = false)
    private LocalDate inicio = LocalDate.now();

    @Column(name = "concluido_em")
    private LocalDate concluidoEm;

    @Column(nullable = false)
    private Integer progresso = 0;

    @Column(name = "horas_trabalhadas", nullable = false)
    private Integer horasTrabalhadas = 0;

    @Column(name = "horas_estimadas", nullable = false)
    private Integer horasEstimadas = 240;

    @Column(name = "avaliacao_360_obrigatoria", nullable = false)
    private Boolean avaliacao360Obrigatoria = false;

    public Boolean getAvaliacao360Obrigatoria() {
        return avaliacao360Obrigatoria;
    }

    public void setAvaliacao360Obrigatoria(Boolean avaliacao360Obrigatoria) {
        this.avaliacao360Obrigatoria = avaliacao360Obrigatoria;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaModel empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_lider", nullable = false)
    private PessoaProjetoTelaModel lider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "projeto_tela_membro",
        joinColumns = @JoinColumn(name = "id_projeto"),
        inverseJoinColumns = @JoinColumn(name = "id_pessoa")
    )
    private Set<PessoaProjetoTelaModel> membros = new LinkedHashSet<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idTarefa ASC")
    private List<TarefaProjetoTelaModel> tarefas = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idMarco ASC")
    private List<MarcoProjetoTelaModel> marcos = new ArrayList<>();

    public Long getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(Long idProjeto) {
        this.idProjeto = idProjeto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ProjetoStatusTela getStatus() {
        return status;
    }

    public void setStatus(ProjetoStatusTela status) {
        this.status = status;
    }

    public Boolean getAtrasado() {
        return atrasado;
    }

    public void setAtrasado(Boolean atrasado) {
        this.atrasado = atrasado;
    }

    public PrioridadeProjetoTela getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeProjetoTela prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getConcluidoEm() {
        return concluidoEm;
    }

    public void setConcluidoEm(LocalDate concluidoEm) {
        this.concluidoEm = concluidoEm;
    }

    public Integer getProgresso() {
        return progresso;
    }

    public void setProgresso(Integer progresso) {
        this.progresso = progresso;
    }

    public Integer getHorasTrabalhadas() {
        return horasTrabalhadas;
    }

    public void setHorasTrabalhadas(Integer horasTrabalhadas) {
        this.horasTrabalhadas = horasTrabalhadas;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public EmpresaModel getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
    }

    public PessoaProjetoTelaModel getLider() {
        return lider;
    }

    public void setLider(PessoaProjetoTelaModel lider) {
        this.lider = lider;
    }

    public Set<PessoaProjetoTelaModel> getMembros() {
        return membros;
    }

    public void setMembros(Set<PessoaProjetoTelaModel> membros) {
        this.membros = membros;
    }

    public List<TarefaProjetoTelaModel> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<TarefaProjetoTelaModel> tarefas) {
        this.tarefas = tarefas;
    }

    public List<MarcoProjetoTelaModel> getMarcos() {
        return marcos;
    }

    public void setMarcos(List<MarcoProjetoTelaModel> marcos) {
        this.marcos = marcos;
    }
}
