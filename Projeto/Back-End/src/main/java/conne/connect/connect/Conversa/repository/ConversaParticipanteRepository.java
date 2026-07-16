package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.model.ConversaParticipanteModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversaParticipanteRepository extends JpaRepository<ConversaParticipanteModel, Long> {

    List<ConversaParticipanteModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(Long idUsuarioEmpresa);

    List<ConversaParticipanteModel> findByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNullOrderByEntrouEmAsc(
            Long idConversa
    );

    List<ConversaParticipanteModel> findByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNull(Long idConversa);

    boolean existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(Long idConversa, Long idUsuarioEmpresa);

    long countByIdConversa_IdConversaAndAtivoTrueAndExcluidoIsNull(Long idConversa);
}
