package conne.connect.connect.Resgate.service;

import conne.connect.connect.Resgate.model.PedidoResgateModel;
import conne.connect.connect.Resgate.repository.PedidoResgateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoResgateService {

    @Autowired
    private PedidoResgateRepository pedidoResgateRepository;

    public List<PedidoResgateModel> findAll() {
        return pedidoResgateRepository.findAll();
    }

    public PedidoResgateModel criarPedidoResgate(PedidoResgateModel pedidoResgateModel) {
        return pedidoResgateRepository.save(pedidoResgateModel);
    }

    public Optional<PedidoResgateModel> buscarPorId(Long idPedidoResgate) {
        return pedidoResgateRepository.findById(idPedidoResgate);
    }

    public PedidoResgateModel atualizarPedidoResgate(Long idPedidoResgate, PedidoResgateModel pedidoResgateModel) {
        PedidoResgateModel pedidoResgate = pedidoResgateRepository.findById(idPedidoResgate).get();
        pedidoResgate.setIdEmpresa(pedidoResgateModel.getIdEmpresa());
        pedidoResgate.setIdUsuarioEmpresa(pedidoResgateModel.getIdUsuarioEmpresa());
        pedidoResgate.setXpTotal(pedidoResgateModel.getXpTotal());
        return pedidoResgateRepository.save(pedidoResgate);
    }

    public void excluirPedidoResgate(Long idPedidoResgate) {
        pedidoResgateRepository.deleteById(idPedidoResgate);
    }
}
