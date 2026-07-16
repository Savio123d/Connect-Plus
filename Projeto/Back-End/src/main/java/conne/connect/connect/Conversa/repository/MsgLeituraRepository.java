package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.model.MsgLeituraModel;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgLeituraRepository extends JpaRepository<MsgLeituraModel, Long> {

    boolean existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresaAndExcluidoIsNull(
            Long idMensagem,
            Long idUsuarioEmpresa
    );

    long countByIdMensagem_IdMensagemAndExcluidoIsNull(Long idMensagem);

    List<MsgLeituraModel> findByIdMensagem_IdMensagemInAndExcluidoIsNull(Collection<Long> idsMensagens);
}
