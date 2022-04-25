package lixeira;

import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import org.json.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.MainViewLixeira;
import view.FirstViewLixeira;

/**
 * *
 *
 * @author JONATAS DE JESUS LIMA
 */
public class Lixeira extends Thread {

    private static final String ipCliente = "";
    private static int port_server;
    private static DatagramSocket cliente;
    private static JSONObject json;
    private static FirstViewLixeira inicializacao;
    private static MainViewLixeira mainview;
    private static InetAddress ip_servidor;
    private static byte[] cartaAEnviar;
    private static byte[] cartaAReceber;
    private static boolean recadastrado_servidor;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     *
     */
    @Override
    public void run() {

        while (true) {
            try {
                cartaAEnviar = new byte[2048];
                if (!json.getBoolean("connected")) { //servidor nao conectado
                    json.put("capacidade_atual", mainview.obter_capacidade_atual());
                    json.put("capacidade_disponivel", mainview.obter_capacidade_disponivel());
                    json.put("bloqueio", mainview.obter_status_bloqueio());

                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    System.out.println("<<[LIXEIRA]: TO SERVER: " + json.toString());

                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip_servidor, port_server);

                    cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                    Thread.sleep(3000);
                } else {
                    if (mainview.deve_atualizar() || recadastrado_servidor) { //enviar dados apenas depois de adicionar lixo.
                        recadastrado_servidor = false;
                        Thread.sleep(1500);//1,5 segundo
                        
                        json.put("capacidade_atual", mainview.obter_capacidade_atual());
                        json.put("capacidade_disponivel", mainview.obter_capacidade_disponivel());
                        json.put("bloqueio", mainview.obter_status_bloqueio());

                        cartaAEnviar = (json.toString()).getBytes();

                        System.out.println("<<[LIXEIRA]: TO SERVER: " + json.toString());

                        DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip_servidor, port_server);

                        cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                        mainview.atualizado();
                    }

                }
            } catch (IOException | JSONException | InterruptedException ex) {
                Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * CRIA ARQUIVO JSON
     *
     * @throws JSONException erro ao inserir dados ao objeto JSON.
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("tipo", 3); //por enquanto para identificar que é uma lixeira- nº3
        json.put("capacidade_atual", 0);
        json.put("bloqueio", true); //lixeira está bloqueada INCIALMENTE até se conectar ao servidor
        json.put("connected", false);
        json.put("coletada", false);
    }

    /**
     * BLOQUEIA A LIXEIRA OU DESBLOQUEIA
     *
     * @throws JSONException erro ao inserir dados ao objeto JSON
     */
    private static void bloquearOnOff() throws JSONException {
        if (json.getBoolean("bloqueio")) {
            json.put("bloqueio", false);
            mainview.set_label_desbloqueada();
        } else {
            json.put("bloqueio", true);
            mainview.set_label_bloqueada();
        }
    }

    /**
     * INSERE O RESTANTE DOS DADOS AO ARQUIVO JSON DA LIXEIRA
     *
     * @throws JSONException erro ao inserir dados ao objeto json.
     *
     */
    public static void inserirdados() throws JSONException {
        json.put("id", "");
        json.put("capacidade_max", inicializacao.getCapacidadeMaxima());
        json.put("latitude", inicializacao.getLatitude());
        json.put("longitude", inicializacao.getLongitude());
        json.put("porta", cliente.getLocalPort());
        json.put("capacidade_disponivel", inicializacao.getCapacidadeMaxima());

        System.out.println("DADOS LIXEIRA: " + json.toString());//afim de verificar se está tudo ok
    }

