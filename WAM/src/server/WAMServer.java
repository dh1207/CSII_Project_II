package WAM.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.Closeable;
import java.io.PrintStream;
import java.io.Scanner;

    
public class WAMServer implements Runnable{

    private int limit;
    private int[] scores;
    private int[] board;
    private ServerSocket socket;
    private clientState[] clients;
    
    
    public WAMServer(String[] args) {
	String[] split = args.split(" ");
	socket = new ServerSocket(Character.getNumericValue(split[0]));
	clients = new clientState[Character.getNumericValue(split[3])];
	scores = new int[Character.getNumericValue(split[3])];
	board = new int[Character.getNumericValue(split[1]) * Character.getNumericValue(split[2])];	
	for (int i = 0; i < scores.length; i++) {
	    clients[i] = new clientState(i, socket.accept(); this);
	    clients[i].sendWelcome(Character.getNumericValue(split[1]), Character.getNumericValue(split[2]), Character.getNumericValue(split[3]));
	}
	limit = Character.getNumericValue(split[4]);
    }
    
    public void checkMole(int mole, int player) {
	if (board[mole] == 1) {
	    board[mole] = ThreadLocalRandom.current().nextInt(3, 10) * -1; //sets the mole to pop up after a random amount of seconds from 3 to 9
	    moleDown(mole);
	    scoreChange(player, 20);
	}
	else {
	    scoreChange(player, -5);
	}
    }

    public void scoreChange(int player, int amt) {
	clients[player].changeScore(amt);
    }

    public void moleDown(int mole) {
	board[mole] = false;
	for (int i = 0; i < clients.length; i++) {
	    clients[i].moleDown(mole);
	}
    }
    public void moleUp(int mole) {
	board[mole] = true;
	for (int i = 0; i < clients.length; i++) {
	    clients[i].moleUp(mole);
	}
    }
    public void getResult(int player) {
	int score = scores[player];
	boolean tie_flag = false;
	for (int i = 0; i < scores.length; i++) {
	    if (i != player) {
		if (scores[i] > score) {
		    clients[player].sendResult(-1); //loss
		    return;
		}
		if(scores[i] == score) {
		    tie_flag = true;
		}
	    }
	}
	if (tie_flag) {
	    clients[player].sendResults(0); //tie
	}
	else {
	    clients[player].sendResults(1); //win
	}
    }
    public static void main(String[] args) {
	if (args.length != 5) {
            System.out.println("Usage: java WAMServer <port> <rows> <cols> <num_players> <runtime>");
            System.exit(1);
        }
	WAMServer server = new WAMServer(args);
	server.run();
    }

    public void run() {
	long timeout = System.currentTimeMillis + (limit * 1000);
	long orig = System.currentTimeMillis;
	while (System.currentTimeMillis < timeout) {
	    if (System.currentTimeMillis - orig >= 1000) {
		orig = System.currentTimeMillis;
		for (int i = 0; i < board.length; i++) {
		    if (board[i] < 0) {
			board[i]++;
			if (board[i] == 0) {
			    moleUp(i);
			    board[i]++;
			}
		    }
		}
	    }
	    for (int i = 0; i < clients.length; i++) {
		if (client[i].whackRecieved()) {
		    client[i].moleWhacked();
		}
	    }
	}
	for (int i = 0; i < clients.length; i++) {
	    getResults(i);
	}
	
    }
    
}


public class clientState {
    private WAMServer server;
    private int player;
    private Socket client;
    private Scanner reciever;
    private PrintStream toClient;

    public clientState(int player, Socket socket, WAMServer server) {
	this.player = player;
	this.client = socket;
	this.server = server;
	this.reciever = new Scanner(client.getOutputStream());
	this.toClient = new PrintStream(client.getInputStream());
    }
    
    public void sendWelcome(int rows, int cols, int players) {
	toClient.println(WAM.common.WAMProtocol.WELCOME + " " + rows + " " + cols + " " + players + " " + this.player);
    }
    public boolean whackRecieved() {
	return reciever.hasNextLine();
    }

    public void moleWhacked() {
	String request = reciever.next();
	int mole = reciever.nextInt();
	int player = receiver.nextInt();

	server.checkMole(mole, player);
    }

    public void moleUp(int mole) {
	toClient.println(WAM.common.WAMProtocol.MOLE_UP + " " + mole);
    }

    public void moleDown(int mole) {
	toClient.println(WAM.common.WAMProtocol.MOLE_DOWN +  " " + mole);
    }

    public void changeScore(int change) {
	toClient.println(WAM.common.WAMProtocol.SCORE + " " + change);
    }
    public void sendResults(int res) {
	switch (res) {
	case -1:
	    toClient.println(WAM.common.WAMProtocol.GAME_LOST);
	case 0:
	    toClient.println(WAM.common.WAMProtocol.GAME_TIED);
	case 1:
	    toClient.println(WAM.common.WAMProtocol.GAME_WON);
	}
    }
				   
}
