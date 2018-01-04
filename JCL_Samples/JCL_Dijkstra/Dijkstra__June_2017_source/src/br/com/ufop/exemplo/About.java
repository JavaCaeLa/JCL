package br.com.ufop.exemplo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class About extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public About() {
		setResizable(false);
		setTitle("About");
		setBounds(100, 100, 399, 406);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout());
			{
				JLabel lblDijkstraProblemSolver = new JLabel("Dijkstra problem solver");
				lblDijkstraProblemSolver.setFont(new Font("Times New Roman", Font.BOLD, 17));
				panel.add(lblDijkstraProblemSolver);
			}
		}
		FlowLayout fl_contentPanel = new FlowLayout();
		fl_contentPanel.setHgap(20);
		contentPanel.setLayout(fl_contentPanel);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblExempleOfKnapsack = new JLabel("Exemple of Dijkstra solver using Java C\u00E1&L\u00E1.");
			contentPanel.add(lblExempleOfKnapsack);
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setVgap(0);
			flowLayout.setHgap(0);
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			contentPanel.add(panel);
			{
				JLabel lblNewLabel = new JLabel("");
				panel.add(lblNewLabel);
				lblNewLabel.setIcon(new ImageIcon(About.class.getResource("/br/com/ufop/exemplo/hpclab2.png")));
			}
		}
		{
			JButton btnOk = new JButton("          OK           ");
			btnOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			contentPanel.add(btnOk);
		}
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JLabel lblForMoreExamples = new JLabel("For more examples visit http://www.decom.ufop.br/hpclab/ ");
				buttonPane.add(lblForMoreExamples);
			}
		}
	}

}
