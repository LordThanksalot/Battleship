package zeeslag;

//problem to solve first: sometimes thinks an attacked ship has already sunk while it is still standing (top fire method)
//it thinks of the last ship that was hit and fills it in for all hit ships
//at least it never forgets the first hit ship
//what if you have the end of the ship where you want to reach the other end, but the last point to shoot first is an unshootable
//intelligence necessities: fire at 1 around X if ship is not sunk. New: From two X on same ship, follow direction

import java.io.*;
import sun.audio.*;

public class Opponent2 {

	private static int x;		//starting position ship or missile target
	private static int y;
	
	private static int[][] mineSweeper = new int[2][2];			//array is [number of spots][x,y]
	private static int[][] frigate = new int[3][2];
	private static int[][] submarine = new int[3][2];
	private static int[][] cruiser = new int[4][2];
	private static int[][] aircraftCarrier = new int[5][2];
	
	private static String[][] square = new String[10][10];
	
	private static int[][] memory = new int[100][2];
	
	static {
		for(int i=0;i<memory.length;i++){
			memory[i][0] = -1;
			memory[i][1] = -1;
		}
	}
	
	public static void fillArray(){
		for(int y=0;y<10;y++){
			for(int x=0;x<10;x++){
				square[x][y] = ".   ";
			}
		}
		placeShip(aircraftCarrier);				//placement is downwards or to the right of starting position
		placeShip(cruiser);
		placeShip(submarine);
		placeShip(frigate);
		placeShip(mineSweeper);
	}
	private static void randomPosition(){
		x = Randomizers.posXorY();
		y = Randomizers.posXorY();
	}
	private static void placeShip(int[][] ship){
		int som;							//if there is a collision, sum will be above zero, so reset of this ship is necessary
		do{
		do{
			randomPosition();					//creates initial position
			}
			while((x+ship.length-1>9&&y+ship.length-1>9)||square[x][y].equals("O   "));		//if there is no room to expand ship or position is occupied
			square[x][y] = "O   ";
			ship[0][0] = x;
			ship[0][1] = y;
			som = 0;
			
			if(x+ship.length-1>9){									//if there is only room to expand vertically
				for(int i=1;i<ship.length&&som==0;i++){
					if(square[x][y+i].equals("O   ")){
						som++;
						for(int j=0;j<i;j++){				//if collision detected, resetting set positions except collision point
							square[x][y+j] = ".   ";
						}
					}
					else{
						square[x][y+i] = "O   ";
						ship[i][0] = x;
						ship[i][1] = y+i;
					}
				}
			}
			else if(y+ship.length-1>9){								//if there is only room to expand horizontally
				for(int i=1;i<ship.length&&som==0;i++){
					if(square[x+i][y].equals("O   ")){
						som++;
						for(int j=0;j<i;j++){
							square[x+j][y] = ".   ";
						}
					}
					else{
						square[x+i][y] = "O   ";
						ship[i][0] = x+i;
						ship[i][1] = y;
					}
				}
			}
			else if(Randomizers.direction()){					//if there is room anyway but choosing orientation. this one is horizontal
				for(int i=1;i<ship.length&&som==0;i++){
					if(square[x+i][y].equals("O   ")){
						som++;
						for(int j=0;j<i;j++){
							square[x+j][y] = ".   ";
						}
					}
					else{
						square[x+i][y] = "O   ";
						ship[i][0] = x+i;
						ship[i][1] = y;
					}
				}
			}
			else{
				for(int i=1;i<ship.length&&som==0;i++){
					if(square[x][y+i].equals("O   ")){
						som++;
						for(int j=0;j<i;j++){
							square[x][y+j] = ".   ";
						}
					}
					else{
						square[x][y+i] = "O   ";
						ship[i][0] = x;							//filling the ships array to know where it is
						ship[i][1] = y+i;
					}
				}
			}
		}
		while(som>0);
	}
	public static String[][] getArray(){
		return square;
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
	public static void fire() throws Exception{
		
		String explode = "/Users/FransM/workspace/Personal/src/zeeslag/explode1.wav";
	    InputStream ex = new FileInputStream(explode);
	    AudioStream explodeStream = new AudioStream(ex);
		String splash = "/Users/FransM/workspace/Personal/src/zeeslag/splash.wav";
	    InputStream spl = new FileInputStream(splash);
	    AudioStream splashStream = new AudioStream(spl);
	    
		int test;
			do{
			test = 0;
			int som = 0;
												//here comes the AI decision part (about recently hit targets)
			for(int i=99;i>=0;i--){				//it looks for the last hit on a still floating ship
				
				if(memory[i][0]!=-1) System.out.println(i+" != -1");																							//THESE 3 LINES ARE TO BE DELETED WHEN AI OK
				if(memory[i][0]!=-1 && Player.getArray()[memory[i][0]][memory[i][1]].equals("X   ")) System.out.println("last place ("+i+") is hit");
				if(memory[i][0]!=-1 && Player.getArray()[memory[i][0]][memory[i][1]].equals("X   ") && !isHitShipSunk(i)) System.out.println("last place hit ("+i+") has a still standing ship. Shoot the bitch.");
				
				if(memory[i][0]!=-1 && Player.getArray()[memory[i][0]][memory[i][1]].equals("X   ") && !isHitShipSunk(i)){
					System.out.println("I'm going after you!");																									//THIS LINE IS TO BE DELETED WHEN AI OK
					if(	((memory[i][0]+1 > 9) ||				//if there is no room left to shoot but the ship is still afloat
						(Player.getArray()[memory[i][0]+1][memory[i][1]].equals("X   ")||Player.getArray()[memory[i][0]+1][memory[i][1]].equals("*   "))) &&
						((memory[i][0]-1 < 0) ||
						(Player.getArray()[memory[i][0]-1][memory[i][1]].equals("X   ")||Player.getArray()[memory[i][0]-1][memory[i][1]].equals("*   "))) &&
						((memory[i][1]+1 > 9) ||
						(Player.getArray()[memory[i][0]][memory[i][1]+1].equals("X   ")||Player.getArray()[memory[i][0]][memory[i][1]+1].equals("*   "))) &&
						((memory[i][1]-1 < 0) ||
						(Player.getArray()[memory[i][0]][memory[i][1]-1].equals("X   ")||Player.getArray()[memory[i][0]][memory[i][1]-1].equals("*   ")))){
						System.out.println("Got to reach the other end");																						//THIS LINE IS TO BE DELETED WHEN AI OK
						shootShipEnd(i);
					}
					else{								//if there is room to shoot and the ship is still afloat
					int a = Randomizers.deviation();
					while(memory[i][0]+a<0||memory[i][0]+a>9){
						a = Randomizers.deviation();
					}
					x = memory[i][0]+a;
					
					if(a!=0){													//make sure the next target is not diagonally oriented from the last hit
						y = memory[i][1];
					}
					else{
						int b = Randomizers.deviation();
						while(memory[i][1]+b<0||memory[i][1]+b>9){
							b = Randomizers.deviation();
						}
						y = memory[i][1]+b;
					}
					}
				som++; break;
				}
			}
			
			if(som==0){
			y = Randomizers.posXorY();
			x = Randomizers.posXorY();
			}								//avoiding places where nothing can be found
			if((Player.getArray()[x][y] == ".   ") &&		//only if there is no ship
			(	((x+1 > 9) || (Player.getArray()[x+1][y].equals("*   ") || Player.getArray()[x+1][y].equals("X   "))) &&
				((x-1 < 0) || (Player.getArray()[x-1][y].equals("*   ") || Player.getArray()[x-1][y].equals("X   "))) &&
				((y+1 > 9) || (Player.getArray()[x][y+1].equals("*   ") || Player.getArray()[x][y+1].equals("X   "))) &&
				((y-1 < 0) || (Player.getArray()[x][y-1].equals("*   ") || Player.getArray()[x][y-1].equals("X   "))))){
				test++;
			}
			else{
			StringBuilder s = new StringBuilder();
			s.append((char)(y+65));
			s.append((x+1));
			
			if(Player.getArray()[x][y] == "O   "){
				Player.getArray()[x][y] = "X   ";
				System.out.println("\nYour opponent shot at "+s+" and YOU GOT HIT!!\n");
				AudioPlayer.player.start(explodeStream);
				}
			else if(Player.getArray()[x][y] == ".   "){
				Player.getArray()[x][y] = "*   ";
				System.out.println("\nYour opponent shot at "+s+" and missed!\n");
				AudioPlayer.player.start(splashStream);
				}
				else{
					test++;
				}
			}
			}
			while(test>0);
																	//the hit spots are saved into AI memory
			for(int i=0;i<memory.length;i++){
				if(memory[i][0]==-1){
					memory[i][0] = x;
					memory[i][1] = y; break;
				}
			}
			Battle.displayBothArrays();
			}
	public static int[][] getMemory(){
		return memory;
	}
	private static boolean isHitShipSunk(int memoryLocation){
		for(int i=0;i<Player.getAircraftCarrier().length;i++){				//below: check if ship hit was an aircraft carrier
			if(Player.getArray()[Player.getAircraftCarrier()[i][0]][Player.getAircraftCarrier()[i][1]] == 					//if location of point in ship on array == memory location on array
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){								//if yes: it is that ship. Is the ship sunken: true, otherwise: false
				System.out.println("ship last hit at "+memoryLocation+" was an aircraft carrier");
				if(Player.isShipSunk(Player.getAircraftCarrier())){
					System.out.println("the aircraft carrier actually sank");
					return true;
				}
				else return false;
			}
		}
		for(int i=0;i<Player.getCruiser().length;i++){				//below: check if ship hit was a cruiser
			if(Player.getArray()[Player.getCruiser()[i][0]][Player.getCruiser()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				System.out.println("ship last hit at "+memoryLocation+" was a cruiser");
				if(Player.isShipSunk(Player.getCruiser())){
					System.out.println("the cruiser actually sank");
					return true;
				}
				else return false;
			}
		}
		for(int i=0;i<Player.getSubmarine().length;i++){				//below: check if ship hit was a submarine
			if(Player.getArray()[Player.getSubmarine()[i][0]][Player.getSubmarine()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				System.out.println("ship last hit at "+memoryLocation+" was a submarine");
				if(Player.isShipSunk(Player.getSubmarine())){
					System.out.println("the submarine actually sank");
					return true;
				}
				else return false;
			}
		}
		for(int i=0;i<Player.getFrigate().length;i++){				//below: check if ship hit was a frigate
			if(Player.getArray()[Player.getFrigate()[i][0]][Player.getFrigate()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				System.out.println("ship last hit at "+memoryLocation+" was a frigate");
				if(Player.isShipSunk(Player.getFrigate())){
					System.out.println("the frigate actually sank");
					return true;
				}
				else return false;
			}
		}
		for(int i=0;i<Player.getMineSweeper().length;i++){				//below: check if ship hit was a mine sweeper
			if(Player.getArray()[Player.getMineSweeper()[i][0]][Player.getMineSweeper()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				System.out.println("ship last hit at "+memoryLocation+" was a mine sweeper");
				if(Player.isShipSunk(Player.getMineSweeper())){
					System.out.println("the mine sweeper actually sank");
					return true;
				}
				else return false;
			}
		}
		return false;
	}
	private static void shootShipEnd(int memoryLocation){
		int som = 0;
		for(int i=0;i<Player.getAircraftCarrier().length&&som==0;i++){				//below: check if ship hit was an aircraft carrier
			if(Player.getArray()[Player.getAircraftCarrier()[i][0]][Player.getAircraftCarrier()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				if(Player.getArray()[Player.getAircraftCarrier()[0][0]][Player.getAircraftCarrier()[0][1]].equals("O   ")){
					x = Player.getAircraftCarrier()[0][0];											//shoot one end
					y = Player.getAircraftCarrier()[0][1];
				}
				else if(Player.getArray()[Player.getAircraftCarrier()[Player.getAircraftCarrier().length-1][0]][Player.getAircraftCarrier()[Player.getAircraftCarrier().length-1][1]].equals("O   ")){
					x = Player.getAircraftCarrier()[Player.getAircraftCarrier().length-1][0];		//OR shoot the other
					y = Player.getAircraftCarrier()[Player.getAircraftCarrier().length-1][1];
				}
				som++;
			}
		}
		for(int i=0;i<Player.getCruiser().length&&som==0;i++){				//below: check if ship hit was a cruiser
			if(Player.getArray()[Player.getCruiser()[i][0]][Player.getCruiser()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				if(Player.getArray()[Player.getCruiser()[0][0]][Player.getCruiser()[0][1]].equals("O   ")){
					x = Player.getCruiser()[0][0];
					y = Player.getCruiser()[0][1];
				}
				else if(Player.getArray()[Player.getCruiser()[Player.getCruiser().length-1][0]][Player.getCruiser()[Player.getCruiser().length-1][1]].equals("O   ")){
					x = Player.getCruiser()[Player.getCruiser().length-1][0];
					y = Player.getCruiser()[Player.getCruiser().length-1][1];
				}
				som++;
			}
		}
		for(int i=0;i<Player.getSubmarine().length&&som==0;i++){				//below: check if ship hit was a submarine
			if(Player.getArray()[Player.getSubmarine()[i][0]][Player.getSubmarine()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				if(Player.getArray()[Player.getSubmarine()[0][0]][Player.getSubmarine()[0][1]].equals("O   ")){
					x = Player.getSubmarine()[0][0];
					y = Player.getSubmarine()[0][1];
				}
				else if(Player.getArray()[Player.getSubmarine()[Player.getSubmarine().length-1][0]][Player.getSubmarine()[Player.getSubmarine().length-1][1]].equals("O   ")){
					x = Player.getSubmarine()[Player.getSubmarine().length-1][0];
					y = Player.getSubmarine()[Player.getSubmarine().length-1][1];
				}
				som++;
			}
		}
		for(int i=0;i<Player.getFrigate().length&&som==0;i++){				//below: check if ship hit was a frigate
			if(Player.getArray()[Player.getFrigate()[i][0]][Player.getFrigate()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				if(Player.getArray()[Player.getFrigate()[0][0]][Player.getFrigate()[0][1]].equals("O   ")){
					x = Player.getFrigate()[0][0];
					y = Player.getFrigate()[0][1];
				}
				else if(Player.getArray()[Player.getFrigate()[Player.getFrigate().length-1][0]][Player.getFrigate()[Player.getFrigate().length-1][1]].equals("O   ")){
					x = Player.getFrigate()[Player.getFrigate().length-1][0];
					y = Player.getFrigate()[Player.getFrigate().length-1][1];
				}
				som++;
			}
		}
		for(int i=0;i<Player.getMineSweeper().length&&som==0;i++){				//below: check if ship hit was a mine sweeper
			if(Player.getArray()[Player.getMineSweeper()[i][0]][Player.getMineSweeper()[i][1]] == 
					Player.getArray()[memory[memoryLocation][0]][memory[memoryLocation][1]]){
				if(Player.getArray()[Player.getMineSweeper()[0][0]][Player.getMineSweeper()[0][1]].equals("O   ")){
					x = Player.getMineSweeper()[0][0];
					y = Player.getMineSweeper()[0][1];
				}
				else if(Player.getArray()[Player.getMineSweeper()[Player.getMineSweeper().length-1][0]][Player.getMineSweeper()[Player.getMineSweeper().length-1][1]].equals("O   ")){
					x = Player.getMineSweeper()[Player.getMineSweeper().length-1][0];
					y = Player.getMineSweeper()[Player.getMineSweeper().length-1][1];
				}
				som++;
			}
		}
	}
}
