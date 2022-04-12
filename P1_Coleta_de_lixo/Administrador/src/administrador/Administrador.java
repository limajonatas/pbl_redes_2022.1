package administrador;


import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import org.json.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Administrador extends Thread {

    private static final String ipCliente = "";
    private static int porta;
    private static int main_port_server;
    private static int port_server;
    private static DatagramSocket adm_socket;
    private static JSONObject json;
    private static boolean serverOk;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     */
    @Override
    public void run() {

        while (true) {

            //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
            byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)

            try {
               // Thread.sleep(2000);//2 segundos

                //set_get_Date();

                cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                System.out.println("__________\n" + json.toString());
             

                InetAddress ip = InetAddress.getByName(""); //atribuindo o ip

                //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);

                adm_socket.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                //set_get_Date();
            } catch (IOException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /*
    Criar aquirvo JSON
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("tipo", 1); //por enquanto para identificar que é uma lixeira- nº3
        json.put("connected", false);
    }


    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("porta", adm_socket.getLocalPort());
        System.out.println("DADOS LIXEIRA: " + json.toString());//afim de verificar se está tudo ok
    }

    public static void startupThreads() {
        Thread send = new Administrador();
        send.start();
        receiveThread();
    }

    /**
     * RECEBE AS MENSAGENS DO SERVIDOR
     */
    public static void receiveThread() {
        Thread a = new Thread() {
            @Override
            public void run() {
                while (true) {

                    byte[] cartaAReceber = new byte[1024];
                    DatagramPacket envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                    try {
                        System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
                        adm_socket.receive(envelopeAReceber); //recebe o envelope do Servidor
                        //converte os dados do envelope para string
                        String mensagemRecebida = new String(envelopeAReceber.getData());

                        System.out.println("\n________\nCHEGOU DO SERVIDOR:" + mensagemRecebida + "\n");
                        JSONObject objReceive = new JSONObject(mensagemRecebida);
      

                        //set_get_Date();

                        Thread.sleep(1000);
                    } catch (IOException | JSONException | InterruptedException ex) {
                        Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        a.start();
    }

    /*public static void set_get_Date() throws JSONException {
        if (mainview != null) {//garantir que a instancia da tela principal nao seja utilizada antes de ser criada fora da thread
            //pega informação sobre a capacidade atual
            json.put("capacidade_atual", mainview.getDate().getDouble("capacidade_atual"));
            json.put("capacidade_disponivel", mainview.getDate().getDouble("capacidade_disponivel"));

            mainview.setDados(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"), json.getDouble("capacidade_disponivel"));
        }

    }*/

    public static void main(String args[]) throws JSONException, UnknownHostException {
        try {
            port_server = 5000;
            adm_socket = new DatagramSocket();

            createJson();

            /*Altera o design da janela para o flatlaf dark */
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Falha ao iniciar o LaF");
            }
        

            inserirdados();//insere os dados ao json
            startupThreads(); //inicializa a tentativa de conexao com o servidor

           /*
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (json.getBoolean("connected")) {
                                Thread.sleep(15000);//15s
                                json.put("connected", false);
                            }
                        } catch (JSONException | InterruptedException ex) {
                            Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

            };
            t.start();*/

        } catch (SocketException e) {
            System.err.println("NAO FOI POSSIVEL CRIAR O SOCKET LIXEIRA!");
        }

    }
}
