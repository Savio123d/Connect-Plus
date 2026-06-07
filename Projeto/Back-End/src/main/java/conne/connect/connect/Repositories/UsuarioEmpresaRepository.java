package conne.connect.connect.Repositories;

import conne.connect.connect.Models.UsuarioEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresaModel, Long> {

    List<UsuarioEmpresaModel> findByIdEmpresa_IdEmpresa(Long idEmpresa);

    Optional<UsuarioEmpresaModel> findByIdUsuario_IdUsuario(Long idUsuario);

    @Query(value = """
        SELECT COUNT(*)
        FROM usuario_empresa ue
        JOIN usuario u ON u.id = ue.usuario_id
        WHERE ue.empresa_id = :empresaId
        AND ue.ativo = true
        AND ue.excluido IS NULL
        AND u.status = 'ativo'
    """, nativeQuery = true)
    Long countUsuariosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}
