package conne.connect.connect.Projeto.model;

import conne.connect.connect.Projeto.enums.MarcoStatusProjetoTela;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "marco_projeto_tela")
public class MarcoProjetoTelaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marco")
    private Long idMarco;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MarcoStatusProjetoTela status = MarcoStatusProjetoTela.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto", nullable = false)
    private ProjetoTelaModel projeto;

    public Long getIdMarco() {
        return idMarco;
    }

    public void setIdMarco(Long idMarco) {
        this.idMarco = idMarco;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public MarcoStatusProjetoTela getStatus() {
        return status;
    }

    public void setStatus(MarcoStatusProjetoTela status) {
        this.status = status;
    }

    public ProjetoTelaModel getProjeto() {
        return projeto;
    }

    public void setProjeto(ProjetoTelaModel projeto) {
        this.projeto = projeto;
    }
}
