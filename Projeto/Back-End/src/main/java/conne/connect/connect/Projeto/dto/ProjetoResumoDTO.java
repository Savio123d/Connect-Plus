package conne.connect.connect.Projeto.dto;

import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import java.time.LocalDate;

public record ProjetoResumoDTO(
    Long id,
    String nome,
    String descricao,
    ProjetoStatusTela status,
    Boolean atrasado,
    LocalDate prazo,
    Integer progresso,
    Long quantidadeMembros,
    String liderNome,
    String liderIniciais
) {}
