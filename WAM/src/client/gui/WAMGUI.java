package WAM.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.GridPane;
import javafx.scene.control.TextField;

private WAM.common.gameState board;
private WAM.client.WAMClient client;
private Group root;
private GridPane gridpane;
private Scene scene;
private Stage stage;
private moleHole[][] local;
private TextField scoreboard;

public class WAMGUI extends Application{
    public void init() {
	try {
            // get the command line args
            List<String> args = getParameters().getRaw();

            // get host info and port from command line
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
	    this.board = new WAM.common.gameState;
	    board.definePlayer(this);
	    this.client = new WAM.client.WAMClient(host, port, board);
	    
            // TODO
        } catch(NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
	}
    }

    public void start(Stage stage) {
	this.stage = stage;
	stage.setTitle("Whack-A-Mole!");
	stage.setWidth(1000);
	stage.setHeight(700);

	this.gridpane = new GridPane();
	for (int i = 0; i < board.getRows(); i++) {
	    for (int j = 0; i < board.getColumns(); j++) {
		moleHole temp = new moleHole(board.getColumns() * i + j + 1);
		local[i][j] = temp;
		gridpane.add(temp, i, j);
	    }					 
	}
	this.root = new Group(gridpane);
	this.scoreboard = new TextField();
	scoreboard.setText("Score: " + 0);
	this.guiboard = new Scene(root);
	stage.setScene(guiboard);
	stage.show();
	client.startListener();
    }

    public void update() {
	switch(board.getStatus()) {
	case MOLE_UP: //triggered for any state change
	    boolean[][] cur = board.getBoard();
	    for (int i = 0; i < cur.length; i++) {
		for (int j = 0; j < cur[0].length; j++) {
		    if (local[i][j].getState() != cur[i][j]) {
			if (cur[i][j]) {
			    local[i][j].setMole(); //current board has this value as filled, so setMole()
			}
			else {
			    local[i][j].setHole(); //current board has this value as empty, so setHole()
			}
		    }
		}
	    }
	case SCORE:
	    this.scoreboard = setText("Score: " + board.getScore());
	case GAME_WON:
	    stage.setTitle("you won! :)");
	    stop();
	case GAME_LOST:
	    stage.setTitle("you lost :(");
	    stop();
	case GAME_TIED:
	    stage.setTitle("you tied :|");
	    stop();
	case ERROR:
	    stage.setTitle("The game has ended in an error");
	    stop();
	}
    }
    public void stop() {
	client.close();
	scoreboard.setText("Game Over, thanks for Playing!");
    }
    public static void main(String[] args) {
		if (args.length != 2) {
	    System.out.println("Usage: java WAMGUI host port");
	    System.exit(-1);
	} else {
	    Application.launch(args);
	}
    }
}

public class moleHole extends Button {
    private int pos;
    private boolean state; //down is false, up is true
	
    public moleHole(int pos){
	this.pos = pos;
	this.state =false;
	Image image = new Image(getClass().getResourceAsStream("hole.png"));
	this.setGraphic(new ImageView(image));
	this.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		client.moleWhacked(this.pos);
	    }
	});
	
    }
    
    public boolean getState() {
	return state;
    }
    public void setHole() {
	Image image = new Image(getClass().getResourceAsStream("hole.jpg"));
	this.setGraphic(new ImageView(image));
	this.state = false;
    }
    public void setMole() {
	Image image = new Image(getClass().getResourceAsStream("mole.jpg"));
	this.setGraphic(new ImageView(image));
	this.setOnAction(null);
	this.state = true;
    }
}
