package conne.connect.connect.Usuario.service;

import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioEmpresaService {

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    public List<UsuarioEmpresaModel> findAll() {
        return usuarioEmpresaRepository.findAll();
    }

    public UsuarioEmpresaModel criarUsuarioEmpresa(UsuarioEmpresaModel usuarioEmpresaModel) {
        return usuarioEmpresaRepository.save(usuarioEmpresaModel);
    }

    public Optional<UsuarioEmpresaModel> buscarPorId(Long idUsuarioEmpresa) {
        return usuarioEmpresaRepository.findById(idUsuarioEmpresa);
    }

    public UsuarioEmpresaModel atualizarUsuarioEmpresa(Long idUsuarioEmpresa, UsuarioEmpresaModel usuarioEmpresaModel) {
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaRepository.findById(idUsuarioEmpresa).get();
        usuarioEmpresa.setIdEmpresa(usuarioEmpresaModel.getIdEmpresa());
        usuarioEmpresa.setIdUsuario(usuarioEmpresaModel.getIdUsuario());
        usuarioEmpresa.setIdSetor(usuarioEmpresaModel.getIdSetor());
        usuarioEmpresa.setPapel(usuarioEmpresaModel.getPapel());
        usuarioEmpresa.setAtivo(usuarioEmpresaModel.getAtivo());
        return usuarioEmpresaRepository.save(usuarioEmpresa);
    }

    public void excluirUsuarioEmpresa(Long idUsuarioEmpresa) {
        usuarioEmpresaRepository.deleteById(idUsuarioEmpresa);
    }
}
