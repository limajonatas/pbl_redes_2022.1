package view;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CLASE INTERFACE CAMINHAO
 *
 * @author JONATAS DE JESUS LIMA
 */
public class MainViewCaminhao extends javax.swing.JFrame {

    private double capacidade_maxima;
    private double capacidade_atual;
    private double capacidade_disponivel;
    private JSONObject proxima_lixeira_json;
    private JSONObject lixeira_atual_json;
    private JSONObject lixeira_coletada_json;
    private boolean restart;
    private boolean coletado;
    private boolean lixeira_anterior_coletada;
    private static int id_lixeira_coletada;
    private static double quantidade_lixo_coletado;

    /**
     * CONSTRUTOR DA CLASSE
     */
    public MainViewCaminhao() {
        initComponents();

        this.setTitle("Caminhao");
        this.setLocationRelativeTo(null);
        this.spinner_capacidade_maxima.setForeground(Color.yellow);

        this.label_txt_cap_atual.setForeground(Color.red);
        this.label_txt_cap_disp.setForeground(Color.red);
        this.label_txt_cap_max.setForeground(Color.red);
        this.label_capacidade_atual.setForeground(Color.red);
        this.label_capacidade_disponivel.setForeground(Color.red);
        this.label_capacidade_maxima.setForeground(Color.red);
        this.label_proxima_lixeira.setForeground(Color.red);
        this.label_lixeira_atual.setForeground(Color.red);
        this.btn_coletar.setEnabled(false);
        this.progress_coletando.setVisible(false);
        this.label_info_server.setVisible(false);

        this.setVisible(true);

        this.proxima_lixeira_json = new JSONObject();
        this.id_lixeira_coletada = -1;
        this.quantidade_lixo_coletado = -1;

        this.coletado = false;
        this.restart = false;
        //variavel usada para ativar o botao de coleta no método "recebe_proxima_lixeira"
        lixeira_anterior_coletada = true;
        this.btn_descarregar.setVisible(false);
        this.btn_restart.setVisible(false);
        this.btn_restart.setEnabled(true);
        this.label_aviso.setVisible(false);
        this.label_aviso_descarregar.setVisible(false);
        this.label_aviso_sup.setVisible(false);

        spinner_capacidade_maxima.setModel(new SpinnerNumberModel(1000, 1000, 10000, 100)); //min 1000

    }

    /**
     * se o botao restart foi acionado
     *
     * @return
     */
    public boolean btn_reiniciar_clicado() {
        return this.restart;
    }

    /**
     * ATIVAR O BOTAO DE REINICIAR O PROCESSO DE COLETA
     */
    public void ativar_btn_reiniciar() {

        this.label_aviso_sup.setText("Para iniciar um novo processo de coleta, clique em \'Restart\' ");
        this.label_aviso_sup.setVisible(true);
        this.btn_restart.setVisible(true);
        this.btn_coletar.setEnabled(false);
        // this.btn_restart.setEnabled(true);

    }

    /**
     * BOTAO DESLIGAR RESTART
     */
    public void set_restartOFF() {
        this.restart = false;
        this.btn_restart.setVisible(false);
    }

    /**
     * SE A LIXEIRA FOIR COLETADA
     */
    public void lixeira_foi_coletada() {
        this.lixeira_anterior_coletada = true;
    }

    /**
     * ALTERA OS DADOS DO CAMINHAO APÓS UMA COLETA DE ALGUMA LIXEIRA Após uma
     * coleta, o caminhao envia as informações para o servidor e ele, por sua
     * vez, atualiza os dados na lixeira coleta e retorna a informação para o
     * caminhão.
     *
     * @param json recebe os dados do caminho - capacidade atual e disponivel.
     */
    public void set_date_truck(double capacidade_at, double capacidade_disp) {
        this.capacidade_atual = capacidade_at;
        this.capacidade_disponivel = capacidade_disponivel;
        this.label_capacidade_atual.setText("" + capacidade_at);
        this.label_capacidade_disponivel.setText("" + capacidade_disp);
        this.label_info_server.setVisible(false);
    }

