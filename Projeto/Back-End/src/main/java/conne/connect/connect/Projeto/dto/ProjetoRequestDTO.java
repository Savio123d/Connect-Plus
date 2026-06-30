package conne.connect.connect.Projeto.dto;

import conne.connect.connect.Projeto.enums.PrioridadeProjetoTela;
import java.time.LocalDate;
import java.util.List;

public record ProjetoRequestDTO(
    Long empresaId,
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
    Long responsavelId,
    Long idResponsavelUsuarioEmpresa,
    PrioridadeProjetoTela prioridade,
    Integer horasEstimadas
) {}
