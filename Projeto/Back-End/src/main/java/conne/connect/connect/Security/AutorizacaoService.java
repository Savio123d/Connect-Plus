package conne.connect.connect.Security;

import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Checagens de autorização usadas em @PreAuthorize.
 * Garante o escopo multi-tenant: o papel só vale dentro da empresa do token.
 */
@Service("autorizacao")
public class AutorizacaoService {

    private static final String PAPEL_GESTOR = "gestor";
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public AutorizacaoService(UsuarioEmpresaRepository usuarioEmpresaRepository) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    /** Usuário autenticado é gestor E a empresa alvo é a do próprio token. */
    public boolean gestorDaEmpresa(Long idEmpresa) {
        return ehGestor() && mesmaEmpresa(idEmpresa);
    }

    /** A empresa alvo é a mesma do token (qualquer papel). */
    public boolean mesmaEmpresa(Long idEmpresa) {
        Long empresaDoToken = empresaDoToken();
        return idEmpresa != null && idEmpresa.equals(empresaDoToken);
    }

    /** O usuário alvo é o próprio usuário autenticado. */
    public boolean proprioUsuario(Long idUsuario) {
        Jwt jwt = jwtAtual();

        if (jwt == null || idUsuario == null) {
            return false;
        }

        return String.valueOf(idUsuario).equals(jwt.getSubject());
    }

    public boolean gestorDoUsuario(Long idUsuario) {
        return ehGestor() && usuarioDaEmpresa(idUsuario);
    }

    public boolean usuarioDaEmpresa(Long idUsuario) {
        Long idEmpresa = empresaDoToken();
        return idUsuario != null
                && idEmpresa != null
                && usuarioEmpresaRepository
                .existsByIdUsuario_IdUsuarioAndIdEmpresa_IdEmpresaAndAtivoTrueAndExcluidoIsNull(
                        idUsuario,
                        idEmpresa
                );
    }

    public boolean gestorDoVinculo(Long idUsuarioEmpresa) {
        return ehGestor() && vinculoDaEmpresa(idUsuarioEmpresa);
    }

    public boolean proprioVinculo(Long idUsuarioEmpresa) {
        Long vinculoDoToken = usuarioEmpresaDoToken();
        return idUsuarioEmpresa != null && idUsuarioEmpresa.equals(vinculoDoToken);
    }

    public boolean vinculoDaEmpresa(Long idUsuarioEmpresa) {
        return idUsuarioEmpresa != null
                && usuarioEmpresaRepository
                .findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa)
                .map(vinculo -> vinculo.getIdEmpresa() != null
                        && mesmaEmpresa(vinculo.getIdEmpresa().getIdEmpresa()))
                .orElse(false);
    }

    public Long empresaAtual() {
        return empresaDoToken();
    }

    public Long usuarioEmpresaAtual() {
        return usuarioEmpresaDoToken();
    }

    public void validarEmpresaAtual(Long idEmpresa) {
        if (idEmpresa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa é obrigatória.");
        }

        if (!mesmaEmpresa(idEmpresa)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso a outra empresa não permitido.");
        }
    }

    public void validarVinculoAtual(Long idUsuarioEmpresa) {
        if (!proprioVinculo(idUsuarioEmpresa)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vínculo de usuário diferente do token.");
        }
    }

    public void validarAcessoAoVinculo(Long idUsuarioEmpresa) {
        if (!vinculoDaEmpresa(idUsuarioEmpresa)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo de usuário não encontrado.");
        }

        if (!proprioVinculo(idUsuarioEmpresa) && !ehGestor()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso ao perfil de outro usuário não permitido.");
        }
    }

    public boolean ehGestor() {
        Jwt jwt = jwtAtual();
        return jwt != null && PAPEL_GESTOR.equalsIgnoreCase(jwt.getClaimAsString(TokenService.CLAIM_PAPEL));
    }

    private Long empresaDoToken() {
        Jwt jwt = jwtAtual();

        if (jwt == null) {
            return null;
        }

        Object claim = jwt.getClaim(TokenService.CLAIM_EMPRESA);
        return claim instanceof Number numero ? numero.longValue() : null;

    }
    private Long usuarioEmpresaDoToken() {
        Jwt jwt = jwtAtual();

        if (jwt == null) {
            return null;
        }

        Object claim = jwt.getClaim(TokenService.CLAIM_USUARIO_EMPRESA);
        return claim instanceof Number numero ? numero.longValue() : null;
    }

    private Jwt jwtAtual() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao != null && autenticacao.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        return null;
    }
}
