package conne.connect.connect.Services;

import conne.connect.connect.Dto.ProjetoResponseDTO;
import conne.connect.connect.Models.PessoaProjetoTelaModel;
import conne.connect.connect.Models.ProjetoTelaModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProjetoTelaMapper {

    public ProjetoResponseDTO toResponse(ProjetoTelaModel projeto) {
        return new ProjetoResponseDTO(
            projeto.getIdProjeto(),
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getStatus(),
            projeto.getAtrasado(),
            projeto.getPrioridade(),
            projeto.getPrazo(),
            projeto.getInicio(),
            projeto.getProgresso(),
            projeto.getHorasTrabalhadas(),
            projeto.getHorasEstimadas(),
            toPessoaResponse(projeto.getLider()),
            projeto.getMembros().stream()
                .sorted(Comparator.comparing(PessoaProjetoTelaModel::getNome))
                .map(this::toPessoaResponse)
                .toList(),
            projeto.getTarefas().stream()
                .map(tarefa -> new ProjetoResponseDTO.TarefaDTO(
                    tarefa.getIdTarefa(),
                    tarefa.getTitulo(),
                    tarefa.getResponsavel(),
                    tarefa.getPrioridade(),
                    tarefa.getStatus()
                ))
                .toList(),
            projeto.getMarcos().stream()
                .map(marco -> new ProjetoResponseDTO.MarcoDTO(
                    marco.getIdMarco(),
                    marco.getTitulo(),
                    marco.getData(),
                    marco.getStatus()
                ))
                .toList()
        );
    }

    public List<ProjetoResponseDTO> toResponseList(List<ProjetoTelaModel> projetos) {
        return projetos.stream().map(this::toResponse).toList();
    }

    public ProjetoResponseDTO.PessoaDTO toPessoaResponse(PessoaProjetoTelaModel pessoa) {
        return new ProjetoResponseDTO.PessoaDTO(
            pessoa.getIdPessoa(),
            pessoa.getNome(),
            pessoa.getCargo(),
            pessoa.getEmail(),
            pessoa.getIniciais(),
            pessoa.getHorasTrabalhadas(),
            pessoa.getDescricaoHoras(),
            pessoa.getAtivo()
        );
    }
}
