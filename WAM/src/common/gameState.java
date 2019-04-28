public class gameState {
    private int score; //current score
    private boolean[][] board; //array representing board
    private int order;
    private int num_players;
    private boolean isGoing;
    private int endState;
    private WAM.common.WAMProtocol status;
    private WAM.client.gui.WAMGUI view;

    public gameState () {
	isGoing = true;
    }
    public boolean[][] getBoard() {
	return board;
    }
    public WAM.common.WAMProtocol getStatus() {
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

    public setParams (int row, int col, int total_players, int this_player) {
	board = new boolen[row][col];
	order = this_player;
	num_players = total_players;
    }
    public void modify (int row, int col) {
	board[row][col] = !board[row][col];
	status = WAM.common.WAMProtocol.MOLE_UP; //does not matter if it's up or down, board will get sweeped for updates
	alertPlayer();
	
    }
    public void scoreChange (int change) {
	score += change;
	status = WAM.common.WAMProtocol.SCORE;
	alertPlayer();
    }
    public void endGame(int result) {
	isGoing = false;
	if (i == 0) {
	    status = WAM.common.WAMProtocol.GAME_TIED;
	}
	else if (i == 1) {
	    status = WAM.common.WAMProtocol.GAME_WON;
	}
	else if ( i == -1) {
	    status = WAM.common.WAMProtocol.GAME_LOST;
	}
	else {
	    status = WAM.common.WAMProtocol.ERROR;
	}
	alertPlayer();
    }
}
