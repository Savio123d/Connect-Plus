package conne.connect.connect.Usuario.repository;

import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresaModel, Long> {

    List<UsuarioEmpresaModel> findByIdEmpresa_IdEmpresa(Long idEmpresa);

    Optional<UsuarioEmpresaModel> findByIdUsuario_IdUsuario(Long idUsuario);

    Optional<UsuarioEmpresaModel> findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(Long idUsuarioEmpresa);

    Optional<UsuarioEmpresaModel> findFirstByIdUsuario_IdUsuarioAndAtivoTrueAndExcluidoIsNull(Long idUsuario);

    List<UsuarioEmpresaModel> findByIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(Long idEmpresa);

    @Query("""
            select count(usuarioEmpresa)
            from UsuarioEmpresaModel usuarioEmpresa
            where usuarioEmpresa.idEmpresa.idEmpresa = :empresaId
              and usuarioEmpresa.ativo = true
              and usuarioEmpresa.excluido is null
            """)
    Long countUsuariosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}
