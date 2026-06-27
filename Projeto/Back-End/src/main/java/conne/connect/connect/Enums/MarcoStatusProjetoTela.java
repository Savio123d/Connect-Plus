package conne.connect.connect.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MarcoStatusProjetoTela {
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído");

    private final String valor;

    MarcoStatusProjetoTela(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static MarcoStatusProjetoTela from(String valor) {
        if (valor == null) {
            return PENDENTE;
        }

        String normalizado = valor.trim()
            .replace("í", "i")
            .replace("Í", "I")
            .replace("ú", "u")
            .replace("Ú", "U")
            .replace(" ", "_")
            .toUpperCase();

        return switch (normalizado) {
            case "PENDENTE" -> PENDENTE;
            case "EM_ANDAMENTO" -> EM_ANDAMENTO;
            case "CONCLUIDO", "CONCLUÍDO" -> CONCLUIDO;
            default -> throw new IllegalArgumentException("Status de marco inválido: " + valor);
        };
    }
}
