package br.com.ufop.exemplo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;

import br.com.ufop.jars.Knapsack;

import java.awt.Component;

import javax.swing.JMenuItem;

public class main {

	private JFrame frmKnapsackSolver;
	private JTextArea taLoad;
	private JComboBox cbDiv;
	private List<String> data = new ArrayList<String>();
	private JComboBox cbCap;
	private JTextArea taResult;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main window = new main();
					window.frmKnapsackSolver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final About dialog = new About();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(false);
		frmKnapsackSolver = new JFrame();
		frmKnapsackSolver.setTitle("Knapsack Solver");
		frmKnapsackSolver.setBounds(100, 100, 670, 576);
		frmKnapsackSolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmKnapsackSolver.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("File");
		menuBar.add(mnMenu);
		
		JMenuItem mntmLoadFile = new JMenuItem("Load File");
		mntmLoadFile.addActionListener(this.load());
		mntmLoadFile.setIcon(null);
		mnMenu.add(mntmLoadFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmKnapsackSolver.dispose();
				dialog.dispose();
			}
		});
		mntmExit.setIcon(null);
		mnMenu.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAboutUs = new JMenuItem("About");
		mntmAboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.setVisible(true);
			}
		});
		mnHelp.add(mntmAboutUs);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JButton btnLoad = new JButton("Load File");
		btnLoad.addActionListener(this.load());
		
		JButton btnExecute = new JButton("Execute Knapsack");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long inicio=0;
				long fim = 0;
				int sun=0;
				
				inicio = System.currentTimeMillis();
				MainKnapsack exec = new MainKnapsack();				 
				List<Item> result = exec.exec(data,Integer.parseInt((String)cbCap.getSelectedItem()),Integer.parseInt((String)cbDiv.getSelectedItem()));
				taResult.setText("");
				taResult.append("Weight    Value\n");
				for (Item item : result){					
					taResult.append(item.weight()+"              "+item.value()+"\n")	;
					sun+=item.value();
				}
				taResult.append("Best Value: "+sun+"\n");
				fim = System.currentTimeMillis();
				taResult.append("Runtime: "+(fim-inicio)+"ms.\n");
			}
		});
		
		cbDiv = new JComboBox();
		cbDiv.setFont(new Font("Tahoma", Font.PLAIN, 14));
		cbDiv.setModel(new DefaultComboBoxModel(new String[] {"2", "4", "8", "16", "31"}));
		
		JLabel lblNewLabel = new JLabel("DIV:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblCapacity = new JLabel("capacity:");
		lblCapacity.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		cbCap = new JComboBox();
		cbCap.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"}));
		cbCap.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 633, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(10)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 614, GroupLayout.PREFERRED_SIZE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 191, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(11)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
		);
		
		taResult = new JTextArea();
		taResult.setEditable(false);
		scrollPane_1.setViewportView(taResult);
		panel_1.setLayout(gl_panel_1);
		GroupLayout groupLayout = new GroupLayout(frmKnapsackSolver.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 633, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnLoad, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnExecute, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
							.addGap(29)
							.addComponent(lblCapacity)
							.addGap(18)
							.addComponent(cbCap, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblNewLabel)
							.addGap(18)
							.addComponent(cbDiv, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 633, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnLoad, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnExecute, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
								.addComponent(cbDiv, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
								.addComponent(cbCap, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCapacity, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)))
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
					.addGap(67))
		);
		groupLayout.linkSize(SwingConstants.VERTICAL, new Component[] {cbDiv, cbCap});
		
		JScrollPane scrollPane = new JScrollPane();
		
		taLoad = new JTextArea();
		taLoad.setEditable(false);
		scrollPane.setViewportView(taLoad);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 614, GroupLayout.PREFERRED_SIZE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(11)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
		);
		panel.setLayout(gl_panel);
		frmKnapsackSolver.getContentPane().setLayout(groupLayout);
	}
	
public ActionListener load(){
	return new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(null); //replace null with your swing container
			File file = null;
			if(returnVal == JFileChooser.APPROVE_OPTION){     
			  file = chooser.getSelectedFile();

				BufferedReader in = null;
				
				try {
					in = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String line = null;
				try {
					line = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				taLoad.setText("");
				data.clear();
				while((line != null) &&(line.trim().length()>0)){
					
					taLoad.append(line + "\n");
					data.add(line);
					try {
						line = in.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} //fim while					
			}
			

		}
	};
}
}
