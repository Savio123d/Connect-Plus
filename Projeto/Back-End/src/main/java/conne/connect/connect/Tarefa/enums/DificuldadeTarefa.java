package conne.connect.connect.Tarefa.enums;

public enum DificuldadeTarefa {
    facil(10),
    medio(20),
    dificil(50);

    private final int xpConclusao;

    DificuldadeTarefa(int xpConclusao) {
        this.xpConclusao = xpConclusao;
    }

    public int getXpConclusao() {
        return xpConclusao;
    }
}
