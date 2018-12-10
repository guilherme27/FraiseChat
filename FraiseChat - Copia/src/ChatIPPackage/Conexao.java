package ChatIPPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao extends Observable {

    private String ip;
    private int porta;
    private String name;
    private String mensagem;

    public Conexao(String ip, int porta, String nome) {
        this.ip = ip;
        this.porta = porta;
        this.name = nome;
        new Thread(new Recebe()).start();
    }

    public String getName() {
        return name;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getIp() {
        return ip;
    }

    public int getPorta() {
        return porta;
    }

    public void envia(String texto) {
        new Thread(new Envia(texto)).start();
    }

    public void notifica(String mensagem) {
        this.mensagem = mensagem;
        setChanged();
        notifyObservers();
    }

    class Recebe implements Runnable {

        byte[] dadosReceber = new byte[255];
        boolean erro = false;
        DatagramSocket socket = null;

        @Override
        public void run() {
            while (true) {
                try {
                    socket = new DatagramSocket(getPorta());
                } catch (SocketException ex) {
                    Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
                }
                erro = false;
                while (!erro) {
                    DatagramPacket pacoteRecebido = new DatagramPacket(dadosReceber, dadosReceber.length);
                    try {
                        socket.receive(pacoteRecebido);
                        byte[] b = pacoteRecebido.getData();
                        String msg = "";
                        for (int i = 0; i < b.length; i++) {
                            if (b[i] != 0) {
                                msg += (char) b[i];
                            }
                        }
                        String nome = pacoteRecebido.getAddress().toString() + " disse:";
                        notifica(nome + msg);
                    } catch (Exception e) {
                        System.out.println("erro");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        erro = true;
                    }
                }
            }
        }
    }

    class Envia implements Runnable {

        String texto;

        public Envia(String texto) {
            this.texto = texto;
        }

        @Override
        public void run() {

            byte[] dados = texto.getBytes();

            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress addr = InetAddress.getByName(getIp());
                DatagramPacket pacote = new DatagramPacket(dados, dados.length, addr, getPorta());
                clientSocket.send(pacote);
                clientSocket.close();
            } catch (SocketException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}