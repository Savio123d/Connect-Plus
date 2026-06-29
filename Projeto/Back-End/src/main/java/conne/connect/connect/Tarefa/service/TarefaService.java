package conne.connect.connect.Tarefa.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Projeto.model.ProjetoModel;
import conne.connect.connect.Projeto.repository.ProjetoRepository;
import conne.connect.connect.Tarefa.dto.TarefaRequestDTO;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    public List<TarefaModel> findAll() {
        return tarefaRepository.findAll();
    }

    public List<TarefaModel> listarPorEmpresa(Long empresaId) {
        return tarefaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(empresaId);
    }

    public TarefaModel buscarPorId(Long idTarefa) {
        return tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa nao encontrada"));
    }

    public TarefaModel criarTarefa(TarefaRequestDTO dto) {
        TarefaModel tarefa = new TarefaModel();
        preencherTarefaComDto(tarefa, dto);
        tarefa.setStatus(StatusTarefa.pendente);
        return tarefaRepository.save(tarefa);
    }

    public TarefaModel atualizarTarefa(Long idTarefa, TarefaRequestDTO dto) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        preencherTarefaComDto(tarefa, dto);
        return tarefaRepository.save(tarefa);
    }

    public TarefaModel atualizarStatus(Long idTarefa, StatusTarefa novoStatus) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        tarefa.setStatus(novoStatus);

        if (novoStatus == StatusTarefa.concluida) {
            tarefa.setConcluidaEm(LocalDateTime.now());
        } else {
            tarefa.setConcluidaEm(null);
        }

        return tarefaRepository.save(tarefa);
    }

    public void excluirTarefa(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        tarefaRepository.delete(tarefa);
    }

    private void preencherTarefaComDto(TarefaModel tarefa, TarefaRequestDTO dto) {
        EmpresaModel empresa = buscarEmpresa(dto.getIdEmpresa());
        ProjetoModel projeto = buscarProjeto(dto.getIdProjeto());
        validarProjetoDaEmpresa(projeto, empresa);
        UsuarioEmpresaModel responsavel = buscarResponsavel(dto.getIdResponsavelUsuarioEmpresa(), empresa.getIdEmpresa());

        tarefa.setIdEmpresa(empresa);
        tarefa.setIdProjeto(projeto);
        tarefa.setIdResponsavelUsuarioEmpresa(responsavel);
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setPrioridade(dto.getPrioridade());
        tarefa.setDificuldade(dto.getDificuldade());
        tarefa.setHorasEstimadas(dto.getHorasEstimadas() != null ? dto.getHorasEstimadas() : 0);
        tarefa.setPrazo(dto.getPrazo());
    }

    private EmpresaModel buscarEmpresa(Long idEmpresa) {
        if (idEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da empresa e obrigatorio");
        }

        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa nao encontrada"));
    }

    private ProjetoModel buscarProjeto(Long idProjeto) {
        if (idProjeto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do projeto e obrigatorio");
        }

        return projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto nao encontrado"));
    }

    private UsuarioEmpresaModel buscarResponsavel(Long idResponsavelUsuarioEmpresa, Long idEmpresa) {
        if (idResponsavelUsuarioEmpresa == null) {
            return null;
        }

        UsuarioEmpresaModel responsavel = usuarioEmpresaRepository.findById(idResponsavelUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsavel nao encontrado"));

        if (responsavel.getIdEmpresa() == null
                || responsavel.getIdEmpresa().getIdEmpresa() == null
                || !responsavel.getIdEmpresa().getIdEmpresa().equals(idEmpresa)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsavel nao pertence a empresa da tarefa");
        }

        if (!Boolean.TRUE.equals(responsavel.getAtivo()) || responsavel.getExcluido() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsavel inativo ou excluido");
        }

        return responsavel;
    }

    private void validarProjetoDaEmpresa(ProjetoModel projeto, EmpresaModel empresa) {
        if (projeto.getIdEmpresa() == null
                || projeto.getIdEmpresa().getIdEmpresa() == null
                || !projeto.getIdEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto nao pertence a empresa da tarefa");
        }
    }
}
