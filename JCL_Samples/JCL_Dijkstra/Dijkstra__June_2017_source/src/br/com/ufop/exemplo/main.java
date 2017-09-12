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

import br.com.ufop.jars.Vertex;

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

import javax.swing.JMenuItem;

public class main {

	private JFrame frmDijkstraSolver;
	private JTextArea taLoad;
	private JComboBox cbFrom;
	private JComboBox cbTo;
	private List<String> data = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main window = new main();
					window.frmDijkstraSolver.setVisible(true);
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
		frmDijkstraSolver = new JFrame();
		frmDijkstraSolver.setTitle("Dijkstra Solver");
		frmDijkstraSolver.setBounds(100, 100, 670, 426);
		frmDijkstraSolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmDijkstraSolver.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("File");
		menuBar.add(mnMenu);
		
		JMenuItem mntmLoadFile = new JMenuItem("Load File");
		mntmLoadFile.addActionListener(this.load());
		mnMenu.add(mntmLoadFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmDijkstraSolver.dispose();
				dialog.dispose();
			}
		});
		mnMenu.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JButton btnNewButton = new JButton("Load File");
		btnNewButton.addActionListener(this.load());
		
		JButton btnExecuteDijkstra = new JButton("Execute Dijkstra");
		btnExecuteDijkstra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long inicio=0;
				long fim = 0;
				
				inicio = System.currentTimeMillis();
				MainDijkstra exec = new MainDijkstra();
				List<Vertex> result = exec.exec(data,cbFrom.getSelectedIndex(),cbTo.getSelectedIndex());
				fim = System.currentTimeMillis();
				JOptionPane.showMessageDialog(null,"Distance to " + result.get(result.size()-1).toString() + ": " + result.get(result.size()-1).minDistance+"\n"+"Path: "+result+"\n"+"Runtime: "+(fim - inicio)+"ms.");					
			}
		});
		
		cbFrom = new JComboBox();
		
		JLabel lblNewLabel = new JLabel("From vertex:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		cbTo = new JComboBox();
		
		JLabel lblTo = new JLabel("To vertex:");
		lblTo.setFont(new Font("Tahoma", Font.BOLD, 12));
		GroupLayout groupLayout = new GroupLayout(frmDijkstraSolver.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 633, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnExecuteDijkstra, GroupLayout.PREFERRED_SIZE, 634, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(46)
					.addComponent(lblNewLabel)
					.addGap(18)
					.addComponent(cbFrom, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblTo, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(cbTo, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
					.addGap(34))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnExecuteDijkstra, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(cbFrom, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(lblTo, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbTo, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(21, Short.MAX_VALUE))
		);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 614, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		taLoad = new JTextArea();
		taLoad.setEditable(false);
		scrollPane.setViewportView(taLoad);
		panel.setLayout(gl_panel);
		frmDijkstraSolver.getContentPane().setLayout(groupLayout);
	}

public ActionListener load(){
	return	new ActionListener() {
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
				data.clear();
				taLoad.setText("");
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
				cbFrom.removeAllItems();
				cbTo.removeAllItems();
				for (int i = 0;i<Integer.parseInt(data.get(0));i++){
				cbFrom.addItem(i);
				cbTo.addItem(i);
				}
			}
			

		}
	};
}
}
