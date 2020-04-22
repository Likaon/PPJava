package PPRodVic;

// código baseado em um curso da udemy

import java.io.Serializable;

public class JogadorCliente implements Serializable {

    private String nome = "";
    private int x, y;
    boolean ok = false;
    boolean restart = false;

    public JogadorCliente(String nome) {
        this.nome = nome;
        x = 740;
        y = 210;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "PlayerClient [nome=" + nome + ", x=" + x + ", y=" + y + "]";
    }
}
