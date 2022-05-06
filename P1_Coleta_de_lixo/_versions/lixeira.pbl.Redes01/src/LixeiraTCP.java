
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class LixeiraTCP {

    private static Scanner teclado;

    public LixeiraTCP(String ip, int port) throws IOException {

        Socket cliente = new Socket(ip, port);
        System.out.println("Servidor conectado !");

        PrintStream saida = new PrintStream(cliente.getOutputStream());

        while (teclado.hasNextLine()) {
            saida.println(teclado.nextLine());
        }
        saida.close();
        teclado.close();
        cliente.close();
    }

    public static void main(String[] args) throws IOException {
        teclado = new Scanner(System.in);
        // System.out.println("IP: ");
        // String ip = teclado.nextLine();
        LixeiraTCP cliente = new LixeiraTCP("", 12345);
        System.out.println("Cliente concluido!");

    }
}
