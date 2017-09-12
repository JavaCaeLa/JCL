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
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class About extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblExempleOfKnapsack;
	private JPanel panel_1;
	private JButton btnOk;

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
				JLabel label = new JLabel("Knapsack problem solver");
				label.setFont(new Font("Times New Roman", Font.BOLD, 17));
				panel.add(label);
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblExempleOfKnapsack = new JLabel("Exemple of Knapsack solver using Java C\u00E1&L\u00E1.");
		}
		{
			panel_1 = new JPanel();
			FlowLayout fl_panel_1 = (FlowLayout) panel_1.getLayout();
			fl_panel_1.setVgap(0);
			fl_panel_1.setHgap(0);
			panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			{
				JLabel lblNewLabel = new JLabel("");
				panel_1.add(lblNewLabel);
				lblNewLabel.setIcon(new ImageIcon(About.class.getResource("/br/com/ufop/exemplo/hpclab2.png")));
			}
		}
		{
			btnOk = new JButton("OK ");
			btnOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(79)
							.addComponent(lblExempleOfKnapsack))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(58)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(58, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap(150, Short.MAX_VALUE)
					.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
					.addGap(151))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(lblExempleOfKnapsack)
					.addGap(5)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOk)
					.addGap(23))
		);
		contentPanel.setLayout(gl_contentPanel);
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
