package conne.connect.connect.Resgate.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Resgate.model.PedidoResgateModel;
import conne.connect.connect.Resgate.repository.PedidoResgateRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoResgateService {

    private final PedidoResgateRepository pedidoResgateRepository;
    private final AutorizacaoService autorizacaoService;

    public PedidoResgateService(PedidoResgateRepository pedidoResgateRepository, AutorizacaoService autorizacaoService) {
        this.pedidoResgateRepository = pedidoResgateRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<PedidoResgateModel> findAll() {
        return pedidoResgateRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public PedidoResgateModel criarPedidoResgate(PedidoResgateModel pedidoResgateModel) {
        validarEscopo(pedidoResgateModel);
        return pedidoResgateRepository.save(pedidoResgateModel);
    }

    @Transactional(readOnly = true)
    public Optional<PedidoResgateModel> buscarPorId(Long idPedidoResgate) {
        return pedidoResgateRepository.findByIdPedidoResgateAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idPedidoResgate, autorizacaoService.empresaAtual());
    }

    @Transactional
    public PedidoResgateModel atualizarPedidoResgate(Long idPedidoResgate, PedidoResgateModel pedidoResgateModel) {
        validarEscopo(pedidoResgateModel);
        PedidoResgateModel pedidoResgate = buscarPedidoExistente(idPedidoResgate);
        pedidoResgate.setIdUsuarioEmpresa(pedidoResgateModel.getIdUsuarioEmpresa());
        pedidoResgate.setXpTotal(pedidoResgateModel.getXpTotal());
        return pedidoResgateRepository.save(pedidoResgate);
    }

    @Transactional
    public void excluirPedidoResgate(Long idPedidoResgate) {
        PedidoResgateModel pedidoResgate = buscarPedidoExistente(idPedidoResgate);
        pedidoResgate.setExcluido(LocalDate.now());
        pedidoResgateRepository.save(pedidoResgate);
    }

    private void validarEscopo(PedidoResgateModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdUsuarioEmpresa() != null
                && registro.getIdUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
    }

    private PedidoResgateModel buscarPedidoExistente(Long idPedidoResgate) {
        return buscarPorId(idPedidoResgate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido de resgate não encontrado."));
    }
}
