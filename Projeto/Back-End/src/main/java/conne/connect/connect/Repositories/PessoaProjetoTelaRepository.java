package conne.connect.connect.Repositories;

import conne.connect.connect.Models.PessoaProjetoTelaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PessoaProjetoTelaRepository extends JpaRepository<PessoaProjetoTelaModel, Long> {
    List<PessoaProjetoTelaModel> findByAtivoTrueOrderByNomeAsc();
}
