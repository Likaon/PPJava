package PPRodVic;

// código baseado na criação em aula

public class Bolinha extends Thread {

    private int ALTURA;
    private int LARGURA;
    private int x;
    private int y;
    private double x_velocidade;
    private double y_velocidade;
    private int radius;
    

    @Override
    public void run() {
        while (true) {
            movimenta();
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Bolinha(int x, int y, double xv, double yv, int radius, int LARGURA, int ALTURA) {
        super();
        this.x = x;
        this.y = y;
        this.x_velocidade = xv;
        this.y_velocidade = yv;
        this.radius = radius;
        this.LARGURA = LARGURA;
        this.ALTURA = ALTURA;
    }

    public void movimenta() {
        if (x + x_velocidade > (LARGURA - radius) - 7) {
            x = (LARGURA - radius) - 7;
            x_velocidade = x_velocidade * -1;

        }

        if (x + x_velocidade < 9) {
            x = 9;
            x_velocidade = x_velocidade * -1;
        }

        if (y + y_velocidade < radius / 2 + 7) {
            y = 29;
            y_velocidade = y_velocidade * -1;
        }

        if (y + y_velocidade > (ALTURA - radius) - 6) {
            y = (ALTURA - radius) - 6;
            y_velocidade = y_velocidade * -1;

        }
        x += x_velocidade;
        y += y_velocidade;

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

    public double getXv() {
        return x_velocidade;
    }

    public void setXv(double xv) {
        this.x_velocidade = xv;
    }

    public double getYv() {
        return y_velocidade;
    }

    public void setYv(double yv) {
        this.y_velocidade = yv;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
