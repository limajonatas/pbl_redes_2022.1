

import org.json.*;

/**
 *
 * @author dhoml
 */
public class Lixeira2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JSONException {

        JSONObject obj = new JSONObject();
        obj.put("capacidade_atual", 20.5);
        obj.put("capacidade_max", 120.0);
        
        System.out.println(obj.toString());
        
        System.out.println("CAPACIDADE MAXIMA: "+obj.getDouble("capacidade_max"));
        
        String a=obj.toString();
        
        JSONObject obj2 = new JSONObject(a);
        
        System.out.println("\nCapacidade Máxima = "+obj2.getDouble("capacidade_max")
                +"\nCapacidade ATUAL = "+obj2.getDouble("capacidade_atual"));
    }

}
