package conne.connect.connect.Assinatura.controller;

import conne.connect.connect.Assinatura.service.AssinaturaService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mercado-pago")
public class MercadoPagoWebhookController {

    private final AssinaturaService assinaturaService;

    public MercadoPagoWebhookController(AssinaturaService assinaturaService) {
        this.assinaturaService = assinaturaService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(
            @RequestBody(required = false) Map<String, Object> payload,
            @RequestParam Map<String, String> parametros) {
        assinaturaService.processarWebhook(payload != null ? payload : Map.of(), parametros);
        return ResponseEntity.ok().build();
    }
}