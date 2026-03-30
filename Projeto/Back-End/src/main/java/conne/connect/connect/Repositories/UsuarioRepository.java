package conne.connect.connect.Repositories;

import conne.connect.connect.Models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdUsuarioNot(String email, Long idUsuario);
}
