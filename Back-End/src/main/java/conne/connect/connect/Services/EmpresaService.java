package conne.connect.connect.Services;

import conne.connect.connect.Models.EmpresaModel;
import conne.connect.connect.Repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<EmpresaModel> findAll() {
        return empresaRepository.findAll();
    }

    public EmpresaModel criarEmpresa(EmpresaModel empresaModel) {
        return empresaRepository.save(empresaModel);
    }

    public Optional<EmpresaModel> buscarPorId(Long idEmpresa) {
        return empresaRepository.findById(idEmpresa);
    }

    public EmpresaModel atualizarEmpresa(Long idEmpresa, EmpresaModel empresaModel) {
        EmpresaModel empresa = empresaRepository.findById(idEmpresa).get();
        empresa.setRazaoSocial(empresaModel.getRazaoSocial());
        empresa.setNomeFantasia(empresaModel.getNomeFantasia());
        empresa.setCnpj(empresaModel.getCnpj());
        empresa.setStatus(empresaModel.getStatus());
        return empresaRepository.save(empresa);
    }

    public void excluirEmpresa(Long idEmpresa) {
        empresaRepository.deleteById(idEmpresa);
    }
}
