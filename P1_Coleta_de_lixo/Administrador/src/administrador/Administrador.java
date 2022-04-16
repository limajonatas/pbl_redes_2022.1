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
import view.MainViewAdministrador;

public class Administrador extends Thread {
    
    private static final String ipCliente = "";
    private static int porta;
    private static int main_port_server;
    private static int port_server;
    private static DatagramSocket adm_socket;
    private static JSONObject json;
    private static boolean serverOk;
    private static byte[] cartaAEnviar = new byte[1024];
    private static InetAddress ip;
    private static MainViewAdministrador mainview;
    private static Date dataHoraAtual;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     */
    @Override
    public void run() {
        
        while (true) {
            try {
                if (!json.getBoolean("connected")) {//PRIMEIRA CONEXAO
                    
                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    System.out.println("__________\n" + json.toString());

                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);
                    
                    adm_socket.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 
                    Thread.sleep(2000);
                } else {
                    if (mainview.isAtualizado()) {
                        json.put("msg", "REQUEST");
                        
                        cartaAEnviar = (json.toString()).getBytes();
                        DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);
                        adm_socket.send(envelopeAEnviar);
                        Thread.sleep(5000);
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
                        
                        if (objReceive.getString("msg").equals("EMPTY")) {//nao há lixeiras
                            mainview.nao_ha_lixeiras();
                            dataHoraAtual = new Date();
                            String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
                            mainview.atualizado(hora);
                        } else if (objReceive.getString("msg").equals("OK")) {
                            dataHoraAtual = new Date();
                            String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
                            mainview.atualizado(hora);
                           /*FALTA DESTRICHAR A QUESTAO DAS INFORMAÇÕES DA LIXEIRA E MANDAR PARA A INTERFACCE*/
                        } else if (objReceive.getString("msg").equals("PORT")) {
                            json.put("connected", true);
                            port_server = objReceive.getInt("porta");
                            System.out.println("RESPOSTA DO SERVIDOR");
                        }

                        //set_get_Date();
                       // Thread.sleep(1000);
                    } catch (IOException | JSONException  ex) {
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
