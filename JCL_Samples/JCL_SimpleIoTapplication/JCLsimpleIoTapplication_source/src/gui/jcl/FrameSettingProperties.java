package gui.jcl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class FrameSettingProperties extends javax.swing.JFrame {
        
    public FrameSettingProperties(){        
        initComponents();
        setTitle("JCL IOT");
        readHPCProperties();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("RUN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("serverMainAdd");

        jLabel2.setText("serverMainPort");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(131, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(117, 117, 117))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void readHPCProperties(){
        try{
            String rootPath = "..";
            Properties properties = new Properties();
            String IP = "";
            String Port = "";
            File file = new File(rootPath + "/jcl_conf/config.properties");
            if(file.exists()){
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));
                jTextField1.setText(properties.getProperty("serverMainAdd"));
                jTextField2.setText(properties.getProperty("serverMainPort"));              
            }           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:       
        String ip = null;
        ip = jTextField1.getText();
        String port = null;
        port = jTextField2.getText();
        writeHPCProperties(ip,port);
        frameDevices = new JCLGui();
        frameDevices.setVisible(true); 
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void writeHPCProperties(String serverIp, String serverPort) {
        try {

            String rootPath = "..";

            Properties properties = new Properties();
            File file = new File(rootPath + "/jcl_conf/config.properties");

            //caso exista properties, da um load
            if (file.exists())
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));

                //caso não exita, cria um com os campos padrão
            else {
                File mediaStorageDir = new File(rootPath + "/jcl_conf/");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }
                String pro = "###################################################\n" +
                        "#               JCL config file                   #\n" +
                        "###################################################\n" +
                        "# Config JCL type\n" +
                        "# true => Pacu version\n" +
                        "# false => Lambari version\n" +
                        "distOrParell = true\n" +
                        "serverMainPort = 6969\n" +
                        "superPeerMainPort = 6868\n" +
                        "routerMainPort = 7070\n" +
                        "serverMainAdd = localhost\n" +
                        "hostPort = 5151\n" +
                        "nic = \n" +
                        "simpleServerPort = 4949\n" +
                        "timeOut = 5000\n" +
                        "byteBuffer = 10000000\n" +
                        "routerLink = 5\n" +
                        "enablePBA = false\n" +
                        "PBAsize=50\n" +
                        "delta=0\n" +
                        "PGTerm = 10\n" +
                        "twoStep = false\n" +
                        "useCore=100\n" +
                        "deviceID = Host1\n" +
                        "enableDinamicUp = false\n" +
                        "findServerTimeOut = 1000\n" +
                        "findHostTimeOut = 1000\n" +
                        "enableFaultTolerance = false\n" +
                        "verbose = true\n"+
                        "encryption = false\n"+
                        "deviceType = 3\n";
                PrintWriter writer = new PrintWriter(rootPath + "/jcl_conf/config.properties", "UTF-8");
                writer.print(pro);
                writer.close();
                properties.load(new FileInputStream(rootPath + "/jcl_conf/config.properties"));

            }

            //seta as configurações setadas


            properties.setProperty("serverMainPort", serverPort.trim());
            properties.setProperty("serverMainAdd", serverIp.trim());
            
            
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "");
            fileOut.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameSettingProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameSettingProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameSettingProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameSettingProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameSettingProperties().setVisible(true);
            }
        });
    }

    private JCLGui frameDevices;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
