package implementations.dm_kernel.GUI;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


import implementations.dm_kernel.GUI.boardEnums.AndroidSensors;
import implementations.dm_kernel.GUI.boardEnums.ArduinoMEGAAnalog;
import implementations.dm_kernel.GUI.boardEnums.ArduinoMEGADig;
import implementations.dm_kernel.GUI.boardEnums.BeagleboneBlackRevBAnalog;
import implementations.dm_kernel.GUI.boardEnums.BeagleboneBlackRevBDig;
import implementations.dm_kernel.GUI.boardEnums.Boards;
import implementations.dm_kernel.GUI.boardEnums.GalileoGen2Analog;
import implementations.dm_kernel.GUI.boardEnums.GalileoGen2Dig;
import implementations.dm_kernel.GUI.boardEnums.RaspPi2BAnalog;
import implementations.dm_kernel.GUI.boardEnums.RaspPi2BDig;
import implementations.dm_kernel.IoTuser.JCL_IoTFacadeImpl;
import implementations.dm_kernel.host.SensorAcq;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import implementations.util.IoT.JCL_IoT_SensingModelRetriever;
import interfaces.kernel.JCL_IoT_Sensing_Model;
import interfaces.kernel.JCL_IoTfacade;
import interfaces.kernel.JCL_facade;
import java.awt.Font;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox 		comboBoardIP;
	private JTextField 		txtBoardName;

	private DefaultComboBoxModel modelDigital;
	private DefaultComboBoxModel modelAnalog;
	private DefaultComboBoxModel modelAndroid;

	private JLabel 			labelBoarding;
	private JComboBox 		comboBoardSelection;
	private JPanel 			panelAux;
	private JComboBox 		pino;
	private JComboBox 		tipoSensor;
	private JComboBox 		tipoSensorAndroid;
	private JButton 		addSensor;
	private JButton 		clearAll; 
	private JButton 		deleteSensor;
	private JScrollPane 	scrollAddedSensors;
	private JTextPane 		txtpnAddedSensors;

	private JTabbedPane 	tabbedPane;

	private JTextField 		txtSensorName;
	private JComboBox 		comboBoxType;
	private JSpinner 		spinnerDelay;
	private JComboBox 		comboBoxDir;
	private JSpinner 		spinnerSize;
	private JButton 		btnSave;

	private JScrollPane 	scrollPaneHosts;
	private PanelHosts 		hostsForm;

	private ArrayList<SensorAcq> 	sensorsFromUser;
	private Vector<String> 		selectedPins;
	private JCL_IoTfacade 		jclIoT;
	private JCL_facade 			jclHost;

	public Panel(){
		setLayout(null);
		
		labelBoarding = new JLabel("JCL Boarding");
		labelBoarding.setFont(new Font("Dialog", Font.BOLD, 14));
		labelBoarding.setBounds(200, 32, 150, 20);
		add(labelBoarding);
		
		JLabel lblBoardName = new JLabel("Device Name:");
		lblBoardName.setBounds(41, 108, 95, 15);
		add(lblBoardName);

		txtBoardName = new JTextField();
		txtBoardName.setToolTipText("Type the name you want for your device");
		txtBoardName.setBounds(141, 103, 166, 25);
		add(txtBoardName);
		txtBoardName.setColumns(10);
		txtBoardName.setDocument(new FixedSizeDocument(30));

		tipoSensorAndroid = new JComboBox();
		tipoSensorAndroid.setBounds(41, 208, 231, 24);
		tipoSensorAndroid.setToolTipText("Select the sensor you want to configure on the smartphone");
		tipoSensorAndroid.setVisible(true);

		hostsForm = new PanelHosts();

		scrollPaneHosts = new JScrollPane();
		scrollPaneHosts.setBounds(51, 277, 421, 354);
		add(scrollPaneHosts);
		scrollPaneHosts.setVisible(false);
		scrollPaneHosts.setViewportView(hostsForm);

		pino = new JComboBox();
		pino.setToolTipText("Choose the pin you're using on the board");
		pino.setBounds(209, 208, 75, 24);

		tipoSensor = new JComboBox();
		tipoSensor.setToolTipText("Choose the type of the sensor");
		tipoSensor.setModel(new DefaultComboBoxModel(new String[] {"Type of sensor", "Digital", "Analog"}));
		tipoSensor.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(tipoSensor.getSelectedIndex() == 1)
					pino.setModel(modelDigital);
				else if (tipoSensor.getSelectedIndex() == 2)
					pino.setModel(modelAnalog);
			}
		});
		tipoSensor.setBounds(54, 208, 143, 24);

		comboBoardSelection = new JComboBox();
		comboBoardSelection.setToolTipText("Select the model of the device your going to configure");
		comboBoardSelection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				clearTabs();
				if (comboBoardSelection.getSelectedIndex() == 0){
					setBoardElementsVisible(false);
					scrollAddedSensors.setVisible(false);
					tabbedPane.removeAll();
				}else{
					pino.setModel(new DefaultComboBoxModel(new String[]{" "}));
					tipoSensor.setSelectedIndex(0);
					scrollAddedSensors.setVisible(true);
					if(tipoSensorAndroid.isVisible()){
						tipoSensorAndroid.setVisible(false);
						repaint();
						revalidate();
					}
					if(scrollPaneHosts.isVisible()){
						scrollPaneHosts.setVisible(false);
						repaint();
						revalidate();
					}
					switch (comboBoardSelection.getSelectedIndex()) {
					case 1:
						modelDigital = new DefaultComboBoxModel(GalileoGen2Dig.values());
						modelAnalog = new DefaultComboBoxModel(GalileoGen2Analog.values());
						setBoardElementsVisible(true);
						if(tipoSensorAndroid.isVisible()){
							tipoSensorAndroid.setVisible(false);
							repaint();
							revalidate();
						}
						if(scrollPaneHosts.isVisible()){
							scrollPaneHosts.setVisible(false);
							repaint();
							revalidate();
						}
						tabbedPane.setVisible(true);
						break;
					case 2:
						modelDigital = new DefaultComboBoxModel(ArduinoMEGADig.values());
						modelAnalog = new DefaultComboBoxModel(ArduinoMEGAAnalog.values());
						setBoardElementsVisible(true);
						if(tipoSensorAndroid.isVisible()){
							tipoSensorAndroid.setVisible(false);
							repaint();
							revalidate();
						}
						tabbedPane.setVisible(true);
						if(scrollPaneHosts.isVisible()){
							scrollPaneHosts.setVisible(false);
							repaint();
							revalidate();
						}
						tabbedPane.setVisible(true);
						break;
					case 3:
						modelDigital = new DefaultComboBoxModel(RaspPi2BDig.values());
						modelAnalog = new DefaultComboBoxModel(RaspPi2BAnalog.values());
						setBoardElementsVisible(true);
						if(tipoSensorAndroid.isVisible()){
							tipoSensorAndroid.setVisible(false);
							repaint();
							revalidate();
						}
						if(scrollPaneHosts.isVisible()){
							scrollPaneHosts.setVisible(false);
							repaint();
							revalidate();
						}
						tabbedPane.setVisible(true);
						break;
					case 4:
						modelAndroid = new DefaultComboBoxModel(AndroidSensors.values());
						tipoSensorAndroid.setModel(modelAndroid);
						tipoSensorAndroid.setVisible(true);
						add(tipoSensorAndroid);
						repaint();
						revalidate();
						if(pino.isVisible()){
							setBoardElementsVisible(false);
							repaint();
							revalidate();
						}
						addSensor.setVisible(true);
						deleteSensor.setVisible(true);
						tabbedPane.setVisible(true);
						clearAll.setVisible(true);
						repaint();
						revalidate();

						break;
					case 5:
						scrollPaneHosts.setVisible(true);
						scrollAddedSensors.setVisible(false);
						if(tipoSensorAndroid.isVisible()){
							tipoSensorAndroid.setVisible(false);
						}
						if(pino.isVisible()){
							setBoardElementsVisible(false);
						}
						addSensor.setVisible(false);
						deleteSensor.setVisible(false);
						tabbedPane.setVisible(false);
						clearAll.setVisible(false);
						repaint();
						revalidate();
						break;
					case 6:
						modelDigital = new DefaultComboBoxModel(BeagleboneBlackRevBDig.values());
						modelAnalog = new DefaultComboBoxModel(BeagleboneBlackRevBAnalog.values());
						setBoardElementsVisible(true);
						if(tipoSensorAndroid.isVisible()){
							tipoSensorAndroid.setVisible(false);
							repaint();
							revalidate();
						}
						if(scrollPaneHosts.isVisible()){
							scrollPaneHosts.setVisible(false);
							repaint();
							revalidate();
						}
						tabbedPane.setVisible(true);
						break;
					case 7:

						break;
					case 9:

						break;
					case 10:

						break;

					default:
						break;
					}
				}
			}
		});
		comboBoardSelection.setModel(new DefaultComboBoxModel(Boards.values()));
		comboBoardSelection.setBounds(41, 149, 267, 24);
		add(comboBoardSelection);

		clearAll = new JButton("");
		clearAll.setToolTipText("Click to remove all sensors you already configured");
		try{
			Icon ic = new ImageIcon("images/clear.png");
		    clearAll.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		clearAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearTabs();
				txtpnAddedSensors.setText("Added sensors:\n");
				repaint();
				revalidate();
			}
		});
		clearAll.setBounds(385, 208, 44, 25);
		add(clearAll);
		clearAll.setVisible(false);

		sensorsFromUser = new ArrayList<>();

		/*********************************/		
		selectedPins = new Vector<>();
		setLayout(null);

		scrollAddedSensors = new JScrollPane();
		scrollAddedSensors.setBounds(99, 468, 315, 163);
		scrollAddedSensors.setVisible(false);
		add(scrollAddedSensors);
		txtpnAddedSensors = new JTextPane();
		txtpnAddedSensors.setText("Added Sensors:\n");

		addSensor = new JButton("");
		addSensor.setToolTipText("Click to open the configuration options for the sensor");
		try{
			Icon ic = new ImageIcon("images/add.png");
		    addSensor.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		addSensor.setBounds(290, 208, 44, 25);
		addSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tipoSensor.getSelectedIndex() == 0 && !tipoSensorAndroid.isVisible()){
					JOptionPane.showMessageDialog(null, "Add at least one sensor.");
				}
				else if(tipoSensorAndroid.isVisible() && !selectedPins.contains(tipoSensorAndroid.getSelectedItem().toString())){
					panelAux = createAndroidPanel();
					tabbedPane.addTab(tipoSensorAndroid.getSelectedItem().toString(), null, panelAux, null);
					selectedPins.add(tipoSensorAndroid.getSelectedItem().toString());

					btnSave.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(comboBoardSelection.getSelectedIndex() == 4){
								String s = new String();
								int value = -1;
								s = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
								AndroidSensors a;
								a = (AndroidSensors) AndroidSensors.valueOf(s);
								value = a.getValue();
								saveSensorAndroid(panelAux, value);
							}
						}
					});
				}
				else if(pino.isVisible() && !selectedPins.contains(pino.getSelectedItem().toString())){
					panelAux = new JPanel();
					if(pino.isVisible()){
						panelAux = createIoTPanel();
						tabbedPane.addTab(pino.getSelectedItem().toString(), null, panelAux, null);
						selectedPins.add(pino.getSelectedItem().toString());	
					}

					btnSave.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if ( txtSensorName.getText().trim().equals("") ){
								JOptionPane.showMessageDialog(null, "You must specify the sensor name", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							int brdSelection = comboBoardSelection.getSelectedIndex();
							int value = retrivePinValue(brdSelection,null);
							saveSensorBoards(panelAux, value, tipoSensor.getSelectedIndex());
						}
					});
				}
				else{
					JOptionPane.showMessageDialog(null, "Sensor already added");
				}
			}
		});

		deleteSensor = new JButton("");
		deleteSensor.setToolTipText("Click to remove a added sensor from your list");
		try{
			Icon ic = new ImageIcon("images/remove.png");
		    deleteSensor.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		deleteSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i, n = tabbedPane.getTabCount();
				if (n != 0) {
					for (i = 0; i < n; i++) {
						String s = new String();
						s = tabbedPane.getTitleAt(i);
						if (!tipoSensorAndroid.isVisible() && s == pino.getSelectedItem().toString()) {
							tabbedPane.removeTabAt(i);
							selectedPins.remove(s.toString());
							deleteSensorFromArray(s);
							break;
						}
						else if(s == pino.getSelectedItem().toString()){
							tabbedPane.removeTabAt(i);
							selectedPins.remove(s.toString());
							deleteSensorFromArray(s);
							break;
						}
					}
					if (i == n) {
						JOptionPane.showMessageDialog(null, "Sensor not yet added.");
					} 
				}else{
					JOptionPane.showMessageDialog(null, "Add at least one sensor.");
				}
			}

			private void deleteSensorFromArray(String s) {
				int brdSelection = comboBoardSelection.getSelectedIndex();
				int value = retrivePinValue(brdSelection, s);

				for (int j=0; j<sensorsFromUser.size();j++){
					if(sensorsFromUser.get(j).getPin() == value && sensorsFromUser.get(j).getDir() == tipoSensor.getSelectedIndex()){
						sensorsFromUser.remove(j);
						break;
					}
				}
			}
		});
		deleteSensor.setBounds(338, 208, 44, 25);

		add(tipoSensor);
		add(pino);
		add(addSensor);
		add(deleteSensor);

		pino.setVisible(false);
		tipoSensor.setVisible(false);
		addSensor.setVisible(false);
		deleteSensor.setVisible(false);
		/*********************************/

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setVisible(true);
		tabbedPane.setBounds(41, 244, 459, 212);
		add(tabbedPane);

		JLabel lblBoardIp = new JLabel("Board IP:");
		lblBoardIp.setBounds(41, 73, 64, 15);
		add(lblBoardIp);

		comboBoardIP = new JComboBox(new DefaultComboBoxModel(new String [] {"Choose the device IP"}));
		comboBoardIP.setToolTipText("From the devices registred, choose the IP address you want to configure");
		comboBoardIP.setBounds(141, 68, 250, 24);		
		comboBoardIP.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				comboBoardIP.setModel(new DefaultComboBoxModel(getHostsIP()));
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		comboBoardIP.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = comboBoardIP.getSelectedItem().toString().split("- ")[1];
				Object obj = value;
				switch (value){
				case "Intel Galileo Gen 2":
					comboBoardSelection.setSelectedIndex(1);
					break;
				case "Arduino MEGA":
					comboBoardSelection.setSelectedIndex(2);
					break;
				case "Raspberry Pi Model B+ Rev 1":
					comboBoardSelection.setSelectedIndex(3);
					break;
				case "Android":
					comboBoardSelection.setSelectedIndex(4);
					break;
				case "Beaglebone Black":
					comboBoardSelection.setSelectedIndex(6);
					break;
				}
			}
		});
			

		add(comboBoardIP);

		sensorsFromUser = new ArrayList<>();

		JButton btnSend = new JButton("SEND");
		btnSend.setToolTipText("After configuring a device, click here to apply the modifications");
		try{
			Icon ic = new ImageIcon("images/ok.png");
		    btnSend.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( txtBoardName.getText().trim().equals("") ){
					JOptionPane.showMessageDialog(null, "You must specify the board name", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if((sensorsFromUser.isEmpty() || comboBoardSelection.getSelectedIndex() == 0) && comboBoardSelection.getSelectedIndex() != 5){
					JOptionPane.showMessageDialog(null, "No sensors to send!");
				}
				int selected = comboBoardSelection.getSelectedIndex();
				if (selected !=0 && selected !=5 && sendMetadataIoT()){
					sensorsFromUser.clear();
					//JOptionPane.showMessageDialog(null, "Board Configured");
				}
				else if (selected == 5 && sendMetadataHPC()){
					//JOptionPane.showMessageDialog(null, "Hosts Configured");
					clearTabs();
				}
			}
		});

		JButton btnCancel = new JButton("CANCEL");
		btnCancel.setToolTipText("Click here to close the program");
		try{
			Icon ic = new ImageIcon("images/close.png");
		    btnCancel.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setBounds(260, 643, 117, 25);
		add(btnCancel);
		btnSend.setBounds(138, 643, 117, 25);
		add(btnSend);

		/*try {
			BufferedImage myPicture = ImageIO.read(new File("java.jpg"));
			JLabel icon = new JLabel(new ImageIcon(myPicture));
			icon.setBounds(425, 19, 70, 70);
			add(icon);
			
			JLabel lblJclDeviceConfiguration = new JLabel("JCL Device Configuration Tool");
			lblJclDeviceConfiguration.setFont(new Font("FreeSans", Font.PLAIN, 20));
			lblJclDeviceConfiguration.setBounds(129, 12, 268, 24);
			add(lblJclDeviceConfiguration);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}

	public void saveSensorAndroid(JPanel panelAux, int sensorID) {
		SensorAcq s = new SensorAcq();
		//		ArrayList<JTextField> c = new ArrayList<JTextField>();
		//
		//		c.add((JTextField) panelAux.getComponent(1));
		//		c.add((JTextField) panelAux.getComponent(3));
		//		c.add((JTextField) panelAux.getComponent(5));

		//String alias = ((JTextField) panelAux.getComponent(1)).toString();
		long delay = Long.parseLong(((JSpinner) panelAux.getComponent(3)).getValue().toString());
		int size = Integer.parseInt(((JSpinner) panelAux.getComponent(5)).getValue().toString());;

		s.setAlias(/*alias*/tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
		s.setDelay(delay);
		s.setSize(size);
		s.setPin(sensorID);
		sensorsFromUser.add(s);
		addSensorToList(sensorsFromUser);
	}

	public void saveSensorBoards(JPanel panel, int port, int analogDig){
		SensorAcq s = new SensorAcq();
		//		ArrayList<JTextField> c = new ArrayList<JTextField>();
		//
		//		c.add((JTextField) panel.getComponent(1));
		//		c.add((JTextField) panel.getComponent(3));
		//		c.add((JTextField) panel.getComponent(5));
		//		c.add((JTextField) panel.getComponent(7));

		String alias = ((JTextField)panel.getComponent(1)).getText();
		char dir = ((JComboBox) panel.getComponent(3)).getSelectedItem().toString().toLowerCase().charAt(0);
		int type = ((JComboBox) panel.getComponent(5)).getSelectedIndex();
		long delay = Long.parseLong(((JSpinner) panelAux.getComponent(7)).getValue().toString());
		int size = Integer.parseInt(((JSpinner) panelAux.getComponent(9)).getValue().toString());

		s.setAlias(alias);
		s.setDelay(delay);
		s.setDir(dir);
		s.setSize(size);
		s.setType(type);
		s.setPin(port);
		/*if((comboBoardSelection.getSelectedIndex() == 1 || comboBoardSelection.getSelectedIndex() == 2) && port > 13) 
			s.setPin((port-14));
		else if(comboBoardSelection.getSelectedIndex() == 6 && port > 46){
			s.setPin(port-46);
		}
		else 
			s.setPin(port);*/
		if(comboBoardSelection.getSelectedIndex() != 2){
			JCL_IoT_Sensing_Model sensingModel = JCL_IoT_SensingModelRetriever.getSensingModel(comboBoardSelection.getSelectedItem().toString());
			if(sensingModel != null && sensingModel.validPin(s.getPin())){
				sensorsFromUser.add(s);
				addSensorToList(sensorsFromUser);
			}
			else if (sensingModel != null)
				JOptionPane.showMessageDialog(null, "Invalid Pin on the Board. Failed to save", "saveSensor", JOptionPane.ERROR_MESSAGE);
		}
		else{
			sensorsFromUser.add(s);
			addSensorToList(sensorsFromUser);
		}

	}

	public boolean sendMetadataHPC() {
		jclHost = JCL_FacadeImpl.getInstance();
		Map<String, String> meta = generateMetadataHosts();
		boolean deviceExists = false;
		List<Entry<String, String>> devices = jclHost.getDevices();
		for (Entry<String, String> d:devices){
			Map<String, String> currentMeta = jclHost.getDeviceMetadata(d);
			System.out.println(currentMeta.get("IP") + " = " + meta.get("IP"));
			if ( currentMeta.containsKey("IP") && currentMeta.get("IP").equals(meta.get("IP")) ){
				deviceExists = true;
				if ( jclHost.setDeviceConfig(d, meta) ){
					JOptionPane.showMessageDialog(null, "Metadata successfully sent!", "SetMetadata", JOptionPane.INFORMATION_MESSAGE);
					return true;
				}
				else
					JOptionPane.showMessageDialog(null, "There was an error. Please verify the Metadata!", "SetMetadata", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}

		if (!deviceExists)
			JOptionPane.showMessageDialog(null, "There is no device with the specified IP in the cluster!", "SetMetadata", JOptionPane.ERROR_MESSAGE);
		 return false;
	}

	public boolean sendMetadataIoT(){
		jclIoT = JCL_IoTFacadeImpl.getInstance();
		Map<String, String> meta = generateMetadataIoT();		
		String ip = comboBoardIP.getSelectedItem().toString().split(" -")[0];
		boolean deviceExists = false;
		List<Entry<String, String>> devices = jclIoT.getIoTDevices();
		for (Entry<String, String> d:devices){
			Map<String, String> currentMeta = jclIoT.getIoTDeviceMetadata(d);
			if (currentMeta.containsKey("IP") && currentMeta.get("IP").equals(ip)){
				deviceExists = true;
				if ( jclIoT.setIoTDeviceMetadata(d, meta) ){
					JOptionPane.showMessageDialog(null, "Metadata successfully sent!", "SetMetadata", JOptionPane.INFORMATION_MESSAGE);
					return true;
				}
				else
					JOptionPane.showMessageDialog(null, "There was an error. Please verify the Metadata!", "SetMetadata", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}

		if (!deviceExists)
			JOptionPane.showMessageDialog(null, "There is no device with the specified IP in the cluster!", "SetMetadata", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	public Map<String, String> generateMetadataHosts(){
		Map<String, String> meta = new HashMap<>();
		String[] s = hostsForm.getFields();
		for(int i=0;i<s.length;i++){
			meta.put(s[i], hostsForm.getTxtfields().get(s[i]).getText());
		}
		meta.put("IP", comboBoardIP.getSelectedItem().toString().split(" - ")[0]);
		return meta;
	}

	public Map<String, String> generateMetadataIoT(){
		jclHost = implementations.sm_kernel.JCL_FacadeImpl.getInstance();
		jclIoT = JCL_IoTFacadeImpl.getInstance();
		Map<String, String> meta = new HashMap<>();
//		meta.put("IP", comboBoardIP.getSelectedItem().toString().split(" -")[0]);
		meta.put("PORT", "5151");
		//meta.put("MAC", "AA:AA:AA:AA:AA:7F");
		//meta.put("CORE(S)", "1");
		//List<Entry<String,String>> devices = jclHost.getDevices();
		Entry<String,String> singleDevice=null;//devices.get(0) ;

		meta.put("DEVICE_ID", txtBoardName.getText());
//		meta.put("ServerIP", jclHost.getDeviceMetadata(singleDevice).get("serverMainAdd"));
//		meta.put("ServerPort", jclHost.getDeviceMetadata(singleDevice).get("serverMainPort"));

/*		meta.put("ServerIP", jclIoT.getIoTDeviceMetadata(singleDevice).get("serverMainAdd"));
		meta.put("ServerPort", jclIoT.getIoTDeviceMetadata(singleDevice).get("serverMainPort"));*/
		
		String enableSensors = new String();
		for(int i=0;i< sensorsFromUser.size();i++){
			SensorAcq s = new SensorAcq();
			s = sensorsFromUser.get(i);
			meta.put("SENSOR_ALIAS_"+s.getPin(), s.getAlias());
			meta.put("SENSOR_SAMPLING_"+s.getPin(), ""+s.getDelay());
			meta.put("SENSOR_SIZE_"+s.getPin(),""+s.getSize());
			meta.put("SENSOR_DIR_"+s.getPin(),""+s.getDir());
			meta.put("SENSOR_TYPE_"+s.getPin(),""+s.getType());
			if(i==sensorsFromUser.size()-1)	enableSensors += s.getPin();
			else enableSensors += s.getPin()+";";
		}

		meta.put("ENABLE_SENSOR", enableSensors);
		return meta;
	}

	public void setBoardElementsVisible(boolean b){
		pino.setVisible(b);
		tipoSensor.setVisible(b);
		addSensor.setVisible(b);
		deleteSensor.setVisible(b);
		clearAll.setVisible(b);
	}

	public JPanel createAndroidPanel() {
		JPanel sensorPanel = new JPanel();
		sensorPanel = new JPanel();
		sensorPanel.setBounds(270, 67, 246, 153);
		add(sensorPanel);
		sensorPanel.setLayout(null);

		JLabel lblSensorName = new JLabel("Sensor Name:");
		lblSensorName.setBounds(12, 12, 99, 15);
		sensorPanel.add(lblSensorName);

		txtSensorName = new JTextField();
		txtSensorName.setBounds(124, 12, 114, 19);
		txtSensorName.setToolTipText("Add the name you want for your sensor");
		sensorPanel.add(txtSensorName);
		txtSensorName.setColumns(10);
		txtSensorName.setDocument(new FixedSizeDocument(25));

		JLabel lblDelay = new JLabel("Delay:");
		lblDelay.setBounds(66, 41, 45, 15);
		sensorPanel.add(lblDelay);

		spinnerDelay = new JSpinner();
		spinnerDelay.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
		spinnerDelay.setBounds(124, 39, 114, 19);
		spinnerDelay.setToolTipText("Setup the delay for sensor data (ms)");
		sensorPanel.add(spinnerDelay);
		//txtDelay.setColumns(10);

		JLabel lblSize = new JLabel("Size:");
		lblSize.setBounds(86, 68, 114, 19);
		sensorPanel.add(lblSize);

		spinnerSize = new JSpinner();
		spinnerSize.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinnerSize.setBounds(124, 66, 114, 19);
		spinnerSize.setToolTipText("Setup the cache size for the sensing data (number of registers)");
		sensorPanel.add(spinnerSize);
		//txtSize.setColumns(10);

		btnSave = new JButton("Save");
		btnSave.setBounds(100, 120, 90, 25);
		btnSave.setToolTipText("Save the configuration for the sensor");
		try{
			Icon ic = new ImageIcon("images/save.png");
		    btnSave.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		sensorPanel.add(btnSave);

		return sensorPanel;
	}

	public JPanel createIoTPanel(){
		JPanel sensorPanel = new JPanel();
		sensorPanel = new JPanel();
		sensorPanel.setBounds(270, 67, 246, 153);
		add(sensorPanel);
		sensorPanel.setLayout(null);

		JLabel lblSensorName = new JLabel("Sensor Name:");
		lblSensorName.setBounds(12, 12, 99, 15);
		sensorPanel.add(lblSensorName);

		txtSensorName = new JTextField();
		txtSensorName.setBounds(123, 10, 114, 19);
		txtSensorName.setToolTipText("Add the name you want for your sensor");
		sensorPanel.add(txtSensorName);
		txtSensorName.setColumns(10);
		txtSensorName.setDocument(new FixedSizeDocument(25));

		JLabel label = new JLabel("Type:");
		label.setBounds(71, 73, 39, 15);
		sensorPanel.add(label);

		comboBoxDir = new JComboBox();
		comboBoxDir.setModel(new DefaultComboBoxModel(new String[] {"Input", "Output"}));
		comboBoxDir.setBounds(123, 37, 114, 24);
		comboBoxDir.setToolTipText("Choose if you sensor is for input or output data");
		comboBoxDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBoxDir.getSelectedIndex() == 1){
					spinnerDelay.setEnabled(false);
					spinnerSize.setEnabled(false);
				}
				else{
					spinnerDelay.setEnabled(true);
					spinnerSize.setEnabled(true);
				}
			}
		});
		sensorPanel.add(comboBoxDir);


		JLabel lblDir = new JLabel("Dir:");
		lblDir.setBounds(85, 39, 25, 15);
		sensorPanel.add(lblDir);

		comboBoxType = new JComboBox();
		comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"Generic", "Servo"}));
		comboBoxType.setBounds(123, 68, 114, 24);
		comboBoxType.setToolTipText("Select if your sensor is a Servo or a generic sensor");
		sensorPanel.add(comboBoxType);

		JLabel lblDelay = new JLabel("Delay:");
		lblDelay.setBounds(66, 102, 45, 15);
		sensorPanel.add(lblDelay);

		spinnerDelay = new JSpinner();
		spinnerDelay.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
		spinnerDelay.setBounds(123, 100, 114, 20);
		spinnerDelay.setToolTipText("Setup the frequence for the system to colect the sensing values (seconds)");
		sensorPanel.add(spinnerDelay);

		JLabel lblSize = new JLabel("Size:");
		lblSize.setBounds(76, 129, 35, 15);
		sensorPanel.add(lblSize);

		spinnerSize = new JSpinner();
		spinnerSize.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinnerSize.setBounds(123, 127, 114, 20);
		spinnerSize.setToolTipText("Setup the cache size for the sensing data on the sensor (MB");
		sensorPanel.add(spinnerSize);

		btnSave = new JButton("Save");
		btnSave.setBounds(96, 156, 95, 25);
		spinnerSize.setToolTipText("Setup the cache size for the sensing data on the sensor (MB");
		try{
			Icon ic = new ImageIcon("images/save.png");
		    btnSave.setIcon(ic);
		}catch(Exception e){
			e.printStackTrace();
		}
		sensorPanel.add(btnSave);
		btnSave.setToolTipText("Save the configuration for the sensor");

		return sensorPanel;
	}

	public void clearTabs(){
		tabbedPane.removeAll();
		sensorsFromUser.clear();
		selectedPins.clear();
		//txtpnAddedSensors.setText("Added Sensors:\n");
	}

	public int retrivePinValue(int brdSelection, String s){
		int value = -1;
		if(s == null)
			s = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		if(brdSelection == 1){
			GalileoGen2Dig d  ;
			GalileoGen2Analog a  ;

			if(tipoSensor.getSelectedIndex() == 1){
				d = (GalileoGen2Dig) GalileoGen2Dig.valueOf(s);
				value = d.getValue();
				return value;
			}
			else if(tipoSensor.getSelectedIndex() == 2){
				a = (GalileoGen2Analog) GalileoGen2Analog.valueOf(s);
				value = a.getValue();
				return value;
			}
		}
		else if(brdSelection == 2){
			ArduinoMEGADig d  ;
			ArduinoMEGAAnalog a  ;

			if(tipoSensor.getSelectedIndex() == 1){
				d = (ArduinoMEGADig) ArduinoMEGADig.valueOf(s);
				value = d.getValue();
				return value;
			}
			else if(tipoSensor.getSelectedIndex() == 2){
				a = (ArduinoMEGAAnalog) ArduinoMEGAAnalog.valueOf(s);
				value = a.getValue() ;
				return value;
			}
		}
		else if(brdSelection == 3){
			RaspPi2BDig d;
			RaspPi2BAnalog a;

			if(tipoSensor.getSelectedIndex() == 1){
				d = (RaspPi2BDig) RaspPi2BDig.valueOf(s);
				value = d.getValue();
				return value;
			}
			else if(tipoSensor.getSelectedIndex() == 2){
				a = (RaspPi2BAnalog) RaspPi2BAnalog.valueOf(s);
				value = a.getValue();
				return value;
			}
		}
		else if(brdSelection == 5){
			AndroidSensors a;
			a = (AndroidSensors) AndroidSensors.valueOf(s);
			value = a.getValue();
			return value;
		}
		else if(brdSelection == 6){
			BeagleboneBlackRevBDig d;
			BeagleboneBlackRevBAnalog a;
			if(tipoSensor.getSelectedIndex() == 1){
				d = (BeagleboneBlackRevBDig) BeagleboneBlackRevBDig.valueOf(s);
				value = d.getValue();
				return value;
			}
			else if(tipoSensor.getSelectedIndex() == 2){
				a = (BeagleboneBlackRevBAnalog) BeagleboneBlackRevBAnalog.valueOf(s);
				value = a.getValue();
				return value;
			}
		}
		else if(brdSelection == 7){

		}
		else if(brdSelection == 8){

		}
		else if(brdSelection == 9){

		}

		return value;
	}

	public String [] getHostsIP(){
		jclHost = JCL_FacadeImpl.getInstance();
		jclIoT = JCL_IoTFacadeImpl.getInstance();
		List<Entry<String,String>> hostsIoT = jclIoT.getIoTDevices();
		
		List<Entry<String,String>> hostsHPC = jclHost.getDevices();
		comboBoardIP.setMaximumRowCount(hostsHPC.size());
		String [] hosts = new String[hostsHPC.size()/*+hostsIoT.size()*/]; 
		int i=0;
	
		for(Entry<String,String> device : hostsHPC){
			Map<String, String> map = jclHost.getDeviceMetadata(device);
			jclHost.getDeviceMetadata(device);
			hosts[i] = map.get("IP") + " - " + map.get("DEVICE_PLATFORM");
			i++;
		}
		return hosts;
	}

	public void addSensorToList(final ArrayList<SensorAcq> sensorsFromUser){
		String newText = new String();
		SensorAcq s;

		s = sensorsFromUser.get(sensorsFromUser.size()-1);
		if(comboBoardSelection.getSelectedIndex()!=4){
			newText = "\n------"+comboBoardSelection.getSelectedItem() + "------\n";
			newText = newText+"Name: "+s.getAlias()+"\n";
			newText = newText+"Pin: "+s.getPin()+"\n";
			newText = newText+"Type:"+s.getType()+"\n";
			newText = newText+"Dir:"+s.getDir()+"\n";
			newText = newText+"Delay:"+s.getDelay()+"\n";
			newText = newText+"Size"+s.getSize()+"\n";
		}
		else{
			newText = "\n------"+comboBoardSelection.getSelectedItem() + "------\n";
			newText = newText+"Name: "+s.getAlias()+"\n";
			newText = newText+"Delay:"+s.getDelay()+"\n";
			newText = newText+"Size"+s.getSize()+"\n";
		}

		txtpnAddedSensors.setText(txtpnAddedSensors.getText()+newText);
		scrollAddedSensors.setViewportView(txtpnAddedSensors);
	}
}








class FixedSizeDocument extends PlainDocument
{
   private int max = 10;
   
   public FixedSizeDocument(int max) 
   { 
        this.max = max; 
   } 

   @Override
   public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException
   {
      // check string being inserted does not exceed max length
	   
      if (getLength()+str.length()>max)
      {
         // If it does, then truncate it
    	  
         str = str.substring(0, max - getLength());
      }
      super.insertString(offs, str, a);
   }
}