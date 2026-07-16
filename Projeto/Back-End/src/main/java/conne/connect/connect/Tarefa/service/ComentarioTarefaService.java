package conne.connect.connect.Tarefa.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Tarefa.model.ComentarioTarefaModel;
import conne.connect.connect.Tarefa.repository.ComentarioTarefaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComentarioTarefaService {

    private final ComentarioTarefaRepository comentarioTarefaRepository;
    private final AutorizacaoService autorizacaoService;

    public ComentarioTarefaService(ComentarioTarefaRepository comentarioTarefaRepository, AutorizacaoService autorizacaoService) {
        this.comentarioTarefaRepository = comentarioTarefaRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<ComentarioTarefaModel> findAll() {
        return comentarioTarefaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public ComentarioTarefaModel criarComentarioTarefa(ComentarioTarefaModel comentarioTarefaModel) {
        validarEscopo(comentarioTarefaModel);
        return comentarioTarefaRepository.save(comentarioTarefaModel);
    }

    @Transactional(readOnly = true)
    public Optional<ComentarioTarefaModel> buscarPorId(Long idComentarioTarefa) {
        return comentarioTarefaRepository.findByIdComentarioTarefaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idComentarioTarefa, autorizacaoService.empresaAtual());
    }

    @Transactional
    public ComentarioTarefaModel atualizarComentarioTarefa(Long idComentarioTarefa, ComentarioTarefaModel comentarioTarefaModel) {
        validarEscopo(comentarioTarefaModel);
        ComentarioTarefaModel comentarioTarefa = buscarComentarioExistente(idComentarioTarefa);
        comentarioTarefa.setIdTarefa(comentarioTarefaModel.getIdTarefa());
        comentarioTarefa.setIdAutorUsuarioEmpresa(comentarioTarefaModel.getIdAutorUsuarioEmpresa());
        comentarioTarefa.setConteudo(comentarioTarefaModel.getConteudo());
        return comentarioTarefaRepository.save(comentarioTarefa);
    }

    @Transactional
    public void excluirComentarioTarefa(Long idComentarioTarefa) {
        ComentarioTarefaModel comentarioTarefa = buscarComentarioExistente(idComentarioTarefa);
        comentarioTarefa.setExcluido(LocalDate.now());
        comentarioTarefaRepository.save(comentarioTarefa);
    }

    private void validarEscopo(ComentarioTarefaModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdTarefa() != null && registro.getIdTarefa().getIdEmpresa() != null
                ? registro.getIdTarefa().getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdAutorUsuarioEmpresa() != null
                && registro.getIdAutorUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdAutorUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
    }

    private ComentarioTarefaModel buscarComentarioExistente(Long idComentarioTarefa) {
        return buscarPorId(idComentarioTarefa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário da tarefa não encontrado."));
    }
}
