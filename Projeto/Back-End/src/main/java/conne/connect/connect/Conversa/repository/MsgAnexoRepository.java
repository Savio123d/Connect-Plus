package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.model.MsgAnexoModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgAnexoRepository extends JpaRepository<MsgAnexoModel, Long> {

    List<MsgAnexoModel> findByIdMensagem_IdMensagem(Long idMensagem);
}
