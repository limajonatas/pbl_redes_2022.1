
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.*;

public class Nuvem extends Thread {

    private static ArrayList clientArray;
    private static ArrayList listJson;
    private static final int porta_servidor = 5000;
    private static DatagramSocket servidorUDP;
    private static JSONObject json;
    private static boolean there_is_caminhao;

    private static class NuvemSend extends Thread {

        public NuvemSend() {
        }

        @Override
        public void run() {
            while (true) {
                if (!clientArray.isEmpty()) {
                    Scanner teclado = new Scanner(System.in);
                    System.out.println("DIGITE O CODIGO");
                    String cod = teclado.nextLine();
                    char c1 = cod.charAt(0);
                    char b1 = 'b';
                    char b2 = 'B';

                    //codigo de bloqueio
                    if ((Character.compare(c1, b1) | Character.compare(c1, b2)) == 0) {

                    }
                }
            }
        }
    }

    public void Nuvem() throws JSONException {
        clientArray = new ArrayList();
    }

    @Override
    public void run() {

        System.out.println("Aguardando cliente...");
        while (true) {
            try {
                ///////////RECEBER MENSAGEM DO CLIENTE E IMPRIMIR NA TELA
                byte[] cartaAReceber = new byte[1024];
                DatagramPacket envelopeAReceber
                        = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                System.out.println("OK MENSAGEM A RECEBER...");
                servidorUDP.receive(envelopeAReceber);//recebe o envelope do cliente
                InetAddress ipCliente = envelopeAReceber.getAddress();
                String str = ipCliente.getHostAddress();
                String textoRecebido = new String(envelopeAReceber.getData());//converte os dados para string
                System.out.println("DE: " + str + " -> " + textoRecebido);

                byte[] msgAEnviar = new byte[1024];

                try {
                    JSONObject obj = new JSONObject(textoRecebido);

                    switch (obj.getInt("type")) {
                        case 1:
                            System.out.println("____\nÉ O ADM");
                            break;
                        case 2:
                            System.out.println("_________\nÉ O CAMINHAO");
                            if (there_is_caminhao) {//se há um caminhao conectado
                                //chamar um método para retornar um mensagem de erro para o caminhao
                                enviar_msg_caminhaoExistente(msgAEnviar, obj, ipCliente, envelopeAReceber);
                            } else {
                                connectionCaminhao(obj, ipCliente, envelopeAReceber);///criar a thread que se comunica com o caminhao
                            }
                            break;
                        case 3:
                            System.out.println("________\nÉ A LIXEIRA");
                            connectionLixeira(msgAEnviar, obj, ipCliente, envelopeAReceber);
                            break;

                        default:
                            System.err.println("host desconhecido!");
                    }

                } catch (JSONException ex) {
                    System.err.println("O ARQUIVO RECEBIDO NÃO É JSON");
                    String a = "O ARQUIVO RECEBIDO NÃO É JSON";
                    msgAEnviar = a.getBytes();
                    DatagramPacket envelopeAEnviar
                            = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                                    ipCliente, envelopeAReceber.getPort());
                    try {
                        servidorUDP.send(envelopeAEnviar);
                    } catch (IOException ex1) {
                        System.err.println("ERRO AO ENVIAR MENSAGEM");
                    }
                }

            } catch (IOException ex) {
                System.err.println("ERRO AO CRIAR SERVIDOR");
            }
        }
    }

    public static void enviar_msg_caminhaoExistente(byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws JSONException, IOException {
        
        System.out.println("ENVIEI PARA O CAMINHAO");
        obj = new JSONObject();
        obj.put("msg", "EXIST");
        msgAEnviar = obj.toString().getBytes();

        DatagramPacket envelopeAEnviar
                = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                        ipCliente, envelopeAReceber.getPort());
        
        servidorUDP.send(envelopeAEnviar);
    }

    public static void connectionCaminhao(JSONObject obj, InetAddress ipCaminhao, DatagramPacket envelopeAReceber) {
        Thread c = new Thread() {
            @Override
            public void run() {
                while (true) {

                }
            }
        };
        c.start();

    }

    public static void connectionLixeira(byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws IOException, JSONException {

        boolean exist = false;
        int index = 0;
        //ADICIONAR O JSON DESTA LIXEIRA
        for (int i = 0; i < listJson.size(); i++) {
            //comparando as latitudes das lixeiras
            if (obj.getInt("latitude") == ((JSONObject) listJson.get(i)).getInt("latitude")
                    && obj.getInt("longitude") == ((JSONObject) listJson.get(i)).getInt("longitude")) {
                exist = true;
                index = i;
                System.out.println("ESSE CARA EXISTE" + exist);
                break;
            }
        }

        String a = "Success | MSG RECEIVE: " + obj.toString();
        String str = "OK";

        json.put("msg", "OK");

        //se existir, atualiza
        if (exist && (obj.getInt("port") == ((JSONObject) listJson.get(index)).getInt("port"))) {
            listJson.remove(index);
            listJson.add(index, obj);
        } else if (!exist) {//se nao existir a lixeira na lista, adiciona ela
            listJson.add(obj);
        } else {
            System.err.println("ERROR: JA EXISTE UMA LIXEIRA NESSA POSIÇÃO, ALTERE A LATITUDE E LONGITUDE!");
            a = "ERROR: JA EXISTE UMA LIXEIRA NESSA POSIÇÃO, ALTERE A LATITUDE E LONGITUDE!";
            str = "POS";
            json.remove("msg");
            json.put("msg", "POS");
        }

        System.out.println("___________\nJSON-> " + obj.toString());
        //System.out.println("___________\nCAPACIDADE DA LIXEIRA" + str+ " -->" + obj.getDouble("capacidade_max") + "\n______");

        //msgAEnviar = str.getBytes();
        msgAEnviar = json.toString().getBytes();

        DatagramPacket envelopeAEnviar
                = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                        ipCliente, envelopeAReceber.getPort());

        //ArrayList list = new ArrayList();
        //list.add(ipCliente);
        //list.add(envelopeAReceber.getPort());
        //ADICIONA O ENDEREÇO E PORTA DO CLIENTE
        /* if (!clientArray.contains(list)) {
            clientArray.add(list);
        }*/
        //EXIBE OS CLIENTES QUE QUE ESTÃO NA LISTA
        for (int j = 0; j < listJson.size(); j++) {
            int i = j;
            JSONObject obb = (JSONObject) listJson.get(j);
            System.out.println("CLIENT " + (++i) + " " + obb.toString());
        }

        servidorUDP.send(envelopeAEnviar);
    }

    public static void main(String[] args) {
        there_is_caminhao = true;
        json = new JSONObject();
        try {
            servidorUDP = new DatagramSocket(porta_servidor);
            System.out.println("Servidor em execução na porta " + porta_servidor);
        } catch (SocketException ex) {
            System.err.println("NÃO FOI POSSÍVEL INICIALIZAR O SERVIDOR!");
        }
        clientArray = new ArrayList();
        listJson = new ArrayList();
        Nuvem receive = new Nuvem();
        receive.start();

        //NuvemSend send = new NuvemSend();
        //send.start();
    }
}
