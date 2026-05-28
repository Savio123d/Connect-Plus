package conne.connect.connect.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PrioridadeProjetoTela {
    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String valor;

    PrioridadeProjetoTela(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static PrioridadeProjetoTela from(String valor) {
        if (valor == null) {
            return MEDIA;
        }

        String normalizado = valor.trim()
            .replace("é", "e")
            .replace("É", "E")
            .replace("á", "a")
            .replace("Á", "A")
            .toUpperCase();

        return switch (normalizado) {
            case "ALTA" -> ALTA;
            case "MEDIA", "MÉDIA" -> MEDIA;
            case "BAIXA" -> BAIXA;
            default -> throw new IllegalArgumentException("Prioridade inválida: " + valor);
        };
    }
}
