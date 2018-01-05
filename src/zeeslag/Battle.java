package zeeslag;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Battle {

	public static void main(String[] args) throws Exception{
		startGame();
	}
	public static void startGame() throws Exception{

		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to the Battleship game!\n\n");
		Opponent.fillArray();
		Player.fillArray();
		displayBothArrays();
		System.out.println("You have the advantage to start.");
		while(!(Opponent.gameOver()||Player.gameOver())){
		Player.fire();
		testSunk();
		if(Opponent.gameOver()) break;
		TimeUnit.SECONDS.sleep(Randomizers.timeToAttack());
		Opponent.fire();
		testSunk();
		}
		if(Opponent.gameOver()){
			System.out.println("\nGAME OVER. We have a wiener!");
		}
		else{
			System.out.println("\nGAME OVER. Loser!");
		}

		sc.close();
	}
	
	public static void displayBothArrays(){
		StringBuilder sb = new StringBuilder();
		StringBuilder st = new StringBuilder();
		st.append("YOU:                                                                             OPPONENT:\n");
		for(int i=1;i<=10;i++){
			sb.append("   "+i);
		}
		st.append(sb+"                                        "+sb);
		System.out.println(st+"\n-----------------------------------------                                        -----------------------------------------");
		
		for(int y=0;y<10;y++){
			StringBuilder s = new StringBuilder();
			for(int x=0;x<10;x++){
				s.append(Player.getArray()[x][y]);
			}
			s.append("                                      "+(char)(y+65)+"| ");
			for(int x=0;x<10;x++){
				if(Opponent.getArray()[x][y].equals("O   "))	s.append(".   ");
				else s.append(Opponent.getArray()[x][y]);
			}
			System.out.println((char)(y+65)+"| "+s+"\n |                                                                                |");
		}
	}
	private static void testSunk(){
		System.out.println();
		if(Player.isShipSunk(Player.getMineSweeper()))		System.out.println("Your mine sweeper is down...");
		if(Player.isShipSunk(Player.getFrigate()))			System.out.println("Your frigate is down...");
		if(Player.isShipSunk(Player.getSubmarine()))			System.out.println("Your submarine is down...");
		if(Player.isShipSunk(Player.getCruiser()))			System.out.println("Your cruiser is down...");
		if(Player.isShipSunk(Player.getAircraftCarrier()))	System.out.println("Your aircraft carrier is down...");
		System.out.println();
		if(Opponent.isShipSunk(Opponent.getMineSweeper()))		System.out.println("Their mine sweeper is down!");
		if(Opponent.isShipSunk(Opponent.getFrigate()))			System.out.println("Their frigate is down!");
		if(Opponent.isShipSunk(Opponent.getSubmarine()))			System.out.println("Their submarine is down!");
		if(Opponent.isShipSunk(Opponent.getCruiser()))			System.out.println("Their cruiser is down!");
		if(Opponent.isShipSunk(Opponent.getAircraftCarrier()))	System.out.println("Their aircraft carrier is down!");
	}
}
