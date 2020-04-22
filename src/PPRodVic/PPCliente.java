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
import java.net.Socket;
import javax.swing.JFrame;

public class PPCliente extends JFrame implements KeyListener, Runnable, WindowListener {

    private static final String TITLE = "Ping Pong";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;
    boolean isRunning = false;

    // jogadores
    private Servidor playerServer;
    private JogadorCliente playerClient;
    private int barWidth = 30;		
    private int playerHeight = 120; 	
    private int playerMovement = 5; 		

    // servidor
    private static Socket clientSoc;
    private int portAdd;
    private String ipAdd;
    private boolean reset = false;
    private int countS = 0;

    // graficos parte visual
    private Graphics g;
    private Font sFont = new Font("TimesRoman", Font.BOLD, 90);
    private Font mFont = new Font("TimesRoman", Font.BOLD, 50);
    private Font nFont = new Font("TimesRoman", Font.BOLD, 32);
    private Font rFont = new Font("TimesRoman", Font.BOLD, 18);
    private String[] message;	

    // construtor
    public PPCliente(String clienteNome, String portAdd, String ipAdd) {

        playerServer = new Servidor();
        playerClient = new JogadorCliente(clienteNome);
        playerServer.setNome(clienteNome);

        this.ipAdd = ipAdd;
        this.portAdd = Integer.parseInt(portAdd);
        this.isRunning = true;

        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        addKeyListener(this);
    }

    @Override
    public void run() {
        // servidor de Sockets //
        try {

            System.out.println("Procurando servidor...\nConectando em " + ipAdd + ":" + portAdd);
            clientSoc = new Socket(ipAdd, portAdd);
            System.out.println("Conectado ao servidor!");

            if (clientSoc.isConnected()) {
                while (true) {
                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
                    sendObj.writeObject(playerClient);
                    sendObj = null;

                    ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
                    playerServer = (Servidor) getObj.readObject();
                    getObj = null;

                    if (reset) {

                        if (countS > 5) {
                            playerClient.restart = false;
                            reset = false;
                            countS = 0;
                        }
                    }
                    countS++;
                    repaint();
                }

            } else {
                System.out.println("Desconectado...");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private Image createImage() {

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();

        // mesa
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // linhas demarcadoras
        g.setColor(Color.white);
        g.fillRect(WIDTH / 2, 0, 5, HEIGHT);

        // pontuação
        g.setColor(Color.white);
        g.setFont(sFont);
        g.drawString("" + playerServer.getScoreS(), WIDTH / 2 - 60, 120);
        g.drawString("" + playerServer.getScoreP(), WIDTH / 2 + 15, 120);

        // nomes dos jogadores
        g.setFont(nFont);
        g.setColor(Color.white);
        g.drawString(playerServer.getNome(), WIDTH / 10, HEIGHT - 20);
        g.drawString(playerClient.getNome(), 600, HEIGHT - 20);

        // raquetes "na falta de nome melhorzin"
        g.setColor(Color.white);
        g.fillRect(playerServer.getX(), playerServer.getY(), barWidth, playerHeight);
        g.setColor(Color.white);
        g.fillRect(playerClient.getX(), playerClient.getY(), barWidth, playerHeight);

        // bolinha
        g.setColor(Color.white);
        g.fillOval(playerServer.getBallx(), playerServer.getBally(), 45, 45);
        g.setColor(Color.CYAN);
        g.fillOval(playerServer.getBallx() + 5, playerServer.getBally() + 5, 45 - 10, 45 - 10);

        // a mensagem enviada
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

    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 0, this);
        playerClient.ok = true;
    }

    public void playerUP() {
        if (playerClient.getY() - playerMovement > playerHeight / 2 - 10) {

            playerClient.setY(playerClient.getY() - playerMovement);
        }
    }

    public void playerDOWN() {
        if (playerClient.getY() + playerMovement < HEIGHT - playerHeight - 30) {

            playerClient.setY(playerClient.getY() + playerMovement);
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        
        int keycode = arg0.getKeyCode();
        if (keycode == KeyEvent.VK_UP) {
            playerUP();
            repaint();
        }
        if (keycode == KeyEvent.VK_DOWN) {
            playerDOWN();
            repaint();
        }
        if (playerServer.isRestart()) {
            playerClient.restart = true;
            reset = true;
        }
        if (keycode == KeyEvent.VK_ESCAPE || keycode == KeyEvent.VK_N && playerServer.isRestart()) {
            try {
                this.setVisible(false);
                clientSoc.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            clientSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

}