    /**
     * RECEBE A PROXIMA LIXEIRA - MOSTRAR NA INTERFACE
     *
     * @param lix lixeira
     * @throws JSONException
     */
    public void recebe_proxima_lixeira(String lix) throws JSONException {
        JSONObject aux = new JSONObject(lix);

        if (aux.getInt("id_lixeira") == this.id_lixeira_coletada) {
            label_proxima_lixeira.setText("N/D");
        } else {

            this.proxima_lixeira_json = new JSONObject(lix);

            this.label_proxima_lixeira.setText("ID: " + this.proxima_lixeira_json.getInt("id_lixeira") + " LAT: "
                    + this.proxima_lixeira_json.getInt("latitude_lixeira")
                    + "º LON: " + this.proxima_lixeira_json.getInt("longitude_lixeira")
                    + "º CAP: " + this.proxima_lixeira_json.getDouble("capacidade_lixeira"));

            if (this.proxima_lixeira_json.getDouble("capacidade_lixeira") < 1) {
                this.label_aviso.setText("Não é possível fazer a coleta, lixeira vazia.");
                this.label_aviso.setForeground(Color.red);
                this.label_aviso.setVisible(true);
                this.btn_coletar.setEnabled(false);
            } else {
                if (this.proxima_lixeira_json.getDouble("capacidade_lixeira") > (this.capacidade_maxima - this.capacidade_atual)) {
                    this.label_aviso.setText("Pouca quantidade disponível no caminhão - descarregue na estação");
                    this.btn_coletar.setEnabled(true);
                } else {
                    this.label_aviso.setVisible(false);
                    if (this.lixeira_anterior_coletada) { //se a lixeira anterior ja foi coletada
                        this.btn_coletar.setEnabled(true);
                    }

                }
            }
        }
    }

    /**
     * SE FEZ ALGUMA COLETA
     *
     * @return true se houve uma coleta anterior, false se nao.
     * @throws JSONException
     */
    public boolean fez_alguma_coletada() throws JSONException {//se fez a coleta
        boolean col = false;
        if (this.coletado) {//se foi coletado
            col = true;
            this.coletado = false;
        }
        return col;
    }

    /**
     * obter o id da lixeira coletada
     * @return 
     */
    public int get_id_lixeira_coletada() {
        return this.id_lixeira_coletada;
    }

    /**
     * obter a quantidade de lixo que foi coletado da lixeira.
     * @return 
     */
    public double get_qtd_lixo_coletado() {
        return this.quantidade_lixo_coletado;
    }

    /**
     * LIMPAR DADOS DA LIXEIRA COLETADA.
     */
    public void limpar_dados_lixeira_coletada() {
        this.id_lixeira_coletada = -1;
        this.quantidade_lixo_coletado = -1;
    }

    /**
     * MUDA O STATUS DO CAMINHAO - SE EXISTE OUTRO CAMINHAO CADASTRADO NO SERVIDOR.
     */
    public void set_status_server_existCaminhao() {
        this.label_add_cap_maxima.setText("Já existe um caminhão!");
        this.label_add_cap_maxima.setVisible(true);
        this.label_add_cap_maxima.setForeground(Color.red);
    }

