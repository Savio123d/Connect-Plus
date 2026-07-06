package conne.connect.connect.Assinatura.dto;

import conne.connect.connect.Assinatura.enums.StatusAssinatura;

public class AssinaturaCadastroResultadoDTO {

    private final Long idAssinatura;
    private final StatusAssinatura statusAssinatura;
    private final String checkoutUrl;

    public AssinaturaCadastroResultadoDTO(Long idAssinatura, StatusAssinatura statusAssinatura, String checkoutUrl) {
        this.idAssinatura = idAssinatura;
        this.statusAssinatura = statusAssinatura;
        this.checkoutUrl = checkoutUrl;
    }

    public Long getIdAssinatura() {
        return idAssinatura;
    }

    public StatusAssinatura getStatusAssinatura() {
        return statusAssinatura;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }
}