package ChatIPPackage;

import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao extends Observable {

    private String ip;
    private int porta;
    private String name;
    private String mensagem;
    private Socket socket;
    private Mensagem pacote;

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

    public void envia(Mensagem texto) throws IOException {
        socket = new Socket(ip, porta);
        new Thread(new Envia(texto, socket)).start();
    }

    public void notifica(String mensagem) {
        this.mensagem = mensagem;
        setChanged();
        notifyObservers();
    }

    class Recebe implements Runnable {

        String dadosReceber;
        boolean erro = false;
        Socket socket = null;

        @Override
        public void run() {
            while (true) {
                try {
                    socket = new Socket(getIp(), getPorta());
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    pacote = (Mensagem)input.readObject();
                    System.out.println(pacote.getMensagem());
                } catch (SocketException ex) {
                    Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                erro = false;
                while (!erro) {
                    Mensagem pacoteRecebido;
                    try {
                        dadosReceber = pacote.getMensagem();
                        String nome = pacote.getRemetente() + " disse:";
                        notifica(nome + dadosReceber);
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

        Mensagem texto;

        public Envia(Mensagem msg, Socket socket) throws IOException {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            output.writeObject(msg);
            output.flush();
        }

        @Override
        public void run() {

            byte[] dados = texto.getMensagem().getBytes();

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