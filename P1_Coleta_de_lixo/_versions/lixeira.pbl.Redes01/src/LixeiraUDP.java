
import java.net.*;
import java.util.*;

public class LixeiraUDP {

    private static String ipCliente;

    public static void main(String args[]) throws Exception {

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
            byte[] cartaAEnviar = new byte[100]; //criando um array de byte (necessário)
            cartaAEnviar = mensagem.getBytes(); //converte a mensagem String para array de bytes
            InetAddress ip = InetAddress.getByName(""); //atribuindo o ip
            //adicionar a mensagem à um "envelope", que inclui o tamanho, ip e a porta de destino
            DatagramPacket envelopeAEnviar
                    = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ip, 5000);
            cliente.send(envelopeAEnviar); //aqui envia esse envelope com sua mensagem 
            if(mensagem.equals("s")){
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
