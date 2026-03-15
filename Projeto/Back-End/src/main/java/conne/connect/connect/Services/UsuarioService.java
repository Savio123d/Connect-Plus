package conne.connect.connect.Services;
import conne.connect.connect.Models.UsuarioModel;
import conne.connect.connect.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioModel> findAll(){
        return usuarioRepository.findAll();
    }

    public UsuarioModel criarUsuario(UsuarioModel usuarioModel){
        return usuarioRepository.save(usuarioModel);
    }

    public Optional<UsuarioModel> buscarPorId(Long idUsuario){
        return usuarioRepository.findById(idUsuario);
    }

    public UsuarioModel atualizarUsuario(Long id, UsuarioModel usuarioModel){
    UsuarioModel usuario = usuarioRepository.findById(id).get();
    usuario.setNome(usuarioModel.getNome());
    usuario.setEmail(usuarioModel.getEmail());
    usuario.setSenha(usuarioModel.getSenha());
    return usuarioRepository.save(usuario);
    }

    public void excluirUsuario(Long idUsuario){
        usuarioRepository.deleteById(idUsuario);
    }
}
