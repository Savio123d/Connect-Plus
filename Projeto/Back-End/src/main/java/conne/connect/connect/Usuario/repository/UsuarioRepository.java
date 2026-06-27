package conne.connect.connect.Usuario.repository;

import conne.connect.connect.Usuario.model.UsuarioModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdUsuarioNot(String email, Long idUsuario);

    Optional<UsuarioModel> findByEmail(String email);;
}
