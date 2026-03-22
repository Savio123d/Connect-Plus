package conne.connect.connect.Services;

import conne.connect.connect.Models.ProjetoModel;
import conne.connect.connect.Models.ProjetoUsuarioModel;
import conne.connect.connect.Models.UsuarioEmpresaModel;
import conne.connect.connect.Repositories.ProjetoRepository;
import conne.connect.connect.Repositories.ProjetoUsuarioRepository;
import conne.connect.connect.Repositories.UsuarioEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ProjetoUsuarioService {

    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioEmpresaRepository usuarioEmpresaRepository;

    public List<ProjetoUsuarioModel> findAll() {
        return projetoUsuarioRepository.findAll();
    }

    public ProjetoUsuarioModel criarProjetoUsuario(ProjetoUsuarioModel projetoUsuarioModel) {
        Optional<ProjetoModel> projetoOptional = projetoRepository.findById(projetoUsuarioModel.getIdProjeto());
        Optional<UsuarioEmpresaModel> usuarioEmpresaOptional = usuarioEmpresaRepository.findById(projetoUsuarioModel.getIdUsuarioEmpresa());

        if (projetoOptional.isEmpty()) {
            throw new RuntimeException("Projeto não encontrado");
        }

        if (usuarioEmpresaOptional.isEmpty()) {
            throw new RuntimeException("Usuário da empresa não encontrado");
        }

        ProjetoModel projeto = projetoOptional.get();
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaOptional.get();

        if (!projeto.getIdEmpresa().equals(usuarioEmpresa.getIdEmpresa())) {
            throw new RuntimeException("Projeto e usuário não pertencem à mesma empresa");
        }

        projetoUsuarioModel.setIdEmpresa(projeto.getIdEmpresa());

        return projetoUsuarioRepository.save(projetoUsuarioModel);
    }

    public Optional<ProjetoUsuarioModel> buscarPorId(Long idProjetoUsuario) {
        return projetoUsuarioRepository.findById(idProjetoUsuario);
    }

    public ProjetoUsuarioModel atualizarProjetoUsuario(Long idProjetoUsuario, ProjetoUsuarioModel projetoUsuarioModel) {
        ProjetoUsuarioModel projetoUsuario = projetoUsuarioRepository.findById(idProjetoUsuario).get();

        Optional<ProjetoModel> projetoOptional = projetoRepository.findById(projetoUsuarioModel.getIdProjeto());
        Optional<UsuarioEmpresaModel> usuarioEmpresaOptional = usuarioEmpresaRepository.findById(projetoUsuarioModel.getIdUsuarioEmpresa());

        if (projetoOptional.isEmpty()) {
            throw new RuntimeException("Projeto não encontrado");
        }

        if (usuarioEmpresaOptional.isEmpty()) {
            throw new RuntimeException("Usuário da empresa não encontrado");
        }

        ProjetoModel projeto = projetoOptional.get();
        UsuarioEmpresaModel usuarioEmpresa = usuarioEmpresaOptional.get();

        if (!projeto.getIdEmpresa().equals(usuarioEmpresa.getIdEmpresa())) {
            throw new RuntimeException("Projeto e usuário não pertencem à mesma empresa");
        }

        projetoUsuario.setIdEmpresa(projeto.getIdEmpresa());
        projetoUsuario.setIdProjeto(projetoUsuarioModel.getIdProjeto());
        projetoUsuario.setIdUsuarioEmpresa(projetoUsuarioModel.getIdUsuarioEmpresa());

        return projetoUsuarioRepository.save(projetoUsuario);
    }

    public void excluirProjetoUsuario(Long idProjetoUsuario) {
        projetoUsuarioRepository.deleteById(idProjetoUsuario);
    }
}
