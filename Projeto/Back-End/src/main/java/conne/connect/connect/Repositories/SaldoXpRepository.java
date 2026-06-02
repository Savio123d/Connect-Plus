package conne.connect.connect.Repositories;

import conne.connect.connect.Models.SaldoXpModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaldoXpRepository extends JpaRepository<SaldoXpModel, Long> {

    Optional<SaldoXpModel> findByIdUsuarioEmpresa_IdUsuarioEmpresa(Long idUsuarioEmpresa);
}