package implementations.dm_kernel.GUI;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Insets;
import java.util.HashMap;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

@SuppressWarnings("serial")
public class PanelHosts extends JPanel {
	private HashMap<String, JLabel> labels;
	private HashMap<String, JTextField> txtFields;
	private String[] fields =  {"distOrParell","serverMainPort","superPeerMainPort","routerMainPort","serverMainAdd","hostPort","nic",
            "simpleServerPort","timeOut","byteBuffer","routerLink","enablePBA","PBAsize","delta","PGTerm","twoStep",
            "useCore","deviceID","enableDinamicUp","findServerTimeOut","findHostTimeOut","enableFaultTolerance","verbose"};
	private String[] defaults = {"true","6969","7070","7070","127.0.0.1","5555",null,"4949","5000","5242880","5","false","50","0","10","true",
								"100","Host1","false","1000","1000","false","true"};
	
	public PanelHosts() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 2);
        
		labels = new HashMap<>();
		txtFields = new HashMap<>();
		for(int i=0;i<fields.length;i++){
			JLabel label = new JLabel(fields[i],JLabel.RIGHT);
			label.setVisible(true);
			gc.gridx = 0;
			gc.gridy = i+1;
			add(label,gc);
			labels.put(fields[i], label);
		}
		
		int cols = 20;
		for(int i=0;i<fields.length;i++){
			JTextField txtField = new JTextField(cols);
			txtField.setText(defaults[i]);
			txtField.setVisible(true);
			gc.gridx = 1;
			gc.gridy = i+1;
			add(txtField,gc);
			txtFields.put(fields[i], txtField);
		}
	}
	
	public String[] getFields(){
		return fields;
	}
	
	public HashMap<String,JLabel> getLabels(){
		return labels;
	}
	
	public HashMap<String,JTextField> getTxtfields(){
		return txtFields;
	}
}
