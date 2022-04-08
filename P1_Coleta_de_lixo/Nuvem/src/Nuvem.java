
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

public class Nuvem extends Thread {

    private static ArrayList clientArray;
    private static ArrayList listJson;
    private static final int porta_servidor = 5000;
    private static final int porta_clientes = 5001;
    private static final int porta_adm = 5002;
    private static final int porta_caminhao = 5003;
    private static DatagramSocket servidorUDP;
    private static DatagramSocket servidor_thread_clientes;
    private static DatagramSocket servidor_thread_adm;
    private static DatagramSocket servidor_thread_caminhao;
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
                                /* if (obj.getBoolean("requisitar")) { //o caminhao está requisitando...
                                    connectionCaminhao(obj, ipCliente, envelopeAReceber);
                                }*/
                                connectionCaminhao(obj, ipCliente, envelopeAReceber);
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

    public static void connectionCaminhao(JSONObject objReceive, InetAddress ipCaminhao,
            DatagramPacket envelopeAReceber) throws JSONException {

        byte[] msgAEnviar = new byte[1024];
        JSONObject objSend = new JSONObject();

        while (true) {
            if (listJson.isEmpty()) {//não há lixeira
                System.out.println("ENVIEI PARA O CAMINHAO");

                try {
                    objSend.put("msg", "NULL");
                } catch (JSONException ex) {
                    Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                }
                msgAEnviar = objSend.toString().getBytes();

                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                try {
                    servidorUDP.send(envelopeAEnviar);
                } catch (IOException ex) {
                    Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (objReceive.getBoolean("restart")) {//se o caminhao reiniciou o processo.
                    alterarStatusLixeiras(); //altera os status "collected" de todas as lixeiras para false         
                }
                

                objSend.put("msg", "PROX"); //envio de uma próxima lixeira

                //se fez uma coleta anterior - aqui atualiza os dados da lixeira coletada
                if (objReceive.getString("msg").equals("COLLECTED")) {

                    //alterar dado de lixeira coletada
                    alteraDadosLixeira(objReceive);

                }

                //se todas lixeiras ja foram coletada, envia uma mensagem para o caminhao, para reiniciar o processo de coleta
                if (todasLixeirasColetadas()) {
                    objSend.put("msg", "ALLCOLLECTED");
                    msgAEnviar = objSend.toString().getBytes();

                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                } else {
                    //primeira coleta - somente envia para o caminhao a proxima lixeira
                    ordenarLixeiras();//da mais "cheia" à menos "cheia"
                    for (int count = 0; count < listJson.size(); count++) {
                        if (!((JSONObject) listJson.get(count)).getBoolean("collected")) {//evitar que seja a lixeira anterior
                            objSend.put("latitude_lixeira", (((JSONObject) listJson.get(count)).getInt("latitude")));
                            objSend.put("longitude_lixeira", (((JSONObject) listJson.get(count)).getInt("longitude")));
                            objSend.put("capacidade_lixeira", (((JSONObject) listJson.get(count)).getInt("capacida_atual")));
                            break;
                        }
                    }
                    msgAEnviar = objSend.toString().getBytes();

                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                    //ordena lista primeiro
                    //verificar se todas foram coletadas.
                    //enviar a proxima lixeira.
                    //receber a informacao de que essa lixeira foi coletada
                }

            }

            there_is_caminhao = true;

            /*
            //ESPERAR UMA SOLICITACAO DO CAMINHAO DE LIXO.
            byte[] cartaAReceber = new byte[1024];
            envelopeAReceber = new DatagramPacket(cartaAReceber, cartaAReceber.length);

            System.out.println("AGUARDANDO CONFIRMAÇÃO DO SERVIDOR...");
            try {
                servidorUDP.receive(envelopeAReceber); //recebe o envelope do Servidor
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
            //converte os dados do envelope para string
            String mensagemRecebida = new String(envelopeAReceber.getData());

            if (!mensagemRecebida.equals("true")) {
                System.out.println("MENSAGEM INVALIDA!");
            }
             */
        }

    }

    public static void connectionLixeira(byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws IOException, JSONException {

        if (listJson.size() <= 3) {
            boolean exist = false;
            int index = 0;
            //ADICIONAR O JSON DESTA LIXEIRA
            for (int i = 0; i < listJson.size(); i++) {
                //comparando as latitudes das lixeiras
                if (obj.getInt("latitude") == ((JSONObject) listJson.get(i)).getInt("latitude")
                        && obj.getInt("longitude") == ((JSONObject) listJson.get(i)).getInt("longitude")) {
                    exist = true;
                    index = i;
                    System.out.println("ESSA LIXEIRA EXISTE" + exist);
                    break;
                }
            }

            json.put("msg", "OK");

            //se existir, atualiza
            if (exist && (obj.getInt("port") == ((JSONObject) listJson.get(index)).getInt("port"))) {
                listJson.remove(index);
                listJson.add(index, obj);
            } else if (!exist) {//se nao existir a lixeira na lista, adiciona ela
                listJson.add(obj);
            } else {
                System.err.println("ERROR: JA EXISTE UMA LIXEIRA NESSA POSIÇÃO, ALTERE A LATITUDE E LONGITUDE!");
                json.remove("msg");
                json.put("msg", "POS");
            }

            System.out.println("___________\nJSON-> " + obj.toString());
            //System.out.println("___________\nCAPACIDADE DA LIXEIRA" + str+ " -->" + obj.getDouble("capacidade_max") + "\n______");

        } else {
            json.put("msg", "FULL"); //´nao pode cadastrar mais lixeiras
        }
        //msgAEnviar = str.getBytes();
        msgAEnviar = json.toString().getBytes();

        DatagramPacket envelopeAEnviar
                = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                        ipCliente, envelopeAReceber.getPort());

        //EXIBE OS CLIENTES QUE QUE ESTÃO NA LISTA
        for (int j = 0; j < listJson.size(); j++) {
            int i = j;
            JSONObject obb = (JSONObject) listJson.get(j);
            System.out.println("CLIENT " + (++i) + " " + obb.toString());
        }

        servidorUDP.send(envelopeAEnviar);
    }

    public static void ordenarLixeiras() throws JSONException {
        Iterator i = listJson.iterator();
        ArrayList array = new ArrayList();

        while (i.hasNext()) {
            JSONObject obj = (JSONObject) i.next();

            //maior que 90%
            if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.9)) {
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.8)) {//>80%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.7)) {//>70%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.6)) {//>60%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.5)) {//>50%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.35)) {//>35%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.20)) {//>20%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") > (obj.getDouble("capacidade_atual") * 0.05)) {//>5%
                array.add(obj);
            } else if (obj.getDouble("capacidade_atual") < (obj.getDouble("capacidade_atual") * 0.05)) {//<5%
                array.add(obj);
            }
        }
        listJson.clear();
        for (Object array1 : array) {
            listJson.add(array.indexOf(i));
        }
    }

    public static int buscarLixeira(int latitude, int longitude) throws JSONException {
        Iterator i = listJson.iterator();
        JSONObject obj;
        int pos = -1;
        while (i.hasNext()) {
            pos++;
            obj = (JSONObject) i.next();
            if (obj.getInt("latitude") == latitude && obj.getInt("longitude") == longitude) {
                return pos;
            }
        }
        return pos;
    }

    public static void alteraDadosLixeira(JSONObject objReceive) throws JSONException {
        int pos = buscarLixeira(objReceive.getInt("latitude"), objReceive.getInt("longitude"));
        if (pos != -1) {
            JSONObject obj = (JSONObject) listJson.get(pos);
            obj.put("capacidade_atual", 0);
            obj.put("collected", true);
            listJson.remove(pos);
            listJson.add(obj);

        }
    }

    public static boolean todasLixeirasColetadas() {
        return true;
    }

    public static void alterarStatusLixeiras() {
    }

    public static void main(String[] args) {
        there_is_caminhao = false;
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
