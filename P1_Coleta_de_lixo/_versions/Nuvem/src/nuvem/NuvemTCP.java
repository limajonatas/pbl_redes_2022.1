package nuvem;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuvemTCP extends Thread {

    private Socket client;
    private static ServerSocket servidor;
    
    public NuvemTCP(Socket cliente) {
        this.client = cliente;
    }

    @Override
    public void run() {
        System.out.println("Cliente "
                + client.getInetAddress().getHostAddress() + " adicionado."
        );

        Scanner s = null;
        try {
            s = new Scanner(client.getInputStream());

            while (s.hasNextLine()) {
                System.out.println(s.nextLine());
            }
            s.close();
            client.close();
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(NuvemTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public static void main(String[] args) throws IOException {

        servidor = new ServerSocket(12345);
        System.out.println("Porta 12345 aberta!");
        while (true) {
            Socket cliente = servidor.accept();
            Thread t = new NuvemTCP(cliente);
            t.start();
        }

        //System.out.println("Servidor Concluído!");
    }

}
