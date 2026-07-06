package conne.connect.connect.Assinatura.dto;

public class MercadoPagoAssinaturaDTO {

    private final String id;
    private final String status;
    private final String initPoint;
    private final String sandboxInitPoint;
    private final String externalReference;

    public MercadoPagoAssinaturaDTO(
            String id,
            String status,
            String initPoint,
            String sandboxInitPoint,
            String externalReference) {
        this.id = id;
        this.status = status;
        this.initPoint = initPoint;
        this.sandboxInitPoint = sandboxInitPoint;
        this.externalReference = externalReference;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getInitPoint() {
        return initPoint;
    }

    public String getSandboxInitPoint() {
        return sandboxInitPoint;
    }

    public String getExternalReference() {
        return externalReference;
    }
}