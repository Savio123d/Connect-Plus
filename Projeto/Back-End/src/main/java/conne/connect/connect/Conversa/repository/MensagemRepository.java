package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.model.MensagemModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<MensagemModel, Long> {

    List<MensagemModel> findByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmAsc(Long idConversa);

    List<MensagemModel> findByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmAsc(Long idConversa);

    Optional<MensagemModel> findByIdMensagemAndIdEmpresa_IdEmpresaAndExcluidoIsNullAndExcluidaEmIsNull(
            Long idMensagem,
            Long idEmpresa
    );

    Optional<MensagemModel> findTopByIdConversa_IdConversaAndExcluidaEmIsNullOrderByEnviadaEmDesc(Long idConversa);

    Optional<MensagemModel> findTopByIdConversa_IdConversaAndExcluidoIsNullAndExcluidaEmIsNullOrderByEnviadaEmDesc(Long idConversa);
}
