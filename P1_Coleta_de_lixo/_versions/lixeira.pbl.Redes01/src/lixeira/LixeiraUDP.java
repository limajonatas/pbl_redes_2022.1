
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;

public class LixeiraUDP extends Thread{

    private static String ipCliente;
    private static double capacidade_atual;
    private static double capacidade_max;
    private static int latitude;
    private static int longitude;
    private static boolean bloqueio;
    
    public static JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("capacidade_atual", capacidade_atual);
        json.put("capacidade_max", capacidade_max);
        json.put("latitute", latitude);
        json.put("longitude", longitude);
        json.put("bloqueio", bloqueio);
        

        return json;
    }
    private void adicionarLixo(){
        
    }
    private void bloquearOnOff(){
    }
    public static void inserirdados(){
        capacidade_max = Double.parseDouble(JOptionPane.showInputDialog("capacidade_max", 10.1));
        latitude = Integer.parseInt(JOptionPane.showInputDialog("LATITUDE", 2));
        longitude = Integer.parseInt(JOptionPane.showInputDialog("LONGITUDE", 1));

        System.out.println(toJson().toString());
    }
    
    

    public static void main(String args[]) throws Exception {
        inserirdados();
        
        //cria-se o socket udp do cliente
        DatagramSocket cliente = new DatagramSocket();

        //CÓDIGO PARA OBTER UM TEXTO VIA TECLADO
        //System.out.print("DIGITE O IP:");
        Scanner teclado = new Scanner(System.in);
        // ipCliente = teclado.nextLine();
        while (true) {
            System.out.print("Digite uma mensagem:");
            String mensagem = teclado.nextLine();

            //CÓDIGO PARA ENVIAR UMA MENSAGEM PARA O SERVIDOR
            byte[] cartaAEnviar = new byte[1024]; //criando um array de byte (necessário)
            //cartaAEnviar = mensagem.getBytes(); //converte a mensagem String para array de bytes
            cartaAEnviar = (toJson().toJSONString()).getBytes(); //converte a mensagem String para array de bytes
            
            String a = toJson().toJSONString();
            System.out.println("LATITUDE LIXEIRA: "+ toJson().get(latitude));
           
            
            InetAddress ip = InetAddress.getByName(""); //atribuindo o ip
            //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
            DatagramPacket envelopeAEnviar
                    = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, 5000);
            cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 
            if (mensagem.equals("s")) {
                break;
            }
        }

        /*
        ////////CÓDIGO PARA RECEBER UMA MENSAGEM DE RESPOSTA DO SERVIDOR
        byte[] cartaAReceber = new byte[100];
        DatagramPacket envelopeAReceber
                = new DatagramPacket(cartaAReceber,cartaAReceber.length);
        cliente.receive(envelopeAReceber); //recebe o envelope do Servidor
        //converte os dados do envelope para string
        String mensagemRecebida = new String(envelopeAReceber.getData());
        System.out.println("CHEGOU DO SERVIDOR:" + mensagemRecebida);

        //SE NÃO TIVER MAIS NADA PARA FAZER
        //finaliza a conexão
         */
        cliente.close();

    }

}
