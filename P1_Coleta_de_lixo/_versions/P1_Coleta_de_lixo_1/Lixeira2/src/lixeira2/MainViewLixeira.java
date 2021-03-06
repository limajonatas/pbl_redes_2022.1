/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lixeira2;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author dhoml
 */
public class MainViewLixeira extends javax.swing.JFrame {

    private double capacidade_maxima;
    private double capacidade_atual;
    private boolean bloqueio;
    private int latitude;
    private int longitude;
    private boolean connected;
    private double capacidade_disponivel;

    /**
     * Creates new form setDados
     *
     * @param cap_max
     * @param cap_atual
     * @param bloq
     * @param lat
     * @param longi
     */
    public void setDados(double cap_max, double cap_atual, boolean bloq,
            int lat, int longi, boolean connected) {

        this.capacidade_atual = cap_atual;
        this.capacidade_maxima = cap_max;
        this.bloqueio = bloq;
        this.latitude = lat;
        this.longitude = longi;
        this.connected = connected;

        this.label_capacidade_atual.setText(Double.toString(this.capacidade_atual));
        this.label_capacidade_maxima.setText(Double.toString(this.capacidade_maxima));
        this.label_latitude.setText(Integer.toString(this.latitude) + "º");
        this.label_longitude.setText(Integer.toString(this.longitude) + "º");
        if (this.bloqueio) {
            this.label_bloqueio.setText("YES");
        } else {
            this.label_bloqueio.setText("NO");
        }
        if (this.connected) {
            this.label_servidor.setText("SERVIDOR CONECTADO!");
            this.label_servidor.setForeground(Color.white);
        } else {
            this.label_servidor.setText("Servidor desconectado!");
            this.label_servidor.setForeground(Color.red);
        }
        this.capacidade_disponivel = (this.capacidade_maxima - this.capacidade_atual);
        this.label_disponivel.setText("ESPAÇO DISPONÍVEL: " + this.capacidade_disponivel);
        this.slider_lixo.setMaximum((int) this.capacidade_disponivel);
        
        if(this.capacidade_disponivel==0) {
            this.label_msg_addLixo.setText("Não é possível colocar mais lixo. Agora somente após a coleta");
            this.label_msg_addLixo.setForeground(Color.red);
            this.label_msg_addLixo.setVisible(true);
        }
    }

    public JSONObject getDate() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("capacidade_atual", this.capacidade_atual);

        return json;
    }

    public MainViewLixeira() {

        initComponents();

       
        this.setTitle("Lixeira Ativa");
        this.setLocationRelativeTo(null);
        this.label_msg_addLixo.setVisible(false);
      
    }

    public void setBloqueio() {
        this.bloqueio = !this.bloqueio;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label_servidor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_capacidade_maxima = new javax.swing.JLabel();
        btn_confirmar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        slider_lixo = new javax.swing.JSlider();
        label_disponivel = new javax.swing.JLabel();
        label_slider = new javax.swing.JLabel();
        label_capacidade_atual = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_latitude = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_longitude = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_bloqueio = new javax.swing.JLabel();
        label_msg_addLixo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_servidor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_servidor.setText("Aguardando resposta do servidor...");
        getContentPane().add(label_servidor, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 380, -1));

        jLabel3.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CAPACIDADE ATUAL");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 400, 30));

        label_capacidade_maxima.setBackground(new java.awt.Color(0, 102, 102));
        label_capacidade_maxima.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        label_capacidade_maxima.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_capacidade_maxima.setText("0.00");
        getContentPane().add(label_capacidade_maxima, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 165, 400, 20));

        btn_confirmar.setText("CONFIRMAR");
        btn_confirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirmarActionPerformed(evt);
            }
        });
        getContentPane().add(btn_confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, 120, 30));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Adicionar lixo:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 400, -1));

        slider_lixo.setMinimum(1);
        slider_lixo.setPaintLabels(true);
        slider_lixo.setValue(1);
        slider_lixo.setMaximumSize(new java.awt.Dimension(32767, 30));
        slider_lixo.setMinimumSize(new java.awt.Dimension(300, 30));
        slider_lixo.setPreferredSize(new java.awt.Dimension(300, 40));
        slider_lixo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_lixoStateChanged(evt);
            }
        });
        getContentPane().add(slider_lixo, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, 300, 40));

        label_disponivel.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_disponivel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_disponivel.setText("ESPAÇO DISPONÍVEL: ");
        getContentPane().add(label_disponivel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 205, 400, 30));

        label_slider.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        label_slider.setText("1");
        getContentPane().add(label_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, 40, 20));

        label_capacidade_atual.setBackground(new java.awt.Color(0, 102, 102));
        label_capacidade_atual.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        label_capacidade_atual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_capacidade_atual.setText("0.00");
        getContentPane().add(label_capacidade_atual, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 400, 30));

        jLabel8.setText("Latitude: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, -1, -1));

        label_latitude.setText("0º");
        getContentPane().add(label_latitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 380, 30, -1));

        jLabel10.setText("Longitude:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 380, -1, -1));

        label_longitude.setText("0º");
        getContentPane().add(label_longitude, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 380, 30, -1));

        jLabel12.setText("Blocked:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 380, -1, -1));

        label_bloqueio.setText("NO");
        getContentPane().add(label_bloqueio, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 380, 40, -1));

        label_msg_addLixo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_msg_addLixo.setText("mensagem aqui apos confirmar");
        label_msg_addLixo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(label_msg_addLixo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 400, 20));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("CAPACIDADE MÁXIMA");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 400, 30));

        label_background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rect28903.png"))); // NOI18N
        label_background.setOpaque(isOpaque());
        getContentPane().add(label_background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_confirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirmarActionPerformed
        // TODO add your handling code here:

        this.capacidade_disponivel = this.capacidade_maxima - this.capacidade_atual;
        if (this.capacidade_disponivel >= Double.valueOf(this.slider_lixo.getValue())) {

            this.capacidade_atual += Double.valueOf(this.slider_lixo.getValue());

            this.label_msg_addLixo.setText("Você colocou lixo na lixeira!");
            this.label_msg_addLixo.setForeground(Color.yellow);
            this.label_msg_addLixo.setVisible(true);
            this.label_disponivel.setText("ESPAÇO DISPONÍVEL: " + (this.capacidade_maxima - this.capacidade_atual));
        } 

    }//GEN-LAST:event_btn_confirmarActionPerformed

    private void slider_lixoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_lixoStateChanged
        // TODO add your handling code here:

        label_slider.setText(String.valueOf(slider_lixo.getValue()));
    }//GEN-LAST:event_slider_lixoStateChanged

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }

        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainViewLixeira().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_confirmar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel label_background;
    public javax.swing.JLabel label_bloqueio;
    private javax.swing.JLabel label_capacidade_atual;
    private javax.swing.JLabel label_capacidade_maxima;
    private javax.swing.JLabel label_disponivel;
    private javax.swing.JLabel label_latitude;
    private javax.swing.JLabel label_longitude;
    private javax.swing.JLabel label_msg_addLixo;
    private javax.swing.JLabel label_servidor;
    private javax.swing.JLabel label_slider;
    private javax.swing.JSlider slider_lixo;
    // End of variables declaration//GEN-END:variables
}
