package zeeslag;

import java.util.Random;

public class Randomizers {

	private static Random r = new Random();
	
	public static int posXorY(){
		int a = r.nextInt(10);
		return a;
	}
	public static boolean direction(){				//true = horizontal
		boolean b = r.nextBoolean();
		return b;
	}
	public static int timeToAttack(){
		int a = r.nextInt(4)+3;
		return a;
	}
	public static int deviation(){
		int a = r.nextInt(3)-1;
		return a;
	}
}
