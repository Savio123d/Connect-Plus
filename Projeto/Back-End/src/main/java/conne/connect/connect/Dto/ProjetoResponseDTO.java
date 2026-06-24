package conne.connect.connect.Dto;

import conne.connect.connect.Enums.MarcoStatusProjetoTela;
import conne.connect.connect.Enums.PrioridadeProjetoTela;
import conne.connect.connect.Enums.ProjetoStatusTela;
import conne.connect.connect.Enums.TarefaStatusProjetoTela;

import java.time.LocalDate;
import java.util.List;

public record ProjetoResponseDTO(
    Long id,
    String nome,
    String descricao,
    ProjetoStatusTela status,
    Boolean atrasado,
    PrioridadeProjetoTela prioridade,
    LocalDate prazo,
    LocalDate inicio,
    Integer progresso,
    Integer horasTrabalhadas,
    Integer horasEstimadas,
    PessoaDTO lider,
    List<PessoaDTO> membros,
    List<TarefaDTO> tarefas,
    List<MarcoDTO> marcos
) {
    public record PessoaDTO(
        Long id,
        String nome,
        String cargo,
        String email,
        String iniciais,
        Integer horasTrabalhadas,
        String descricaoHoras,
        Boolean ativo
    ) {}

    public record TarefaDTO(
        Long id,
        String titulo,
        String responsavel,
        PrioridadeProjetoTela prioridade,
        TarefaStatusProjetoTela status
    ) {}

    public record MarcoDTO(
        Long id,
        String titulo,
        LocalDate data,
        MarcoStatusProjetoTela status
    ) {}
}
