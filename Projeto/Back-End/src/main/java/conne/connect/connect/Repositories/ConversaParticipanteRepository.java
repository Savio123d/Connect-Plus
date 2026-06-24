package conne.connect.connect.Repositories;

import conne.connect.connect.Models.ConversaParticipanteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversaParticipanteRepository extends JpaRepository<ConversaParticipanteModel, Long> {

    List<ConversaParticipanteModel> findByIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrue(Long idUsuarioEmpresa);

    List<ConversaParticipanteModel> findByIdConversa_IdConversaAndAtivoTrueOrderByEntrouEmAsc(Long idConversa);

    boolean existsByIdConversa_IdConversaAndIdUsuarioEmpresa_IdUsuarioEmpresaAndAtivoTrue(Long idConversa, Long idUsuarioEmpresa);

    long countByIdConversa_IdConversaAndAtivoTrue(Long idConversa);
}