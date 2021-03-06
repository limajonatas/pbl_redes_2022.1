package view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.util.StringUtils;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lixeira.Lixeira_Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author jonatas
 */
public class MainViewAdministrador extends javax.swing.JFrame {

    private static boolean atualizado;

    private static ArrayList<Lixeira_Date> list_lixeiras;
    //private Lixeira_Date lixeira_alterada;
    //private int id_lixeira_altera;
    private static boolean lixeira_alterada_boolean;
    private static JSONObject lixeira_alterada_json;
    private static final String jcombo_str_lixeiras = "LIXEIRA - ID: ";

    /**
     * Creates new form MainViewAdministrador
     */
    public MainViewAdministrador() {
        initComponents();
        this.setTitle("ADMINISTRADOR");
        this.setLocationRelativeTo(null);
        this.label_info.setText("<html><p text-align: center>ID - 00; CAPACIDADE ATUAL - 1000; BLOQUEADA - SIM;<br>CAPACIDADE MÁXIMA - 00; CAPACIDADE DISPONIVEL - 1000;</p></html>");
        this.buttonGroup1.add(this.radio_on);
        this.buttonGroup1.add(this.radio_off);
        this.atualizado = false;

        this.label_save.setVisible(false);
        this.label_atualizacao.setVisible(false);
        this.label_info.setText("N/D");
        this.label_caminhao.setText("N/D");
        list_lixeiras = new ArrayList<>();
        jProgressBar1.setVisible(false);
    }

    public JSONObject lixeira_alterada() {
        return this.lixeira_alterada_json;
    }

    public boolean lixeira_foi_alterada() {
        if (this.lixeira_alterada_boolean) {
            this.lixeira_alterada_boolean = false;
            return true;
        } else {
            return false;
        }
    }

    private boolean lixeira_bloqueada(Lixeira_Date lix) {
        for (int i = 0; i < list_lixeiras.size(); i++) {

            if (list_lixeiras.get(i).getId() == lix.getId()) {
                return list_lixeiras.get(i).isBloqueio();
            }
        }
        return false;
    }

    private Lixeira_Date find_bin(int id) {
        for (int i = 0; i < list_lixeiras.size(); i++) {

            if (list_lixeiras.get(i).getId() == id) {
                return list_lixeiras.get(i);
            }
        }
        return null;
    }

    public boolean isAtualizado() {
        return this.atualizado;
    }

    public void atualizado(String hora) {
        this.label_atualizacao.setVisible(true);

        this.atualizado = false;
        this.label_atualizacao.setText("Última Atualização: " + hora);
    }

    public void proxima_lixeira(int id, int latitude, int longitude) {
        this.label_caminhao.setText("ID: " + id + "; LATITUDE: " + latitude + "º; LONGITUDE" + longitude + "º.");
    }

    public void inserir_lixeiras(Lixeira_Date lixeira) {
        this.lixeira_alterada_json = null;
        label_save.setVisible(false);
        btn_save.setEnabled(true);
        combo_lixeiras.removeAllItems();
        if (list_lixeiras.isEmpty()) {
            list_lixeiras.add(lixeira);
            //combo_lixeiras.addItem(jcombo_str_lixeiras + list_lixeiras.get(0).getId());
        } else {
            boolean find = false;
            for (int i = 0; i < list_lixeiras.size(); i++) {

                if (lixeira.getId() == list_lixeiras.get(i).getId()) {//ja existe na lista, atualiza.
                    //combo_lixeiras.removeItem(jcombo_str_lixeiras + list_lixeiras.get(i).getId());
                    list_lixeiras.remove(i);
                    list_lixeiras.add(i, lixeira);
                    //combo_lixeiras.addItem(jcombo_str_lixeiras + list_lixeiras.get(i).getId());
                    find = true;
                    break;
                }

            }
            if (!find) {
                list_lixeiras.add(lixeira);
                //combo_lixeiras.addItem(jcombo_str_lixeiras + lixeira.getId());
            }
        }
        for (int i = 0; i < list_lixeiras.size(); i++) {
            combo_lixeiras.addItem(jcombo_str_lixeiras + list_lixeiras.get(i).getId());
        }
    }

