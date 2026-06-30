package conne.connect.connect.Perfil.service;

import conne.connect.connect.Perfil.dto.ConquistaPerfilDTO;
import conne.connect.connect.Perfil.dto.HistoricoDesempenhoDTO;
import conne.connect.connect.Perfil.dto.PerfilResponseDTO;
import conne.connect.connect.Perfil.dto.PerfilUsuarioDTO;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.model.UsuarioModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import conne.connect.connect.Xp.repository.TransacaoXpRepository;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilService {

    private static final int XP_POR_NIVEL = 500;
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MM/yyyy");

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final SaldoXpRepository saldoXpRepository;
    private final TransacaoXpRepository transacaoXpRepository;

    public PerfilService(
            UsuarioEmpresaRepository usuarioEmpresaRepository,
            SaldoXpRepository saldoXpRepository,
            TransacaoXpRepository transacaoXpRepository
    ) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.saldoXpRepository = saldoXpRepository;
        this.transacaoXpRepository = transacaoXpRepository;
    }

    public PerfilResponseDTO buscarPerfil(Long idUsuarioEmpresa) {
        if (idUsuarioEmpresa == null || idUsuarioEmpresa < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario empresa invalido.");
        }

        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository
                .findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario empresa nao encontrado."
                ));

        int xpAtual = buscarXpAtual(idUsuarioEmpresa);

        return new PerfilResponseDTO(
                montarUsuario(usuarioEmpresa, xpAtual),
                montarConquistas(xpAtual),
                montarHistorico(idUsuarioEmpresa)
        );
    }

    private PerfilUsuarioDTO montarUsuario(UsuarioEmpresaModel usuarioEmpresa, int xpAtual) {
        UsuarioModel usuario = usuarioEmpresa.getIdUsuario();

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario do perfil nao encontrado.");
        }

        int nivel = calcularNivel(xpAtual);

        String departamento = usuarioEmpresa.getIdSetor() != null
                ? usuarioEmpresa.getIdSetor().getNome()
                : "Nao informado";

        String cargo = usuarioEmpresa.getPapel() != null
                ? formatarCargo(usuarioEmpresa.getPapel().name())
                : "Colaborador";

        return new PerfilUsuarioDTO(
                usuario.getIdUsuario(),
                usuarioEmpresa.getIdUsuarioEmpresa(),
                usuario.getNome(),
                usuario.getEmail(),
                cargo,
                departamento,
                nivel,
                xpAtual,
                proximoNivel(nivel)
        );
    }

    private int buscarXpAtual(Long idUsuarioEmpresa) {
        return saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresa(idUsuarioEmpresa)
                .map(saldo -> saldo.getXpTotal() != null ? saldo.getXpTotal() : 0)
                .orElse(0);
    }

    private List<ConquistaPerfilDTO> montarConquistas(int xpAtual) {
        List<ConquistaPerfilDTO> conquistas = new ArrayList<>();

        conquistas.add(new ConquistaPerfilDTO("Perfil ativo", "OK", "azul"));

        if (xpAtual >= 100) {
            conquistas.add(new ConquistaPerfilDTO("Primeiros 100 XP", "100", "verde"));
        }

        if (xpAtual >= 500) {
            conquistas.add(new ConquistaPerfilDTO("Nivel 2 alcancado", "N2", "roxo"));
        }

        if (xpAtual >= 1000) {
            conquistas.add(new ConquistaPerfilDTO("Mil XP", "1K", "laranja"));
        }

        return conquistas;
    }

    private List<HistoricoDesempenhoDTO> montarHistorico(Long idUsuarioEmpresa) {
        List<TransacaoXpModel> transacoes = transacaoXpRepository
                .findTop50ByIdUsuarioEmpresa_IdUsuarioEmpresaOrderByDataCriacaoDesc(idUsuarioEmpresa);

        Map<YearMonth, List<TransacaoXpModel>> transacoesPorMes = transacoes.stream()
                .filter(transacao -> transacao.getDataCriacao() != null)
                .collect(Collectors.groupingBy(
                        transacao -> YearMonth.from(transacao.getDataCriacao()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return transacoesPorMes.entrySet().stream()
                .map(entry -> new HistoricoDesempenhoDTO(
                        FORMATO_MES.format(entry.getKey()),
                        contarTarefasConcluidas(entry.getValue()),
                        somarXpGanho(entry.getValue())
                ))
                .toList();
    }

    private int contarTarefasConcluidas(List<TransacaoXpModel> transacoes) {
        return (int) transacoes.stream()
                .filter(transacao -> transacao.getIdTarefa() != null)
                .filter(transacao -> transacao.getValor() != null && transacao.getValor() > 0)
                .count();
    }

    private int somarXpGanho(List<TransacaoXpModel> transacoes) {
        return transacoes.stream()
                .map(TransacaoXpModel::getValor)
                .filter(valor -> valor != null && valor > 0)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int calcularNivel(int xpAtual) {
        return (xpAtual / XP_POR_NIVEL) + 1;
    }

    private int proximoNivel(int nivelAtual) {
        return Math.max(nivelAtual, 1) * XP_POR_NIVEL;
    }

    private String formatarCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            return "Colaborador";
        }

        String cargoFormatado = cargo.toLowerCase(Locale.ROOT).replace("_", " ");
        return cargoFormatado.substring(0, 1).toUpperCase(Locale.ROOT) + cargoFormatado.substring(1);
    }
}
