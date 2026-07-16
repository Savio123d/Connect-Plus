package conne.connect.connect.Empresa.repository;

import conne.connect.connect.Empresa.model.EmpresaModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<EmpresaModel, Long> {

    boolean existsByCnpj(String cnpj);

    List<EmpresaModel> findByExcluidoIsNull();
}
