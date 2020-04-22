package PPRodVic;

// código baseado em um curso da udemy

import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JogadorPP extends JFrame implements KeyListener, Runnable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final BufferedImage image;
    private static final String TITLE = "Ping Pong RCA VOC - Servidor";
    //tamanho da tela
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 460;
    private String nomeServidor, clienteNome;

    public JogadorPP() throws IOException {
        this.clienteNome = "clientname";
        this.nomeServidor = "servername";
        image = ImageIO.read(new File("./background.png"));
    }

    @Override
    public void run() {
        this.setVisible(true);
        this.setTitle(TITLE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);
        this.addKeyListener(this);
    }

    public static void main(final String[] args) throws IOException {
        final JogadorPP newT = new JogadorPP();
        newT.run();
    }

    @Override
    public void paint(final Graphics g) {
        g.drawImage(image, 0, 20, this);
    }

    @Override
    public void keyPressed(final KeyEvent arg0) {

        final int keyCode = arg0.getKeyCode();
        String portAddress = null;
        String ipAdd = null;

        // Entradas para criação do servidor
        // Escolhe a tecla A para Criar
        if (keyCode == KeyEvent.VK_A) {
            portAddress = "1024";
            if (portAddress != null) {
                if (!isPort(portAddress)) {
                    JOptionPane.showMessageDialog(null, "Porta em formato incorreto!", "Erro", 1);

                    final PPServidor meuServer = new PPServidor(nomeServidor, portAddress);
                    final Thread myServerThread = new Thread(meuServer);
                    myServerThread.start();
                    this.setVisible(false);
                } else {
                    nomeServidor = JOptionPane.showInputDialog(null, "Nome com pelo menos 3 letras", "Digite seu nome",
                            1);
                    nomeServidor += "";

                    if (nomeServidor.length() > 10 || nomeServidor.length() < 3 || nomeServidor.startsWith("null")) {
                        JOptionPane.showMessageDialog(null,
                                "Nome em formato incorreto (menor que 3 caracteres/maior que 10 caracteres/vazio)!",
                                "Erro", 1);
                    } else {
                        final PPServidor meuServer = new PPServidor(nomeServidor, portAddress);
                        final Thread myServerThread = new Thread(meuServer);
                        myServerThread.start();
                        this.setVisible(false);
                    }
                }
            }
        }

        // Entradas para o cliente
        // escolha a tecla J para logar como jogador
        if (keyCode == KeyEvent.VK_J) {
            ipAdd = JOptionPane.showInputDialog(null, "Examplo: 127.0.0.1",
                    "Digite o IP do servidor onde deseja conectar", 1);

            if (ipAdd != null) {
                if (!isIPAddress(ipAdd)) {
                    JOptionPane.showMessageDialog(null, "IP em formato incorreto!", "Erro", 1);
                } else {
                    portAddress = JOptionPane.showInputDialog(null, "Exemplo: 1024", "Digite a porta do servidor", 1);

                    if (portAddress != null) {
                        if (!isPort(portAddress)) {
                            JOptionPane.showMessageDialog(null, "Porta em formato incorreto!", "Erro", 1);
                        } else {
                            clienteNome = JOptionPane.showInputDialog(null, "Nome com pelo menos 3 caracteres",
                                    "Digite seu nome", 1);
                            clienteNome += "";
                            if (clienteNome.length() > 10 || clienteNome.length() < 3
                                    || clienteNome.startsWith("null")) {
                                JOptionPane.showMessageDialog(null,
                                        "Nome errado (menor que 3 caracteres/maior que 10 letras)!", "Erro", 1);
                            } else {
                                final PPCliente meuCliente = new PPCliente(clienteNome, portAddress, ipAdd);
                                final Thread myClientT = new Thread(meuCliente);
                                myClientT.start();
                                this.setVisible(false);
                            }
                        }
                    }
                }
            }
        }
    }

    // utilidades
    private boolean isPort(final String str) {
        final Pattern pPattern = Pattern.compile("\\d{1,4}");
        return pPattern.matcher(str).matches();
    }

    private boolean isIPAddress(final String str) {
        final Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        return ipPattern.matcher(str).matches();
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

}
