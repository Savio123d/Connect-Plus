package conne.connect.connect.Recompensa.service;

import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.repository.EmpresaRepository;
import conne.connect.connect.Recompensa.dto.LojaItemDTO;
import conne.connect.connect.Recompensa.dto.LojaItemRequestDTO;
import conne.connect.connect.Recompensa.dto.LojaResgateRequestDTO;
import conne.connect.connect.Recompensa.model.RecompensaModel;
import conne.connect.connect.Recompensa.repository.RecompensaRepository;
import conne.connect.connect.Resgate.model.ResgateRecompensaModel;
import conne.connect.connect.Resgate.repository.ResgateRecompensaRepository;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import conne.connect.connect.Xp.enums.TipoTransacaoXp;
import conne.connect.connect.Xp.model.SaldoXpModel;
import conne.connect.connect.Xp.model.TransacaoXpModel;
import conne.connect.connect.Xp.repository.SaldoXpRepository;
import conne.connect.connect.Xp.repository.TransacaoXpRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Autowired
    private SaldoXpRepository saldoXpRepository;

    @Autowired
    private TransacaoXpRepository transacaoXpRepository;

    @Autowired
    private ResgateRecompensaRepository resgateRecompensaRepository;

    public List<RecompensaModel> findAll() {
        return recompensaRepository.findAll();
    }

    public RecompensaModel criarRecompensa(RecompensaModel recompensaModel) {
        return recompensaRepository.save(recompensaModel);
    }

    public Optional<RecompensaModel> buscarPorId(Long idRecompensa) {
        return recompensaRepository.findById(idRecompensa);
    }

    public RecompensaModel atualizarRecompensa(Long idRecompensa, RecompensaModel recompensaModel) {
        RecompensaModel recompensa = recompensaRepository.findById(idRecompensa).get();
        recompensa.setIdEmpresa(recompensaModel.getIdEmpresa());
        recompensa.setNome(recompensaModel.getNome());
        recompensa.setDescricao(recompensaModel.getDescricao());
        recompensa.setXpNecessario(recompensaModel.getXpNecessario());
        recompensa.setAtiva(recompensaModel.getAtiva());
        return recompensaRepository.save(recompensa);
    }

    public void excluirRecompensa(Long idRecompensa) {
        recompensaRepository.deleteById(idRecompensa);
    }

    public List<LojaItemDTO> listarItensLoja(Long idEmpresa, Long idUsuarioEmpresa, boolean somenteAtivas) {
        validarIdEmpresa(idEmpresa);

        List<RecompensaModel> recompensas = somenteAtivas
                ? recompensaRepository.findByIdEmpresa_IdEmpresaAndAtivaTrueAndExcluidoIsNullOrderByNomeAsc(idEmpresa)
                : recompensaRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNullOrderByNomeAsc(idEmpresa);

        return recompensas.stream()
                .map(recompensa -> toLojaItemDTO(recompensa, idUsuarioEmpresa))
                .collect(Collectors.toList());
    }

    public LojaItemDTO buscarItemLoja(Long idRecompensa, Long idEmpresa, Long idUsuarioEmpresa) {
        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, idEmpresa);
        return toLojaItemDTO(recompensa, idUsuarioEmpresa);
    }

    @Transactional
    public LojaItemDTO criarItemLoja(LojaItemRequestDTO request) {
        EmpresaModel empresa = buscarEmpresa(request.getIdEmpresa());

        RecompensaModel recompensa = new RecompensaModel();
        recompensa.setIdEmpresa(empresa);
        aplicarRequest(recompensa, request);

        return toLojaItemDTO(recompensaRepository.save(recompensa), null);
    }

    @Transactional
    public LojaItemDTO atualizarItemLoja(Long idRecompensa, LojaItemRequestDTO request) {
        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, request.getIdEmpresa());
        aplicarRequest(recompensa, request);

        return toLojaItemDTO(recompensaRepository.save(recompensa), null);
    }

    @Transactional
    public void excluirItemLoja(Long idRecompensa, Long idEmpresa) {
        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, idEmpresa);
        recompensa.setAtiva(false);
        recompensa.setExcluido(LocalDate.now());
        recompensaRepository.save(recompensa);
    }

    @Transactional
    public LojaItemDTO esgotarItemLoja(Long idRecompensa, Long idEmpresa) {
        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, idEmpresa);
        recompensa.setQuantidadeDisponivel(0);
        recompensa.setAtiva(false);

        return toLojaItemDTO(recompensaRepository.save(recompensa), null);
    }

    @Transactional
    public LojaItemDTO reporItemLoja(Long idRecompensa, Long idEmpresa, Integer quantidade) {
        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, idEmpresa);
        int quantidadeAtual = recompensa.getQuantidadeDisponivel() == null ? 0 : recompensa.getQuantidadeDisponivel();
        int quantidadeReposicao = quantidade == null || quantidade < 1 ? 1 : quantidade;

        recompensa.setQuantidadeDisponivel(quantidadeAtual + quantidadeReposicao);
        recompensa.setAtiva(true);

        return toLojaItemDTO(recompensaRepository.save(recompensa), null);
    }

    @Transactional
    public LojaItemDTO resgatarItemLoja(Long idRecompensa, LojaResgateRequestDTO request) {
        Long idEmpresa = request.getIdEmpresa();
        Long idUsuarioEmpresa = request.getIdUsuarioEmpresa();
        int quantidade = request.getQuantidade() == null || request.getQuantidade() < 1 ? 1 : request.getQuantidade();

        RecompensaModel recompensa = buscarRecompensaDaEmpresa(idRecompensa, idEmpresa);
        UsuarioEmpresaModel usuarioEmpresa = buscarUsuarioEmpresa(idUsuarioEmpresa);
        validarVinculoEmpresa(usuarioEmpresa, idEmpresa);

        if (Boolean.FALSE.equals(recompensa.getAtiva())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recompensa indisponivel para resgate.");
        }

        if (recompensa.getQuantidadeDisponivel() != null && recompensa.getQuantidadeDisponivel() < quantidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade insuficiente em estoque.");
        }

        int xpGasto = recompensa.getXpNecessario() * quantidade;
        SaldoXpModel saldo = saldoXpRepository
                .findByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdEmpresa_IdEmpresa(idUsuarioEmpresa, idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo XP nao encontrado para este usuario."));

        if (saldo.getXpTotal() < xpGasto) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo XP insuficiente para resgatar esta recompensa.");
        }

        saldo.setXpTotal(saldo.getXpTotal() - xpGasto);
        saldoXpRepository.save(saldo);

        TransacaoXpModel transacao = new TransacaoXpModel();
        transacao.setIdEmpresa(recompensa.getIdEmpresa());
        transacao.setIdUsuarioEmpresa(usuarioEmpresa);
        transacao.setIdRecompensa(recompensa);
        transacao.setTipo(TipoTransacaoXp.resgate);
        transacao.setValor(xpGasto);
        transacao.setObservacao("Resgate na loja: " + recompensa.getNome());
        transacao = transacaoXpRepository.save(transacao);

        ResgateRecompensaModel resgate = new ResgateRecompensaModel();
        resgate.setIdEmpresa(recompensa.getIdEmpresa());
        resgate.setIdUsuarioEmpresa(usuarioEmpresa);
        resgate.setIdRecompensa(recompensa);
        resgate.setIdTransacaoXp(transacao);
        resgate.setQuantidade(quantidade);
        resgate.setXpGasto(xpGasto);
        resgateRecompensaRepository.save(resgate);

        if (recompensa.getQuantidadeDisponivel() != null) {
            int novaQuantidade = recompensa.getQuantidadeDisponivel() - quantidade;
            recompensa.setQuantidadeDisponivel(novaQuantidade);

            if (novaQuantidade <= 0) {
                recompensa.setAtiva(false);
            }

            recompensaRepository.save(recompensa);
        }

        return toLojaItemDTO(recompensa, idUsuarioEmpresa);
    }

    private LojaItemDTO toLojaItemDTO(RecompensaModel recompensa, Long idUsuarioEmpresa) {
        LojaItemDTO dto = new LojaItemDTO();
        dto.setId(recompensa.getIdRecompensa());
        dto.setIdLoja(recompensa.getIdRecompensa());
        dto.setIdEmpresa(recompensa.getIdEmpresa().getIdEmpresa());
        dto.setNome(recompensa.getNome());
        dto.setDescricao(recompensa.getDescricao());
        dto.setCustoXp(recompensa.getXpNecessario());
        dto.setQuantidadeDisponivel(recompensa.getQuantidadeDisponivel());
        dto.setCategoria(recompensa.getCategoria());
        dto.setIcone(recompensa.getIcone());
        dto.setCor(recompensa.getCor());
        dto.setAtiva(recompensa.getAtiva());
        dto.setDataCriacao(recompensa.getDataCriacao());
        dto.setDataAtualizacao(recompensa.getDataAtualizacao());
        dto.setResgatada(idUsuarioEmpresa != null
                && resgateRecompensaRepository.existsByIdUsuarioEmpresa_IdUsuarioEmpresaAndIdRecompensa_IdRecompensaAndStatusAndExcluidoIsNull(
                        idUsuarioEmpresa,
                        recompensa.getIdRecompensa(),
                        "resgatado"
                ));
        return dto;
    }

    private void aplicarRequest(RecompensaModel recompensa, LojaItemRequestDTO request) {
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da recompensa e obrigatorio.");
        }

        if (request.getCustoXp() == null || request.getCustoXp() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custo em XP deve ser maior que zero.");
        }

        recompensa.setNome(request.getNome().trim());
        recompensa.setDescricao(request.getDescricao());
        recompensa.setXpNecessario(request.getCustoXp());
        recompensa.setQuantidadeDisponivel(request.getQuantidadeDisponivel());
        recompensa.setCategoria(valorPadrao(request.getCategoria(), "Beneficio"));
        recompensa.setIcone(valorPadrao(request.getIcone(), "Presente"));
        recompensa.setCor(valorPadrao(request.getCor(), "Azul"));
        recompensa.setAtiva(request.getAtiva() == null || request.getAtiva());
    }

    private RecompensaModel buscarRecompensaDaEmpresa(Long idRecompensa, Long idEmpresa) {
        validarIdEmpresa(idEmpresa);

        return recompensaRepository
                .findByIdRecompensaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idRecompensa, idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recompensa nao encontrada para esta empresa."));
    }

    private EmpresaModel buscarEmpresa(Long idEmpresa) {
        validarIdEmpresa(idEmpresa);

        return empresaRepository
                .findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa nao encontrada."));
    }

    private UsuarioEmpresaModel buscarUsuarioEmpresa(Long idUsuarioEmpresa) {
        if (idUsuarioEmpresa == null || idUsuarioEmpresa < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario empresa e obrigatorio.");
        }

        return usuarioEmpresaRepository
                .findById(idUsuarioEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario empresa nao encontrado."));
    }

    private void validarVinculoEmpresa(UsuarioEmpresaModel usuarioEmpresa, Long idEmpresa) {
        if (!usuarioEmpresa.getIdEmpresa().getIdEmpresa().equals(idEmpresa)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario nao pertence a empresa informada.");
        }
    }

    private void validarIdEmpresa(Long idEmpresa) {
        if (idEmpresa == null || idEmpresa < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa e obrigatoria para acessar a loja.");
        }
    }

    private String valorPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor.trim();
    }
}
