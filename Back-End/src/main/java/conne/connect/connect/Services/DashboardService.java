package conne.connect.connect.Services;

import conne.connect.connect.Dto.DashboardDTO;
import conne.connect.connect.Enums.StatusProjeto;
import conne.connect.connect.Enums.StatusTarefa;
import conne.connect.connect.Enums.StatusUsuario;
import conne.connect.connect.Repositories.EmpresaRepository;
import conne.connect.connect.Repositories.ProjetoRepository;
import conne.connect.connect.Repositories.TarefaRepository;
import conne.connect.connect.Repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaRepository tarefaRepository;

    public DashboardService(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            ProjetoRepository projetoRepository,
            TarefaRepository tarefaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.projetoRepository = projetoRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public DashboardDTO buscarDadosDashboard() {
        long usuariosAtivos = usuarioRepository.countByStatus(StatusUsuario.ativo);
        long empresasCadastradas = empresaRepository.count();
        long projetosEmAndamento = projetoRepository.countByStatus(StatusProjeto.em_andamento);
        long tarefasPendentes = tarefaRepository.countByStatus(StatusTarefa.pendente);
        long tarefasEmAndamento = tarefaRepository.countByStatus(StatusTarefa.em_andamento);
        long tarefasConcluidas = tarefaRepository.countByStatus(StatusTarefa.concluida);

        return new DashboardDTO(
                usuariosAtivos,
                empresasCadastradas,
                projetosEmAndamento,
                tarefasPendentes,
                tarefasEmAndamento,
                tarefasConcluidas
        );
    }
}