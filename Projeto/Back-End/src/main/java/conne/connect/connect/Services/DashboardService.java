package conne.connect.connect.Services;

import conne.connect.connect.Dto.DashboardResumoDTO;
import conne.connect.connect.Dto.DesempenhoEquipeDTO;
import conne.connect.connect.Repositories.FeedbackRepository;
import conne.connect.connect.Repositories.ProjetoRepository;
import conne.connect.connect.Repositories.TarefaRepository;
import conne.connect.connect.Repositories.UsuarioEmpresaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
