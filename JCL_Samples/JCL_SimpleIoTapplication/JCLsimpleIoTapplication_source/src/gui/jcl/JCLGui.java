package gui.jcl;

import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.datatype.Device;
import interfaces.kernel.datatype.Sensor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JCLGui extends javax.swing.JFrame {
   
    public JCLGui() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IOT JCL Devices");

        jList1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setAlignmentX(1.0F);
        jList1.setAlignmentY(1.0F);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Update");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:  
      //  System.out.println("entrou");
        iot = JCL_IoTFacadeImpl.getInstance();
        devices.clear();
        sensores.clear();
        sensing.clear();
        devices = iot.getIoTDevices();    
        for(Device device:devices){
        List<Sensor> sens = iot.getSensors(device); 
        sensores.put(device.getValue(), sens);              
            for(Sensor sensor:sens){
               // if(iot.getSensingData(device, sensor)!= null){
                Map<Integer, JCL_Sensor> sensing1 = iot.getSensingData(device, sensor);      
                List<JCL_Sensor> sens1 = new ArrayList<>();
                if(sensing1 != null){
                    sens1.addAll(sensing1.values());
                }     
                sensing.put(device.getValue() +  sensor.getKey(), sens1); 
              //  }
            }
        }
         
        int tam = devices.size();
        String[] name = new String[tam];
        String[] key = new String[tam];
        int j =0;
        Iterator it = devices.iterator();
        while(it.hasNext()){
                Entry<String, String> entry = (Entry)it.next();
                key[j] = entry.getKey();   
                name[j] = entry.getValue(); 
                j++;
	}
        
        String[] strings = new String [tam];
        
        for(int i = 0; i< tam; i++){
            strings[i] = key[i] + ":" + name[i];
        }
        
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        
        //iot.PacuHPC.destroy();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:       
        if(sensorFrame == null){
            sensorFrame = new FrameSensorData();
            String selectData = jList1.getSelectedValue();
            String[] aux = selectData.split(":");            
            List<Sensor> sensorList = sensores.get(aux[1]);
            sensorFrame.atualiza(this,sensorList,aux[1],sensing);  
            sensorFrame.setVisible(true);
        }else{
            sensorFrame = new FrameSensorData();
            String selectData = jList1.getSelectedValue();
            String[] aux = selectData.split(":");
            List<Sensor> sensoresList = sensores.get(aux[1]);

                     sensorFrame.atualiza(this,sensoresList,aux[1],sensing);  

           sensorFrame.setVisible(true);
        }
    }//GEN-LAST:event_jList1MouseClicked

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JCLGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JCLGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JCLGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JCLGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JCLGui().setVisible(true);
            }
        });
    }
    
    private FrameSensorData sensorFrame;
    private List<Device> devices = new ArrayList<>();     
    private Map<String, List<Sensor>> sensores = new HashMap<>();
    private Map<String, List<JCL_Sensor>> sensing = new HashMap<>();
    private JCL_IoTfacade iot = JCL_IoTFacadeImpl.getInstance();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
