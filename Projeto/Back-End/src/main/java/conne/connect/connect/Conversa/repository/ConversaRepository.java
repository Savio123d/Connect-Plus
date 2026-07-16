package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.enums.TipoConversa;
import conne.connect.connect.Conversa.model.ConversaModel;
import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversaRepository extends JpaRepository<ConversaModel, Long> {

    Optional<ConversaModel> findByIdConversaAndIdEmpresa_IdEmpresaAndExcluidoIsNull(
            Long idConversa,
            Long idEmpresa
    );

    @Query("""
            select distinct c
            from ConversaModel c
            join ConversaParticipanteModel cp1 on cp1.idConversa = c
            join ConversaParticipanteModel cp2 on cp2.idConversa = c
            where c.idEmpresa.idEmpresa = :idEmpresa
              and c.tipo = :tipo
              and c.excluido is null
              and cp1.idUsuarioEmpresa.idUsuarioEmpresa = :idUsuarioEmpresaA
              and cp1.ativo = true
              and cp1.excluido is null
              and cp2.idUsuarioEmpresa.idUsuarioEmpresa = :idUsuarioEmpresaB
              and cp2.ativo = true
              and cp2.excluido is null
              and (
                    select count(cp)
                    from ConversaParticipanteModel cp
                    where cp.idConversa = c
                      and cp.ativo = true
                      and cp.excluido is null
              ) = 2
            """)
    Optional<ConversaModel> findConversaPrivadaExistente(
            @Param("idEmpresa") Long idEmpresa,
            @Param("tipo") TipoConversa tipo,
            @Param("idUsuarioEmpresaA") Long idUsuarioEmpresaA,
            @Param("idUsuarioEmpresaB") Long idUsuarioEmpresaB
    );
}
