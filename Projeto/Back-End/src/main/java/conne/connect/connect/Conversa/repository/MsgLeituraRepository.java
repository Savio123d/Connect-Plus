package conne.connect.connect.Conversa.repository;

import conne.connect.connect.Conversa.model.MsgLeituraModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgLeituraRepository extends JpaRepository<MsgLeituraModel, Long> {

    boolean existsByIdMensagem_IdMensagemAndIdUsuarioEmpresa_IdUsuarioEmpresa(Long idMensagem, Long idUsuarioEmpresa);

    long countByIdMensagem_IdMensagem(Long idMensagem);
}
