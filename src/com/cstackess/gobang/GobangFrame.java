package com.cstackess.gobang;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

/**
 * @author cstackess
 */
public class GobangFrame extends JFrame {
	private static final long serialVersionUID = -7844061449912554572L;
	JRadioButton manualBtn = new JRadioButton("˫��");
	JRadioButton halfAutoBtn = new JRadioButton("�˻�", true);
	JRadioButton autoBtn = new JRadioButton("˫��");
	JCheckBox orderBtn = new JCheckBox("��ʾ����˳��");
	JRadioButton oneBtn = new JRadioButton("��ֵ����");
	JRadioButton treeBtn = new JRadioButton("��ֵ����+������", true);
	JComboBox<Integer> levelCombo = new JComboBox<Integer>(new Integer[] { 1,
			2, 3 });
	JComboBox<Integer> nodeCombo = new JComboBox<Integer>(new Integer[] { 3, 5,
			10 });
	JButton btn = new JButton("����Ϸ");
	JButton undoBtn = new JButton("����");
	TextArea area = new TextArea();
	GobangPanel panel = new GobangPanel(area);// �������

	public GobangFrame() {
		super("����������");
		add(panel, BorderLayout.WEST);

		ButtonGroup grp_mode = new ButtonGroup();
		grp_mode.add(manualBtn);
		grp_mode.add(halfAutoBtn);
		grp_mode.add(autoBtn);
		ButtonGroup grp_alg = new ButtonGroup();
		grp_alg.add(oneBtn);
		grp_alg.add(treeBtn);

		JPanel rightPanel = new JPanel();
		area.setEditable(false);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(new TitledBorder("�������ϵ�������Ҽ����鿴�����ֵ"));
		panel1.add(area);
		rightPanel.add(panel1);

		JPanel optPanel = new JPanel();
		optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
		optPanel.setBorder(new TitledBorder("��Ϸ����"));

		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("ģʽ"));
		panel2.add(manualBtn);
		panel2.add(halfAutoBtn);
		panel2.add(autoBtn);
		optPanel.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("����"));
		panel3.add(oneBtn);
		panel3.add(treeBtn);
		optPanel.add(panel3);

		JPanel panel4 = new JPanel();
		panel4.setBorder(new TitledBorder("������"));
		panel4.add(new JLabel("�������"));
		panel4.add(levelCombo);
		panel4.add(new JLabel("ÿ��ڵ�"));
		panel4.add(nodeCombo);
		optPanel.add(panel4);

		optPanel.add(btn);
		rightPanel.add(optPanel);

		JPanel panel5 = new JPanel();
		panel5.setBorder(new TitledBorder("����"));
		panel5.add(orderBtn);
		panel5.add(undoBtn);
		rightPanel.add(panel5);

		add(rightPanel);
		btn.addActionListener(l);
		orderBtn.addActionListener(l);
		undoBtn.addActionListener(l);

		setSize(900, 700);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private ActionListener l = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == btn) {
				int mode = -1, intel = -1, level, node;
				if (manualBtn.isSelected())
					mode = GobangPanel.MANUAL;
				else if (halfAutoBtn.isSelected())
					mode = GobangPanel.HALF;
				else if (autoBtn.isSelected())
					mode = GobangPanel.AUTO;

				if (oneBtn.isSelected())
					intel = GobangPanel.EVAL;
				else if (treeBtn.isSelected())
					intel = GobangPanel.TREE;

				level = (Integer) levelCombo.getSelectedItem();
				node = (Integer) nodeCombo.getSelectedItem();

				panel.startGame(mode, intel, level, node);
			} else if (source == orderBtn) {
				panel.troggleOrder();
			} else if (source == undoBtn) {
				panel.undo();
			}
		}
	};
}
