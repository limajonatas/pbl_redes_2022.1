package Nuvem;

import Lixeiras.Lixeira_Date;
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

    private static ArrayList<JSONObject> lista_lixeiras_json;
    private static ArrayList<Lixeira_Date> lista_2;
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
    private static byte[] msgAReceber = new byte[1024];
    private static byte[] msgAEnviar = new byte[1024];

    /**
     * RECEIVE
     */
    @Override
    public void run() {

        System.out.println("Aguardando cliente...");
        while (true) {
            try {
                DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                System.out.println("[MAIN_SERVER]: OK MENSAGEM A RECEBER...");
                servidorUDP.receive(envelopeAReceber);//recebe o envelope do cliente
                InetAddress ipCliente = envelopeAReceber.getAddress();
                String textoRecebido = new String(envelopeAReceber.getData());//converte os dados para string
                System.out.println("DE: " + ipCliente.getHostAddress() + " -> " + textoRecebido);

                try {
                    JSONObject obj = new JSONObject(textoRecebido);

                    switch (obj.getInt("tipo")) {
                        case 1:
                            enviar_porta_cliente(porta_adm, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            System.out.println("____\n?? O ADM");
                            break;
                        case 2:
                            System.out.println("_________\n?? O CAMINHAO");
                            if (there_is_caminhao) {//se h?? um caminhao conectado
                                //chamar um m??todo para retornar um mensagem de erro para o caminhao
                                enviar_msg_caminhaoExistente(msgAEnviar, obj, ipCliente, envelopeAReceber);
                            } else {
                                enviar_porta_cliente(porta_caminhao, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            }
                            break;
                        case 3:
                            System.out.println("________\n?? UMA A LIXEIRA");
                            if (lista_2.size() > 2) {//se ja houver 3 lixeiras cadastradas
                                JSONObject objSend = new JSONObject();
                                objSend.put("msg", "FULL");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCliente, envelopeAReceber.getPort());
                                servidorUDP.send(envelopeAEnviar);
                            } else {
                                enviar_porta_cliente(porta_lixeiras, msgAEnviar, obj, ipCliente, envelopeAReceber);
                            }
                            break;

                        default:
                            System.err.println("host desconhecido!");
                    }

                    Thread.sleep(1000);

                } catch (JSONException ex) {
                    System.err.println("O ARQUIVO RECEBIDO N??O ?? JSON");
                    String a = "O ARQUIVO RECEBIDO N??O ?? JSON";
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

    public static void enviar_msg_caminhaoExistente(byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws JSONException, IOException {

        System.out.println("ENVIEI PARA O CAMINHAO --> JA EXISTE CAMINHAO");
        obj = new JSONObject();
        obj.put("msg", "EXIST");
        msgAEnviar = obj.toString().getBytes();

        DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                ipCliente, envelopeAReceber.getPort());

        servidorUDP.send(envelopeAEnviar);
    }

    /**
     * THREAD PARA RECEBER DADOS DO CAMINHAO
     *
     * @throws JSONException
     */
    public static void thread_caminhao() throws JSONException {

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println("[SERVER_CAMIHAO]: OK, MENSAGEM A RECEBER...");
                    try {
                        servidor_thread_caminhao.receive(envelopeAReceber);//recebe o envelope do cliente
                    } catch (IOException ex) {
                        Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        InetAddress ipCaminhao = envelopeAReceber.getAddress();
                        String str = new String(envelopeAReceber.getData());
                        JSONObject objReceive = new JSONObject(str);

                        System.out.println("DE: " + ipCaminhao.getHostAddress() + " -> " + objReceive.toString());

                        JSONObject objSend = new JSONObject();
                        /*
                        if (objReceive.getString("msg").equals("requisitar")) {//o caminhao faz uma requisi????o
                            System.out.println("O CAMINHAO REQUISITOU");
                            if (lista_lixeiras_json.isEmpty()) {//n??o h?? lixeira

                                objSend.put("msg", "NULL");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                servidor_thread_caminhao.send(envelopeAEnviar);
                                System.out.println("ENVIEI PARA O CAMINHAO" + envelopeAEnviar.toString());

                            } else {
                                if (objReceive.getBoolean("restart")) {//se o caminhao reiniciou o processo.
                                    alterarStatusLixeiras(); //altera os status "collected" de todas as lixeiras para false         
                                    System.out.println("RESTART ON");
                                }

                                objSend.put("msg", "PROX"); //envio de uma pr??xima lixeira

                                //se fez uma coleta anterior - aqui atualiza os dados da lixeira coletada
                                if (objReceive.getBoolean("lixeira_coletada")) {
                                    System.out.println("LIXEIRA COLETADA");
                                    alteraDadosLixeira(objReceive); //alterar dado de lixeira coletada
                                }

                                //se todas lixeiras ja foram coletadas, envia uma mensagem para o caminhao, para reiniciar o processo de coleta
                                if (todasLixeirasColetadas()) {
                                    objSend.put("msg", "ALLCOLLECTED");
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                                    servidor_thread_caminhao.send(envelopeAEnviar);

                                } else {
                                    System.out.println("=======A ENVIAR LIXEIRA PARA CAMINHAO==========");

                                    //primeira coleta - somente envia para o caminhao a proxima lixeira  
                                    System.out.println("TAMANHO DA LISTA: " + lista_lixeiras_json.size());

                                    System.out.println("TAMANHO DA LISTA: " + lista_lixeiras_json.size());
                                    for (int count = 0; count < lista_lixeiras_json.size(); count++) {
                                        System.out.println("ENTROU NA LISTA");
                                        if (!((JSONObject) lista_lixeiras_json.get(count)).getBoolean("coletada")) {//evitar que seja a lixeira anterior
                                            objSend.put("latitude_lixeira", (((JSONObject) lista_lixeiras_json.get(count)).getInt("latitude")));
                                            objSend.put("longitude_lixeira", (((JSONObject) lista_lixeiras_json.get(count)).getInt("longitude")));
                                            objSend.put("capacidade_lixeira", (((JSONObject) lista_lixeiras_json.get(count)).getInt("capacidade_atual")));
                                            System.out.println("PEGOU A LIXEIRA!");
                                            break;
                                        }
                                    }
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                    servidor_thread_caminhao.send(envelopeAEnviar);
                                }
                            }
                        }*/

                        ////////////////////////////////////////////////
                        if (objReceive.getString("msg").equals("requisitar")) {//o caminhao faz uma requisi????o
                            System.out.println("O CAMINHAO REQUISITOU");
                            if (lista_2.isEmpty()) {//n??o h?? lixeira

                                objSend.put("msg", "NULL");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                servidor_thread_caminhao.send(envelopeAEnviar);
                                System.out.println("ENVIEI PARA O CAMINHAO" + envelopeAEnviar.toString());

                            } else {
                                if (objReceive.getBoolean("restart")) {//se o caminhao reiniciou o processo.
                                    alterarStatusLixeiras(); //altera os status "collected" de todas as lixeiras para false         
                                    System.out.println("RESTART ON");

                                    objSend.put("msg", "REINICIADO");
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                                    servidor_thread_caminhao.send(envelopeAEnviar);
                                    continue;
                                }

                                objSend.put("msg", "PROX"); //envio de uma pr??xima lixeira

                                //se fez uma coleta anterior - aqui atualiza os dados da lixeira coletada
                                if (objReceive.getBoolean("lixeira_coletada")) {
                                    System.out.println("00000000000000000000000000000000\n LIXEIRA COLETADA PELO CAMINHAO");
                                    altera_status_coletada_lixeira(objReceive); //alterar dado de lixeira coletada
                                }

                                //se todas lixeiras ja foram coletadas, envia uma mensagem para o caminhao, para reiniciar o processo de coleta
                                if (todasLixeirasColetadas()) {
                                    objSend.put("msg", "ALLCOLLECTED");
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());
                                    servidor_thread_caminhao.send(envelopeAEnviar);

                                } else {
                                    System.out.println("=======A ENVIAR LIXEIRA PARA CAMINHAO==========");

                                    for (int count = 0; count < lista_2.size(); count++) {
                                        System.out.println("ENTROU NA LISTA");
                                        if (!lista_2.get(count).isColetada()) {//evitar que seja a lixeira anterior
                                            System.out.println("//////// CAPACIDADE MAXIMA" + lista_2.get(count).getCapacidade_atual());
                                            System.out.println("//////// CAPACIDADE ATUAL" + lista_2.get(count).getCapacidade_max());
                                            objSend.put("id_lixeira", lista_2.get(count).getId());
                                            objSend.put("latitude_lixeira", lista_2.get(count).getLatitude());
                                            objSend.put("longitude_lixeira", lista_2.get(count).getLongitude());
                                            objSend.put("capacidade_lixeira", lista_2.get(count).getCapacidade_atual());
                                            System.out.println("PEGOU A LIXEIRA!" + objSend.toString());
                                            break;
                                        }
                                    }

                                    proxima_lixeira = new JSONObject(objSend.toString());
                                    msgAEnviar = objSend.toString().getBytes();
                                    DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCaminhao, envelopeAReceber.getPort());

                                    servidor_thread_caminhao.send(envelopeAEnviar);
                                }
                            }
                        }

                        there_is_caminhao = true;

                    } catch (JSONException | IOException ex) {
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
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println("[SERVER_LIXEIRA]: OK, MENSAGEM A RECEBER...");
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
                    System.out.print(" DE: " + ipCliente.getHostAddress() + " -> " + objReceive.toString());

                    try {

                        if (objReceive.getString("msg").equals("CONFIRMAR")) {
                            JSONObject objSend = new JSONObject();
                            objSend.put("msg", "CONFIRMADO");

                            msgAEnviar = objSend.toString().getBytes();

                            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipCliente, envelopeAReceber.getPort());

                            servidor_thread_lixeiras.send(envelopeAEnviar);
                            continue;
                        }

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

                        if (lista_2.isEmpty()) {
                            //lista_lixeiras_json.add(objReceive);
                            lista_2.add(lixeira);
                        } else {

                            boolean exist = false;
                            /*
                                System.out.println("======== LISTAAAA: " + lista_lixeiras_json.size() + lista_lixeiras_json.isEmpty());

                                ArrayList<JSONObject> list_aux = new ArrayList<>();
                                for (int i = 0; i < lista_lixeiras_json.size(); i++) {
                                    list_aux.add(lista_lixeiras_json.get(i));
                                }
                                System.out.println(">>>>>>>>>>>>>>>. LISTA AUXILIAR " + list_aux.size());

                                //lista_lixeiras_json.clear();
                                System.out.println("LISTA=========  " + lista_lixeiras_json.size() + lista_lixeiras_json.isEmpty());

                                for (int j = 0; j < lista_lixeiras_json.size(); j++) {
                                    System.out.println("==========ENTROU NO DOWHILEEEE");

                                    if (objReceive.getInt("id") == lista_lixeiras_json.get(j).getInt("id")) {
                                        lista_lixeiras_json.remove(j);
                                        exist = true;
                                        lista_lixeiras_json.add(j, objReceive);
                                        System.out.println("ESSA LIXEIRA EXISTE" + exist);
                                    }
                                }

                                if (!exist) {
                                    lista_lixeiras_json.add(objReceive);
                                    System.out.println("LIXEIRA NAO ENCONTRADA - NOVO ADD");
                                    System.out.println("=======>>>>>> LISTA PRINCIPAL " + lista_lixeiras_json.size());
                                }
                             */

                            ////////////////////////////////////
                            ArrayList<Lixeira_Date> list_aux2 = new ArrayList<>();
                            for (int c = 0; c < lista_2.size(); c++) {
                                list_aux2.add(lista_2.get(c));
                            }

                            for (int count = 0; count < lista_2.size(); count++) {

                                if (lixeira.getId() == lista_2.get(count).getId()) {
                                    lista_2.remove(count);
                                    exist = true;
                                    lista_2.add(count, lixeira);
                                }
                            }

                            if (!exist) {
                                lista_2.add(lixeira);
                            }
                        }

                        System.out.println("*******************************************");
                        Collections.sort(lista_2); //ORDENA A LISTA
                        for (int i = 0; i < lista_2.size(); i++) {
                            System.out.println(lista_2.get(i).toString());
                        }
                        System.out.println("*******************************************");

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

    public static void thread_Adm() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    DatagramPacket envelopeAReceber = new DatagramPacket(msgAReceber, msgAReceber.length);
                    System.out.println("[SERVER_ADMINISTRADOR]: OK, MENSAGEM A RECEBER...");
                    try {
                        servidor_thread_adm.receive(envelopeAReceber);//recebe o envelope do cliente
                    } catch (IOException ex) {
                        System.err.println("ERRO A RECEBER MENSAGEM DO ADMINISTRADOR!");
                    }

                    InetAddress ipAdm = envelopeAReceber.getAddress();
                    String str = new String(envelopeAReceber.getData());
                    JSONObject objReceive = null;
                    try {
                        objReceive = new JSONObject(str);

                        System.out.println("DE: " + ipAdm.getHostAddress() + " -> " + objReceive.toString());

                        JSONObject objSend;

                        if (objReceive.getString("msg").equals("REQUEST")) {
                            if (lista_2.isEmpty()) {
                                objSend = new JSONObject();
                                objSend.put("msg", "EMPTY");
                                msgAEnviar = objSend.toString().getBytes();
                                DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, ipAdm, envelopeAReceber.getPort());
                                servidor_thread_adm.send(envelopeAEnviar);
                            } else {
                                String quantidade = "quantidade:" + lista_2.size() + ",";
                                System.out.println("== QUANTIDADE LIXEIRA: " + quantidade);
                                String arraylixeiras = "lixeiras:[{";
                                String array_prox_lixeira = ", proxima_lixeira:{";

                                String strlixeira = "lixeira";
                                String strStr = "";
                                for (int i = 0; i < lista_2.size(); i++) {
                                    int num = i + 1;
                                    strStr += strlixeira + num + ":{id:" + lista_2.get(i).getId()
                                            + ",capacidade_atual:" + lista_2.get(i).getCapacidade_atual()
                                            + ",bloqueio:" + lista_2.get(i).isBloqueio()
                                            + ",capacidade_max:" + lista_2.get(i).getCapacidade_max()
                                            + ",capacidade_disponivel:" + lista_2.get(i).getCapacidade_disponivel()
                                            + ",latitude:" + lista_2.get(i).getLatitude()
                                            + ",longitude:" + lista_2.get(i).getLongitude() + "}";
                                    System.out.println(strStr);

                                    if (num < lista_2.size()) {
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
                            System.out.println("===== LIXEIRA ALTERADA====");
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

    public static int buscarLixeira(int latitude, int longitude) throws JSONException {
        Iterator i = lista_lixeiras_json.iterator();
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

    public static int buscarLixeira(int id) throws JSONException {
        Iterator i = lista_2.iterator();
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

    public static void altera_bloqueio_lixeira(JSONObject objReceive) throws JSONException {
        int pos = buscarLixeira(objReceive.getInt("id_lixeira"));

        if (pos != -1) {
            Lixeira_Date lixeira = lista_2.get(pos);
            //lixeira.setCapacidade_atual(0.0);
            //lixeira.setColetada(true);
            lista_2.remove(pos);
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

    public static void altera_status_coletada_lixeira(JSONObject objReceive) throws JSONException {
        int pos = buscarLixeira(objReceive.getInt("id_lixeira"));

        if (pos != -1) {
            Lixeira_Date obj = lista_2.get(pos);
            obj.setCapacidade_atual(0.0);
            obj.setColetada(true);
            lista_2.remove(pos);
            lista_2.add(obj);
            //agora envia os dados para a lixeira

            byte[] msgAEnviar = new byte[1024];
            JSONObject objSend = new JSONObject();
            objSend.put("msg", "COLLECTED");
            msgAEnviar = objSend.toString().getBytes();
            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length, obj.getAddress(), obj.getPorta());

            try {
                servidor_thread_caminhao.send(envelopeAEnviar);
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void alteraDadosLixeira(JSONObject objReceive) throws JSONException {
        int pos = buscarLixeira(objReceive.getInt("latitude_lixeira"), objReceive.getInt("longitude_lixeira"));
        if (pos != -1) {
            JSONObject obj = (JSONObject) lista_lixeiras_json.get(pos);
            obj.put("capacidade_atual", 0);
            obj.put("colatada", true);
            lista_lixeiras_json.remove(pos);
            lista_lixeiras_json.add(obj);

        }
    }

    public static boolean todasLixeirasColetadas() throws JSONException {
        /*Iterator i = lista_lixeiras_json.iterator();
        JSONObject obj;

        while (i.hasNext()) {

            obj = (JSONObject) i.next();
            if (!obj.getBoolean("coletada")) {//encontra alguma lixeira que ainda nao foi coletada
                return false;
            }
        }*/

        Iterator it = lista_2.iterator();
        Lixeira_Date lix;

        while (it.hasNext()) {

            lix = (Lixeira_Date) it.next();
            if (!lix.isColetada()) {//encontra alguma lixeira que ainda nao foi coletada
                return false;
            }
        }

        return true;
    }

    public static void alterarStatusLixeiras() {
        byte[] msgAEnviar = new byte[1024];
        JSONObject objSend = new JSONObject();

        try {
            objSend.put("msg", "STATUS");
        } catch (JSONException ex) {
            System.err.println("NAO FOI POSSIVEL ENVIAR MENSAGEM PARA A LIXEIRA MUDAR O STATUS DE COLETADA");
        }

        for (int i = 0; i < lista_2.size(); i++) {
            msgAEnviar = objSend.toString().getBytes();
            DatagramPacket envelopeAEnviar = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                    lista_2.get(i).getAddress(), lista_2.get(i).getPorta());
            try {
                servidor_thread_caminhao.send(envelopeAEnviar);
            } catch (IOException ex) {
                Logger.getLogger(Nuvem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void enviar_porta_cliente(int port, byte[] msgAEnviar, JSONObject obj,
            InetAddress ipCliente, DatagramPacket envelopeAReceber) throws JSONException, IOException {

        obj = new JSONObject();
        obj.put("msg", "PORT");
        Random gerador = new Random();
        obj.put("id", gerador.nextInt(100)); //id para lixeira
        obj.put("porta", port);
        msgAEnviar = obj.toString().getBytes();

        DatagramPacket envelopeAEnviar
                = new DatagramPacket(msgAEnviar, msgAEnviar.length,
                        ipCliente, envelopeAReceber.getPort());

        servidorUDP.send(envelopeAEnviar);
    }

    public static void main(String[] args) throws IOException, JSONException {
        there_is_caminhao = false;

        try {
            servidorUDP = new DatagramSocket(porta_servidor);

            System.out.println("Servidor em execu????o na porta " + porta_servidor);
            servidor_thread_lixeiras = new DatagramSocket(porta_lixeiras);
            servidor_thread_adm = new DatagramSocket(porta_adm);
            servidor_thread_caminhao = new DatagramSocket(porta_caminhao);
        } catch (SocketException ex) {
            System.err.println("N??O FOI POSS??VEL INICIALIZAR O SERVIDOR!");
        }

        lista_lixeiras_json = new ArrayList<>();
        lista_2 = new ArrayList<>();
        Nuvem receive = new Nuvem();
        receive.start();
        thread_lixeira(); //criar thread para receber dados das lixeiras
        thread_caminhao();
        thread_Adm();
        //NuvemSend send = new NuvemSend();
        //send.start();
    }
}