    /**
     * INICIALIZA AS THREADS. Inicia as threads principal de envio de dados ao
     * servidor e a outra thread para receber os dados.
     */
    public static void startupThreads() {
        Thread send = new Lixeira();
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
                    cartaAReceber = new byte[1024];
                    DatagramPacket envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                    try {
                        System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
                        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor
                        //converte os dados do envelope para string
                        String mensagemRecebida = new String(envelopeAReceber.getData());

                        System.out.println(">>>>[SERVIDOR]:" + mensagemRecebida + "\n");
                        JSONObject objReceive = new JSONObject(mensagemRecebida);

                        if (objReceive.getString("msg").equals("OK")) {
                            json.put("connected", true);
                            json.put("address", objReceive.get("address"));
                            System.out.println(">>>>[SERVIDOR]: OK");
                            manda_dados_interface();
                        } else if (objReceive.getString("msg").equals("FULL")) {
                            JOptionPane.showMessageDialog(mainview, "NÃO É POSSÍVEL CADASTRAR MAIS LIXEIRAS!", "There are 3 SmartBin connected", JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        } else if (objReceive.getString("msg").equals("PORT")) {//primeira conexao envia a porta
                            json.put("connected", true);
                            port_server = objReceive.getInt("porta");
                            json.put("bloqueio", false);
                            json.put("id", objReceive.getInt("id"));
                            mainview.recebe_id(objReceive.getInt("id"));
                            manda_dados_interface();
                        } else if (objReceive.getString("msg").equals("COLLECTED")) {
                            json.put("connected", true);
                            mainview.set_label_foi_coletada();
                            json.put("coletada", true);
                            json.put("capacidade_disponivel", json.get("capacidade_max"));
                            mainview.foiColetada();
                            manda_dados_interface();
                        } else if (objReceive.getString("msg").equals("STATUS")) {
                            json.put("connected", true);
                            json.put("coletada", false);
                            System.out.println("PODE FAZER NOVA COLETA");
                            manda_dados_interface();
                        } else if (objReceive.getString("msg").equals("CHARGE_BLOCK")) {
                            json.put("connected", true);
                            bloquearOnOff();
                            manda_dados_interface();
                            recadastrado_servidor = true;
                        }

                        

                        Thread.sleep(1000);
                    } catch (IOException | JSONException | InterruptedException ex) {
                        Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        a.start();
    }

    /**
     * MANDA OS DADOS PARA A INTERFACE
     *
     * @throws JSONException erro ao obter dado de um objeto json.
     */
    public static void manda_dados_interface() throws JSONException {
        if (mainview != null) {//garantir que a instancia da tela principal nao seja utilizada antes de ser criada fora da thread
            mainview.recebe_dados_para_interface(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"), json.getDouble("capacidade_disponivel"));
        }
    }

    /**
     * MÉTODO MAIN. Resposável por inicializar o cliente, threads, interfaces...
     *
     * @param args
     * @throws JSONException erro ao inserir/obter dados de um objeto json
     * @throws UnknownHostException erro ao obter dados da classe InetAddress
     */
    public static void main(String args[]) throws JSONException, UnknownHostException {
        try {
            recadastrado_servidor = false;
            ip_servidor = InetAddress.getByName("");
            port_server = 5000;
            cliente = new DatagramSocket();

            createJson();

            /*Altera o design da janela para o flatlaf dark */
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Falha ao iniciar o LaF");
            }

            //JFrame para inserir os dados da lixeira
            inicializacao = new FirstViewLixeira();
            Random gerador = new Random();
            inicializacao.set_latitude_longitude(gerador.nextInt(-90, 90), gerador.nextInt(-90, 90));
            inicializacao.recebe_ip_e_porta_servidor(ip_servidor, port_server);
            inicializacao.setVisible(true);

            //aguardar o usuario inserir os dados necessarios da lixeira para continuar
            while (inicializacao.isVisible()) {
                System.out.print("");//garatindo o funcionamento do while.
            }
            ip_servidor=inicializacao.obter_ip_servidor();
            port_server=inicializacao.obter_porta_servidor();
            
            
            inserirdados();//insere os dados ao json

            mainview = new MainViewLixeira();
            mainview.recebe_dados_para_interface(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"), json.getDouble("capacidade_disponivel"));

            mainview.setVisible(true);
            
            startupThreads(); //inicializa a tentativa de conexao com o servidor

            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (json.getBoolean("connected")) {
                                Thread.sleep(30000);//30s
                                mainview.tentando_conectar_servidor();
                                Thread.sleep(1000);//1s
                                json.put("connected", false);
                                mainview.servidor_desconectado();
                                
                            }
                        } catch (JSONException | InterruptedException ex) {
                            Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
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
