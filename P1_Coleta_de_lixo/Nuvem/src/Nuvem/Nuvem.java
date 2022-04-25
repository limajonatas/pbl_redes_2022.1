package Nuvem;

import Lixeira_date.Lixeira_Date;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

public class Nuvem extends Thread {

    private static ArrayList<Lixeira_Date> lista_lixeiras;
    private static final int porta_servidor = 5000;
    private static final int porta_lixeiras = 5001;
    private static final int porta_adm = 5002;
    private static final int porta_caminhao = 5003;
    private static DatagramSocket servidorUDP;
    private static DatagramSocket servidor_thread_lixeiras;
    private static DatagramSocket servidor_thread_adm;
    private static DatagramSocket servidor_thread_caminhao;
    private static boolean there_is_caminhao;
    private static JSONObject proxima_lixeira;
    private static byte[] msgAReceber;
    private static byte[] msgAEnviar;

    /**
     * THREAD PRINCIPAL - SERVIDOR PRINCIPAL PARA RECEBER TODOS OS CLIENTES
     */
    @Override
    public void run() {
        while (true) {
            try {
                msgAReceber = new byte[2048];
                DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                System.out.println(">>>>[MAIN_SERVER]: OK MENSAGEM A RECEBER...");
                servidorUDP.receive(envelopeAReceber);//recebe o envelope do cliente
                InetAddress ipCliente = envelopeAReceber.getAddress();
                String textoRecebido = new String(envelopeAReceber.getData());//converte os dados para string
                System.out.println("DE: " + ipCliente.getHostAddress() + " -> " + textoRecebido);

                try {
                    JSONObject obj = new JSONObject(textoRecebido);

                    switch (obj.getInt("tipo")) {
                        case 1:
                            enviar_porta_cliente(false, porta_adm, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            System.out.println(">>>>[MAIN_SERVER]: É UM ADM");
                            break;
                        case 2:
                            System.out.println(">>>>[MAIN_SERVER]: É UM CAMINHAO");
                            if (there_is_caminhao) {//se há um caminhao conectado
                                //chamar um método para retornar um mensagem de erro para o caminhao
                                enviar_msg_caminhaoExistente(msgAEnviar, obj, ipCliente, envelopeAReceber);
                            } else {
                                enviar_porta_cliente(false, porta_caminhao, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            }
                            break;
                        case 3:
                            System.out.println(">>>>[MAIN_SERVER]: É UMA LIXEIRA");
                            if (lista_lixeiras.size() > 2) {//se ja houver 3 lixeiras cadastradas
                                JSONObject objSend = new JSONObject();
                                objSend.put("msg", "FULL");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCliente, envelopeAReceber.getPort());
                                servidorUDP.send(envelopeAEnviar);
                            } else {
                                enviar_porta_cliente(true, porta_lixeiras, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            }
                            break;

                        default:
                            System.err.println("host desconhecido!");
                    }

                    Thread.sleep(1000);

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
                } catch (InterruptedException ex) {
                    Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                System.err.println("ERRO AO CRIAR SERVIDOR");
            }

        }
    }

    /**
     * ENVIA MENSAGEM PARA O CAMINHAO QUE JA EXISTE UM CAMINHAO CADASTRADO.
     *
     * @param msgAEnviar mensagem a ser enviada
     * @param obj objeto a ser inserido à mensagem
     * @param ipCliente ip do cliente
     * @param envelopeAReceber a ser colocado no pacote
     * @throws JSONException erro ao inserir/obter arquivo de um json
     * @throws IOException
     */
    public static void enviar_msg_caminhaoExistente(byte[] msgAEnviar, JSONObject obj, InetAddress ipCliente, DatagramPacket envelopeAReceber) throws JSONException, IOException {
        msgAEnviar = new byte[2048];
        System.out.println("MENSAGEM PARA ALGUM CAMINHAO: \"JÁ EXISTE UM CAMINHAO CADASTRADO!\"");
        obj = new JSONObject();
        obj.put("msg", "EXIST");
        msgAEnviar = obj.toString().getBytes();

        DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                ipCliente, envelopeAReceber.getPort());

        servidorUDP.send(envelopeAEnviar);
    }

    /**
     * THREAD PARA RECEBER DADOS DE UM CAMINHAO E PARA ENVIAR PARA ELE.
     *
     */
    public static void thread_caminhao() {

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    msgAReceber = new byte[2048];
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println(">>[SERVER_CAMINHAO]: MENSAGEM A RECEBER...");
                    try {
                        servidor_thread_caminhao.receive(envelopeAReceber);//recebe o envelope do cliente
                    } catch (IOException ex) {
                        System.err.println("OCORREU O ERRO NO RECEBIMENTO DE MENSAGEM DO CAMINHAO - SERVER IN PORT 5002");
                    }

                    try {
                        InetAddress ipCaminhao = envelopeAReceber.getAddress();
                        String str = new String(envelopeAReceber.getData());
                        JSONObject objReceive = new JSONObject(str);

                        System.out.println(">>[SERVER_CAMINHAO] DE: " + ipCaminhao.getHostAddress() + " MSG: " + objReceive.toString());

                        JSONObject objSend;
                        msgAEnviar = new byte[2048];
                        if (objReceive.getString("msg").equals("REQUIRE")) {//o caminhao faz uma requisição
                            if (lista_lixeiras.isEmpty()) {//não há lixeira
                                objSend = new JSONObject();
                                objSend.put("msg", "NULL");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                try {
                                    servidor_thread_caminhao.send(envelopeAEnviar);
                                    System.out.println(">>[SERVER_CAMINHAO] ENVIADO PARA CAMINHÃO \"NÃO HÁ LIXEIRAS\"");
                                } catch (IOException ex) {
                                    System.out.println(">>[SERVER-CAMINHÃO]: ERRO AO ENVIAR RESPOSTA AO CAMINHÃO [THERE AREN'T BIN]");
                                }

                            } else {

                                objSend = new JSONObject();
                                objSend.put("msg", "PROX"); //envio de uma próxima lixeira

                                //se todas lixeiras ja foram coletadas, envia uma mensagem para o caminhao, para reiniciar o processo de coleta
                                if (todasLixeirasColetadas()) {
                                    proxima_lixeira = null;
                                    objSend = new JSONObject();
                                    objSend.put("msg", "ALLCOLLECTED");
                                    msgAEnviar = objSend.toString().getBytes();

                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                                    try {
                                        servidor_thread_caminhao.send(envelopeAEnviar);
                                        System.out.println(">>[SERVER_CAMIHAO] ENVIADO PARA CAMINHÃO \"TODAS LIXEIRAS FORAM COLETADAS\"");
                                    } catch (IOException ex) {
                                        System.out.println(">>[SERVER-CAMINHÃO]: ERRO AO ENVIAR RESPOSTA AO CAMINHÃO [ALL BIN COLLECTED]");
                                    }

                                } else {
                                    for (int count = 0; count < lista_lixeiras.size(); count++) {

                                        if (!lista_lixeiras.get(count).isColetada()) {//evitar que seja a lixeira anterior
                                            objSend.put("id_lixeira", lista_lixeiras.get(count).getId());
                                            objSend.put("latitude_lixeira", lista_lixeiras.get(count).getLatitude());
                                            objSend.put("longitude_lixeira", lista_lixeiras.get(count).getLongitude());
                                            objSend.put("capacidade_lixeira", lista_lixeiras.get(count).getCapacidade_atual());
                                            break;
                                        }
                                    }

                                    proxima_lixeira = new JSONObject(objSend.toString());
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                    try {
                                        servidor_thread_caminhao.send(envelopeAEnviar);
                                        System.out.println(">>[SERVER-CAMINHAO]: LOCALIZAÇÃO DA PROXIMA LIXEIRA ENVIADA PARA O CAMINHÃO");
                                    } catch (IOException ex) {
                                        System.out.println(">>[SERVER-CAMINHÃO]: ERRO AO ENVIAR RESPOSTA AO CAMINHÃO");
                                    }
                                }
                            }
                        } else if (objReceive.getString("msg").equals("BIN_COLLECTED")) { // O CAMINHAO FEZ UMA COLETA
                            objSend = new JSONObject();
                            atualiza_dados_lixeira_coletada(objReceive); //alterar dado de lixeira coletada
                            double capacidade_maxima_caminhao = objReceive.getDouble("capacidade_maxima_caminhao");
                            double capacidade_atual_caminhao = objReceive.getDouble("capacidade_atual_caminhao") + objReceive.getDouble("quantidade_lixo_coletado");
                            double capacidade_disponivel_caminhao = capacidade_maxima_caminhao - capacidade_atual_caminhao;

                            objSend.put("msg", "COLETA_OK");
                            objSend.put("capacidade_atual_caminhao", capacidade_atual_caminhao);
                            objSend.put("capacidade_disponivel_caminhao", capacidade_disponivel_caminhao);

                            msgAEnviar = objSend.toString().getBytes();
                            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                            try {
                                servidor_thread_caminhao.send(envelopeAEnviar);
                                System.out.println(">>[SERVER_CAMIHAO] ENVIADO PARA CAMINHÃO \"COLETA BEM SUCEDIDA\"");
                            } catch (IOException ex) {
                                System.out.println(">>[SERVER-CAMINHÃO]: ERRO AO ENVIAR RESPOSTA AO CAMINHÃO [THERE AREN'T BIN]");
                            }
                        } else if (objReceive.getString("msg").equals("RESTART")) {
                            alterarStatusLixeiras(); //altera os status "collected" de todas as lixeiras para false         
                            System.out.println(">>[SERVER_CAMINHAO]: RESTART");
                            objSend = new JSONObject();
                            objSend.put("msg", "REINICIADO");
                            msgAEnviar = objSend.toString().getBytes();

                            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                            try {
                                servidor_thread_caminhao.send(envelopeAEnviar);
                                System.out.println(">>[SERVER_CAMIHAO] ENVIADO PARA CAMINHÃO \"PROCESSO DE COLETA REINICIADO\"");
                            } catch (IOException ex) {
                                System.out.println(">>[SERVER-CAMINHÃO]: ERRO AO ENVIAR RESPOSTA AO CAMINHÃO [RESTART PROCESS]");
                            }
                        }

                        there_is_caminhao = true;

                    } catch (JSONException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        t.start();
    }

    /**
     * THREAD QUE RECEBE OS DADOS DAS LIXEIRAS
     *
     * @throws IOException
     * @throws JSONException
     */
    public static void thread_lixeira() throws IOException, JSONException {

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    msgAReceber = new byte[2048];
                    msgAEnviar = new byte[2048];
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println(">[SERVER_LIXEIRA]: OK, MENSAGEM A RECEBER...");
                    try {
                        servidor_thread_lixeiras.receive(envelopeAReceber);//recebe o envelope do cliente
                    } catch (IOException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    InetAddress ipCliente = envelopeAReceber.getAddress();
                    JSONObject objReceive = null;
                    try {
                        objReceive = new JSONObject(new String(envelopeAReceber.getData()));
                    } catch (JSONException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.print(">[SERVIDOR_LIXEIRA]: DE: " + ipCliente.getHostAddress() + " -> " + objReceive.toString());

                    try {
                        objReceive.put("address", ipCliente);

                        ///////////////////criar instancia lixeira_date///////////////////////
                        Lixeira_Date lixeira = new Lixeira_Date(objReceive.getBoolean("connected"),
                                ((InetAddress) objReceive.get("address")), objReceive.getInt("porta"),
                                objReceive.getBoolean("coletada"), objReceive.getInt("latitude"), objReceive.getInt("id"),
                                objReceive.getInt("tipo"), objReceive.getDouble("capacidade_disponivel"),
                                objReceive.getDouble("capacidade_atual"), objReceive.getDouble("capacidade_max"),
                                objReceive.getBoolean("bloqueio"), objReceive.getInt("longitude")
                        );

                        JSONObject objSend = new JSONObject();
                        objSend.put("msg", "OK");

                        if (lista_lixeiras.isEmpty()) {
                            lista_lixeiras.add(lixeira);
                        } else {

                            boolean exist = false;

                            ////////////////////////////////////
                            ArrayList<Lixeira_Date> list_aux2 = new ArrayList<>();
                            for (int c = 0; c < lista_lixeiras.size(); c++) {
                                list_aux2.add(lista_lixeiras.get(c));
                            }

                            for (int count = 0; count < lista_lixeiras.size(); count++) {

                                if (lixeira.getId() == lista_lixeiras.get(count).getId()) {
                                    lista_lixeiras.remove(count);
                                    exist = true;
                                    lista_lixeiras.add(count, lixeira);
                                }
                            }

                            if (!exist) {
                                lista_lixeiras.add(lixeira);
                            }
                        }

                        Collections.sort(lista_lixeiras); //ORDENA A LISTA
                        for (int i = 0; i < lista_lixeiras.size(); i++) {
                            System.out.println(">[SERVER-LIXEIRA]: Lixeira" + (i + 1) + " " + lista_lixeiras.get(i).toString());
                        }

                        objSend.put("address", ipCliente);

                        msgAEnviar = objSend.toString().getBytes();

                        DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCliente, envelopeAReceber.getPort());

                        servidor_thread_lixeiras.send(envelopeAEnviar);

                    } catch (JSONException | IOException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        };
        t.start();

    }

    /**
     * THREAD RESPOSÁVEL PELO RECEBIMENTO E ENVIO DE MENSAGENS DO ADMINISTRADOR.
     */
    public static void thread_Adm() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    msgAEnviar = new byte[2048];
                    msgAReceber = new byte[2048];
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println(">>>[SERVER_ADMINISTRADOR]: OK, MENSAGEM A RECEBER...");
                    try {
                        servidor_thread_adm.receive(envelopeAReceber);//recebe o envelope do cliente
                    } catch (IOException ex) {
                        System.err.println(">>>[SERVER_ADMINISTRADOR]: ERRO A RECEBER MENSAGEM DO ADMINISTRADOR!");
                    }

                    InetAddress ipAdm = envelopeAReceber.getAddress();
                    String str = new String(envelopeAReceber.getData());
                    JSONObject objReceive = null;
                    try {
                        objReceive = new JSONObject(str);

                        System.out.println(">>>[SERVIDOR_ADMINISTADOR]: DE: " + ipAdm.getHostAddress() + " -> " + objReceive.toString());

                        JSONObject objSend;

                        if (objReceive.getString("msg").equals("REQUEST")) {
                            if (lista_lixeiras.isEmpty()) {
                                objSend = new JSONObject();
                                objSend.put("msg", "EMPTY");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipAdm, envelopeAReceber.getPort());
                                servidor_thread_adm.send(envelopeAEnviar);
                            } else {
                                String quantidade = "quantidade:" + lista_lixeiras.size() + ",";
                                String arraylixeiras = "lixeiras:[{";
                                String array_prox_lixeira = ", proxima_lixeira:{";

                                String strlixeira = "lixeira";
                                String strStr = "";
                                for (int i = 0; i < lista_lixeiras.size(); i++) {
                                    int num = i + 1;
                                    strStr += strlixeira + num + ":{id:" + lista_lixeiras.get(i).getId()
                                            + ",capacidade_atual:" + lista_lixeiras.get(i).getCapacidade_atual()
                                            + ",bloqueio:" + lista_lixeiras.get(i).isBloqueio()
                                            + ",capacidade_max:" + lista_lixeiras.get(i).getCapacidade_max()
                                            + ",capacidade_disponivel:" + lista_lixeiras.get(i).getCapacidade_disponivel()
                                            + ",latitude:" + lista_lixeiras.get(i).getLatitude()
                                            + ",longitude:" + lista_lixeiras.get(i).getLongitude() + "}";
                                    System.out.println(strStr);

                                    if (num < lista_lixeiras.size()) {
                                        strStr += ",";
                                    }
                                }

                                arraylixeiras += (strStr + "}]");

                                String strJson = "";
                                if (proxima_lixeira != null) {
                                    array_prox_lixeira += "id:" + proxima_lixeira.getInt("id_lixeira")
                                            + ", latitude:" + proxima_lixeira.getInt("latitude_lixeira")
                                            + ", longitude:" + proxima_lixeira.getInt("longitude_lixeira") + "}";
                                    strJson = "{" + quantidade + arraylixeiras + array_prox_lixeira + "}";
                                } else {
                                    strJson = "{" + quantidade + arraylixeiras + "}";
                                }

                                objSend = new JSONObject(strJson);
                                objSend.put("msg", "OK");

                                System.out.println(objSend.toString());
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipAdm, envelopeAReceber.getPort());
                                servidor_thread_adm.send(envelopeAEnviar);
                            }

                        } else if (objReceive.getString("msg").equals("CHANGE")) {
                            altera_bloqueio_lixeira(objReceive);
                            System.out.println(">>>[SERVIDOR_ADMINISTRADOR]: LIXEIRA ALTERADA");
                        } else {
                            objSend = new JSONObject();
                            objSend.put("msg", "INVALIDO");
                            msgAEnviar = objSend.toString().getBytes();
                            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipAdm, envelopeAReceber.getPort());
                            servidor_thread_adm.send(envelopeAEnviar);
                        }
                    } catch (JSONException | IOException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        t.start();
    }

    /**
     * BUSCA POR UMA LIXEIRA ATRAVES DO SEU ID.
     *
     * @param id da lixeira
     * @return retorna a posição da lixeira.
     * @throws JSONException erro ao inserir/obter dados de um arquivo json.
     */
    public static int buscarLixeira(int id) throws JSONException {
        Iterator i = lista_lixeiras.iterator();
        Lixeira_Date lix;
        int pos = -1;
        while (i.hasNext()) {
            pos++;
            if (((Lixeira_Date) i.next()).getId() == id) {
                return pos;
            }
        }
        return pos;
    }

    /**
     * ALTERA O BLOQUEIO DA LIXEIRA.
     *
     * @param objReceive objeto recebido do adm
     * @throws JSONException
     */
    public static void altera_bloqueio_lixeira(JSONObject objReceive) throws JSONException {
        msgAEnviar = new byte[2048];
        int pos = buscarLixeira(objReceive.getInt("id_lixeira"));

        if (pos != -1) {
            Lixeira_Date lixeira = lista_lixeiras.get(pos);
            //lixeira.setCapacidade_atual(0.0);
            //lixeira.setColetada(true);
            lista_lixeiras.remove(pos);
            //lista_2.add(lixeira);
            //agora envia os dados para a lixeira

            msgAEnviar = new byte[1024];
            JSONObject objSend = new JSONObject();
            objSend.put("msg", "CHARGE_BLOCK");
            msgAEnviar = objSend.toString().getBytes();
            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, lixeira.getAddress(), lixeira.getPorta());

            try {
                servidor_thread_caminhao.send(envelopeAEnviar);
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * ATUALIZA DADOS DA LIXEIRA QUE FOI COLETADA. Altera o status da lixeira de
     * não coletada para coletada, além de alterar a capacidade atual e
     * disponível dela.
     *
     * @param objReceive objeto que foi recebido pelo caminhão após a coleta da
     * lixeira.
     * @throws JSONException erro ao inserir/obter dados de um arquivo json.
     */
    public static void atualiza_dados_lixeira_coletada(JSONObject objReceive) throws JSONException {
        msgAEnviar = new byte[2048];
        int pos = buscarLixeira(objReceive.getInt("id_lixeira_coletada"));

        if (pos != -1) {
            Lixeira_Date obj = lista_lixeiras.get(pos);
            //obj.setCapacidade_atual(obj.getCapacidade_atual() - (objReceive.getDouble("quantidade_lixo_coletado")));
            obj.setColetada(true);
            lista_lixeiras.remove(pos);
            lista_lixeiras.add(obj);
            //agora envia os dados para a lixeira

            JSONObject objSend = new JSONObject();
            objSend.put("msg", "COLLECTED");
            objSend.put("quantidade_lixo_coletado", objReceive.getDouble("quantidade_lixo_coletado"));
            msgAEnviar = objSend.toString().getBytes();
            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, obj.getAddress(), obj.getPorta());

            try {
                servidor_thread_caminhao.send(envelopeAEnviar);
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * VERIFICA SE TODAS LIXEIRAS JÁ FORAM COLETADAS.
     *
     * @return false se encontrar alguma lixeira que ainda nao foi coleta, true
     * se todas foram coletadas.
     * @throws JSONException erro na inserção ou obtenção de dados de um objeto
     * json.
     */
    public static boolean todasLixeirasColetadas() throws JSONException {
        Iterator it = lista_lixeiras.iterator();
        Lixeira_Date lix;

        while (it.hasNext()) {

            lix = (Lixeira_Date) it.next();
            if (!lix.isColetada()) {//encontra alguma lixeira que ainda nao foi coletada
                return false;
            }
        }

        return true;
    }

    /**
     * ALTERA O STATUS DA LIXEIRAS - MUDA DE COLETADA PARA NÃO COLETADA. o
     * método é chamado após o caminhão reiniciar o processo de coleta de lixo.
     */
    public static void alterarStatusLixeiras() {
        msgAEnviar = new byte[2048];

        JSONObject objSend = new JSONObject();

        try {
            objSend.put("msg", "STATUS");
        } catch (JSONException ex) {
            System.err.println("NAO FOI POSSIVEL ENVIAR MENSAGEM PARA A LIXEIRA MUDAR O STATUS DE COLETADA");
        }

        for (int i = 0; i < lista_lixeiras.size(); i++) {
            msgAEnviar = objSend.toString().getBytes();
            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                    lista_lixeiras.get(i).getAddress(), lista_lixeiras.get(i).getPorta());
            try {
                servidor_thread_caminhao.send(envelopeAEnviar);
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * MÉTODO RESPOSÁVEL POR ENVIAR A PORTA DO SERVIDOR RESPOSÁVEL AO CLIENTE.
     * Para as lixeiras, é enviado, além disso, o id.
     *
     * @param is_lixeira se é uma lixeira
     * @param port a porta que será enviada
     * @param msgAEnviar mensagem a ser enviado para o cliente
     * @param obj objeto json a ser inserido no pacote para envio
     * @param ipCliente o ip do cliente.
     * @param envelopeAReceber o envelope será posto no pacote de envio
     * @throws JSONException erro na inserção ou obtenção de dados de um objeto
     * json.
     * @throws IOException
     */
    public static void enviar_porta_cliente(boolean is_lixeira, int port, byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws JSONException, IOException {
        msgAEnviar = new byte[2048];
        obj = new JSONObject();
        obj.put("msg", "PORT");
        Random gerador = new Random();
        if (is_lixeira) {
            obj.put("id", gerador.nextInt(100)); //id para lixeira
        }
        obj.put("porta", port);
        msgAEnviar = obj.toString().getBytes();

        DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCliente, envelopeAReceber.getPort());

        servidorUDP.send(envelopeAEnviar);
    }

    /**
     * MÉTODO MAIN. Resposável por inicializar o servidor - socket UDP, além das
     * threads que vão receber e enviar os dados dos clientes
     *
     * @param args
     * @throws IOException
     * @throws JSONException erro na inserção ou obtenção de dados de um objeto
     * json.
     */
    public static void main(String[] args) throws IOException, JSONException {
        there_is_caminhao = false;

        try {
            servidorUDP = new DatagramSocket(porta_servidor);
            servidor_thread_lixeiras = new DatagramSocket(porta_lixeiras);
            servidor_thread_adm = new DatagramSocket(porta_adm);
            servidor_thread_caminhao = new DatagramSocket(porta_caminhao);
            System.out.println("==================================================================");
            System.out.println("  SERVIDOR PRINCIPAL- IP: " + servidorUDP.getLocalAddress().getHostAddress() + " - PORTA " + servidorUDP.getLocalPort());
            System.out.println("  SERVIDOR PARA O CAMINHAO - IP: " + servidorUDP.getLocalAddress().getHostAddress() + " - PORTA " + servidor_thread_caminhao.getLocalPort());
            System.out.println("  SERVIDOR PARA AS LIXEIRAS - IP: " + servidorUDP.getLocalAddress().getHostAddress() + " - PORTA " + servidor_thread_lixeiras.getLocalPort());
            System.out.println("  SERVIDOR PARA O ADMINISTRADOR - IP: " + servidorUDP.getLocalAddress().getHostAddress() + " - PORTA " + servidor_thread_adm.getLocalPort());
            System.out.println("===================================================================");
        } catch (SocketException ex) {
            System.err.println("NÃO FOI POSSÍVEL INICIALIZAR O SERVIDOR!");
        }

        lista_lixeiras = new ArrayList<>();
        Nuvem receive = new Nuvem();
        receive.start();
        //inicializa threads resposáveis pelo recebimento e envio de mensagens das lixeiras, administrador e do caminhão
        thread_lixeira();
        thread_caminhao();
        thread_Adm();
    }
}
