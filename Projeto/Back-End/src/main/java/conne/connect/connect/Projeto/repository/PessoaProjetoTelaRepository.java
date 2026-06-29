package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.model.PessoaProjetoTelaModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaProjetoTelaRepository extends JpaRepository<PessoaProjetoTelaModel, Long> {
    List<PessoaProjetoTelaModel> findByAtivoTrueOrderByNomeAsc();

    List<PessoaProjetoTelaModel> findByEmpresa_IdEmpresaAndAtivoTrueOrderByNomeAsc(Long empresaId);

    Optional<PessoaProjetoTelaModel> findByUsuarioEmpresa_IdUsuarioEmpresa(Long usuarioEmpresaId);

    Optional<PessoaProjetoTelaModel> findByEmail(String email);
}
