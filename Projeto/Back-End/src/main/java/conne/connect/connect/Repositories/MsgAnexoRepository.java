package conne.connect.connect.Repositories;

import conne.connect.connect.Models.MsgAnexoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsgAnexoRepository extends JpaRepository<MsgAnexoModel, Long> {

    List<MsgAnexoModel> findByIdMensagem_IdMensagem(Long idMensagem);
}