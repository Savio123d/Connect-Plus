package conne.connect.connect.Feedback.repository;

import conne.connect.connect.Feedback.model.Feedback360ObservacaoModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Feedback360ObservacaoRepository extends JpaRepository<Feedback360ObservacaoModel, Long> {

    Optional<Feedback360ObservacaoModel> findByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresa(
            Long rodadaId,
            Long avaliadorId
    );

    List<Feedback360ObservacaoModel> findByEmpresa_IdEmpresaOrderByCriadaEmDesc(Long empresaId);
}
