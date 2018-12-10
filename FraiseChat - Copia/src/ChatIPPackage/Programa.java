package ChatIPPackage;

import javax.swing.JOptionPane;

public class Programa {

    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Informe o IP","192.168.");
        int porta = Integer.parseInt(JOptionPane.showInputDialog("Informe a Porta","5000"));
        String nome = JOptionPane.showInputDialog("Informe o nome de usuário","Cliente");

        Conexao con = new Conexao(ip, porta, nome);

        JanelaChat window = new JanelaChat(con);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}