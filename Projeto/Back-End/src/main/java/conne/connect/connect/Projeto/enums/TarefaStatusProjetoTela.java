package conne.connect.connect.Projeto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TarefaStatusProjetoTela {
    A_FAZER("A Fazer"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído");

    private final String valor;

    TarefaStatusProjetoTela(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static TarefaStatusProjetoTela from(String valor) {
        if (valor == null) {
            return A_FAZER;
        }

        String normalizado = valor.trim()
            .replace("í", "i")
            .replace("Í", "I")
            .replace("ú", "u")
            .replace("Ú", "U")
            .replace(" ", "_")
            .toUpperCase();

        return switch (normalizado) {
            case "A_FAZER" -> A_FAZER;
            case "EM_ANDAMENTO" -> EM_ANDAMENTO;
            case "CONCLUIDO", "CONCLUÍDO" -> CONCLUIDO;
            default -> throw new IllegalArgumentException("Status de tarefa inválido: " + valor);
        };
    }
}
