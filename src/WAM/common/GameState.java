package WAM.common;

public class GameState {
    private int score; //current score
    private boolean[][] board; //array representing board
    private int order;
    private int num_players;
    private boolean isGoing;
    private int endState;
    private String status;
    private WAM.client.gui.WAMGUI view;

    public GameState () {
	isGoing = true;
    }
    public boolean[][] getBoard() {
	return board;
    }
    public String getStatus() {
	return status;
    }
    public int getRows() {
	return board.length;
    }
    public int getColumns() {
	return board[0].length;
    }
    public int getScore() {
	return this.score;
    }
    public void definePlayer (WAM.client.gui.WAMGUI view) {
	this.view = view;
    }
    public void alertPlayer() {
	view.update();
    }

    public void setParams (int row, int col, int total_players, int this_player) {
	board = new boolean[row][col];
	order = this_player;
	num_players = total_players;
    }
    public void modify (int row, int col) {
	board[row][col] = !board[row][col];
	status = "MOLE_UP"; //does not matter if it's up or down, board will get sweeped for updates
	alertPlayer();
	
    }
    public void modifyMod(int val) {
	int row = val / board[0].length;
	int col = val % board[0].length;

	modify(row, col);
    }
    public void scoreChange (int change) {
	score += change;
	status = "SCORE";
	alertPlayer();
    }
    public void endGame(int res) {
	isGoing = false;
	if (res == 0) {
	    status = "GAME_TIED";
	}
	else if (res == 1) {
	    status = "GAME_WON";
	}
	else if (res == -1) {
	    status = "GAME_LOST";
	}
	else {
	    status = "ERROR";
	}
	alertPlayer();
    }
}
