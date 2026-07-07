package conne.connect.connect.Feedback.repository;

import conne.connect.connect.Feedback.model.Feedback360AvaliacaoModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Feedback360AvaliacaoRepository extends JpaRepository<Feedback360AvaliacaoModel, Long> {

    boolean existsByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresaAndAvaliado_IdUsuarioEmpresa(
            Long rodadaId,
            Long avaliadorId,
            Long avaliadoId
    );

    long countByRodada_IdRodadaAndRespondidaFalse(
            Long rodadaId
    );

    long countByRodada_IdRodadaAndAvaliador_IdUsuarioEmpresaAndRespondidaFalse(
            Long rodadaId,
            Long avaliadorId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.empresa.idEmpresa = :empresaId
          and avaliacao.avaliador.idUsuarioEmpresa = :avaliadorId
          and avaliacao.respondida = false
          and rodada.ativa = true
        order by rodada.obrigatoria desc, projeto.nome asc, avaliacao.ordem asc, usuarioAvaliado.nome asc
        """)
    List<Feedback360AvaliacaoModel> buscarPendentesDoAvaliador(
            @Param("empresaId") Long empresaId,
            @Param("avaliadorId") Long avaliadorId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.empresa.idEmpresa = :empresaId
          and avaliacao.avaliador.idUsuarioEmpresa = :avaliadorId
          and avaliacao.respondida = false
          and rodada.ativa = true
          and rodada.obrigatoria = true
        order by projeto.nome asc, avaliacao.ordem asc, usuarioAvaliado.nome asc
        """)
    List<Feedback360AvaliacaoModel> buscarPendentesObrigatoriasDoAvaliador(
            @Param("empresaId") Long empresaId,
            @Param("avaliadorId") Long avaliadorId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliador avaliador
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.idAvaliacao = :avaliacaoId
          and avaliacao.empresa.idEmpresa = :empresaId
          and avaliador.idUsuarioEmpresa = :avaliadorId
          and avaliacao.respondida = false
          and rodada.ativa = true
        """)
    Optional<Feedback360AvaliacaoModel> buscarPendentePorId(
            @Param("avaliacaoId") Long avaliacaoId,
            @Param("empresaId") Long empresaId,
            @Param("avaliadorId") Long avaliadorId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliador avaliador
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.empresa.idEmpresa = :empresaId
          and projeto.idProjeto = :projetoId
          and avaliador.idUsuarioEmpresa = :avaliadorId
          and avaliado.idUsuarioEmpresa = :avaliadoId
          and avaliacao.respondida = false
          and rodada.ativa = true
        """)
    Optional<Feedback360AvaliacaoModel> buscarPendentePorPar(
            @Param("empresaId") Long empresaId,
            @Param("projetoId") Long projetoId,
            @Param("avaliadorId") Long avaliadorId,
            @Param("avaliadoId") Long avaliadoId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.empresa.idEmpresa = :empresaId
          and avaliacao.respondida = true
        order by projeto.nome asc, usuarioAvaliado.nome asc, avaliacao.respondidaEm desc
        """)
    List<Feedback360AvaliacaoModel> buscarRespondidasPorEmpresa(
            @Param("empresaId") Long empresaId
    );

    @Query("""
        select avaliacao
        from Feedback360AvaliacaoModel avaliacao
        join fetch avaliacao.rodada rodada
        join fetch avaliacao.projeto projeto
        join fetch avaliacao.avaliado avaliado
        left join avaliado.idUsuario usuarioAvaliado
        where avaliacao.empresa.idEmpresa = :empresaId
          and avaliacao.avaliador.idUsuarioEmpresa = :avaliadorId
        order by rodada.abertaEm desc, projeto.nome asc, avaliacao.ordem asc, usuarioAvaliado.nome asc
        """)
    List<Feedback360AvaliacaoModel> buscarPorAvaliador(
            @Param("empresaId") Long empresaId,
            @Param("avaliadorId") Long avaliadorId
    );

    boolean existsByEmpresa_IdEmpresaAndProjeto_IdProjetoAndAvaliador_IdUsuarioEmpresaAndAvaliado_IdUsuarioEmpresaAndRespondidaTrue(
            Long empresaId,
            Long projetoId,
            Long avaliadorId,
            Long avaliadoId
    );
}