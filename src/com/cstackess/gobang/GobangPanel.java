package com.cstackess.gobang;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author cstackess
 */
public class GobangPanel extends JPanel {
	private static final long serialVersionUID = 667503661521167626L;
	private final int OFFSET = 40;// ����ƫ��
	private final int CELL_WIDTH = 40;// �����
	private int computerSide = Chess.BLACK;// Ĭ�ϻ����ֺ�
	private int humanSide = Chess.WHITE;
	private int cx = Board.CENTER, cy = Board.CENTER;
	private boolean isShowOrder = false;// ��ʾ����˳��
	private int[] lastStep;// ��һ�����ӵ�
	private Board bd;// ���̣���Ҫ
	private Brain br;// AI����Ҫ
	public static final int MANUAL = 0;// ˫��ģʽ
	public static final int HALF = 1;// �˻�ģʽ
	public static final int AUTO = 2;// ˫��ģʽ
	public static final int EVAL = 3;// ��ֵ����
	public static final int TREE = 4;// ��ֵ����+������
	private int mode;// ģʽ
	private int intel;// ����
	private boolean isGameOver = true;
	private TextArea area;

	// ��ʾ����˳��
	public void troggleOrder() {
		isShowOrder = !isShowOrder;
		repaint();
	}

	// ����
	public void undo() {
		Point p = bd.undo();
		lastStep[0] = p.x;
		lastStep[1] = p.y;
		repaint();
	}

