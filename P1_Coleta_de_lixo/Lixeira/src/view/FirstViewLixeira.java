/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import javax.swing.UIManager;
import com.formdev.flatlaf.*;
import java.awt.Color;
import javax.swing.SpinnerNumberModel;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author dhoml
 */
public class FirstViewLixeira extends javax.swing.JFrame {

    private double capacidade_max;
    private int latitude;
    private int longitude;

    /**
     * Creates new form Inicializacao
     */
    public FirstViewLixeira() {
        initComponents();
        this.setTitle("Insira os dados necessários");
        this.setLocationRelativeTo(null);
        this.label_position.setVisible(false);
        //definir maximos da latitude e longitude
        spinner_latitude.setModel(new SpinnerNumberModel(0, -90, 90, 1));
        spinner_longitude.setModel(new SpinnerNumberModel(0, -90, 90, 1));
        spinner_capacidade_max.setModel(new SpinnerNumberModel(100, 1, 1000, 1));
        
    }

    public void setChargePosition(String title) {
        this.setTitle(title);
        this.label_position.setForeground(Color.red);
        this.label_longitude.setForeground(Color.yellow);
        this.label_latitude.setForeground(Color.YELLOW);
        this.label_position.setVisible(true);
        
    }
    public void set_latitude_longitude(int latit, int longit){
        this.latitude = latit;
        this.longitude = longit;
        this.spinner_latitude.setValue(latit);
        this.spinner_longitude.setValue(longit);
    }

    public Double getCapacidadeMaxima() {
        return this.capacidade_max;
    }

    public int getLatitude() {
        return this.latitude;
    }

    public int getLongitude() {
        return this.longitude;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        label_latitude = new javax.swing.JLabel();
        button_confirma = new javax.swing.JButton();
        spinner_latitude = new javax.swing.JSpinner();
        label_longitude = new javax.swing.JLabel();
        text_latitude2 = new javax.swing.JLabel();
        spinner_longitude = new javax.swing.JSpinner();
        spinner_capacidade_max = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_position = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        label_latitude.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        label_latitude.setText("LATITUDE");

        button_confirma.setText("Confirmar");
        button_confirma.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        button_confirma.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button_confirma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_confirmaActionPerformed(evt);
            }
        });

        label_longitude.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        label_longitude.setText("LONGITUDE");

        text_latitude2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        text_latitude2.setText("CAPACIDADE MÁXIMA");

        spinner_capacidade_max.setAutoscrolls(true);
        spinner_capacidade_max.setMaximumSize(new java.awt.Dimension(32, 32767));
        spinner_capacidade_max.setMinimumSize(new java.awt.Dimension(32, 22));
        spinner_capacidade_max.setPreferredSize(new java.awt.Dimension(32, 22));

        jLabel1.setText("(-90º a 90º)");

        jLabel2.setText("(-90º a 90º)");

        label_position.setBackground(new java.awt.Color(255, 51, 51));
        label_position.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_position.setText("Altere a latitude ou longitude!");

        jLabel3.setText("(1 a 1000m3)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(text_latitude2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(spinner_capacidade_max, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(25, 25, 25))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label_latitude)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label_longitude)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)))
                        .addGap(90, 90, 90)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinner_longitude, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                            .addComponent(spinner_latitude))
                        .addGap(24, 24, 24))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_position, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(button_confirma, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_position)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_latitude, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinner_latitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_longitude, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinner_longitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(text_latitude2)
                    .addComponent(spinner_capacidade_max, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(button_confirma, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_confirmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_confirmaActionPerformed
        int i = (int) spinner_capacidade_max.getValue();
        this.capacidade_max = (double) i;
        this.latitude = (int) spinner_latitude.getValue();
        this.longitude = (int) spinner_longitude.getValue();
        System.out.println("CAPACIDADE MAXIMA: " + this.capacidade_max
                + "m3 \nLATITUDE: " + this.latitude + "º\nLONGITUDE: " + this.longitude + "º");
        //System.exit(0);
        this.setVisible(false);
    }//GEN-LAST:event_button_confirmaActionPerformed

    /**
     * s
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FirstViewLixeira().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_confirma;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel label_latitude;
    private javax.swing.JLabel label_longitude;
    private javax.swing.JLabel label_position;
    private javax.swing.JSpinner spinner_capacidade_max;
    private javax.swing.JSpinner spinner_latitude;
    private javax.swing.JSpinner spinner_longitude;
    private javax.swing.JLabel text_latitude2;
    // End of variables declaration//GEN-END:variables
}