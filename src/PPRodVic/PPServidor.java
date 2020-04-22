package PPRodVic;


// código baseado em um curso da udemy

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

public class PPServidor extends JFrame implements KeyListener, Runnable, WindowListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Ping Pong";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;

    boolean isRunning = false;
    boolean verifica = true;
    boolean initgame = false;

    Bolinha moveBolinha;
    private final Servidor playerServer;
    private JogadorCliente playerClient;

    private final int ballVelocity = 4;
    private final int playerWitdth = 30;
    private final int playerHeight = 120;
    private final int maxScore = 3;
    private final int playerMovement = 5;
    private boolean restart = false;
    private boolean restartON = false;

    private static Socket clientSoc = null;
    private static ServerSocket serverSoc = null;
    private final int portAdd;

    private Graphics g;
    private final Font sFont = new Font("TimesRoman", Font.BOLD, 90);
    private final Font mFont = new Font("TimesRoman", Font.BOLD, 50);
    private final Font nFont = new Font("TimesRoman", Font.BOLD, 32);
    private final Font rFont = new Font("TimesRoman", Font.BOLD, 18);
    private String[] message;
    private Thread movB;

    public PPServidor(final String nomeServidor, final String portAdd) {

        playerServer = new Servidor();
        playerClient = new JogadorCliente("");
        playerServer.setNome(nomeServidor);

        this.portAdd = Integer.parseInt(portAdd);
        this.isRunning = true;
        this.setTitle(TITLE + " - Porta: " + portAdd);
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        moveBolinha = new Bolinha(playerServer.getBallx(), playerServer.getBally(), ballVelocity, ballVelocity, 45,
                WIDTH, HEIGHT);

        addKeyListener(this);
    }

    @Override
    public void run() {
        try {
            serverSoc = new ServerSocket(portAdd);
            System.out.println("Server rodando na porta " + portAdd + ".\nAguardando por um jogador...");
            System.out.println("Aguardando por conexão...");
            playerServer.setImessage("      Aguardando...");
            clientSoc = serverSoc.accept();

            System.out.println("Jogador conectado...");

            if (clientSoc.isConnected()) {

                boolean notchecked = true;
                movB = new Thread(moveBolinha);
                while (true) {
                    if (playerServer.getPontos2() >= maxScore
                            || playerServer.getPontos1() >= maxScore && restart == false) {
                        playerServer.setImessage("       Fim de jogo!");
                        restart = true;
                        movB.checkAccess();
                    }

                    if (playerClient.ok && notchecked) {
                        playerServer.setImessage("");
                        movB.start();
                        notchecked = false;
                    }

                    updateBall();

                    ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
                    playerClient = (JogadorCliente) getObj.readObject();
                    getObj = null;

                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
                    sendObj.writeObject(playerServer);
                    sendObj = null;

                    if (restartON) {

                        if (playerClient.restart) {
                            playerServer.setPontos1(0);
                            playerServer.setPontos2(0);
                            playerServer.setOmessage("");
                            playerServer.setImessage("");
                            restart = false;
                            playerServer.setRestart(false);
                            playerServer.setBallx(380);
                            playerServer.setBally(230);
                            moveBolinha.setX(380);
                            moveBolinha.setY(230);
                            movB.checkAccess();
                            restartON = false;
                        }
                    }
                    repaint();
                }
            } else {
                System.out.println("Desconectado...");
            }
        } catch (final Exception e) {
            System.out.println(e);
        }
    }

    private Image createImage() {

        final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();

        // mesa
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Linhas de demarcação
        g.setColor(Color.white);
        g.fillRect(WIDTH / 2, 0, 5, HEIGHT);

        // Pontuação
        g.setFont(sFont);
        g.setColor(Color.white);
        g.drawString("" + playerServer.getPontos1(), WIDTH / 2 - 60, 120);
        g.drawString("" + playerServer.getPontos2(), WIDTH / 2 + 15, 120);

        // Nomes dos jogadores fonte tamanho e tals
        g.setFont(nFont);
        g.setColor(Color.white);
        g.drawString(playerServer.getNome(), WIDTH / 10, HEIGHT - 20);
        g.drawString(playerClient.getNome(), 600, HEIGHT - 20);

        // jogadores
        g.setColor(Color.white);
        g.fillRect(playerServer.getX(), playerServer.getY(), playerWitdth, playerHeight);
        g.setColor(Color.white);
        g.fillRect(playerClient.getX(), playerClient.getY(), playerWitdth, playerHeight);

        // Bolinha
        g.setColor(Color.white);
        g.fillOval(playerServer.getBallx(), playerServer.getBally(), 45, 45);
        g.setColor(Color.CYAN);
        g.fillOval(playerServer.getBallx() + 5, playerServer.getBally() + 5, 45 - 10, 45 - 10);

        // mensagens
        message = playerServer.getImessage().split("-");
        g.setFont(mFont);
        g.setColor(Color.white);
        if (message.length != 0) {
            g.drawString(message[0], WIDTH / 4 - 31, HEIGHT / 2 + 38);
            if (message.length > 1) {
                if (message[1].length() > 6) {
                    g.setFont(rFont);
                    g.setColor(new Color(228, 38, 36));
                    g.drawString(message[1], WIDTH / 4 - 31, HEIGHT / 2 + 100);
                }
            }
        }
        return bufferedImage;
    }

    @Override
    public void paint(final Graphics g) {
        g.drawImage(createImage(), 0, 0, this);
    }

    public void updateBall() {
        verificaColisao();

        playerServer.setBallx(moveBolinha.getX());
        playerServer.setBally(moveBolinha.getY());

    }

    public void playerUP() {
        if (playerServer.getY() - playerMovement > playerHeight / 2 - 10) {

            playerServer.setY(playerServer.getY() - playerMovement);
        }
    }

    public void playerDOWN() {
        if (playerServer.getY() + playerMovement < HEIGHT - playerHeight - 30) {

            playerServer.setY(playerServer.getY() + playerMovement);
        }
    }

    public void verificaColisao() {

        // verifica o lado da bolinha
        if (playerServer.getBallx() < playerClient.getX() && playerServer.getBallx() > playerServer.getX()) {
            verifica = true;
        }

        // Pontuação do primeiro a logar
        if (playerServer.getBallx() > playerClient.getX() && verifica) {

            playerServer.setPontos1(playerServer.getPontos2() + 1);

            verifica = false;
        } // Pontuação do segundo a logar
        else if (playerServer.getBallx() <= playerServer.getX() && verifica) {

            playerServer.setPontos2(playerServer.getPontos2() + 1);

            verifica = false;

        }

        // Posição da barra do primeiro cliente
        if (moveBolinha.getX() <= playerServer.getX() + playerWitdth
                && moveBolinha.getY() + moveBolinha.getRadius() >= playerServer.getY()
                && moveBolinha.getY() <= playerServer.getY() + playerHeight) {
            moveBolinha.setX(playerServer.getX() + playerWitdth);
            playerServer.setBallx(playerServer.getX() + playerWitdth);
            moveBolinha.setXv(moveBolinha.getXv() * -1);
        }

        // verificando a posição com relação a barra do segundo cliente
        if (moveBolinha.getX() + moveBolinha.getRadius() >= playerClient.getX()
                && moveBolinha.getY() + moveBolinha.getRadius() >= playerClient.getY()
                && moveBolinha.getY() <= playerClient.getY() + playerHeight) {
            moveBolinha.setX(playerClient.getX() - moveBolinha.getRadius());
            playerServer.setBallx(playerClient.getX() - moveBolinha.getRadius());
            moveBolinha.setXv(moveBolinha.getXv() * -1);
        }

    }

    @Override
    public void keyPressed(final KeyEvent arg0) {

        final int keycode = arg0.getKeyCode();
        if (keycode == KeyEvent.VK_UP) {
            playerUP();
            repaint();
        }
        if (keycode == KeyEvent.VK_DOWN) {
            playerDOWN();
            repaint();
        }
        if (restart == true) {
            restartON = true;
            playerServer.setRestart(true);
        }

        if (keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && restart == true) {
            try {
                this.setVisible(false);
                serverSoc.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void windowClosing(final WindowEvent arg0) {
        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            serverSoc.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    @Override
    public void keyReleased(final KeyEvent arg0) {
    }

    @Override
    public void keyTyped(final KeyEvent arg0) {
    }

    @Override
    public void windowActivated(final WindowEvent arg0) {
    }

    @Override
    public void windowClosed(final WindowEvent arg0) {
        System.exit(1);
    }

    @Override
    public void windowDeactivated(final WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(final WindowEvent arg0) {
    }

    @Override
    public void windowIconified(final WindowEvent arg0) {
    }

    @Override
    public void windowOpened(final WindowEvent arg0) {
    }

}