	public GobangPanel(TextArea area) {
		this.area = area;
		lastStep = new int[2];
		addMouseMotionListener(mouseMotionListener);
		addMouseListener(mouseListener);
		this.setBackground(Color.ORANGE);
		setPreferredSize(new Dimension(650, 700));
		bd = new Board();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		g2d.setStroke(new BasicStroke(2));
		g2d.setFont(new Font("April", Font.BOLD, 12));
		// ������
		drawBoard(g2d);
		// ����Ԫ����
		drawStar(g2d, Board.CENTER, Board.CENTER);
		drawStar(g2d, (Board.BOARD_SIZE + 1) / 4, (Board.BOARD_SIZE + 1) / 4);
		drawStar(g2d, (Board.BOARD_SIZE + 1) / 4,
				(Board.BOARD_SIZE + 1) * 3 / 4);
		drawStar(g2d, (Board.BOARD_SIZE + 1) * 3 / 4,
				(Board.BOARD_SIZE + 1) / 4);
		drawStar(g2d, (Board.BOARD_SIZE + 1) * 3 / 4,
				(Board.BOARD_SIZE + 1) * 3 / 4);
		// �����ֺ���ĸ
		drawNumAndLetter(g2d);
		// ����ʾ��
		drawCell(g2d, cx, cy, 0);

		if (!isGameOver) {
			// ����������
			for (int x = 1; x <= Board.BOARD_SIZE; ++x) {
				for (int y = 1; y <= Board.BOARD_SIZE; ++y) {
					drawChess(g2d, x, y, bd.getData()[x][y].getSide());
				}
			}
			// ��˳��
			if (isShowOrder)
				drawOrder(g2d);
			else {
				if (lastStep[0] > 0 && lastStep[1] > 0) {
					g2d.setColor(Color.RED);
					g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSET
							- CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
							+ OFFSET - CELL_WIDTH / 10, CELL_WIDTH / 5,
							CELL_WIDTH / 5);

				}
			}
		}
	}

	// ������
	private void drawBoard(Graphics g2d) {
		for (int x = 0; x < Board.BOARD_SIZE; ++x) {
			g2d.drawLine(x * CELL_WIDTH + OFFSET, OFFSET, x * CELL_WIDTH
					+ OFFSET, (Board.BOARD_SIZE - 1) * CELL_WIDTH + OFFSET);

		}
		for (int y = 0; y < Board.BOARD_SIZE; ++y) {
			g2d.drawLine(OFFSET, y * CELL_WIDTH + OFFSET,
					(Board.BOARD_SIZE - 1) * CELL_WIDTH + OFFSET, y
							* CELL_WIDTH + OFFSET);

		}
	}

	// ����Ԫ����
	private void drawStar(Graphics g2d, int cx, int cy) {
		g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - 4, (cy - 1) * CELL_WIDTH
				+ OFFSET - 4, 8, 8);
	}

	// �����ֺ���ĸ
	private void drawNumAndLetter(Graphics g2d) {
		FontMetrics fm = g2d.getFontMetrics();
		int stringWidth, stringAscent;
		stringAscent = fm.getAscent();
		for (int i = 1; i <= Board.BOARD_SIZE; i++) {

			String num = String.valueOf(Board.BOARD_SIZE - i + 1);
			stringWidth = fm.stringWidth(num);
			g2d.drawString(String.valueOf(Board.BOARD_SIZE - i + 1), OFFSET / 4
					- stringWidth / 2, OFFSET + (CELL_WIDTH * (i - 1))
					+ stringAscent / 2);

			String letter = String.valueOf((char) (64 + i));
			stringWidth = fm.stringWidth(letter);
			g2d.drawString(String.valueOf((char) (64 + i)), OFFSET
					+ (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSET * 3 / 4
					+ OFFSET + CELL_WIDTH * (Board.BOARD_SIZE - 1)
					+ stringAscent / 2);
		}
	}

	// ������
	private void drawChess(Graphics g2d, int cx, int cy, int player) {
		if (player == 0)
			return;
		int size = CELL_WIDTH * 5 / 6;
		g2d.setColor(player == Chess.BLACK ? Color.BLACK : Color.WHITE);
		g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1)
				* CELL_WIDTH - size / 2 + OFFSET, size, size);
	}

	// ��Ԥѡ��
	private void drawCell(Graphics g2d, int x, int y, int c) {// c ��style
		int length = CELL_WIDTH / 4;
		int xx = (x - 1) * CELL_WIDTH + OFFSET;
		int yy = (y - 1) * CELL_WIDTH + OFFSET;
		int x1, y1, x2, y2, x3, y3, x4, y4;
		x1 = x4 = xx - CELL_WIDTH / 2;
		x2 = x3 = xx + CELL_WIDTH / 2;
		y1 = y2 = yy - CELL_WIDTH / 2;
		y3 = y4 = yy + CELL_WIDTH / 2;
		g2d.setColor(Color.RED);
		g2d.drawLine(x1, y1, x1 + length, y1);
		g2d.drawLine(x1, y1, x1, y1 + length);
		g2d.drawLine(x2, y2, x2 - length, y2);
		g2d.drawLine(x2, y2, x2, y2 + length);
		g2d.drawLine(x3, y3, x3 - length, y3);
		g2d.drawLine(x3, y3, x3, y3 - length);
		g2d.drawLine(x4, y4, x4 + length, y4);
		g2d.drawLine(x4, y4, x4, y4 - length);
	}

	// ������˳��
	private void drawOrder(Graphics g2d) {
		int[][] history = bd.getHistory();
		if (history.length > 0) {
			g2d.setColor(Color.RED);
			for (int i = 0; i < history.length; i++) {
				int x = history[i][0];
				int y = history[i][1];
				String text = String.valueOf(i + 1);
				// ����
				FontMetrics fm = g2d.getFontMetrics();
				int stringWidth = fm.stringWidth(text);
				int stringAscent = fm.getAscent();
				g2d.drawString(text, (x - 1) * CELL_WIDTH + OFFSET
						- stringWidth / 2, (y - 1) * CELL_WIDTH + OFFSET
						+ stringAscent / 2);
			}
		}
	}

	// ��ʼ��Ϸ
	public void startGame(int mode, int intel, int level, int node) {
		if (isGameOver) {
			this.mode = mode;
			this.intel = intel;
			bd.reset();
			area.setText("");
			lastStep[0] = lastStep[1] = Board.CENTER;
			br = new Brain(bd, level, node);
			bd.start();
			isGameOver = false;
			JOptionPane.showMessageDialog(GobangPanel.this, "��Ϸ��ʼ��");
			repaint();
			if (mode == AUTO) {// ˫��
				Timer t = new Timer(true);
				t.schedule(new ComputurTask(), 0, 500);
			}
		} else {
			JOptionPane.showMessageDialog(GobangPanel.this,
					"��Ϸ������...������������¿�ʼ�ɣ�");
		}
	}

	// ����ƶ�
	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent e) {
			int tx = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			int ty = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			if (tx != cx || ty != cy) {
				if (tx >= 1 && tx <= Board.BOARD_SIZE && ty >= 1
						&& ty <= Board.BOARD_SIZE) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					repaint();
				} else
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				cx = tx;
				cy = ty;
			}
		}
	};

	// �����
	private MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (isGameOver) {
				JOptionPane.showMessageDialog(GobangPanel.this, "�뿪ʼ����Ϸ��");
				return;
			}
			int x = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			int y = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
			if (cx >= 1 && cx <= Board.BOARD_SIZE && cy >= 1
					&& cy <= Board.BOARD_SIZE) {
				if (mode == MANUAL) {// ˫��
					int mods = e.getModifiers();
					if ((mods & InputEvent.BUTTON3_MASK) != 0) {// ����Ҽ�
						area.append(bd.getData()[x][y].getDetail());
					} else if ((mods & InputEvent.BUTTON1_MASK) != 0)// ������
						putChess(x, y);
				} else if (mode == HALF) {// �˻�
					if (bd.getPlayer() == humanSide) {
						int mods = e.getModifiers();
						if ((mods & InputEvent.BUTTON3_MASK) != 0) {// ����Ҽ�
							area.append(bd.getData()[x][y].getDetail());
						} else if ((mods & InputEvent.BUTTON1_MASK) != 0) {// ������
							if (putChess(x, y)) {
								System.out.println("\n----�������----");
								if (intel == EVAL) {
									int[] bestStep = br.findOneBestStep();// ��ֵ����AI
									putChess(bestStep[0], bestStep[1]);
								} else if (intel == TREE) {
									int[] bestStep = br.findTreeBestStep();// ��ֵ����+������AI
									putChess(bestStep[0], bestStep[1]);
								}
								System.out.println("\n----�������----");
							}
						}
					}
				}
			}
		}
	};

	private boolean putChess(int x, int y) {
		if (bd.putChess(x, y)) {
			lastStep[0] = x;// ������һ�����ӵ�
			lastStep[1] = y;
			repaint();
			int winSide = bd.isGameOver();// �ж��վ�
			if (winSide > 0) {
				if (winSide == humanSide) {
					JOptionPane.showMessageDialog(GobangPanel.this, "�׷�Ӯ�ˣ�");
				} else if (winSide == computerSide) {
					JOptionPane.showMessageDialog(GobangPanel.this, "�ڷ�Ӯ�ˣ�");
				} else {
					JOptionPane.showMessageDialog(GobangPanel.this, "˫��ƽ��");
				}

				// ���
				bd.reset();
				area.setText("");
				isGameOver = true;
				repaint();
				return false;
			}

			return true;
		}
		return false;

	}

	// ˫��
	private class ComputurTask extends TimerTask {
		@Override
		public void run() {
			int[] bestStep = br.findTreeBestStep();
			if (!putChess(bestStep[0], bestStep[1]))
				this.cancel();
		}

	}
}
