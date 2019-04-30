package WAM.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.Scanner;
import java.lang.System;
import java.util.concurrent.ThreadLocalRandom;

    
public class WAMServer implements Runnable{

    private int limit;
    private int[] scores;
    private int[] board;
    private ServerSocket socket;
    private clientState[] clients;
    
    
    public WAMServer(String[] args) {
	try {
	    socket = new ServerSocket(getNum(args[0]));
	    clients = new clientState[getNum(args[3])];
	    scores = new int[getNum(args[3])];
	    board = new int[getNum(args[1]) * getNum(args[2])];	
	    for (int i = 0; i < scores.length; i++) {
		clients[i] = new clientState(i, socket.accept(), this);
		clients[i].sendWelcome(getNum(args[1]), getNum(args[2]), getNum(args[3]));
	    }
	    limit = getNum(args[4]);
	}
	catch (IOException e) {

	}
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

    public int getNum(String str) {
	boolean neg = str.charAt(0) == '-';
	int val = 0;
	for (int i = 0; i < str.length(); i++) {
	    if (Character.isDigit(str.charAt(i))) {
		val *= 10;
		val += Character.getNumericValue(str.charAt(i));
	    }
	}
	if (neg) {
	    return val * -1;
	}
	return val;
    }

    public void scoreChange(int player, int amt) {
	clients[player].changeScore(amt);
    }

    public void moleDown(int mole) {
	for (int i = 0; i < clients.length; i++) {
	    clients[i].moleDown(mole);
	}
    }
    public void moleUp(int mole) {
	for (int i = 0; i < clients.length; i++) {
	    clients[i].moleUp(mole);
	}
    }
    public void getResults(int player) {
	int score = scores[player];
	boolean tie_flag = false;
	for (int i = 0; i < scores.length; i++) {
	    if (i != player) {
		if (scores[i] > score) {
		    clients[player].sendResults(-1); //loss
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
	for (int i = 0; i < board.length; i++) {
	    board[i] = ThreadLocalRandom.current().nextInt(3, 10) * -1;
	}
	long timeout = System.currentTimeMillis() + (limit * 1000);
	long orig = System.currentTimeMillis();
	while (System.currentTimeMillis() < timeout) {
	    if (System.currentTimeMillis() - orig >= 1000) {
		orig = System.currentTimeMillis();
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
		if (clients[i].whackRecieved()) {
		    clients[i].moleWhacked();
		}
	    }
	}
	for (int i = 0; i < clients.length; i++) {
	    getResults(i);
	}
	
    }
    



    public class clientState {
	private WAMServer server;
	private int player;
	private Socket client;
	private Scanner reciever;
	private PrintStream toClient;

	public clientState(int player, Socket socket, WAMServer server) {
	    try {
		this.player = player;
		this.client = socket;
		this.server = server;
		this.reciever = new Scanner(client.getInputStream());
		this.toClient = new PrintStream(client.getOutputStream());
	    }
	    catch (IOException e) {

	    }
	}
    
	public void sendWelcome(int rows, int cols, int players) {
	    toClient.println("WELCOME " + rows + " " + cols + " " + players + " " + this.player);
	}
	public boolean whackRecieved() {
	    return reciever.hasNextLine();
	}

	public void moleWhacked() {
	    if (reciever.hasNextLine()){
		String request = reciever.next();
		int mole = reciever.nextInt();
		reciever.nextLine();

		server.checkMole(mole, this.player);
	    }
	}

	public void moleUp(int mole) {
	    toClient.println("MOLE_UP " + mole);
	}

	public void moleDown(int mole) {
	    toClient.println("MOLE_DOWN " + mole);
	}

	public void changeScore(int change) {
	    toClient.println("SCORE " + change);
	}
	public void sendResults(int res) {
	    switch (res) {
	    case -1:
		toClient.println("GAME_LOST");
	    case 0:
		toClient.println("GAME_TIED");
	    case 1:
		toClient.println("GAME_WON");
	    }
	}
				   
    }
}
