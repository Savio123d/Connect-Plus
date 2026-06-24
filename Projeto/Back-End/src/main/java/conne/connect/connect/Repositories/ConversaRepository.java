package conne.connect.connect.Repositories;

import conne.connect.connect.Enums.TipoConversa;
import conne.connect.connect.Models.ConversaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversaRepository extends JpaRepository<ConversaModel, Long> {

    Optional<ConversaModel> findByIdConversaAndIdEmpresa_IdEmpresa(Long idConversa, Long idEmpresa);

    @Query("""
            select distinct c
            from ConversaModel c
            join ConversaParticipanteModel cp1 on cp1.idConversa = c
            join ConversaParticipanteModel cp2 on cp2.idConversa = c
            where c.idEmpresa.idEmpresa = :idEmpresa
              and c.tipo = :tipo
              and cp1.idUsuarioEmpresa.idUsuarioEmpresa = :idUsuarioEmpresaA
              and cp1.ativo = true
              and cp2.idUsuarioEmpresa.idUsuarioEmpresa = :idUsuarioEmpresaB
              and cp2.ativo = true
              and (
                    select count(cp)
                    from ConversaParticipanteModel cp
                    where cp.idConversa = c
                      and cp.ativo = true
              ) = 2
            """)
    Optional<ConversaModel> findConversaPrivadaExistente(
            @Param("idEmpresa") Long idEmpresa,
            @Param("tipo") TipoConversa tipo,
            @Param("idUsuarioEmpresaA") Long idUsuarioEmpresaA,
            @Param("idUsuarioEmpresaB") Long idUsuarioEmpresaB
    );
}