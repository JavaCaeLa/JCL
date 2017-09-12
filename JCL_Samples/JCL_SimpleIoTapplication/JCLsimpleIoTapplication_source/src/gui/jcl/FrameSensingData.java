package gui.jcl;

import interfaces.kernel.JCL_Sensor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameSensingData extends javax.swing.JFrame {
   
    public FrameSensingData() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        jList1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        String selectData = jList1.getSelectedValue();
        JCL_Sensor photo = datas.get(selectData);
        photo.showData();
    }//GEN-LAST:event_jList1MouseClicked

    boolean atualiza(String selectData, List<JCL_Sensor> sensingData){
        setTitle(selectData + " Data");  
       // if(sensingData.size() == 0){
          //  System.out.println("NO SENSING");
        //}
        //else{
        //datas.clear();
            int tam = sensingData.size();

            JCL_Sensor[] data = new JCL_Sensor[tam];
            String[] strings = new String[tam]; 
            int j =0;

            String type = sensingData.get(0).getType();

            if("image or audio".equals(type)){
                String namePhoto = "sensing ";
                for(int i = 0; i< tam; i++){     
                      datas.put(namePhoto + i, sensingData.get(i));
                      strings[i] = namePhoto+i; 
                      jList1.setModel(new javax.swing.AbstractListModel<String>() {
                                public int getSize() { return strings.length; }
                                public String getElementAt(int i) { return strings[i]; }
                          });	
                }
            }
            else {
                for (int i = 0; i <tam ;i++){
                    data[i] =  sensingData.get(i);			
                }

                for(int i =0; i<tam;i++){
                    strings[i] = (String) data[i].toString();
                }
                jList1.setModel(new javax.swing.AbstractListModel<String>() {
                       public int getSize() { return strings.length; }
                       public String getElementAt(int i) { return strings[i]; }
                   });            
            }
       // }
        return false;       
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
            java.util.logging.Logger.getLogger(FrameSensingData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrameSensingData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrameSensingData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrameSensingData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameSensingData().setVisible(true);
            }
        });
    }

    private  Map<String,JCL_Sensor> datas = new HashMap<String,JCL_Sensor>();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
