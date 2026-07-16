package conne.connect.connect.Security;

import conne.connect.connect.Security.VinculoAutenticacaoService.VinculoAutenticado;
import java.util.List;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationConverter
        implements Converter<Jwt, AbstractOAuth2TokenAuthenticationToken<Jwt>> {

    private final VinculoAutenticacaoService vinculoAutenticacaoService;

    public TokenAuthenticationConverter(VinculoAutenticacaoService vinculoAutenticacaoService) {
        this.vinculoAutenticacaoService = vinculoAutenticacaoService;
    }

    @Override
    public AbstractOAuth2TokenAuthenticationToken<Jwt> convert(Jwt jwt) {
        Long idUsuario = numero(jwt.getSubject(), "sub");
        Long idEmpresa = numero(jwt.getClaim(TokenService.CLAIM_EMPRESA), TokenService.CLAIM_EMPRESA);
        Long idUsuarioEmpresa = numero(
                jwt.getClaim(TokenService.CLAIM_USUARIO_EMPRESA),
                TokenService.CLAIM_USUARIO_EMPRESA
        );
        String papelDoToken = jwt.getClaimAsString(TokenService.CLAIM_PAPEL);

        // Snapshot cacheado (60s): revalida o vínculo sem uma consulta por requisição.
        VinculoAutenticado vinculo = vinculoAutenticacaoService.buscar(idUsuarioEmpresa);

        if (vinculo == null) {
            throw tokenInvalido("Vínculo de usuário inativo ou inexistente.");
        }

        if (!vinculo.usuarioAtivo() || !idUsuario.equals(vinculo.idUsuario())) {
            throw tokenInvalido("Usuário do token não está ativo.");
        }

        if (!vinculo.empresaAtiva() || !idEmpresa.equals(vinculo.idEmpresa())) {
            throw tokenInvalido("Empresa do token não está ativa.");
        }

        String papelAtual = vinculo.papel();
        if (papelDoToken == null || papelAtual == null || !papelAtual.equalsIgnoreCase(papelDoToken)) {
            throw tokenInvalido("Papel do usuário foi alterado. Faça login novamente.");
        }

        return new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + papelAtual.toUpperCase(Locale.ROOT)
                )),
                vinculo.email()
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
