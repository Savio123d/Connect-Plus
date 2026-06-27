package conne.connect.connect.Projeto.mapper;

import conne.connect.connect.Projeto.dto.ProjetoResponseDTO;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

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
        Long idPessoaTela = pessoa.getUsuarioEmpresa() != null
            ? pessoa.getUsuarioEmpresa().getIdUsuarioEmpresa()
            : pessoa.getIdPessoa();

        return new ProjetoResponseDTO.PessoaDTO(
            idPessoaTela,
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
