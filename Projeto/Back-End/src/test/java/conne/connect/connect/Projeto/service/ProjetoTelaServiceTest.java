package conne.connect.connect.Projeto.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Feedback.service.FeedbackService;
import conne.connect.connect.Projeto.dto.ProjetoResumoDTO;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import conne.connect.connect.Projeto.mapper.ProjetoTelaMapper;
import conne.connect.connect.Projeto.repository.PessoaProjetoTelaRepository;
import conne.connect.connect.Projeto.repository.ProjetoTelaRepository;
import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Tarefa.repository.TarefaRepository;
import conne.connect.connect.Tarefa.service.TarefaService;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjetoTelaServiceTest {

    @Mock
    private ProjetoTelaRepository projetoRepository;

    @Mock
    private PessoaProjetoTelaRepository pessoaRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Mock
    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private ProjetoTelaMapper mapper;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private AutorizacaoService autorizacaoService;

    @InjectMocks
    private ProjetoTelaService service;

    @Test
    void listarUsaProjecaoLeveSemMontarProjetosCompletos() {
        Long empresaId = 7L;
        List<ProjetoResumoDTO> resumos = List.of(new ProjetoResumoDTO(
            1L,
            "Portal",
            "Projeto principal",
            ProjetoStatusTela.em_andamento,
            false,
            LocalDate.of(2026, 8, 20),
            40,
            3L,
            "Ana Silva",
            "AS"
        ));
        when(projetoRepository.listarResumosPorEmpresa(empresaId)).thenReturn(resumos);

        List<ProjetoResumoDTO> resultado = service.listarResumos(empresaId);

        assertSame(resumos, resultado);
        verify(autorizacaoService).validarEmpresaAtual(empresaId);
        verify(projetoRepository).listarResumosPorEmpresa(empresaId);
        verifyNoInteractions(mapper);
    }

    @Test
    void listarUsuariosDisponiveisNaoSincronizaNemGravaPessoas() {
        Long empresaId = 7L;
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Ana Silva");
        usuario.setEmail("ana@example.com");

        UsuarioEmpresaModel vinculo = new UsuarioEmpresaModel();
        vinculo.setIdUsuarioEmpresa(15L);
        vinculo.setIdUsuario(usuario);
        vinculo.setAtivo(true);

        when(usuarioEmpresaRepository.findByIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(empresaId))
            .thenReturn(List.of(vinculo));

        var resultado = service.listarUsuariosDisponiveis(empresaId);

        assertAll(
            () -> assertEquals(1, resultado.size()),
            () -> assertEquals(15L, resultado.getFirst().id()),
            () -> assertEquals("Ana Silva", resultado.getFirst().nome()),
            () -> assertEquals(0, resultado.getFirst().horasTrabalhadas())
        );
        verify(autorizacaoService).validarEmpresaAtual(empresaId);
        verifyNoInteractions(pessoaRepository, mapper);
    }
}
