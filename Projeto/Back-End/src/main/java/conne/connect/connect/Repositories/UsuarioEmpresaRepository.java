package conne.connect.connect.Repositories;

import conne.connect.connect.Models.UsuarioEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresaModel, Long> {

    List<UsuarioEmpresaModel> findByIdEmpresa_IdEmpresa(Long idEmpresa);

    Optional<UsuarioEmpresaModel> findByIdUsuario_IdUsuario(Long idUsuario);

    Optional<UsuarioEmpresaModel> findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(
            Long idUsuario
    );
}