    public void nao_ha_lixeiras() {
        this.label_info.setText("NÃO HÁ LIXEIRAS CADASTRADAS");
        this.label_caminhao.setText("N/D");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        combo_lixeiras = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        radio_off = new javax.swing.JRadioButton();
        radio_on = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        btn_save = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        label_info = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_caminhao = new javax.swing.JLabel();
        label_atualizacao = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jProgressBar1 = new javax.swing.JProgressBar();
        label_save = new javax.swing.JLabel();
        btn_atualizar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adm_image.png"))); // NOI18N

        combo_lixeiras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_lixeirasActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INFO:");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        radio_off.setText("OFF");
        radio_off.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_offActionPerformed(evt);
            }
        });

        radio_on.setText("ON");
        radio_on.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_onActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("BLOQUEIO");

        btn_save.setText("SALVAR");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        jLabel4.setText("LIXEIRAS");

        label_info.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_info.setText("ID - 00; CAPACIDADE ATUAL - 1000; BLOQUEADA - SIM;");

        jLabel5.setText("[CAMINHÃO] PROXIMA LIXEIRA: ");

        label_caminhao.setText("ID 00; LATITUDE 0º; LONGITUDE 0º.");

        label_atualizacao.setFont(new java.awt.Font("Segoe UI", 0, 8)); // NOI18N
        label_atualizacao.setForeground(new java.awt.Color(153, 255, 0));
        label_atualizacao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_atualizacao.setText("Última Atualização: 00:00");

        jProgressBar1.setMinimumSize(new java.awt.Dimension(10, 2));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(146, 2));

        label_save.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        label_save.setText("salvo!");

        btn_atualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/atualizacao.png"))); // NOI18N
        btn_atualizar.setText("Atualizar");
        btn_atualizar.setPreferredSize(new java.awt.Dimension(90, 26));
        btn_atualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_atualizarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 8)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 204));
        jLabel6.setText("A atualização automática ocorre a cada 20 segundos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_atualizacao, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(btn_atualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_info, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(combo_lixeiras, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_save)
                                .addGap(23, 23, 23))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radio_off)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radio_on)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btn_save))
                                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(5, 5, 5)
                        .addComponent(label_caminhao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(label_save))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combo_lixeiras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radio_off)
                            .addComponent(radio_on)
                            .addComponent(btn_save))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_info, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(label_caminhao)))
                    .addComponent(btn_atualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_atualizacao)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radio_onActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_onActionPerformed

    }//GEN-LAST:event_radio_onActionPerformed

    private void radio_offActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_offActionPerformed

    }//GEN-LAST:event_radio_offActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        //encontrar a lixeira na lista 
        String str = (String) combo_lixeiras.getSelectedItem();
        int id = Integer.parseInt((str.substring(str.length() - 2)).replace(" ", ""));
        Lixeira_Date lixeira = find_bin(id);
        boolean ok = false;
        if (radio_off.isSelected() || radio_on.isSelected()) {
            if (radio_off.isSelected()) {

                if (!this.lixeira_bloqueada(lixeira)) {
                    JOptionPane.showMessageDialog(null, "LIXEIRA JÁ ESTA DESBLOQUEADA");
                } else {
                    switch (JOptionPane.showConfirmDialog(null, "<HTML>DESEJA DESBLOQUEAR A LIXEIRA <BR><STRONG>id:" + lixeira.getId() + "</STRONG> ?</HTML>", "BLOQUEAR LIXEIRA", JOptionPane.YES_OPTION)) {
                        case 0:
                            System.out.println("botao YES clicado");

                            try {
                                this.lixeira_alterada_json = new JSONObject();
                                this.lixeira_alterada_json.put("id", id);
                                this.lixeira_alterada_json.put("bloqueio", false);
                            } catch (JSONException ex) {
                                Logger.getLogger(MainViewAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            this.lixeira_alterada_boolean = true; //indicar que alguma lixeira foi alterada;
                            new Thread() {
                                @Override
                                public void run() {
                                    btn_save.setEnabled(false);
                                    label_save.setVisible(true);
                                    label_save.setForeground(Color.yellow);
                                    jProgressBar1.setVisible(true);

                                    for (int i = 0; i <= 100; i += 2) {

                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(MainViewAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        label_save.setText("" + i + "%");
                                        jProgressBar1.setValue(i);

                                    }
                                    label_save.setText("Salvo!");
                                    jProgressBar1.setVisible(false);
                                }
                            }.start();

                            break;

                        default:
                            break;
                    }

                }
            } else {
                if (this.lixeira_bloqueada(lixeira)) {
                    JOptionPane.showMessageDialog(null, "LIXEIRA JÁ ESTA BLOQUEADA");
                } else {
                    switch (JOptionPane.showConfirmDialog(null, "<HTML>DESEJA BLOQUEAR A LIXEIRA <BR><STRONG>id:" + lixeira.getId() + "</STRONG> ?</HTML>", "BLOQUEAR LIXEIRA", JOptionPane.YES_OPTION)) {
                        case 0:
                            System.out.println("botao YES clicado");

                            try {
                                this.lixeira_alterada_json = new JSONObject();
                                this.lixeira_alterada_json.put("id", id);
                                this.lixeira_alterada_json.put("bloqueio", true);
                            } catch (JSONException ex) {
                                Logger.getLogger(MainViewAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            this.lixeira_alterada_boolean = true; //indicar que alguma lixeira foi alterada;
                            new Thread() {
                                @Override
                                public void run() {
                                    btn_save.setEnabled(false);
                                    label_save.setForeground(Color.yellow);
                                    label_save.setVisible(true);
                                    jProgressBar1.setVisible(true);
                                    for (int i = 0; i <= 100; i += 2) {

                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(MainViewAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        label_save.setText("" + i + "%");
                                        jProgressBar1.setValue(i);
                                    }
                                    label_save.setText("Salvo!");
                                    jProgressBar1.setVisible(false);
                                }
                            }.start();

                            break;

                        default:
                            break;
                    }

                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "SELECIONE UMA OPCAO DE BLOQUEIO", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_saveActionPerformed

    private void btn_atualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_atualizarActionPerformed
        this.atualizado = true;
    }//GEN-LAST:event_btn_atualizarActionPerformed

    private void combo_lixeirasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_lixeirasActionPerformed
        // TODO add your handling code here:
        System.out.println("SELECT " + combo_lixeiras.getSelectedItem());

        if (combo_lixeiras.getSelectedItem() != null) {
            for (int i = 0; i < list_lixeiras.size(); i++) {
                String str = (String) combo_lixeiras.getSelectedItem();

                int id = Integer.parseInt((str.substring(str.length() - 2)).replace(" ", ""));

                if (list_lixeiras.get(i).getId() == id) {
                    String bloq;
                    if (list_lixeiras.get(i).isBloqueio()) {
                        bloq = "SIM";
                        // radio_on.doClick();
                    } else {
                        bloq = "NÃO";
                        //radio_off.doClick();
                    }
                    this.label_info.setText("<html><p text-align: center>ID - "
                            + list_lixeiras.get(i).getId() + "; CAPACIDADE ATUAL - "
                            + list_lixeiras.get(i).getCapacidade_atual() + "; <strong>BLOQUEADA</strong> - "
                            + bloq + ";<br>CAPACIDADE MÁXIMA - " + list_lixeiras.get(i).getCapacidade_max()
                            + "; CAPACIDADE DISPONIVEL - " + list_lixeiras.get(i).getCapacidade_disponivel()
                            + ";</p></html>");

                    break;
                }

            }

        }
    }//GEN-LAST:event_combo_lixeirasActionPerformed

    /**
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainViewAdministrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_atualizar;
    private javax.swing.JButton btn_save;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> combo_lixeiras;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel label_atualizacao;
    private javax.swing.JLabel label_caminhao;
    private javax.swing.JLabel label_info;
    private javax.swing.JLabel label_save;
    private javax.swing.JRadioButton radio_off;
    private javax.swing.JRadioButton radio_on;
    // End of variables declaration//GEN-END:variables
}
