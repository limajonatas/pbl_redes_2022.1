package caminhao;

import view.MainViewCaminhao;
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
    private static int port_cliente;
    private static int port_server;
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
            try {
                if (!json.getBoolean("connected")) {//se o servidor estiver desconectado, até conectar
                    //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
                    byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)

                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    String a = json.toString();
                    System.out.println("__________\n" + a);

                    //InetAddress ip = InetAddress.getByName(""); //atribuindo o ip
                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);

                    cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                    set_get_Date();
                    Thread.sleep(1000);

                }
            } catch (IOException | JSONException | InterruptedException ex) {
                Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
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
     * THREAD QUE RECEBE RESPOSTA DE SERVIDOR E REQUISITA
     *
     */
    public static void receiveThread() {
        Thread a = new Thread() {
            @Override
            public void run() {
                while (true) {
                    
                    
                    
                    byte[] cartaAReceber = new byte[1024];
                    DatagramPacket envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                    try {
                        
                        if (mainview.isRestart()) {
                            json.put("restart", true);
                            mainview.set_restartOFF();
                        }
                                                
                        System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
                        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor

                        String mensagemRecebida = new String(envelopeAReceber.getData()); //converte os dados do envelope para string

                        System.out.println("\n________\nCHEGOU DO SERVIDOR:" + mensagemRecebida + "\n");
                        JSONObject obj_receive = new JSONObject(mensagemRecebida);

                        json.put("restart", false);

                        if (obj_receive.getString("msg").equals("PROX")) { //se há lixeira //RECEBE INFO DA PROX LIXEIRA

                            json.put("connected", true);
                            json.put("lixeira_coletada", false);

                            System.out.println("SERVIDOR ENVIOU INFO SOBRE NOVA LIXEIRA");
                            System.out.println("_____________________________________");
                            System.out.println(obj_receive.toString());
                            System.out.println("_____________________________________");
                            mainview.set_prox_lixeira(obj_receive.toString()); //atualiza interface
                            Thread.sleep(1000);
                            if (mainview.isCollected()) {//foi coletado?
                                System.out.println("=============== CAMINHAO COLETOU!");
                                json.put("lixeira_coletada", true);
                                json.put("id_lixeira", obj_receive.getInt("id"));
                                json.put("latitude_lixeira", obj_receive.getInt("latitude_lixeira"));
                                json.put("longitude_lixeira", obj_receive.getInt("longitude_lixeira"));
                            }

                            //no final enviar o json - alterar msg para: "COLLECTED"
                            //add ao json a latitude e longitude da lixeira.
                        } else if (obj_receive.getString("msg").equals("EXIST")) {
                            json.put("connected", true);
                            System.out.println("JÁ EXISTE UM CAMINHAO CONNECTADO!");
                            mainview.set_status_server_existCaminhao();
                        } else if (obj_receive.getString("msg").equals("NULL")) {
                            json.put("connected", true);
                            System.out.println("NÃO EXISTE LIXEIRAS!");
                        } else if (obj_receive.getString("msg").equals("ALLCOLLECTED")) {
                            json.put("lixeira_coletada", false);
                            json.put("connected", true);
                            //while() espera ate que o botao de reiniciar seja acionado
                            //apos isso enviar mensagem de reistartar

                            mainview.set_restart();//habilita botao para reiniciar o processo de coleta

                            mainview.setProximaLixeiraNull();

                        } else if (obj_receive.getString("msg").equals("PORT")) {//primeira conexao envia a porta
                            json.put("connected", true);
                            port_server = obj_receive.getInt("porta");
                            System.out.println("SERVIDOR CONECTADO AO CAMINHAO " + json.getBoolean("connected"));
                            System.out.println("MUDEI A PORTA PARA " + port_server);
                        } else if (obj_receive.getString("msg").equals("REINICIADO")) {
                            json.put("connected", true);
                            json.put("restart", false);
                            mainview.set_restartOFF();
                        }

                        set_get_Date();

                    } catch (IOException | JSONException | InterruptedException ex) {
                        Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ///VERIFICAR SE HÁ LIXEIRA/PROXIMA LIXEIRA-SOLICITANDO AO SERVIDOR
                    byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)
                    DatagramPacket envelopeAEnviar = null;
                    try {

                        if (mainview.isRestart()) {
                            json.put("restart", true);
                        }

                        json.put("msg", "requisitar");

                        cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                        //InetAddress ip = InetAddress.getByName(""); //atribuindo o ip
                        //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                        envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);
                    } catch (JSONException ex) {
                        Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        
                        cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 
                    } catch (IOException ex) {
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
        json.put("tipo", 2); //por enquanto para identificar que é um caminhao- nº3
        json.put("capacidade_atual", 0);
        json.put("connected", false); //conectado ao servidor
        json.put("requisitar", false);
        json.put("restart", false);// reiniciar novo process de coleta
        json.put("lixeira_coletada", false); //lixeira coletada
    }

    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("capacidade_max", mainview.getCapacidadeMaxima());
        json.put("address", InetAddress.getByName(""));
        json.put("porta", cliente.getLocalPort());

        System.out.println("DADOS CAMINHAO: " + json.toString());//afim de verificar se está tudo ok
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        try {
            port_server = 5000;
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
