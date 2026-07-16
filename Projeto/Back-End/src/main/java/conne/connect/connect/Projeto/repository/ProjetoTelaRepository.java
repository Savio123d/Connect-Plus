package conne.connect.connect.Projeto.repository;

import conne.connect.connect.Projeto.dto.ProjetoResumoDTO;
import conne.connect.connect.Projeto.model.ProjetoTelaModel;
import conne.connect.connect.Projeto.enums.ProjetoStatusTela;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetoTelaRepository extends JpaRepository<ProjetoTelaModel, Long> {
    @Query("""
            select new conne.connect.connect.Projeto.dto.ProjetoResumoDTO(
                projeto.idProjeto,
                projeto.nome,
                projeto.descricao,
                projeto.status,
                projeto.atrasado,
                projeto.prazo,
                projeto.progresso,
                count(membro),
                lider.nome,
                lider.iniciais
            )
            from ProjetoTelaModel projeto
            join projeto.lider lider
            left join projeto.membros membro
            where projeto.empresa.idEmpresa = :empresaId
              and projeto.excluido is null
            group by projeto.idProjeto, projeto.nome, projeto.descricao, projeto.status,
                     projeto.atrasado, projeto.prazo, projeto.progresso, lider.nome, lider.iniciais
            order by projeto.idProjeto desc
            """)
    List<ProjetoResumoDTO> listarResumosPorEmpresa(@Param("empresaId") Long empresaId);

    List<ProjetoTelaModel> findAllByOrderByIdProjetoDesc();

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaOrderByIdProjetoDesc(Long empresaId);

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaAndExcluidoIsNullOrderByIdProjetoDesc(Long empresaId);

    @EntityGraph(attributePaths = {"lider.usuarioEmpresa", "membros.usuarioEmpresa"})
    Optional<ProjetoTelaModel> findByIdProjetoAndEmpresa_IdEmpresaAndExcluidoIsNull(Long idProjeto, Long empresaId);

    List<ProjetoTelaModel> findByEmpresa_IdEmpresaAndStatusAndConcluidoEmIsNotNullOrderByConcluidoEmDesc(
            Long empresaId,
            ProjetoStatusTela status
    );
}
