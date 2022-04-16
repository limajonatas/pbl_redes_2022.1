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

public class Lixeira extends Thread {

    private static final String ipCliente = "";
    private static int porta;
    private static int main_port_server;
    private static int port_server;
    private static DatagramSocket cliente;
    private static JSONObject json;
    private static FirstViewLixeira inicializacao;
    private static MainViewLixeira mainview;
    private static boolean serverOk;
    private static InetAddress ip;
    private static byte[] cartaAEnviar = new byte[1024];

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     *
     */
    @Override
    public void run() {

        while (true) {
            try {
                if (!json.getBoolean("connected")) { //servidor nao conectado

                    //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
                    //byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)
                    Thread.sleep(2000);//2 segundos

                    set_get_Date();

                    cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes

                    System.out.println("__________\n" + json.toString());
                    // System.out.println("\n______\nLATITUDE LIXEIRA: "+ toJson().getDouble("latitude") + "\n_____");

                    //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                    DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);

                    cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                    //set_get_Date();
                } else {
                    if (mainview.isAtualizado()) { //enviar dados apenas depois de adicionar lixo.

                        Thread.sleep(2000);//2 segundos
                        set_get_Date();

                        cartaAEnviar = (json.toString()).getBytes();

                        System.out.println("__________\n" + json.toString());

                        DatagramPacket envelopeAEnviar = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, port_server);

                        cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                        mainview.atualizado();

                    }

                }
            } catch (IOException | JSONException | InterruptedException ex) {
                Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);

            }

        }
    }

    /*
    Criar aquirvo JSON
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("tipo", 3); //por enquanto para identificar que é uma lixeira- nº3
        json.put("capacidade_atual", 0);
        json.put("bloqueio", true); //lixeira está bloqueada
        json.put("connected", false);
        json.put("coletada", false);
        json.put("msg", "");
    }

    private void bloquearOnOff() throws JSONException {
        if (json.getBoolean("bloqueio")) {
            json.put("bloqueio", false);
        } else {
            json.put("bloqueio", true);
        }
    }

    public static void inserirdados() throws JSONException, UnknownHostException {

        json.put("id", "");
        json.put("capacidade_max", inicializacao.getCapacidadeMaxima());
        json.put("latitude", inicializacao.getLatitude());
        json.put("longitude", inicializacao.getLongitude());
        json.put("porta", cliente.getLocalPort());
        json.put("capacidade_disponivel", inicializacao.getCapacidadeMaxima());

        System.out.println("DADOS LIXEIRA: " + json.toString());//afim de verificar se está tudo ok
    }

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

                    byte[] cartaAReceber = new byte[1024];
                    DatagramPacket envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                    try {
                        System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
                        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor
                        //converte os dados do envelope para string
                        String mensagemRecebida = new String(envelopeAReceber.getData());

                        System.out.println("\n________\nCHEGOU DO SERVIDOR:" + mensagemRecebida + "\n");
                        JSONObject objReceive = new JSONObject(mensagemRecebida);

                        if (objReceive.getString("msg").equals("OK")) {
                            json.put("connected", true);
                            json.put("address", objReceive.get("address"));
                            System.out.println("SERVIDOR OK");
                            System.out.println(json.toString());
                        } else if (objReceive.getString("msg").equals("POS")) {
                            /*System.out.println("JÁ EXISTE UMA LIXEIRA NESTA POSIÇÃO, FAVOR ALTERE A POSICAO!");
                                inicializacao.setChargePosition("EXISTE LIXEIRA NESSA POSIÇÃO!");
                                inicializacao.setVisible(true);

                                //aguardar o usuario inserir os dados necessarios da lixeira para continuar
                                while (inicializacao.isVisible()) {
                                    System.out.print("");//garatindo o funcionamento do while.
                                }

                                inserirdados();*/
                        } else if (objReceive.getString("msg").equals("FULL")) {
                            System.out.println("NÃO PODE CADASTRAR MAIS LIXEIRAS!");
                            JOptionPane.showMessageDialog(mainview, "NÃO É POSSÍVEL CADASTRAR MAIS LIXEIRAS!", "There are 3 SmartBin connected", JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        } else if (objReceive.getString("msg").equals("PORT")) {//primeira conexao envia a porta
                            json.put("connected", true);
                            port_server = objReceive.getInt("porta");
                            json.put("bloqueio", false);
                            json.put("id", objReceive.getInt("id"));
                        } else if (objReceive.getString("msg").equals("COLLECTED")) {
                            json.put("connected", true);
                            System.out.println("================== LIXEIRA COLETADA");
                            json.put("coletada", true);
                            json.put("capacidade_disponivel", json.get("capacidade_max"));
                            mainview.foiColetada();
                        } else if (objReceive.getString("msg").equals("STATUS")) {
                            json.put("connected", true);
                            json.put("coletada", false);
                            System.out.println("PODE FAZER NOVA COLETA");
                        } else if (objReceive.getString("msg").equals("CONFIRMADO")) {
                            json.put("connected", true);
                        }

                        set_get_Date();

                        Thread.sleep(1000);
                    } catch (IOException | JSONException | InterruptedException ex) {
                        Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        a.start();
    }

    public static void set_get_Date() throws JSONException {
        if (mainview != null) {//garantir que a instancia da tela principal nao seja utilizada antes de ser criada fora da thread
            //pega informação sobre a capacidade atual
            json.put("capacidade_atual", mainview.getDate().getDouble("capacidade_atual"));
            json.put("capacidade_disponivel", mainview.getDate().getDouble("capacidade_disponivel"));

            mainview.setDados(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"), json.getDouble("capacidade_disponivel"));
        }

    }

    public static void main(String args[]) throws JSONException, UnknownHostException, IOException {
        try {
            ip = InetAddress.getByName("");
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
            inicializacao.setVisible(true);

            //aguardar o usuario inserir os dados necessarios da lixeira para continuar
            while (inicializacao.isVisible()) {
                System.out.print("");//garatindo o funcionamento do while.
            }

            inserirdados();//insere os dados ao json
            startupThreads(); //inicializa a tentativa de conexao com o servidor

            mainview = new MainViewLixeira();
            mainview.setDados(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"), json.getDouble("capacidade_disponivel"));

            mainview.setVisible(true);
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (json.getBoolean("connected")) {
                                Thread.sleep(30000);//30s
                                mainview.setStatusServer();
                                Thread.sleep(1000);//1s
                                json.put("connected", false);

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
