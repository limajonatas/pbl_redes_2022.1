package administrador;

import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import org.json.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lixeira_date.Lixeira_Date;
import view.MainViewAdministrador;

/**
 * CLASSE DO ADMINISTRADOR
 * @author JONATAS DE JESUS LIMA
 */
public class Administrador extends Thread {

    private static final String ipCliente = "";
    private static int port_server;
    private static DatagramSocket adm_socket;
    private static JSONObject json;
    private static byte[] cartaAEnviar = new byte[2048];
    private static InetAddress ip;
    private static MainViewAdministrador mainview;
    private static Date dataHoraAtual;
    private static boolean atualizar;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     */
    @Override
    public void run() {

        while (true) {
            try {
                if (!json.getBoolean("connected")) {//PRIMEIRA CONEXAO

                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    System.out.println("<<[ADMINISTRADOR]: " + json.toString());

                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);

                    adm_socket.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 
                    Thread.sleep(2000);
                } else {
                    Thread.sleep(500);
                    if (atualizar) {
                        atualizar = false;
                        if (mainview.lixeira_foi_alterada()) {

                            atualizar = true;

                            json.put("msg", "CHANGE");
                            JSONObject obj = new JSONObject(mainview.lixeira_alterada().toString());
                            json.put("id_lixeira", obj.getInt("id"));
                            json.put("bloqueio_lixeira", obj.getBoolean("bloqueio"));
                            cartaAEnviar = (json.toString()).getBytes();
                            DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);

                            adm_socket.send(envelopeAEnviar);
                            System.out.println("LIXEIRA ALTERADA === " + json.toString());
                        } else {
                            json.put("msg", "REQUEST");

                            cartaAEnviar = (json.toString()).getBytes();
                            DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);
                            adm_socket.send(envelopeAEnviar);
                        }
                    } else if (mainview != null) {
                        if (mainview.isAtualizado()) {
                            if (mainview.lixeira_foi_alterada()) {

                                atualizar = true;

                                json.put("msg", "CHANGE");
                                JSONObject obj = new JSONObject(mainview.lixeira_alterada().toString());
                                json.put("id_lixeira", obj.getInt("id"));
                                json.put("bloqueio_lixeira", obj.getBoolean("bloqueio"));
                                cartaAEnviar = (json.toString()).getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);
                                adm_socket.send(envelopeAEnviar);

                                System.out.println("LIXEIRA ALTERADA === " + json.toString());
                            } else {

                                json.put("msg", "REQUEST");

                                cartaAEnviar = (json.toString()).getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);
                                adm_socket.send(envelopeAEnviar);
                            }

                        }

                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
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
        json.put("porta", adm_socket.getLocalPort());

    }

    /**
     * INICIALIZA THREADS
     */
    private static void startupThreads() {
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

                        if (objReceive.getString("msg").equals("EMPTY")) {//nao há lixeiras
                            mainview.nao_ha_lixeiras();
                            dataHoraAtual = new Date();
                            String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
                            mainview.atualizado(hora);
                        } else if (objReceive.getString("msg").equals("OK")) {
                            dataHoraAtual = new Date();
                            String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
                            mainview.atualizado(hora);

                            int quantidade_lixeiras = objReceive.getInt("quantidade");
                            JSONArray array_json_lixeiras = objReceive.getJSONArray("lixeiras");
                            for (int i = 0; i < quantidade_lixeiras; i++) { //adicionar as lixeira
                                int lix = i + 1;
                                JSONObject obj = array_json_lixeiras.getJSONObject(0).getJSONObject("lixeira" + lix);
                                Lixeira_Date lixeira = new Lixeira_Date(true,
                                        null, 0, false, obj.getInt("latitude"), obj.getInt("id"),
                                        3, obj.getDouble("capacidade_disponivel"),
                                        obj.getDouble("capacidade_atual"), obj.getDouble("capacidade_max"),
                                        obj.getBoolean("bloqueio"), obj.getInt("longitude")
                                );
                                mainview.inserir_lixeiras(lixeira);
                            }
                            try {
                                if (objReceive.get("proxima_lixeira") != null) { //caminhao está ativo
                                    System.out.println(objReceive.getJSONObject("proxima_lixeira").getInt("latitude"));
                                    mainview.proxima_lixeira(objReceive.getJSONObject("proxima_lixeira").getInt("id"),
                                            objReceive.getJSONObject("proxima_lixeira").getInt("latitude"),
                                            objReceive.getJSONObject("proxima_lixeira").getInt("longitude"));
                                }
                            } catch (JSONException ex) {
                                System.err.println("NAO EXISTE PROXIMA LIXEIRA PARA COLETA");
                            }


                            /*FALTA DESTRICHAR A QUESTAO DAS INFORMAÇÕES DA LIXEIRA E MANDAR PARA A INTERFACCE*/
                        } else if (objReceive.getString("msg").equals("PORT")) {
                            json.put("connected", true);
                            port_server = objReceive.getInt("porta");
                            System.out.println("RESPOSTA DO SERVIDOR");
                        }

                        //set_get_Date();
                        // Thread.sleep(1000);
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        a.start();
    }

    /**
     * MAIN - INICIALIZA O SOCKET E AS THREADS
     * @param args
     * @throws JSONException
     * @throws UnknownHostException 
     */
    public static void main(String args[]) throws JSONException, UnknownHostException {
        try {
            atualizar = false;
            ip = InetAddress.getByName("");
            port_server = 5000;
            adm_socket = new DatagramSocket();

            /*Altera o design da janela para o flatlaf dark */
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Falha ao iniciar o LaF");
            }

            createJson();
            startupThreads();
            mainview = new MainViewAdministrador();
            mainview.setVisible(true);

            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(20000);//10s
                            atualizar = true;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

            };
            t.start();

        } catch (SocketException e) {
            System.err.println("NAO FOI POSSIVEL CRIAR O SOCKET LIXEIRA!");
        }

    }
}
