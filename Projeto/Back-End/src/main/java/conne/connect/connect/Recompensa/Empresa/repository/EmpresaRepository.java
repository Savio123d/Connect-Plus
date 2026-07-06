package conne.connect.connect.Recompensa.Empresa.repository;

import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<EmpresaModel, Long> {
    boolean existsByCnpj(String cnpj);
}
