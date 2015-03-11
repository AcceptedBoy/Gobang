package com.cstackess.gobang;

/**
 * @author cstackess
 */
public class Brain {
	private Board bd;
	private int INFINITY = 1000000;
	private int movex, movey;
	private int level;// ���
	private int node;// ÿ����

	public Brain(Board bd, int level, int node) {
		this.bd = bd;
		this.level = level;
		this.node = node;
	}

	// ��ֵ����
	public int[] findOneBestStep() {
		Chess[] arr = bd.getSortedChess(bd.getPlayer());
		Chess c = bd.getData()[arr[0].x][arr[0].y];
		int[] result = { c.x, c.y };
		return result;
	}

	// ��ֵ����+������
	public int[] findTreeBestStep() {
		alpha_beta(0, bd, -INFINITY, INFINITY);
		int[] result = { movex, movey };
		return result;
	}

	// alpha-beta��֦�����㷨
	public int alpha_beta(int depth, Board board, int alpha, int beta) {
		if (depth == level || board.isGameOver() != 0) {
			Chess[] sorted = board.getSorted();
			Chess move = board.getData()[sorted[0].x][sorted[0].y];
			// �������������
			System.out.println("\t- " + "��" + (char) (64 + move.x)
					+ (16 - move.y) + "��," + move.getSum());
			return move.getSum();// �������
		}
		// �Ծ����µ÷���ߵļ����������չ
		Board temp = new Board(board);
		Chess[] sorted = temp.getSorted();
		int score;
		for (int i = 0; i < node; i++) {
			int x = sorted[i].x;
			int y = sorted[i].y;
			// �������������
			if (depth >= 1) {
				System.out.println();
				for (int k = 0; k < depth; k++)
					System.out.printf("\t");
			}
			// ������߷�
			if (!temp.putChess(x, y))
				continue;
			if (sorted[i].getOffense() >= Board.Level.ALIVE_4.score) {
				System.out.println("���ǿ�ҪӮ����");
				score = INFINITY + 1;
			} else if (sorted[i].getDefence() >= Board.Level.ALIVE_4.score) {
				System.out.println("�Է���ҪӮ����");
				score = INFINITY;
			} else {
				score = alpha_beta(depth + 1, temp, alpha, beta);
			}
			temp = new Board(board);// ��������߷�

			if (depth % 2 == 0) {// MAX
				if (score > alpha) {
					alpha = score;
					if (depth == 0) {
						movex = x;
						movey = y;
					}
				}
				if (alpha >= beta) {
					score = alpha;
					// System.out.println(" beta��֦");
					return score;
				}
			} else {// MIN
				if (score < beta) {
					beta = score;
				}
				if (alpha >= beta) {
					score = beta;
					// System.out.println(" alpha��֦");
					return score;
				}
			}

		}
		return depth % 2 == 0 ? alpha : beta;
	}

}
