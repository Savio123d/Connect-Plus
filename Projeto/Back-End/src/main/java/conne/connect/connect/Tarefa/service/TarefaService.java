package conne.connect.connect.Tarefa.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Projeto.enums.MarcoStatusProjetoTela;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Tarefa.dto.TarefaRequestDTO;
import conne.connect.connect.Tarefa.enums.DificuldadeTarefa;
import conne.connect.connect.Tarefa.enums.StatusTarefa;
import conne.connect.connect.Tarefa.model.TarefaModel;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Xp.enums.TipoTransacaoXp;
import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import conne.connect.connect.Xp.repository.TransacaoXpRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final EmpresaRepository empresaRepository;
    private final ProjetoTelaRepository projetoRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final SaldoXpRepository saldoXpRepository;
    private final TransacaoXpRepository transacaoXpRepository;
    private final AutorizacaoService autorizacaoService;

    public TarefaService(
            TarefaRepository tarefaRepository,
            EmpresaRepository empresaRepository,
            ProjetoTelaRepository projetoRepository,
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            SaldoXpRepository saldoXpRepository,
            TransacaoXpRepository transacaoXpRepository,
            AutorizacaoService autorizacaoService
    ) {
        this.tarefaRepository = tarefaRepository;
        this.empresaRepository = empresaRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.saldoXpRepository = saldoXpRepository;
        this.transacaoXpRepository = transacaoXpRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<TarefaModel> findAll() {
        return tarefaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Cacheable(value = "tarefasPorEmpresa", key = "@autorizacao.empresaAtual() + ':' + #empresaId")
    @Transactional(readOnly = true)
    public List<TarefaModel> listarPorEmpresa(Long empresaId) {
        autorizacaoService.validarEmpresaAtual(empresaId);
        return tarefaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(empresaId);
    }

    @Cacheable(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa")
    @Transactional(readOnly = true)
    public TarefaModel buscarPorId(Long idTarefa) {
        return tarefaRepository.findByIdTarefaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
                idTarefa,
                autorizacaoService.empresaAtual()
        )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    // Nova tarefa muda a listagem do quadro e o resumo/listagem dos projetos.
    @Caching(evict = {
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel criarTarefa(TarefaRequestDTO dto) {
        TarefaModel tarefa = new TarefaModel();
        preencherTarefaComDto(tarefa, dto);
        tarefa.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusTarefa.pendente);

        if (tarefa.getStatus() == StatusTarefa.concluida) {
            registrarDadosConclusao(tarefa);
        }

        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);

        if (tarefaSalva.getStatus() == StatusTarefa.concluida) {
            concederXpConclusao(tarefaSalva);
        }

        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel atualizarTarefa(Long idTarefa, TarefaRequestDTO dto) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        if (tarefa.getStatus() == StatusTarefa.concluida) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tarefa concluída não pode ser alterada."
            );
        }

        preencherTarefaComDto(tarefa, dto);
        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel atualizarStatus(Long idTarefa, StatusTarefa novoStatus) {
        if (novoStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da tarefa é obrigatório");
        }

        TarefaModel tarefa = buscarPorId(idTarefa);
        StatusTarefa statusAnterior = tarefa.getStatus();

        if (statusAnterior == StatusTarefa.concluida) {
            if (novoStatus == StatusTarefa.concluida) {
                return tarefa;
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tarefa concluída não pode ser reaberta ou movida para outro status"
            );
        }

        if (statusAnterior == novoStatus) {
            return tarefa;
        }

        LocalDateTime agora = LocalDateTime.now();

        if (!statusMantemCronometroRodando(novoStatus)) {
            pausarCronometroAberto(tarefa, agora);
        }

        tarefa.setStatus(novoStatus);

        if (novoStatus == StatusTarefa.concluida) {
            registrarDadosConclusao(tarefa, agora);
            concederXpConclusao(tarefa);
        }

        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public TarefaModel iniciarCronometro(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        if (tarefa.getStatus() == StatusTarefa.concluida
                || tarefa.getStatus() == StatusTarefa.cancelada
                || tarefa.getStatus() == StatusTarefa.arquivada) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tarefa finalizada não pode ter o cronômetro iniciado."
            );
        }

        LocalDateTime agora = LocalDateTime.now();

        if (tarefa.getInicioExecucaoEm() == null) {
            tarefa.setInicioExecucaoEm(agora);
        }

        if (tarefa.getTempoGastoMinutos() == null) {
            tarefa.setTempoGastoMinutos(0L);
        }

        if (tarefa.getCronometroIniciadoEm() == null) {
            tarefa.setCronometroIniciadoEm(agora);
        }

        if (!statusMantemCronometroRodando(tarefa.getStatus())) {
            tarefa.setStatus(StatusTarefa.em_andamento);
        }

        TarefaModel tarefaSalva = tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(tarefaSalva.getIdProjeto());
        return tarefaSalva;
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true)
    })
    public TarefaModel pausarCronometro(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);

        if (tarefa.getStatus() == StatusTarefa.concluida) {
            return tarefa;
        }

        pausarCronometroAberto(tarefa, LocalDateTime.now());
        return tarefaRepository.save(tarefa);
    }

    @Caching(evict = {
            @CacheEvict(value = "tarefaPorId", key = "@autorizacao.empresaAtual() + ':' + #idTarefa"),
            @CacheEvict(value = "tarefasPorEmpresa", allEntries = true),
            @CacheEvict(value = "projetosPorEmpresa", allEntries = true)
    })
    public void excluirTarefa(Long idTarefa) {
        TarefaModel tarefa = buscarPorId(idTarefa);
        ProjetoTelaModel projeto = tarefa.getIdProjeto();
        tarefa.setExcluido(LocalDate.now());
        tarefaRepository.save(tarefa);
        recalcularProgressoProjeto(projeto);
    }

    private void preencherTarefaComDto(TarefaModel tarefa, TarefaRequestDTO dto) {
        EmpresaModel empresa = buscarEmpresa(dto.getIdEmpresa());
        ProjetoTelaModel projeto = buscarProjeto(dto.getIdProjeto());
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

    private void registrarDadosConclusao(TarefaModel tarefa) {
        registrarDadosConclusao(tarefa, LocalDateTime.now());
    }

    private void registrarDadosConclusao(TarefaModel tarefa, LocalDateTime agora) {
        if (tarefa.getInicioExecucaoEm() == null) {
            tarefa.setInicioExecucaoEm(agora);
        }

        pausarCronometroAberto(tarefa, agora);

        if (tarefa.getConcluidaEm() == null) {
            tarefa.setConcluidaEm(agora);
        }

        if (tarefa.getTempoGastoMinutos() == null) {
            tarefa.setTempoGastoMinutos(0L);
        }
    }

    private void pausarCronometroAberto(TarefaModel tarefa, LocalDateTime agora) {
        if (tarefa.getCronometroIniciadoEm() == null) {
            if (tarefa.getTempoGastoMinutos() == null) {
                tarefa.setTempoGastoMinutos(0L);
            }
            return;
        }

        Long tempoAtual = tarefa.getTempoGastoMinutos() != null ? tarefa.getTempoGastoMinutos() : 0L;
        Long tempoSessao = calcularTempoSessaoMinutos(tarefa.getCronometroIniciadoEm(), agora);

        tarefa.setTempoGastoMinutos(tempoAtual + tempoSessao);
        tarefa.setCronometroIniciadoEm(null);
    }

    private Long calcularTempoSessaoMinutos(LocalDateTime inicioSessao, LocalDateTime fimSessao) {
        if (inicioSessao == null || fimSessao == null || fimSessao.isBefore(inicioSessao)) {
            return 0L;
        }

        long segundos = Duration.between(inicioSessao, fimSessao).getSeconds();

        if (segundos <= 0) {
            return 0L;
        }

        return Math.max(1L, (segundos + 59L) / 60L);
    }

    private void concederXpConclusao(TarefaModel tarefa) {
        UsuarioEmpresaModel responsavel = tarefa.getIdResponsavelUsuarioEmpresa();

        if (responsavel == null || responsavel.getIdUsuarioEmpresa() == null) {
            return;
        }

        Long idEmpresa = tarefa.getIdEmpresa() != null ? tarefa.getIdEmpresa().getIdEmpresa() : null;
        Long idEmpresaResponsavel = responsavel.getIdEmpresa() != null
                ? responsavel.getIdEmpresa().getIdEmpresa()
                : null;

        if (idEmpresa == null || !idEmpresa.equals(idEmpresaResponsavel)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Responsável e tarefa devem pertencer à mesma empresa"
            );
        }

        if (tarefa.getIdTarefa() != null
                && transacaoXpRepository
                .findFirstByIdTarefa_IdTarefaAndIdEmpresa_IdEmpresaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndTipoAndExcluidoIsNull(
                tarefa.getIdTarefa(),
                idEmpresa,
                responsavel.getIdUsuarioEmpresa(),
                TipoTransacaoXp.ganho
        ).isPresent()) {
            return;
        }

        Integer xp = calcularXp(tarefa.getDificuldade());

        if (xp <= 0) {
            return;
        }

        SaldoXpModel saldo = saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
                        responsavel.getIdUsuarioEmpresa(),
                        idEmpresa
                )
                .orElseGet(() -> criarSaldoXp(tarefa.getIdEmpresa(), responsavel));
        validarSaldoDaEmpresa(saldo, idEmpresa);

        saldo.setXpTotal((saldo.getXpTotal() != null ? saldo.getXpTotal() : 0) + xp);
        saldoXpRepository.save(saldo);

        TransacaoXpModel transacao = new TransacaoXpModel();
        transacao.setIdEmpresa(tarefa.getIdEmpresa());
        transacao.setIdUsuarioEmpresa(responsavel);
        transacao.setIdTarefa(tarefa);
        transacao.setTipo(TipoTransacaoXp.ganho);
        transacao.setValor(xp);
        transacao.setObservacao("XP concedido pela conclusão da tarefa: " + tarefa.getTitulo());
        transacaoXpRepository.save(transacao);
    }

    private SaldoXpModel criarSaldoXp(EmpresaModel empresa, UsuarioEmpresaModel responsavel) {
        SaldoXpModel saldo = new SaldoXpModel();
        saldo.setIdEmpresa(empresa);
        saldo.setIdUsuarioEmpresa(responsavel);
        saldo.setXpTotal(0);
        return saldo;
    }

    private void validarSaldoDaEmpresa(SaldoXpModel saldo, Long idEmpresa) {
        Long idEmpresaSaldo = saldo.getIdEmpresa() != null ? saldo.getIdEmpresa().getIdEmpresa() : null;
        if (!idEmpresa.equals(idEmpresaSaldo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Saldo de XP pertence a outra empresa"
            );
        }
    }

    private Integer calcularXp(DificuldadeTarefa dificuldade) {
        return dificuldade != null ? dificuldade.getXpConclusao() : 0;
    }

    private boolean statusMantemCronometroRodando(StatusTarefa status) {
        return status == StatusTarefa.em_andamento || status == StatusTarefa.em_revisao;
    }

    private EmpresaModel buscarEmpresa(Long idEmpresa) {
        autorizacaoService.validarEmpresaAtual(idEmpresa);
        if (idEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da empresa é obrigatório");
        }

        return empresaRepository.findById(idEmpresa)
                .filter(empresa -> empresa.getExcluido() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }

    private ProjetoTelaModel buscarProjeto(Long idProjeto) {
        if (idProjeto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do projeto é obrigatório");
        }

        return projetoRepository.findById(idProjeto)
                .filter(projeto -> projeto.getExcluido() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    private UsuarioEmpresaModel buscarResponsavel(Long idResponsavelUsuarioEmpresa, Long idEmpresa) {
        if (idResponsavelUsuarioEmpresa == null) {
            return null;
        }

        UsuarioEmpresaModel responsavel = usuarioEmpresaRepository.findById(idResponsavelUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsável não encontrado"));

        if (responsavel.getIdEmpresa() == null
                || responsavel.getIdEmpresa().getIdEmpresa() == null
                || !responsavel.getIdEmpresa().getIdEmpresa().equals(idEmpresa)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsável não pertence à empresa da tarefa");
        }

        if (!Boolean.TRUE.equals(responsavel.getAtivo()) || responsavel.getExcluido() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsável inativo ou excluído");
        }

        return responsavel;
    }

    private void validarProjetoDaEmpresa(ProjetoTelaModel projeto, EmpresaModel empresa) {
        if (projeto.getEmpresa() == null
                || projeto.getEmpresa().getIdEmpresa() == null
                || !projeto.getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto não pertence à empresa da tarefa");
        }
    }

    private void recalcularProgressoProjeto(ProjetoTelaModel projeto) {
        if (projeto == null || projeto.getIdProjeto() == null) {
            return;
        }

        // Counts no banco evitam carregar todas as tarefas do projeto so para medir progresso.
        long totalTarefas = tarefaRepository
                .countByIdProjeto_IdProjetoAndExcluidoIsNull(projeto.getIdProjeto());
        long tarefasConcluidas = tarefaRepository
                .countByIdProjeto_IdProjetoAndStatusAndExcluidoIsNull(projeto.getIdProjeto(), StatusTarefa.concluida);

        long totalMarcos = projeto.getMarcos().size();
        long marcosConcluidos = projeto.getMarcos().stream()
                .filter(marco -> marco.getStatus() == MarcoStatusProjetoTela.CONCLUIDO)
                .count();

        long totalItens = totalTarefas + totalMarcos;
        long totalConcluidos = tarefasConcluidas + marcosConcluidos;

        projeto.setProgresso(totalItens > 0
                ? (int) Math.round((totalConcluidos * 100.0) / totalItens)
                : 0);

        projetoRepository.save(projeto);
    }
}
