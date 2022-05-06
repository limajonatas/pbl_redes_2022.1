package nuvem;


import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class NuvemUDP extends Thread {

    private static final int porta_servidor = 5000;
    private static DatagramSocket servidorUDP;

    public void NuvemUDP() {
    }

    @Override
    public void run() {

        System.out.println("Aguardando cliente...");
        while (true) {
            try {
                ///////////RECEBER MENSAGEM DO CLIENTE E IMPRIMIR NA TELA
                byte[] cartaAReceber = new byte[100];
                DatagramPacket envelopeAReceber
                        = new DatagramPacket(cartaAReceber, cartaAReceber.length);
                System.out.println("OK MENSAGEM A RECEBER...");
                servidorUDP.receive(envelopeAReceber);//recebe o envelope do cliente
                InetAddress ipCliente = envelopeAReceber.getAddress();
                String str = ipCliente.getHostAddress();
                String textoRecebido = new String(envelopeAReceber.getData());//converte os dados para string
                System.out.println("DE: " + str + " -> " + textoRecebido);
            } catch (IOException ex) {

            }
        }
    }

    public static void main(String[] args) throws SocketException {
        //cria um socket servidor udp com sua porta
        try {
            servidorUDP = new DatagramSocket(porta_servidor);
            System.out.println("Servidor em execução na porta " + porta_servidor);
        } catch (SocketException ex) {
            System.err.println("NÃO FOI POSSÍVEL INICIALIZAR O SERVIDOR!");
        }
        ;
        Thread thread = new NuvemUDP();
        thread.start();
        /*
            //>>>>>>>>>>ENVIAR MENSAGEM DE VOLTA AO CLIENTE
            //CÓDIGO PARA OBTER UM TEXTO VIA TECLADO
            System.out.print("RESPOSTA (digite 's' para sair): ");
            Scanner teclado = new Scanner(System.in);
            String mensagem = teclado.nextLine();
            if ("s".equals(mensagem)) {
            break;
            }
            byte[] cartaAEnviar = new byte[100];
            //String mensagem = "Dados recebidos com sucesso!";
            cartaAEnviar = mensagem.getBytes();
            //Obtive os dados do remetente (ip e porta) a partir 
            //do envelope recebido anteriormente (envelopeAReceber)
            InetAddress ipCliente = envelopeAReceber.getAddress();//pega o ip do cliente
            int portaCliente = envelopeAReceber.getPort();//pega a porta do cliente
            //cria o envelope para enviar para o cliente
            DatagramPacket envelopeAEnviar
            = new DatagramPacket(cartaAEnviar, cartaAEnviar.length, ipCliente, portaCliente);
            servidorUDP.send(envelopeAEnviar);
         */
        //SE NÃO TIVER MAIS NADA PARA FAZER
        //finaliza a conexão
    }
}
