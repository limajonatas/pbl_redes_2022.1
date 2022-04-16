/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lixeiras;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author dhoml
 */
public class NewClass1 {

    public static void main(String[] args) throws JSONException {
        InetAddress a = null;
        Lixeira_Date lixeira1 = new Lixeira_Date(true, a, 55555, false, -1, 00, 3, 10, 10.11, 100, false, -1);
        Lixeira_Date lixeira2 = new Lixeira_Date(true, a, 77777, false, -3, 00, 3, 10, 30.91, 100, false, -3);
        Lixeira_Date lixeira3 = new Lixeira_Date(true, a, 99999, false, -5, 00, 3, 10, 30.9, 100, false, -5);
        Lixeira_Date lixeira4 = new Lixeira_Date(true, a, 33333, false, -7, 00, 3, 10, 29.1, 100, false, -7);
        Lixeira_Date lixeira5 = new Lixeira_Date(true, a, 33333, false, -7, 00, 3, 10, 10.102, 100, false, -7);

        ArrayList<Lixeira_Date> list = new ArrayList<>();
        list.add(lixeira1);
        list.add(lixeira2);
        list.add(lixeira3);
        list.add(lixeira5);
        list.add(lixeira4);

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }
        Collections.sort(list);
        System.out.println("========================");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }

        System.out.println("");

        if ((Double.valueOf(12.0).compareTo(Double.valueOf(11.00)) == -1)) {
            System.out.println(1);
        } else if ((Double.valueOf(12.0).compareTo(Double.valueOf(11.00)) == 1)) {
            System.out.println(-1);
        } else {
            System.out.println(0);
        }

        Random gerador = new Random();
        System.out.println(gerador.nextInt(-90, 90) + "º");
        System.out.println(gerador.nextInt(-90, 90) + "º");
        System.out.println(gerador.nextInt(-90, 90) + "º");
        System.out.println(gerador.nextInt(-90, 90) + "º");
        System.out.println(gerador.nextInt(-90, 90) + "º");

        // JOptionPane.showMessageDialog(new JPanel(), "NÃO É POSSÍVEL CADASTRAR MAIS LIXEIRAS", "JÁ EXISTEM 3 LIXEIRA CONECTADAS", JOptionPane.ERROR_MESSAGE);
        double max = 1000.00;
        double disp = 500.0;
        double cap = 500.0;
        double div = cap / 100;

        System.out.println("CONTA");
        for (int i = 0; i <= 100; i += 2) {
            double b = (disp + (div * i));
            System.out.println(b);
        }

        for (int i = 0; i < 10; i++) {

            if (i % 2 == 1) {
                continue;
            }
            System.out.println("numero " + i);
        }
        int aaa = 15;
        JSONObject obj = new JSONObject(
                "{quantidade:2, lixeiras:[{lixeira1:{id: 22, capacidade_maxima: 10.0}, lixeira2:{id: 56, capacidade_maxima: 55.0}}]}"
        );
        JSONArray arr = obj.getJSONArray("lixeiras");

        int kk = obj.getInt("quantidade");
        
        for (int i = 0; i < kk; i++) {
            int li=i+1;
            JSONObject obb =  arr.getJSONObject(0).getJSONObject("lixeira"+li);
            arr.getJSONObject(0).put("lixeira3", new JSONObject("{id: 772, capacidade_maxima: 5848.0}"));
            System.out.println("ASSIM: "+arr.toString());
            System.out.println(obb.toString());
            System.out.println("CAPACIDADE:" + obb.getDouble("capacidade_maxima"));
        }
         JSONObject obb =  arr.getJSONObject(0).getJSONObject("lixeira3");
           
            System.out.println(obb.toString());
            System.out.println("CAPACIDADE:" + obb.getDouble("capacidade_maxima"));

    }
}
