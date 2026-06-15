package conne.connect.connect.Repositories;

import conne.connect.connect.Models.MensagemModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensagemRepository extends JpaRepository<MensagemModel, Long> {

    List<MensagemModel> findByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmAsc(Long idConversa);

    Optional<MensagemModel> findTopByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmDesc(Long idConversa);
}