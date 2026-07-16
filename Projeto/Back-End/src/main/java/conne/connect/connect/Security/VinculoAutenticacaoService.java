package conne.connect.connect.Security;

import conne.connect.connect.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Snapshot do vínculo usado na validação do token a cada request.
 * Cacheado por 60s: evita uma consulta ao banco por requisição, mantendo
 * a revogação (usuário desativado / papel alterado) com atraso máximo de 1 minuto.
 */
@Service
public class VinculoAutenticacaoService {

    public record VinculoAutenticado(
            Long idUsuario,
            Long idEmpresa,
            String email,
            String papel,
            boolean usuarioAtivo,
            boolean empresaAtiva
    ) {
    }

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public VinculoAutenticacaoService(UsuarioEmpresaRepository usuarioEmpresaRepository) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    @Cacheable(value = "vinculoAutenticacao", key = "#idUsuarioEmpresa", sync = true)
    @Transactional(readOnly = true)
    public VinculoAutenticado buscar(Long idUsuarioEmpresa) {
        return usuarioEmpresaRepository
                .findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa)
                .map(vinculo -> new VinculoAutenticado(
                        vinculo.getIdUsuario() != null ? vinculo.getIdUsuario().getIdUsuario() : null,
                        vinculo.getIdEmpresa() != null ? vinculo.getIdEmpresa().getIdEmpresa() : null,
                        vinculo.getIdUsuario() != null
                                ? vinculo.getIdUsuario().getEmail() : null,
                        vinculo.getPapel() != null ? vinculo.getPapel().name() : null,
                        vinculo.getIdUsuario() != null
                                && vinculo.getIdUsuario().getStatus() == StatusUsuario.ativo,
                        vinculo.getIdEmpresa() != null
                                && vinculo.getIdEmpresa().getStatus() == StatusEmpresa.ativa
                                && vinculo.getIdEmpresa().getExcluido() == null
                ))
                .orElse(null);
    }
}
