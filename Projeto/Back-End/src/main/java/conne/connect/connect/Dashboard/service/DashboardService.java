package conne.connect.connect.Dashboard.service;

import conne.connect.connect.Dashboard.dto.DashboardResumoDTO;
import conne.connect.connect.Dashboard.dto.DesempenhoEquipeDTO;
import conne.connect.connect.Feedback.repository.FeedbackRepository;
import conne.connect.connect.Projeto.repository.ProjetoRepository;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaRepository tarefaRepository;
    private final FeedbackRepository feedbackRepository;

    public DashboardService(
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            ProjetoRepository projetoRepository,
            TarefaRepository tarefaRepository,
            FeedbackRepository feedbackRepository
    ) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.projetoRepository = projetoRepository;
        this.tarefaRepository = tarefaRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public DashboardResumoDTO buscarResumo(Long empresaId) {
        Integer anoAtual = LocalDate.now().getYear();

        List<DesempenhoEquipeDTO> desempenhoEquipe = tarefaRepository
                .countDesempenhoEquipePorMes(empresaId, anoAtual)
                .stream()
                .map(item -> DesempenhoEquipeDTO.builder()
                        .mes(item.getMes())
                        .total(item.getTotal())
                        .build())
                .toList();

        return DashboardResumoDTO.builder()
                .usuariosAtivos(usuarioEmpresaRepository.countUsuariosAtivosPorEmpresa(empresaId))
                .projetosAtivos(projetoRepository.countProjetosAtivosPorEmpresa(empresaId))
                .tarefasConcluidas(tarefaRepository.countTarefasConcluidasPorEmpresa(empresaId))
                .tarefasEmAndamento(tarefaRepository.countTarefasEmAndamentoPorEmpresa(empresaId))
                .tarefasPendentes(tarefaRepository.countTarefasPendentesPorEmpresa(empresaId))
                .tarefasAtrasadas(tarefaRepository.countTarefasAtrasadasPorEmpresa(empresaId))
                .feedbacks(feedbackRepository.countFeedbacksPorEmpresa(empresaId))
                .desempenhoEquipe(desempenhoEquipe)
                .build();
    }
}
