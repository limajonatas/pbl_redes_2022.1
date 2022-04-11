/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lixeiras;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author dhoml
 */
public class NewClass1 {
     public static void main(String[] args) {
        InetAddress a = null;
        Lixeira_Date lixeira1 = new Lixeira_Date(true,a, 55555,false, -1, 00,3, 10,10.11, 100,false, -1);
        Lixeira_Date lixeira2 = new Lixeira_Date(true,a, 77777,false, -3, 00,3, 10,30.91, 100,false, -3);
        Lixeira_Date lixeira3 = new Lixeira_Date(true,a, 99999,false, -5, 00,3, 10,30.9, 100,false, -5);
        Lixeira_Date lixeira4 = new Lixeira_Date(true,a, 33333,false, -7, 00,3, 10,29.1, 100,false, -7);
        Lixeira_Date lixeira5 = new Lixeira_Date(true,a, 33333,false, -7, 00,3, 10,10.102, 100,false, -7);
        
        
        
        ArrayList<Lixeira_Date> list = new ArrayList<>();
        list.add(lixeira1);
        list.add(lixeira2);
        list.add(lixeira3);
        list.add(lixeira5);
        list.add(lixeira4);
        
        for(int i = 0; i < list.size(); i++){
            System.out.println(list.get(i).toString());
        }
        Collections.sort(list);
         System.out.println("========================");
        for(int i = 0; i < list.size(); i++){
            System.out.println(list.get(i).toString());
        }
        
         System.out.println("");
         
        if((Double.valueOf(12.0).compareTo(Double.valueOf(11.00)) == -1))
                System.out.println(1);
        else if((Double.valueOf(12.0).compareTo(Double.valueOf(11.00)) == 1))
                System.out.println(-1);
        else
            System.out.println(0);
        
        
    }
}
