package conne.connect.connect.Perfil.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Perfil.dto.ConquistaPerfilDTO;
import conne.connect.connect.Perfil.dto.HistoricoDesempenhoDTO;
import conne.connect.connect.Perfil.dto.PerfilResponseDTO;
import conne.connect.connect.Perfil.dto.PerfilUsuarioDTO;
import conne.connect.connect.Perfil.repository.PerfilRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final AutorizacaoService autorizacaoService;

    public PerfilService(PerfilRepository perfilRepository, AutorizacaoService autorizacaoService) {
        this.perfilRepository = perfilRepository;
        this.autorizacaoService = autorizacaoService;
    }

    public PerfilResponseDTO buscarPerfil(Long idUsuarioEmpresa) {
        autorizacaoService.validarAcessoAoVinculo(idUsuarioEmpresa);
        if (idUsuarioEmpresa == null || idUsuarioEmpresa <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID do usuário empresa inválido."
            );
        }

        PerfilUsuarioDTO usuario = perfilRepository.buscarUsuarioPerfil(idUsuarioEmpresa);

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Perfil não encontrado."
            );
        }

        Long tarefasConcluidas = perfilRepository.contarTarefasConcluidas(idUsuarioEmpresa);

        List<ConquistaPerfilDTO> conquistas = montarConquistas(
                usuario.xpAtual(),
                tarefasConcluidas
        );

        List<HistoricoDesempenhoDTO> historico =
                perfilRepository.buscarHistorico(idUsuarioEmpresa);

        return new PerfilResponseDTO(
                usuario,
                conquistas,
                historico
        );
    }

    private List<ConquistaPerfilDTO> montarConquistas(
            Integer xpAtual,
            Long tarefasConcluidas
    ) {
        List<ConquistaPerfilDTO> conquistas = new ArrayList<>();

        if (tarefasConcluidas != null && tarefasConcluidas >= 1) {
            conquistas.add(new ConquistaPerfilDTO(
                    "Primeira Tarefa",
                    "◎",
                    "verde"
            ));
        }

        if (tarefasConcluidas != null && tarefasConcluidas >= 10) {
            conquistas.add(new ConquistaPerfilDTO(
                    "10 Tarefas",
                    "🏆",
                    "laranja"
            ));
        }

        if (tarefasConcluidas != null && tarefasConcluidas >= 100) {
            conquistas.add(new ConquistaPerfilDTO(
                    "100 Tarefas",
                    "☆",
                    "azul"
            ));
        }

        if (xpAtual != null && xpAtual >= 1000) {
            conquistas.add(new ConquistaPerfilDTO(
                    "MVP do Mês",
                    "♙",
                    "roxo"
            ));
        }

        return conquistas;
    }
}