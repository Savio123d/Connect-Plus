package conne.connect.connect.Projeto.mapper;

import conne.connect.connect.Projeto.dto.ProjetoResponseDTO;
import conne.connect.connect.Projeto.enums.PrioridadeProjetoTela;
import conne.connect.connect.Projeto.enums.TarefaStatusProjetoTela;
import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Tarefa.enums.PrioridadeTarefa;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjetoTelaMapper {

    private final TarefaRepository tarefaRepository;

    public ProjetoTelaMapper(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

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
            tarefasDoProjeto(projeto),
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

    private List<ProjetoResponseDTO.TarefaDTO> tarefasDoProjeto(ProjetoTelaModel projeto) {
        if (projeto == null || projeto.getIdProjeto() == null) {
            return List.of();
        }

        return tarefaRepository
            .findByIdProjeto_IdProjetoAndExcluidoIsNullOrderByIdTarefaAsc(projeto.getIdProjeto())
            .stream()
            .map(this::toTarefaResponse)
            .toList();
    }

    private ProjetoResponseDTO.TarefaDTO toTarefaResponse(TarefaModel tarefa) {
        return new ProjetoResponseDTO.TarefaDTO(
            tarefa.getIdTarefa(),
            tarefa.getTitulo(),
            nomeResponsavel(tarefa.getIdResponsavelUsuarioEmpresa()),
            prioridadeProjeto(tarefa.getPrioridade()),
            statusProjeto(tarefa.getStatus())
        );
    }

    private String nomeResponsavel(UsuarioEmpresaModel usuarioEmpresa) {
        if (usuarioEmpresa == null) {
            return "Nao atribuido";
        }

        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();
        return usuario != null && usuario.getNome() != null
            ? usuario.getNome()
            : "Usuario #" + usuarioEmpresa.getIdUsuarioEmpresa();
    }

    private PrioridadeProjetoTela prioridadeProjeto(PrioridadeTarefa prioridade) {
        if (prioridade == null) {
            return PrioridadeProjetoTela.MEDIA;
        }

        return switch (prioridade) {
            case baixa -> PrioridadeProjetoTela.BAIXA;
            case media -> PrioridadeProjetoTela.MEDIA;
            case alta -> PrioridadeProjetoTela.ALTA;
        };
    }

    private TarefaStatusProjetoTela statusProjeto(StatusTarefa status) {
        if (status == null) {
            return TarefaStatusProjetoTela.A_FAZER;
        }

        return switch (status) {
            case pendente, cancelada, arquivada -> TarefaStatusProjetoTela.A_FAZER;
            case em_andamento, em_revisao -> TarefaStatusProjetoTela.EM_ANDAMENTO;
            case concluida -> TarefaStatusProjetoTela.CONCLUIDO;
        };
    }
}
