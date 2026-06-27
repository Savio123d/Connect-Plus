package conne.connect.connect.Projeto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjetoStatusTela {
    planejamento("planejamento"),
    em_andamento("em_andamento"),
    concluido("concluido"),
    cancelado("cancelado");

    private final String valor;

    ProjetoStatusTela(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static ProjetoStatusTela from(String valor) {
        for (ProjetoStatusTela status : values()) {
            if (status.valor.equalsIgnoreCase(valor) || status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de projeto inválido: " + valor);
    }
}
