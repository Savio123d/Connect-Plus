package conne.connect.connect.Repositories;

import conne.connect.connect.Models.LojaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface LojaRepository extends JpaRepository<LojaModel, Long> {

        List<LojaModel> findByExcluidoIsNull();

        List<LojaModel> findByAtivaTrueAndExcluidoIsNull();

        List<LojaModel> findByIdEmpresaAndExcluidoIsNull(Long idEmpresa);

        List<LojaModel> findByIdEmpresaAndAtivaTrueAndExcluidoIsNull(Long idEmpresa);

        Optional<LojaModel> findByIdLojaAndExcluidoIsNull(Long idLoja);
    }

