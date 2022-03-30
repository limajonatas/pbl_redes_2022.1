
import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import org.json.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lixeira2.MainViewLixeira;
import lixeira2.OneViewLixeira;

public class Lixeira extends Thread {

    private static final String ipCliente = "";
    private static int port;
    private static DatagramSocket cliente;
    private static JSONObject json;
    private static OneViewLixeira inicializacao;
    private static MainViewLixeira mainview;
    private static boolean serverOk;

    /**
     * ENVIAR DADOS PARA O SERVIDOR
     *
     */
    @Override
    public void run() {

        //Scanner teclado = new Scanner(System.in);
        while (true) {
            //System.out.print("Digite s para sair e qualquer coisa para enviar:");
            //String mensagem = teclado.nextLine();
            // if (mensagem.equals("s")) {
            //   cliente.close();
            //   teclado.close();
            //  break;
            //  }

            //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
            byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)

            try {
                //cartaAEnviar = mensagem.getBytes(); //converte a mensagem String para array de bytes
                cartaAEnviar = (json.toString()).getBytes(); //converte a mensagem String para array de bytes
                //cartaAEnviar = "HELLO, WORLD!".getBytes(); //teste para enviar arquivo que nao é um JSON

                String a = json.toString();
                System.out.println("__________\n" + a);
                // System.out.println("\n______\nLATITUDE LIXEIRA: "+ toJson().getDouble("latitude") + "\n_____");

                InetAddress ip = InetAddress.getByName(""); //atribuindo o ip

                //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
                DatagramPacket envelopeAEnviar
                        = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, 5000);

                cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 

                set_get_Date();
                Thread.sleep(1000);

            } catch (IOException | JSONException | InterruptedException ex) {
                Logger.getLogger(Lixeira.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void confirmadoServer() throws JSONException {

    }

    /*
    Criar aquirvo JSON
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("type", 3); //por enquanto para identificar que é uma lixeira- nº3
        json.put("capacidade_atual", 0);
        json.put("bloqueio", false); //lixeira está bloqueada
        json.put("connected", false);
    }

    private void adicionarLixo(double value) throws JSONException {
        if (value <= (json.getDouble("capacidade_max") - json.getDouble("capacidade_atual"))) {
            json.put("capacidade_atual", (json.getDouble("capacidade_atual") + value));
        } else {
            System.err.println("NAO É POSSÍVEL ADICIONAR ESSA QUANTIDADE DE LIXO NA LIXEIRA!");
        }
    }

    private void bloquearOnOff() throws JSONException {
        if (json.getBoolean("bloqueio")) {
            json.put("bloqueio", false);
        } else {
            json.put("bloqueio", true);
        }
    }

    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("capacidade_max", inicializacao.getCapacidadeMaxima());
        json.put("latitude", inicializacao.getLatitude());
        json.put("longitude", inicializacao.getLongitude());
        json.put("address", InetAddress.getByName(""));
        json.put("port", cliente.getLocalPort());

        System.out.println("DADOS LIXEIRO: " + json.toString());//afim de verificar se está tudo ok
    }

    public static void startupThreads() {
        Thread send = new Lixeira();
        send.start();

        receiveThread();
    }

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

                            json.put("connected", true);
                            System.out.println("SERVIDOR OK");
                            System.out.println(json.toString());
                        } else if (obj.getString("msg").equals("POS")) {
                            System.out.println("JÁ EXISTE UMA LIXEIRA NESTA POSIÇÃO, FAVOR ALTERE A POSICAO!");
                            inicializacao.setChargePosition("EXISTE LIXEIRA NESSA POSIÇÃO!");
                            inicializacao.setVisible(true);

                            //aguardar o usuario inserir os dados necessarios da lixeira para continuar
                            while (inicializacao.isVisible()) {
                                System.out.print("");//garatindo o funcionamento do while.
                            }

                            inserirdados();
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

            mainview.setDados(json.getDouble("capacidade_max"),
                    json.getDouble("capacidade_atual"), json.getBoolean("bloqueio"),
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"));
        }

    }

    public static void main(String args[]) throws JSONException, UnknownHostException {
        try {
            cliente = new DatagramSocket();

            createJson();

            /*Altera o design da janela para o flatlaf dark */
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Falha ao iniciar o LaF");
            }

            //JFrame para inserir os dados da lixeira
            inicializacao = new OneViewLixeira();
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
                    json.getInt("latitude"), json.getInt("longitude"), json.getBoolean("connected"));

            mainview.setVisible(true);
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
