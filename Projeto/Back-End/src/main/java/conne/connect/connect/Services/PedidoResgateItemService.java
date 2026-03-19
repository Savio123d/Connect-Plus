package conne.connect.connect.Services;

import conne.connect.connect.Models.PedidoResgateItemModel;
import conne.connect.connect.Repositories.PedidoResgateItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoResgateItemService {

    @Autowired
    private PedidoResgateItemRepository pedidoResgateItemRepository;

    public List<PedidoResgateItemModel> findAll() {
        return pedidoResgateItemRepository.findAll();
    }

    public PedidoResgateItemModel criarPedidoResgateItem(PedidoResgateItemModel pedidoResgateItemModel) {
        return pedidoResgateItemRepository.save(pedidoResgateItemModel);
    }

    public Optional<PedidoResgateItemModel> buscarPorId(Long idPedidoResgateItem) {
        return pedidoResgateItemRepository.findById(idPedidoResgateItem);
    }

    public PedidoResgateItemModel atualizarPedidoResgateItem(Long idPedidoResgateItem, PedidoResgateItemModel pedidoResgateItemModel) {
        PedidoResgateItemModel pedidoResgateItem = pedidoResgateItemRepository.findById(idPedidoResgateItem).get();
        pedidoResgateItem.setIdPedidoResgate(pedidoResgateItemModel.getIdPedidoResgate());
        pedidoResgateItem.setIdRecompensa(pedidoResgateItemModel.getIdRecompensa());
        pedidoResgateItem.setXpUnitario(pedidoResgateItemModel.getXpUnitario());
        pedidoResgateItem.setQuantidade(pedidoResgateItemModel.getQuantidade());
        return pedidoResgateItemRepository.save(pedidoResgateItem);
    }

    public void excluirPedidoResgateItem(Long idPedidoResgateItem) {
        pedidoResgateItemRepository.deleteById(idPedidoResgateItem);
    }
}
