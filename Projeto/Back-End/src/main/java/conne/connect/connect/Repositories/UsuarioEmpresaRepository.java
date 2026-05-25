package conne.connect.connect.Repositories;

import conne.connect.connect.Models.UsuarioEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresaModel, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM usuario_empresa ue
        JOIN usuario u ON u.id = ue.usuario_id
        WHERE ue.empresa_id = :empresaId
        AND ue.ativo = true
        AND u.status = 'ativo'
    """, nativeQuery = true)
    Long countUsuariosAtivosPorEmpresa(@Param("empresaId") Long empresaId);
}