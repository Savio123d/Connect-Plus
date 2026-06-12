package conne.connect.connect.Dto;

import conne.connect.connect.Enums.PrioridadeProjetoTela;

import java.time.LocalDate;
import java.util.List;

public record ProjetoRequestDTO(
    String nome,
    String descricao,
    LocalDate prazo,
    Long liderId,
    List<Long> membrosIds,

    String status,
    Long usuarioId,

    String titulo,
    LocalDate data,
    String responsavel,
    PrioridadeProjetoTela prioridade
) {}
