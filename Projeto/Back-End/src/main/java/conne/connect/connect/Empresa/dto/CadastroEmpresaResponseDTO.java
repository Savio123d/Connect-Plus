package conne.connect.connect.Empresa.dto;

import conne.connect.connect.Assinatura.enums.StatusAssinatura;
import conne.connect.connect.Plano.enums.TipoPlano;

public class CadastroEmpresaResponseDTO {

    private final Long idEmpresa;
    private final TipoPlano tipoPlano;
    private final StatusAssinatura statusAssinatura;
    private final String checkoutUrl;
    private final String mensagem;

    public CadastroEmpresaResponseDTO(
            Long idEmpresa,
            TipoPlano tipoPlano,
            StatusAssinatura statusAssinatura,
            String checkoutUrl,
            String mensagem) {
        this.idEmpresa = idEmpresa;
        this.tipoPlano = tipoPlano;
        this.statusAssinatura = statusAssinatura;
        this.checkoutUrl = checkoutUrl;
        this.mensagem = mensagem;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public TipoPlano getTipoPlano() {
        return tipoPlano;
    }

    public StatusAssinatura getStatusAssinatura() {
        return statusAssinatura;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public String getMensagem() {
        return mensagem;
    }
}