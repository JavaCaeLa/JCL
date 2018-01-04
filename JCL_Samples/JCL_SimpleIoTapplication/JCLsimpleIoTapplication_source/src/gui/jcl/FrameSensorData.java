package gui.jcl;

import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.datatype.Sensor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author lucfc
 */
public class FrameSensorData extends javax.swing.JFrame {
     
   public FrameSensorData(){
       initComponents();
   }
   
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        jList1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
         if(sensingDataFrame == null){
          // sensing.clear();
            sensingDataFrame = new FrameSensingData();
            String selectData = jList1.getSelectedValue();
            List<JCL_Sensor> sensingData = this.sensing.get(selectData);
          //  if(sensingData != null){
                sensingDataFrame.atualiza(selectData, sensingData);      
                sensingDataFrame.setVisible(true);
           // }     
        }else{
            //sensing =  new HashMap<>();
           // sensing.clear();
            sensingDataFrame = new FrameSensingData();
            String selectData = jList1.getSelectedValue();
            List<JCL_Sensor> sensingData = this.sensing.get(device+selectData);
           // if(sensingData != null){
                sensingDataFrame.atualiza(selectData, sensingData);      
                sensingDataFrame.setVisible(true);
           // }           
         }
    }//GEN-LAST:event_jList1MouseClicked

    void atualiza(JCLGui frame,List<Sensor> sensorList, String nameTitle, Map<String, List<JCL_Sensor>> sensing){
        
        setTitle(nameTitle + " Sensors");
        
        this.devicesFrame = frame;
        this.sensing.clear();
        this.device = nameTitle;
        this.sensing = new HashMap<>(sensing);
        int tam = sensorList.size();
        String[] strings = new String[tam];
        int j =0;
        Iterator it = sensorList.iterator();
        while(it.hasNext()){
                Entry<String, String> entry = (Entry)it.next();
                strings[j] = entry.getKey(); 
                j++;
	}
            
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        
//        iot.PacuHPC.destroy();
        
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrameSensorData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameSensorData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameSensorData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameSensorData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameSensorData().setVisible(true);
            }
        });
    }

    private FrameSensingData sensingDataFrame = new FrameSensingData();
    private JCLGui devicesFrame;
    private Map<String, List<JCL_Sensor>> sensing =  new HashMap<>(); 
    private String device;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
