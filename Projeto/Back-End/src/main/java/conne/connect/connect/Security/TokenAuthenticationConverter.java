package conne.connect.connect.Security;

import conne.connect.connect.Empresa.enums.StatusEmpresa;
import conne.connect.connect.Usuario.enums.StatusUsuario;
import conne.connect.connect.Usuario.model.UsuarioEmpresaModel;
import conne.connect.connect.Usuario.repository.UsuarioEmpresaRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenAuthenticationConverter
        implements Converter<Jwt, AbstractOAuth2TokenAuthenticationToken<Jwt>> {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    public TokenAuthenticationConverter(UsuarioEmpresaRepository usuarioEmpresaRepository) {
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AbstractOAuth2TokenAuthenticationToken<Jwt> convert(Jwt jwt) {
        Long idUsuario = numero(jwt.getSubject(), "sub");
        Long idEmpresa = numero(jwt.getClaim(TokenService.CLAIM_EMPRESA), TokenService.CLAIM_EMPRESA);
        Long idUsuarioEmpresa = numero(
                jwt.getClaim(TokenService.CLAIM_USUARIO_EMPRESA),
                TokenService.CLAIM_USUARIO_EMPRESA
        );
        String papelDoToken = jwt.getClaimAsString(TokenService.CLAIM_PAPEL);

        UsuarioEmpresaModel vinculo = usuarioEmpresaRepository
                .findByIdUsuarioEmpresaAndAtivoTrueAndExcluidoIsNull(idUsuarioEmpresa)
                .orElseThrow(() -> tokenInvalido("Vínculo de usuário inativo ou inexistente."));

        if (vinculo.getIdUsuario() == null
                || vinculo.getIdUsuario().getStatus() != StatusUsuario.ativo
                || !idUsuario.equals(vinculo.getIdUsuario().getIdUsuario())) {
            throw tokenInvalido("Usuário do token não está ativo.");
        }

        if (vinculo.getIdEmpresa() == null
                || vinculo.getIdEmpresa().getStatus() != StatusEmpresa.ativa
                || vinculo.getIdEmpresa().getExcluido() != null
                || !idEmpresa.equals(vinculo.getIdEmpresa().getIdEmpresa())) {
            throw tokenInvalido("Empresa do token não está ativa.");
        }

        String papelAtual = vinculo.getPapel().name();
        if (papelDoToken == null || !papelAtual.equalsIgnoreCase(papelDoToken)) {
            throw tokenInvalido("Papel do usuário foi alterado. Faça login novamente.");
        }

        return new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + papelAtual.toUpperCase(Locale.ROOT)
                )),
                vinculo.getIdUsuario().getEmail()
        );
    }

    private Long numero(Object valor, String claim) {
        try {
            if (valor instanceof Number numero) {
                return numero.longValue();
            }

            if (valor instanceof String texto && !texto.isBlank()) {
                return Long.valueOf(texto);
            }
        } catch (NumberFormatException ignored) {
        }

        throw tokenInvalido("Claim obrigatório inválido: " + claim + ".");
    }

    private InvalidBearerTokenException tokenInvalido(String mensagem) {
        return new InvalidBearerTokenException(mensagem);
    }
}