    /**
     * obter a capacidade maxima do caminhao
     * @return a capacidade maxima do caminhao
     */
    public double getCapacidadeMaxima() {
        return capacidade_maxima;
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label_txt_cap_atual = new javax.swing.JLabel();
        label_txt_cap_max = new javax.swing.JLabel();
        label_txt_cap_disp = new javax.swing.JLabel();
        label_capacidade_maxima = new javax.swing.JLabel();
        label_capacidade_atual = new javax.swing.JLabel();
        label_capacidade_disponivel = new javax.swing.JLabel();
        btn_coletar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        label_lixeira_atual = new javax.swing.JLabel();
        spinner_capacidade_maxima = new javax.swing.JSpinner();
        label_add_cap_maxima = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        progress_coletando = new javax.swing.JProgressBar();
        btn_confirma_cap_max = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btn_restart = new javax.swing.JButton();
        btn_descarregar = new javax.swing.JButton();
        label_aviso = new javax.swing.JLabel();
        label_proxima_lixeira = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        label_minimo_capacidade_caminhao = new javax.swing.JLabel();
        label_aviso_descarregar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_info_server = new javax.swing.JLabel();
        label_aviso_sup = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(440, 300));
        setPreferredSize(new java.awt.Dimension(440, 380));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_txt_cap_atual.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        label_txt_cap_atual.setText("CAPACIDADE ATUAL: ");
        getContentPane().add(label_txt_cap_atual, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 62, -1, -1));

        label_txt_cap_max.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        label_txt_cap_max.setText("CAPACIDADE MÁXIMA: ");
        getContentPane().add(label_txt_cap_max, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        label_txt_cap_disp.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label_txt_cap_disp.setText("ESPAÇO DISPONÍVEL: ");
        getContentPane().add(label_txt_cap_disp, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 151, -1));

        label_capacidade_maxima.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        label_capacidade_maxima.setText("0.00");
        getContentPane().add(label_capacidade_maxima, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 42, -1));

        label_capacidade_atual.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        label_capacidade_atual.setText("0.00");
        getContentPane().add(label_capacidade_atual, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 160, 30));

        label_capacidade_disponivel.setFont(new java.awt.Font("Agency FB", 1, 14)); // NOI18N
        label_capacidade_disponivel.setText("0.00");
        getContentPane().add(label_capacidade_disponivel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 42, -1));

        btn_coletar.setText("COLETAR");
        btn_coletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_coletarActionPerformed(evt);
            }
        });
        getContentPane().add(btn_coletar, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("LIXEIRA ATUAL");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 440, -1));

        label_lixeira_atual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_lixeira_atual.setText("n/d");
        getContentPane().add(label_lixeira_atual, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 440, -1));
        getContentPane().add(spinner_capacidade_maxima, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 80, -1));

        label_add_cap_maxima.setBackground(new java.awt.Color(0, 51, 51));
        label_add_cap_maxima.setForeground(new java.awt.Color(255, 255, 102));
        label_add_cap_maxima.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_add_cap_maxima.setText("Adicione a capacidade máxima:");
        label_add_cap_maxima.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                label_add_cap_maximaAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        getContentPane().add(label_add_cap_maxima, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 400, -1));

        jSeparator1.setPreferredSize(new java.awt.Dimension(450, 10));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 390, 10));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Próxima lixeira");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 440, -1));
        getContentPane().add(progress_coletando, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 200, 5));

        btn_confirma_cap_max.setBackground(new java.awt.Color(102, 0, 102));
        btn_confirma_cap_max.setForeground(new java.awt.Color(255, 255, 51));
        btn_confirma_cap_max.setText("OK");
        btn_confirma_cap_max.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirma_cap_maxActionPerformed(evt);
            }
        });
        getContentPane().add(btn_confirma_cap_max, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\dhoml\\Documents\\NetBeansProjects\\pbl_redes_2022.1\\P1_Coleta_de_lixo\\Caminhao\\images\\coleta.png")); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, -1));

        btn_restart.setText("Restart");
        btn_restart.setEnabled(false);
        btn_restart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_restartActionPerformed(evt);
            }
        });
        getContentPane().add(btn_restart, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 70, 20));

        btn_descarregar.setText("Descarregar");
        btn_descarregar.setEnabled(false);
        btn_descarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_descarregarActionPerformed(evt);
            }
        });
        getContentPane().add(btn_descarregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 40, 100, 20));

        label_aviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_aviso.setText("-------");
        getContentPane().add(label_aviso, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 440, 20));

        label_proxima_lixeira.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_proxima_lixeira.setText("n/d");
        getContentPane().add(label_proxima_lixeira, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 280, 280, -1));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 280, 280, 20));

        label_minimo_capacidade_caminhao.setText("min:1000");
        getContentPane().add(label_minimo_capacidade_caminhao, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, -1, -1));

        label_aviso_descarregar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_aviso_descarregar.setText("-------");
        getContentPane().add(label_aviso_descarregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 440, 20));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 8)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("atualização automática a cada 7 segundos");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 330, 160, -1));

        label_info_server.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        label_info_server.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_info_server.setText("Aguardando mensagem chegar no servidor...");
        getContentPane().add(label_info_server, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 440, -1));

        label_aviso_sup.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_aviso_sup.setText("---");
        getContentPane().add(label_aviso_sup, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_coletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_coletarActionPerformed
        btn_coletar.setEnabled(false);
        try {

            double value = proxima_lixeira_json.getDouble("capacidade_lixeira");
            if (value > 0) {
                //verificar se a quantidade de lixo na próxima lixeira é menor ou igual a quantidade disponível do caminhao
                if (value <= (this.capacidade_maxima - this.capacidade_atual)) {

                    this.coletado = true;

                    //thread para o componente "processo carregando"
                    new Thread() {
                        @Override
                        public void run() {
                            progress_coletando.setVisible(true);

                            for (int i = 0; i < 100; i += 2) {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(MainViewCaminhao.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                progress_coletando.setValue(i);
                            }
                            progress_coletando.setVisible(false);
                            //btn_coletar.setEnabled(true);
                        }
                    }.start();

                    this.label_info_server.setText("Aguardando mensagem chegar no servidor...");
                    this.label_info_server.setVisible(true);
                    this.lixeira_anterior_coletada = false;

                    //altera na interface os dados
                   
                    this.lixeira_atual_json = new JSONObject(this.proxima_lixeira_json.toString());
                    
                    this.label_lixeira_atual.setText("ID: " + this.proxima_lixeira_json.getInt("id_lixeira") + " LAT: "
                            + this.proxima_lixeira_json.getInt("latitude_lixeira")
                            + "º LON: " + this.proxima_lixeira_json.getInt("longitude_lixeira"));

                    this.label_proxima_lixeira.setText("N/D");
                  

                    //adiciono ao json da lixeira coletada
                    System.out.println("PEGUEI LIXEIRA COLETADA");

                    this.id_lixeira_coletada = this.proxima_lixeira_json.getInt("id_lixeira");
                    this.quantidade_lixo_coletado = value;
                    System.out.println("=======================\n" + this.id_lixeira_coletada + "  " + this.quantidade_lixo_coletado);

                } else {
                    this.label_aviso.setForeground(Color.red);
                    this.label_aviso.setText("Pouca quantidade disponível no caminhão - descarregue na estação");
                    this.label_aviso.setVisible(true);

                    //libera opçao de descarregamento
                    this.btn_descarregar.setVisible(true);
                    this.btn_descarregar.setEnabled(true);
                }
            } else {/*
                this.label_aviso.setForeground(Color.red);
                this.label_aviso.setText("Não é possível fazer a coleta, lixeira vazia.");
                this.label_aviso.setVisible(true);*/
            }
        } catch (JSONException ex) {
            Logger.getLogger(MainViewCaminhao.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btn_coletarActionPerformed

    /**
     * EVENTO AO CLICAR O BOTAO DE CONFIRMAR A CAPACIDADE MAXIMA
     * @param evt 
     */
    private void btn_confirma_cap_maxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirma_cap_maxActionPerformed

        int i = (int) this.spinner_capacidade_maxima.getValue();

        this.label_txt_cap_atual.setForeground(Color.white);
        this.label_txt_cap_disp.setForeground(Color.white);
        this.label_txt_cap_max.setForeground(Color.white);
        this.label_capacidade_atual.setForeground(Color.white);
        this.label_capacidade_disponivel.setForeground(Color.white);
        this.label_capacidade_maxima.setForeground(Color.white);
        this.label_proxima_lixeira.setForeground(Color.white);
        this.label_lixeira_atual.setForeground(Color.white);
        // this.btn_coletar.setEnabled(true);

        this.label_add_cap_maxima.setVisible(false);
        this.btn_confirma_cap_max.setVisible(false);
        this.spinner_capacidade_maxima.setVisible(false);
        this.label_minimo_capacidade_caminhao.setVisible(false);

        double a = (double) i;
        this.label_capacidade_maxima.setText("" + a);
        this.label_capacidade_disponivel.setText("" + a);
        this.capacidade_maxima = a;
        this.capacidade_disponivel = a;


    }//GEN-LAST:event_btn_confirma_cap_maxActionPerformed

    private void btn_restartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_restartActionPerformed
        this.restart = true;

        this.label_lixeira_atual.setText("N/D");
        this.label_proxima_lixeira.setText("N/D");
        this.label_aviso_sup.setVisible(false);
        this.btn_restart.setVisible(false);
    }//GEN-LAST:event_btn_restartActionPerformed

    private void label_add_cap_maximaAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_label_add_cap_maximaAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_label_add_cap_maximaAncestorAdded

    private void btn_descarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_descarregarActionPerformed
        this.label_aviso.setVisible(false);

        this.btn_descarregar.setVisible(false);
        //this.label_capacidade_atual.setText("" + this.capacidade_atual);
        //this.label_capacidade_disponivel.setText("" + this.capacidade_disponivel);

        Thread t = new Thread() {
            @Override
            public void run() {
                label_aviso_descarregar.setVisible(true);

                double capAtual = capacidade_atual;
                double capDisp = capacidade_disponivel;
                double div = capAtual / 100;

                try {
                    for (int i = 0; i <= 100; i += 2) {
                        double j = i;
                        Thread.sleep(60);

                        label_aviso_descarregar.setText("Descarregando... " + i + "%");
                        label_capacidade_atual.setText(String.valueOf((capAtual - (div * i))).format("%.2f", (capAtual - (div * j))));

                        //label_capacidade_disponivel.setText(String.valueOf((capDisp + (div * i))).format("%.2f", (capDisp + (div * j))));
                    }
                    Thread.sleep(70);
                    label_aviso_descarregar.setVisible(false);
                    capacidade_atual = 0.0;
                    capacidade_disponivel = capacidade_maxima;
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainViewCaminhao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        t.start();

    }//GEN-LAST:event_btn_descarregarActionPerformed

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
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainViewCaminhao();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_coletar;
    private javax.swing.JButton btn_confirma_cap_max;
    private javax.swing.JButton btn_descarregar;
    private javax.swing.JButton btn_restart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel label_add_cap_maxima;
    private javax.swing.JLabel label_aviso;
    private javax.swing.JLabel label_aviso_descarregar;
    private javax.swing.JLabel label_aviso_sup;
    private javax.swing.JLabel label_capacidade_atual;
    private javax.swing.JLabel label_capacidade_disponivel;
    private javax.swing.JLabel label_capacidade_maxima;
    private javax.swing.JLabel label_info_server;
    private javax.swing.JLabel label_lixeira_atual;
    private javax.swing.JLabel label_minimo_capacidade_caminhao;
    private javax.swing.JLabel label_proxima_lixeira;
    private javax.swing.JLabel label_txt_cap_atual;
    private javax.swing.JLabel label_txt_cap_disp;
    private javax.swing.JLabel label_txt_cap_max;
    private javax.swing.JProgressBar progress_coletando;
    private javax.swing.JSpinner spinner_capacidade_maxima;
    // End of variables declaration//GEN-END:variables
}
