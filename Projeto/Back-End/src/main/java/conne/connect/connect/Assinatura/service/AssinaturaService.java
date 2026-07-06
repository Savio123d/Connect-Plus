package conne.connect.connect.Assinatura.service;

import conne.connect.connect.Assinatura.dto.AssinaturaCadastroResultadoDTO;
import conne.connect.connect.Assinatura.dto.MercadoPagoAssinaturaDTO;
import conne.connect.connect.Assinatura.enums.StatusAssinatura;
import conne.connect.connect.Assinatura.model.AssinaturaModel;
import conne.connect.connect.Assinatura.repository.AssinaturaRepository;
import conne.connect.connect.Recompensa.Empresa.model.EmpresaModel;
import conne.connect.connect.Plano.enums.TipoPlano;
import conne.connect.connect.Plano.model.PlanoModel;
import conne.connect.connect.Plano.repository.PlanoRepository;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final PlanoRepository planoRepository;
    private final MercadoPagoService mercadoPagoService;

    @Value("${mercadopago.planos.premium.valor:49.90}")
    private BigDecimal valorPremiumPadrao;

    @Value("${mercadopago.planos.premium.max-usuarios:50}")
    private Integer maxUsuariosPremium;

    @Value("${mercadopago.planos.gratuito.max-usuarios:5}")
    private Integer maxUsuariosGratuito;

    public AssinaturaService(
            AssinaturaRepository assinaturaRepository,
            PlanoRepository planoRepository,
            MercadoPagoService mercadoPagoService) {
        this.assinaturaRepository = assinaturaRepository;
        this.planoRepository = planoRepository;
        this.mercadoPagoService = mercadoPagoService;
    }

    @Transactional
    public AssinaturaCadastroResultadoDTO criarAssinaturaInicial(
            EmpresaModel empresa,
            TipoPlano tipoPlano,
            String emailAdmin) {
        TipoPlano tipoEscolhido = tipoPlano != null ? tipoPlano : TipoPlano.gratuito;
        PlanoModel plano = buscarOuCriarPlano(tipoEscolhido);

        AssinaturaModel assinatura = new AssinaturaModel();
        assinatura.setIdEmpresa(empresa);
        assinatura.setIdPlano(plano);
        assinatura.setQuantidadeUsuarios(1);
        assinatura.setStatus(tipoEscolhido == TipoPlano.premium ? StatusAssinatura.pendente : StatusAssinatura.ativa);
        assinatura = assinaturaRepository.save(assinatura);

        if (tipoEscolhido != TipoPlano.premium) {
            return new AssinaturaCadastroResultadoDTO(
                    assinatura.getIdAssinatura(),
                    assinatura.getStatus(),
                    null);
        }

        String referenciaExterna = montarReferenciaExterna(empresa, assinatura);
        assinatura.setMercadoPagoExternalReference(referenciaExterna);

        MercadoPagoAssinaturaDTO retornoMercadoPago = mercadoPagoService.criarAssinaturaPremium(
                emailAdmin,
                referenciaExterna,
                valorDaAssinatura(plano));

        aplicarRetornoMercadoPago(assinatura, retornoMercadoPago);
        assinatura = assinaturaRepository.save(assinatura);

        return new AssinaturaCadastroResultadoDTO(
                assinatura.getIdAssinatura(),
                assinatura.getStatus(),
                checkoutUrl(retornoMercadoPago));
    }

    @Transactional
    public void processarWebhook(Map<String, Object> payload, Map<String, String> parametros) {
        String tipo = texto(payload.get("type"));
        String topico = texto(payload.get("topic"));

        if (!"subscription_preapproval".equals(tipo) && !"subscription_preapproval".equals(topico)) {
            return;
        }

        String preapprovalId = idNotificado(payload, parametros);
        if (preapprovalId == null || preapprovalId.isBlank()) {
            return;
        }

        MercadoPagoAssinaturaDTO assinaturaMercadoPago = mercadoPagoService.buscarAssinatura(preapprovalId);
        Optional<AssinaturaModel> assinaturaEncontrada = assinaturaRepository
                .findByMercadoPagoPreapprovalId(preapprovalId)
                .or(() -> assinaturaRepository.findByMercadoPagoExternalReference(
                        assinaturaMercadoPago.getExternalReference()));

        assinaturaEncontrada.ifPresent(assinatura -> {
            aplicarRetornoMercadoPago(assinatura, assinaturaMercadoPago);
            assinaturaRepository.save(assinatura);
        });
    }

    private PlanoModel buscarOuCriarPlano(TipoPlano tipoPlano) {
        return planoRepository.findFirstByTipoAndExcluidoIsNull(tipoPlano)
                .orElseGet(() -> criarPlanoPadrao(tipoPlano));
    }

    private PlanoModel criarPlanoPadrao(TipoPlano tipoPlano) {
        PlanoModel plano = new PlanoModel();
        plano.setTipo(tipoPlano);

        if (tipoPlano == TipoPlano.premium) {
            plano.setNome("Premium");
            plano.setMaxUsuarios(maxUsuariosPremium);
            plano.setValor(valorPremiumPadrao);
        } else {
            plano.setNome("Gratuito");
            plano.setMaxUsuarios(maxUsuariosGratuito);
            plano.setValor(BigDecimal.ZERO);
        }

        return planoRepository.save(plano);
    }

    private BigDecimal valorDaAssinatura(PlanoModel plano) {
        BigDecimal valor = plano.getValor();
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0 ? valor : valorPremiumPadrao;
    }

    private String montarReferenciaExterna(EmpresaModel empresa, AssinaturaModel assinatura) {
        return "connect-plus-empresa-" + empresa.getIdEmpresa() + "-assinatura-" + assinatura.getIdAssinatura();
    }

    private void aplicarRetornoMercadoPago(
            AssinaturaModel assinatura,
            MercadoPagoAssinaturaDTO retornoMercadoPago) {
        if (temTexto(retornoMercadoPago.getId())) {
            assinatura.setMercadoPagoPreapprovalId(retornoMercadoPago.getId());
        }

        if (temTexto(retornoMercadoPago.getExternalReference())) {
            assinatura.setMercadoPagoExternalReference(retornoMercadoPago.getExternalReference());
        }

        assinatura.setMercadoPagoStatus(retornoMercadoPago.getStatus());
        assinatura.setMercadoPagoInitPoint(retornoMercadoPago.getInitPoint());
        assinatura.setMercadoPagoSandboxInitPoint(retornoMercadoPago.getSandboxInitPoint());
        assinatura.setStatus(mapearStatus(retornoMercadoPago.getStatus()));
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private StatusAssinatura mapearStatus(String statusMercadoPago) {
        if (statusMercadoPago == null) {
            return StatusAssinatura.pendente;
        }

        String status = statusMercadoPago.toLowerCase(Locale.ROOT);
        if ("authorized".equals(status) || "active".equals(status)) {
            return StatusAssinatura.ativa;
        }

        if ("cancelled".equals(status) || "paused".equals(status) || "finished".equals(status)) {
            return StatusAssinatura.cancelada;
        }

        return StatusAssinatura.pendente;
    }

    private String checkoutUrl(MercadoPagoAssinaturaDTO retornoMercadoPago) {
        if (retornoMercadoPago.getInitPoint() != null && !retornoMercadoPago.getInitPoint().isBlank()) {
            return retornoMercadoPago.getInitPoint();
        }

        return retornoMercadoPago.getSandboxInitPoint();
    }

    @SuppressWarnings("unchecked")
    private String idNotificado(Map<String, Object> payload, Map<String, String> parametros) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            String id = texto(((Map<String, Object>) dataMap).get("id"));
            if (id != null && !id.isBlank()) {
                return id;
            }
        }

        String idParametro = parametros.get("data.id");
        if (idParametro != null && !idParametro.isBlank()) {
            return idParametro;
        }

        return parametros.get("id");
    }

    private String texto(Object valor) {
        return valor != null ? String.valueOf(valor) : null;
    }
}