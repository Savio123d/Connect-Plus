package conne.connect.connect.Assinatura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conne.connect.connect.Assinatura.dto.MercadoPagoAssinaturaDTO;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MercadoPagoService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @Value("${mercadopago.api-base-url:https://api.mercadopago.com}")
    private String apiBaseUrl;

    @Value("${mercadopago.assinatura.back-url:http://localhost:4200/login}")
    private String backUrl;

    @Value("${mercadopago.assinatura.frequency:1}")
    private Integer frequency;

    @Value("${mercadopago.assinatura.frequency-type:months}")
    private String frequencyType;

    @Value("${mercadopago.assinatura.reason-prefix:Connect+ Premium}")
    private String reasonPrefix;

    public MercadoPagoService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MercadoPagoAssinaturaDTO criarAssinaturaPremium(
            String emailPagador,
            String referenciaExterna,
            BigDecimal valor) {
        validarConfiguracao(valor);

        Map<String, Object> autoRecurring = new LinkedHashMap<>();
        autoRecurring.put("frequency", frequency);
        autoRecurring.put("frequency_type", frequencyType);
        autoRecurring.put("transaction_amount", valor);
        autoRecurring.put("currency_id", "BRL");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("reason", reasonPrefix);
        payload.put("external_reference", referenciaExterna);
        payload.put("payer_email", emailPagador);
        payload.put("auto_recurring", autoRecurring);
        payload.put("back_url", backUrl);
        payload.put("status", "pending");

        return executarPostPreapproval(payload);
    }

    public MercadoPagoAssinaturaDTO buscarAssinatura(String preapprovalId) {
        validarAccessToken();

        String idSeguro = URLEncoder.encode(preapprovalId, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiBaseUrl + "/preapproval/" + idSeguro))
                .header("Authorization", "Bearer " + accessToken.trim())
                .GET()
                .build();

        return executarRequest(request);
    }

    private MercadoPagoAssinaturaDTO executarPostPreapproval(Map<String, Object> payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(apiBaseUrl + "/preapproval"))
                    .header("Authorization", "Bearer " + accessToken.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            return executarRequest(request);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel montar a assinatura.");
        }
    }

    private MercadoPagoAssinaturaDTO executarRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Mercado Pago recusou a assinatura: " + limitarResposta(response.body()));
            }

            JsonNode json = objectMapper.readTree(response.body());
            return new MercadoPagoAssinaturaDTO(
                    texto(json, "id"),
                    texto(json, "status"),
                    texto(json, "init_point"),
                    texto(json, "sandbox_init_point"),
                    texto(json, "external_reference"));
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao comunicar com o Mercado Pago.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Comunicacao com o Mercado Pago interrompida.");
        }
    }

    private void validarConfiguracao(BigDecimal valor) {
        validarAccessToken();

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do plano Premium deve ser maior que zero.");
        }
    }

    private void validarAccessToken() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure MERCADOPAGO_ACCESS_TOKEN para habilitar assinaturas Premium.");
        }
    }

    private String texto(JsonNode json, String campo) {
        JsonNode valor = json.get(campo);
        return valor != null && !valor.isNull() ? valor.asText() : null;
    }

    private String limitarResposta(String body) {
        if (body == null || body.isBlank()) {
            return "sem detalhes";
        }

        return body.length() <= 250 ? body : body.substring(0, 250);
    }
}