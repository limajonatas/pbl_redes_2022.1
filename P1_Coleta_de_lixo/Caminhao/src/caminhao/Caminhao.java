package caminhao;

import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.json.*;

/**
 *
 * @author jonatas
 */
public class Caminhao extends Thread {

    private static final String ipCliente = "";
    private static int port;
    private static DatagramSocket cliente;
    private static JSONObject json;
    private static MainViewCaminhao mainview;
    private static boolean serverOk;
    private static JSONObject lixeiras;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     *
     */
    @Override
    public void run() {
        while (true) {
            if (serverOk == false) {//se o servidor estiver desconectado, até conectar
                //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
                byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)

                try {
                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    String a = json.toString();
                    System.out.println("__________\n" + a);

                    //InetAddress ip = InetAddress.getByName(""); //atribuindo o ip
                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar
                            = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), 5000);

                    cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                    set_get_Date();
                    Thread.sleep(1000);

                } catch (IOException | JSONException | InterruptedException ex) {
                    Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void set_get_Date() throws JSONException {
        if (mainview != null) {//garantir que a instancia da tela principal nao seja utilizada antes de ser criada fora da thread
            //pega informação sobre a capacidade atual
            json.put("capacidade_atual", mainview.getDate().getDouble("capacidade_atual"));

            mainview.setDados(json.getDouble("capacidade_atual"), json.getBoolean("connected"));
        }

    }

    public static void startupThreads() {
        Thread send = new Caminhao();
        send.start();

        receiveThread();
    }

    /**
     * Thread específica para quando conectar-se ao servidor.
     * 
     */
    public static void receiveThread() {
        Thread a = new Thread() {
            @Override
            public void run() {
                while (true) {
                    byte[] cartaAReceber = new byte[1024];
                    DatagramPacket envelopeAReceber
                            = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                    try {
                        System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
                        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor
                        //converte os dados do envelope para string
                        String mensagemRecebida = new String(envelopeAReceber.getData());

                        System.out.println("\n________\nCHEGOU DO SERVIDOR:" + mensagemRecebida + "\n");
                        JSONObject obj = new JSONObject(mensagemRecebida);
                        
                        if (obj.getString("msg").equals("OK")) {
                            serverOk = true;
                            json.put("connected", true);
                            System.out.println("SERVIDOR OK");
                            System.out.println(json.toString());
                        } else if (obj.getString("msg").equals("EXIST")) {
                            serverOk = true;
                            System.out.println("JÁ EXISTE UM CAMINHAO CONNECTADO!");
                            mainview.set_status_server_existCaminhao();
                        }

                        set_get_Date();

                        Thread.sleep(1000);
                    } catch (IOException | JSONException | InterruptedException ex) {
                        Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        a.start();
    }
    

 /*
    Criar aquirvo JSON
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("type", 2); //por enquanto para identificar que é um caminhao- nº3
        json.put("capacidade_atual", 0);
        json.put("connected", false); //conectado ao servidor
    }

    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("capacidade_max", mainview.getCapacidadeMaxima());
        //   json.put("latitude", mainview.getLatitude());
        // json.put("longitude", mainview.getLongitude());
        json.put("address", InetAddress.getByName(""));
        json.put("port", cliente.getLocalPort());

        System.out.println("DADOS LIXEIRO: " + json.toString());//afim de verificar se está tudo ok
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        try {
            cliente = new DatagramSocket();

            try {
                createJson();

                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    System.err.println("Failed to initialize LaF");
                }

                mainview = new MainViewCaminhao();
                while (mainview.getCapacidadeMaxima() == 0) {
                    Thread.sleep(500);
                    System.out.println("AINDA É ZERO");
                }
                System.out.println("OK capacidade->> " + mainview.getCapacidadeMaxima());
                inserirdados();
                startupThreads(); //inicializa as thres (de envio e recebimento)

            } catch (JSONException ex) {
                Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SocketException ex) {
            Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
