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
 * CLASSE DO CAMINHAO
 *
 * @author JONATAS DE JESU LIMA
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
    private static byte[] cartaAEnviar;
    private static byte[] cartaAReceber;
    private static boolean btn_reiniciar_ligado;
    private static boolean todas_lixeiras_coletadas;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     *
     */
    @Override
    public void run() {
        while (true) {
            cartaAEnviar = new byte[2048];
            try {
                if(mainview.caminhao_foi_descarregado()){
                    mainview.set_date_truck(0.0, json.getDouble("capacidade_max"));
                
                }
                if (!json.getBoolean("connected")) {//se o servidor estiver desconectado, até conectar

                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    System.out.println("DATE TRUCK: " + json.toString());

                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);

                    cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                    Thread.sleep(3000);
                } else {
                    try {
                        if (mainview.fez_alguma_coletada()) {
                            JSONObject objSend = new JSONObject();

                            objSend.put("msg", "BIN_COLLECTED");
                            objSend.put("id_lixeira_coletada", mainview.get_id_lixeira_coletada());
                            objSend.put("quantidade_lixo_coletado", mainview.get_qtd_lixo_coletado());
                            objSend.put("capacidade_atual_caminhao", json.getDouble("capacidade_atual"));
                            objSend.put("capacidade_disponivel_caminhao", json.getDouble("capacidade_atual"));
                            objSend.put("capacidade_maxima_caminhao", json.getDouble("capacidade_max"));

                            cartaAEnviar = (objSend.toString()).getBytes(); //converte a mensagem String para array de bytes
                            DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);
                            cliente.send(envelopeAEnviar);
                            mainview.lixeira_foi_coletada();
                        } else if (mainview.btn_reiniciar_clicado()) {
                            JSONObject objSend = new JSONObject();

                            objSend.put("msg", "RESTART");

                            cartaAEnviar = (objSend.toString()).getBytes(); //converte a mensagem String para array de bytes
                            DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);
                            cliente.send(envelopeAEnviar);
                            btn_reiniciar_ligado = false;
                        } else if (!btn_reiniciar_ligado) {//quando o processo pode ser reiniciado o caminhão nao faz requisições
                            json.put("msg", "REQUIRE");

                            cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                            DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ((InetAddress) json.get("address")), port_server);
                            cliente.send(envelopeAEnviar);
                        }
                        Thread.sleep(7000);
                    } catch (JSONException ex) {
                        Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } catch (IOException | JSONException | InterruptedException ex) {
                Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * INICIALIZA AS THREADS.
     */
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
                    cartaAReceber = new byte[2048];
                    DatagramPacket envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);

                    try {
                        

                        System.out.println("AGUARDANDO RESPOSTA DO SERVIDOR...");
                        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor

                        String mensagemRecebida = new String(envelopeAReceber.getData()); //converte os dados do envelope para string

                        System.out.println(">>>>[SERVIDOR]: " + mensagemRecebida + "\n");
                        JSONObject obj_receive = new JSONObject(mensagemRecebida);

                        //json.put("restart", false);
                        if (obj_receive.getString("msg").equals("PROX")) { //se há lixeira //RECEBE INFO DA PROX LIXEIRA

                            json.put("connected", true);
                            json.put("lixeira_coletada", false);

                            System.out.println(">>>>[SERVER]: PROXIMA LIXEIRA: " + obj_receive.toString());

                            mainview.recebe_proxima_lixeira(obj_receive.toString()); //atualiza interface

                        } else if (obj_receive.getString("msg").equals("EXIST")) {
                            json.put("connected", true);
                            System.out.println("JÁ EXISTE UM CAMINHAO CONNECTADO!");
                            mainview.set_status_server_existCaminhao();
                        } else if (obj_receive.getString("msg").equals("NULL")) {
                            json.put("connected", true);
                            System.out.println("NÃO EXISTE LIXEIRAS!");
                        } else if (obj_receive.getString("msg").equals("ALLCOLLECTED")) {
                            json.put("connected", true);
                            if (!todas_lixeiras_coletadas) {
                                mainview.ativar_btn_reiniciar();
                                btn_reiniciar_ligado = true;
                                todas_lixeiras_coletadas = true;
                            }

                        } else if (obj_receive.getString("msg").equals("PORT")) {//primeira conexao envia a porta
                            json.put("connected", true);
                            port_server = obj_receive.getInt("porta");
                            System.out.println("<<[CAMINHÃO]:PORTA DO SERVIDOR ALTERADA COM SUCESSO!");
                        } else if (obj_receive.getString("msg").equals("REINICIADO")) {
                            json.put("connected", true);
                            // json.put("restart", false);
                            mainview.set_restartOFF();
                            btn_reiniciar_ligado = false;
                            todas_lixeiras_coletadas = false;

                        } else if (obj_receive.getString("msg").equals("COLETA_OK")) {
                            mainview.limpar_dados_lixeira_coletada();
                            json.put("capacidade_atual", obj_receive.getDouble("capacidade_atual_caminhao"));
                            json.put("capacidade_disponivel", obj_receive.getDouble("capacidade_disponivel_caminhao"));
                            mainview.set_date_truck(obj_receive.getDouble("capacidade_atual_caminhao"), obj_receive.getDouble("capacidade_disponivel_caminhao"));
                        }
                    } catch (IOException | JSONException ex) {
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
        //json.put("restart", false);// reiniciar novo process de coleta
        //json.put("lixeira_coletada", false); //lixeira coletada
    }

    /**
     * INSERE OS DADOS RESTANTE NO ARQUIVO JSON DO CAMINHAO
     *
     * @throws JSONException
     * @throws UnknownHostException
     */
    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("capacidade_max", mainview.getCapacidadeMaxima());
        json.put("capacidade_disponivel", json.getDouble("capacidade_max"));
        json.put("address", InetAddress.getByName(""));
        json.put("porta", cliente.getLocalPort());
        System.out.println("ARQUIVO JSON CRIADO: " + json.toString());
    }

    /**
     * MAIN Instancia o socket, interface e as threads.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        try {
            todas_lixeiras_coletadas = false;
            btn_reiniciar_ligado = false;
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
                    Thread.sleep(1000);
                    System.out.println("A CAPACIDADE AINDA É ZERO");
                }

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
