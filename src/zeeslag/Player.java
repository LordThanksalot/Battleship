package zeeslag;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Player {
	
	private static int x;		//position ship or missile target
	private static int y;
	
	private static int[][] mineSweeper = new int[2][2];			//array is [number of spots][x,y]
	private static int[][] frigate = new int[3][2];
	private static int[][] submarine = new int[3][2];
	private static int[][] cruiser = new int[4][2];
	private static int[][] aircraftCarrier = new int[5][2];
	
	private static String[][] square = new String[10][10];
	
	private static Scanner sc = new Scanner(System.in);

	public static void displayArray(){
		StringBuilder sb = new StringBuilder();
		for(int i=1;i<=10;i++){
			sb.append("   "+i);
		}
		System.out.println(sb+"\n-----------------------------------------");
		
		for(int y=0;y<10;y++){
			StringBuilder s = new StringBuilder();
			for(int x=0;x<10;x++){
				if(square[x][y] == null)	s.append(".   ");
				else s.append(square[x][y]);
			}
			System.out.println((char)(y+65)+"| "+s+"\n |");
		}
	}
	public static void fillArray(){
		for(int y=0;y<10;y++){
			for(int x=0;x<10;x++){
				square[x][y] = ".   ";
			}
		}
		displayArray();
		System.out.println("Choose where you want to place your AIRCRAFT CARRIER (5 spots).\nEnter the position of its extremities only.");
		placeShip(aircraftCarrier);
		displayArray();
		System.out.println("Choose where you want to place your CRUISER (4 spots).\nEnter the position of its extremities only.");
		placeShip(cruiser);
		displayArray();
		System.out.println("Choose where you want to place your SUBMARINE (3 spots).\nEnter the position of its extremities only.");
		placeShip(submarine);
		displayArray();
		System.out.println("Choose where you want to place your FRIGATE (3 spots).\nEnter the position of its extremities only.");
		placeShip(frigate);
		displayArray();
		System.out.println("Choose where you want to place your MINE SWEEPER (2 spots).\nEnter both positions.");
		placeShip(mineSweeper);
	}
	private static void placeShip(int[][] ship){
		int som;
		
		do{
			som = 0;
		for(int i=0;i<2;i++){
		String location = sc.nextLine().toUpperCase();					//filter input for valid location
		if(i==1 && location.equals("REPLACE")){
			System.out.println("Enter new location:");
			square[x][y] = ".   ";
			i=-1;
		}
		else{ while(	location.equals("") ||
				(!Character.isAlphabetic(location.charAt(0))) ||
				(location.charAt(0) < 'A' || location.charAt(0) > 'J') ||
				(location.length()!=2 && location.length()!=3) ||
				(Character.isAlphabetic(location.charAt(1))) ||
				(Character.isAlphabetic(location.charAt(location.length()-1))) ||
				((Integer.parseInt(location.substring(1)) < 1) || (Integer.parseInt(location.substring(1)) > 10))){
			System.out.println("Please insert a valid coordinate.");
			location = sc.nextLine().toUpperCase();
			}
		y = location.charAt(0)-65;
		x = Integer.parseInt(location.substring(1))-1;
		if(square[x][y].equals("O   ")){								//if ship extremities collide with another ship
			if(i==1){
				square[ship[0][0]][ship[0][1]] = ".   ";
			}
			System.out.println("Please reposition your ship for the ends of your ship collide with another ship.");
			i=-1;
		}
		else square[x][y] = "O   ";
		if(i==0){
			ship[0][0] = x;
			ship[0][1] = y;
			}
		else if(i==1){
			ship[ship.length-1][0] = x;
			ship[ship.length-1][1] = y;
			if(ship[ship.length-1][1]!=ship[0][1] && ship[ship.length-1][0]!=ship[0][0]){		//if ship is not horizontal or vertical
				System.out.println("Please reposition your ship for it is horizontal nor vertical.");
				square[x][y] = ".   ";
				square[ship[0][0]][ship[0][1]] = ".   ";
				i=-1;
			}
			else if(Math.abs(ship[ship.length-1][0]-ship[0][0])!=ship.length-1 &&
					Math.abs(ship[ship.length-1][1]-ship[0][1])!=ship.length-1){				//if length ship is not respected
				System.out.println("Please reposition your ship for the length is incorrect.");
				square[x][y] = ".   ";
				square[ship[0][0]][ship[0][1]] = ".   ";
				i=-1;
			}
			}
		if(i==0) System.out.println("*if you want to replace this ship, enter \"replace\" instead of the coordinate*");
		}
		}
		for(int i=1;i<ship.length-1&&som==0;i++){					//fill in the gap
			if(ship[0][0]==ship[ship.length-1][0]){			//if ship is vertical
				if(ship[ship.length-1][1]>ship[0][1]){		//if it's downwards
					if(square[x][y-i]=="O   "){				//if a spot is occupied
						System.out.println("Your ship collides with another ship. Please re-enter the coordinates.");
						square[x][y] = ".   ";
						square[ship[0][0]][ship[0][1]] = ".   ";
						for(int j=0;j<i;j++){
							square[x][y-j] = ".   ";
						}
						som++;
					}
					else{square[x][y-i] = "O   ";
					ship[i][0] = x;
					ship[i][1] = y-i;
					}
				}
				else if(square[x][y+i]=="O   "){				//if a spot is occupied
					System.out.println("Your ship collides with another ship. Please re-enter the coordinates.");
					square[x][y] = ".   ";
					square[ship[0][0]][ship[0][1]] = ".   ";
					for(int j=0;j<i;j++){
						square[x][y+j] = ".   ";
					}
					som++;
				}
				else{										//if it's upwards
				square[x][y+i] = "O   ";
				ship[i][0] = x;
				ship[i][1] = y+i;
				}
			}
			else if(ship[0][1]==ship[ship.length-1][1]){	//if ship is horizontal
				if(ship[ship.length-1][0]>ship[0][0]){		//if it's to the right
					if(square[x-i][y]=="O   "){				//if a spot is occupied
						System.out.println("Your ship collides with another ship. Please re-enter the coordinates.");
						square[x][y] = ".   ";
						square[ship[0][0]][ship[0][1]] = ".   ";
						for(int j=0;j<i;j++){
							square[x-j][y] = ".   ";
						}
						som++;
					}
					else{
						square[x-i][y] = "O   ";
						ship[i][0] = x-i;
						ship[i][1] = y;
					}
				}
				else if(square[x+i][y]=="O   "){				//if a spot is occupied
					System.out.println("Your ship collides with another ship. Please re-enter the coordinates.");
					square[x][y] = ".   ";
					square[ship[0][0]][ship[0][1]] = ".   ";
					for(int j=0;j<i;j++){
						square[x+j][y] = ".   ";
					}
					som++;
				}
				else{										//if it's to the left
					square[x+i][y] = "O   ";
					ship[i][0] = x+i;
					ship[i][1] = y;
				}
				
			}
		}
		}
		while(som>0);
	}
	public static boolean gameOver(){
		int som = 0;
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(square[j][i].equals("O   ")){
					som++;
				}
			}
		}
		if(som==0) return true;
		else return false;
	}
	public static void fire() throws Exception{
		
		String explode = "/Users/Hades/workspace/Personal/src/zeeslag/explode1.wav";
	    InputStream ex = new FileInputStream(explode);
	    AudioStream explodeStream = new AudioStream(ex);
		String splash = "/Users/Hades/workspace/Personal/src/zeeslag/splash.wav";
	    InputStream spl = new FileInputStream(splash);
	    AudioStream splashStream = new AudioStream(spl);
		
		String test;
		do{
			test = "";
			System.out.println("\nGive your target location (e.g. A10):");
			String target = sc.nextLine().toUpperCase();
			
			while(	target.equals("") ||
					(!Character.isAlphabetic(target.charAt(0))) ||
					(target.charAt(0) < 'A' || target.charAt(0) > 'J') ||
					(target.length()!=2 && target.length()!=3) ||
					(Character.isAlphabetic(target.charAt(1))) ||
					(Character.isAlphabetic(target.charAt(target.length()-1))) ||
					((Integer.parseInt(target.substring(1)) < 1) || (Integer.parseInt(target.substring(1)) > 10))){
				System.out.println("Please insert a valid coordinate.");
				target = sc.nextLine().toUpperCase();
			}
			
			int y = target.charAt(0)-65;
			int x = Integer.parseInt(target.substring(1))-1;
			System.out.println();
			if(Opponent.getArray()[x][y] == "O   "){
				Opponent.getArray()[x][y] = "X   ";
				System.out.println("DIRECT HIT!!\n");
				AudioPlayer.player.start(explodeStream);
				Battle.displayBothArrays();
			}
			else if(Opponent.getArray()[x][y] == ".   "){
				Opponent.getArray()[x][y] = "*   ";
				System.out.println("missed :(\n");
				AudioPlayer.player.start(splashStream);
				Battle.displayBothArrays();
			}
			else{
				test = "You already shot this place... Try again.";
				System.out.println(test);
			}
		}
		while(test.equals("You already shot this place... Try again."));
	}
	public static String[][] getArray(){
		return square;
	}
	public static boolean isShipSunk(int[][] ship){
		int som = 0;
		for(int i=0;i<ship.length;i++){
			if(square[ship[i][0]][ship[i][1]].equals("X   ")){
				som++;
			}
		}
		if(som==ship.length) return true;
		else return false;
	}
	public static int[][] getMineSweeper(){
		return mineSweeper;
	}
	public static int[][] getFrigate(){
		return frigate;
	}
	public static int[][] getSubmarine(){
		return submarine;
	}
	public static int[][] getCruiser(){
		return cruiser;
	}
	public static int[][] getAircraftCarrier(){
		return aircraftCarrier;
	}
}
