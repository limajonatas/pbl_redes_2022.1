
package caminhao;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/**
 *
 * @author jonatas
 */
public class Caminhao {
    private static final String ipCliente = "";
    private static int port;
    private static DatagramSocket cliente;
    private static JSONObject json;
    private static MainViewCaminhao mainview;
    private static boolean serverOk;
    private static JSONObject lixeiras;
    
    /*
    Criar aquirvo JSON
     */
    public static void createJson() throws JSONException {
        json = new JSONObject();
        json.put("type", 2); //por enquanto para identificar que é um caminhao- nº3
        json.put("capacidade_atual", 0);
        json.put("connected", false); //conectado ao servidor
    }
    
    public static void inserirdados() throws JSONException, UnknownHostException {
        json.put("capacidade_max", mainview.getCapacidadeMaxima());
     //   json.put("latitude", mainview.getLatitude());
       // json.put("longitude", mainview.getLongitude());
        json.put("address", InetAddress.getByName(""));
        json.put("port", cliente.getLocalPort());

        System.out.println("DADOS LIXEIRO: " + json.toString());//afim de verificar se está tudo ok
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            cliente = new DatagramSocket();
            
            try {
                createJson();
                
                
                
            } catch (JSONException ex) {
                Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
            
        } catch (SocketException ex) {
            Logger.getLogger(Caminhao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
