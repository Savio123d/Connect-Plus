package conne.connect.connect.Resgate.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Resgate.model.PedidoResgateItemModel;
import conne.connect.connect.Resgate.repository.PedidoResgateItemRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoResgateItemService {

    private final PedidoResgateItemRepository pedidoResgateItemRepository;
    private final AutorizacaoService autorizacaoService;

    public PedidoResgateItemService(PedidoResgateItemRepository pedidoResgateItemRepository, AutorizacaoService autorizacaoService) {
        this.pedidoResgateItemRepository = pedidoResgateItemRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<PedidoResgateItemModel> findAll() {
        return pedidoResgateItemRepository.findByIdPedidoResgate_IdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public PedidoResgateItemModel criarPedidoResgateItem(PedidoResgateItemModel pedidoResgateItemModel) {
        validarEscopo(pedidoResgateItemModel);
        return pedidoResgateItemRepository.save(pedidoResgateItemModel);
    }

    @Transactional(readOnly = true)
    public Optional<PedidoResgateItemModel> buscarPorId(Long idPedidoResgateItem) {
        return pedidoResgateItemRepository.findByIdPedidoResgateItemAndIdPedidoResgate_IdEmpresa_IdEmpresaAndExcluidoIsNull(idPedidoResgateItem, autorizacaoService.empresaAtual());
    }

    @Transactional
    public PedidoResgateItemModel atualizarPedidoResgateItem(Long idPedidoResgateItem, PedidoResgateItemModel pedidoResgateItemModel) {
        validarEscopo(pedidoResgateItemModel);
        PedidoResgateItemModel pedidoResgateItem = buscarItemExistente(idPedidoResgateItem);
        pedidoResgateItem.setIdPedidoResgate(pedidoResgateItemModel.getIdPedidoResgate());
        pedidoResgateItem.setIdRecompensa(pedidoResgateItemModel.getIdRecompensa());
        pedidoResgateItem.setXpUnitario(pedidoResgateItemModel.getXpUnitario());
        pedidoResgateItem.setQuantidade(pedidoResgateItemModel.getQuantidade());
        return pedidoResgateItemRepository.save(pedidoResgateItem);
    }

    @Transactional
    public void excluirPedidoResgateItem(Long idPedidoResgateItem) {
        PedidoResgateItemModel pedidoResgateItem = buscarItemExistente(idPedidoResgateItem);
        pedidoResgateItem.setExcluido(LocalDate.now());
        pedidoResgateItemRepository.save(pedidoResgateItem);
    }

    private void validarEscopo(PedidoResgateItemModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdPedidoResgate() != null
                && registro.getIdPedidoResgate().getIdEmpresa() != null
                ? registro.getIdPedidoResgate().getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdRecompensa() != null
                && registro.getIdRecompensa().getIdEmpresa() != null
                ? registro.getIdRecompensa().getIdEmpresa().getIdEmpresa() : null);
    }

    private PedidoResgateItemModel buscarItemExistente(Long idPedidoResgateItem) {
        return buscarPorId(idPedidoResgateItem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do pedido de resgate não encontrado."));
    }
}
