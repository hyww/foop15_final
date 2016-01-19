import java.awt.EventQueue;
import java.util.ArrayList;
class comGUI
{
	public static final int tableIndex = 0;
	public static final int myPlayerOpenIndex = 1;
	public static final int rightPlayerIndex = 2;
	public static final int upPlayerIndex = 3;
	public static final int leftPlayerIndex = 4;
	public static final int myPlayerHandIndex = 5;
	
	
	private int numRightPlayer, numUpPlayer, numLeftPlayer;
	private int numRightPlayerExposedKong, numUpPlayerExposedKong, numLeftPlayerExposedKong;
	
	private ArrayList<Tile> rightPlayerOpen;
	private ArrayList<Tile> upPlayerOpen;
	private ArrayList<Tile> leftPlayerOpen;
	private ArrayList<Tile> myPlayerOpen;
	private ArrayList<Tile> table;
	private ArrayList<Tile> myPlayerHand;
	
	private ArrayList<Tile> rightPlayerHand;
	private ArrayList<Tile> upPlayerHand;
	private ArrayList<Tile> leftPlayerHand;
	
	public FrameTest frame;
	
	public comGUI.PlayerGUI player;
	
	public comGUI()
	{
		numLeftPlayer = 0;
		numRightPlayer = 0;
		numUpPlayer = 0;
		
		leftPlayerOpen = new ArrayList<Tile>();
		upPlayerOpen = new ArrayList<Tile>();
		rightPlayerOpen = new ArrayList<Tile>();
		myPlayerOpen = new ArrayList<Tile>();
		myPlayerHand = new ArrayList<Tile>();
		table = new ArrayList<Tile>();
		
		leftPlayerHand = new ArrayList<Tile>();
		upPlayerHand = new ArrayList<Tile>();
		rightPlayerHand = new ArrayList<Tile>();
		
		frame = new FrameTest();
		//player = new comGUI.PlayerGUI("A");
	}
	public void initPlayerGUI(String name, int score, comGUI _c)
	{
		player = new comGUI.PlayerGUI(name, score);
		player.c = _c;
	}
	
	public void renewGUI()
	{
		ArrayList<ArrayList<Tile>> temp = new ArrayList<ArrayList<Tile>>();
		temp.add(table);
		temp.add(myPlayerOpen);
		temp.add(rightPlayerOpen);
		temp.add(upPlayerOpen);
		temp.add(leftPlayerOpen);
		
		player.getHand();
		temp.add(player.myHand);
		
		int[] tempNum = new int[3];
		tempNum[0] = numRightPlayer;
		tempNum[1] = numUpPlayer;
		tempNum[2] = numLeftPlayer;
		
		
		frame.setAllContent(temp, tempNum);
		frame.reset();
	}
	
	
	public void assignTile(ArrayList<ArrayList<Tile>> allTile)
	{
		table = allTile.get(tableIndex);
		rightPlayerOpen = allTile.get(rightPlayerIndex);
		leftPlayerOpen = allTile.get(leftPlayerIndex);
		upPlayerOpen = allTile.get(upPlayerIndex);
		myPlayerOpen = allTile.get(myPlayerOpenIndex);
		
		//myPlayerHand = allTile.get(myPlayerHandIndex);
		myPlayerHand = player.myHand;
	}
	public void assignTile(int which, ArrayList<Tile> allTile)
	{
		if(which == tableIndex)
			table = allTile;
		else if(which == rightPlayerIndex)
			rightPlayerOpen = allTile;
		else if(which == leftPlayerIndex)
			leftPlayerOpen = allTile;
		else if(which == upPlayerIndex)
			upPlayerOpen = allTile;
		else if(which == myPlayerOpenIndex)
			myPlayerOpen = allTile;
		//else
			//myPlayerHand = allTile;
	}
	public void assignHandNum(int which, int num)
	{
		if(which == rightPlayerIndex)
			numRightPlayer = num;
		else if(which == leftPlayerIndex)
			numLeftPlayer = num;
		else
			numUpPlayer = num;
	}
	public void assignHandNum(int[] num)
	{
		numRightPlayer = num[0];
		numUpPlayer = num[1];
		numLeftPlayer = num[2];
		
	}
	public void assignExposedKongNum(int which, int num)
	{
		if(which == rightPlayerIndex)
			numRightPlayerExposedKong = num;
		else if(which == leftPlayerIndex)
			numLeftPlayerExposedKong = num;
		else
			numUpPlayerExposedKong = num;
	}
	public void flipTile(int index, ArrayList<Tile> tile)
	{
		if(index == 0)
			frame.setFlip(index, rightPlayerHand);
		else if(index == 1)
			frame.setFlip(index, upPlayerHand);
		else if(index == 2)
			frame.setFlip(index, leftPlayerHand);
	}
	
	
	public void showGUI()
	{
		frame.start();
	}
	
	
	
	public class PlayerGUI extends Player
	{
		public ArrayList<Tile> myHand;
		//private Hand hand;
		private Tile newTile;
		private ArrayList<Tile> discardTile;
		private ArrayList<Tile> pushTile;
		private boolean[] choice;
		private int action;
		
		comGUI c;
		
		public PlayerGUI(String name, int score)
		{
			super(name, score);
			myHand = new ArrayList<Tile>();
		}
		
		/*public PlayerGUI(String name)
		{
			super(name);
		}*/
		
		public void failed()
		{
			frame.actionFail();
		}
		
		@Override
		public void initHand(ArrayList<ArrayList<Tile>> allTiles)
		{
			frame.setFlip(-1, new ArrayList<Tile>());
			//myHand = new ArrayList<Tile>();
			hand = new Hand(allTiles);
			getHand();
		}
		

		public Action doSomething(int from, Tile tile)
		{
			if(from == 0){
				//myHand.add(tile);
				hand.add(tile);
				
			}
			//boolean[] b;
			newTile = tile.same();
			doSelect(from, newTile);
			
			//frame.changeEnable(false);
			if(action == -1)
				return null;
			return new Action(action, discardTile);
		}
		
		
		
		private void doSelect(int from, Tile newTile)
		{
			//boolean[] b = {true, true, true, true, true};
			boolean[] b = {false, false, false, false, false}; /*吃, 碰, 槓, 聽, 胡*/
			
			//boolean[] b = {true, true, true, false, false};
			int tempType = hand.chowable(newTile);
			ArrayList<Tile> temp = hand.tingable(newTile);
			
			if(tempType != 0 && from == 3){
				b[0] = true;
				frame.setChowOption(tempType, getChewChoice(tempType, newTile));
			}
			
			if(from != 0)
				b[1] = hand.pongable(newTile);
			
			b[2] = hand.kongable(newTile);
			
			if(temp == null)
				b[4] = true;
			else if(temp.size() != 0 && from == 0){
				//b[3] = true;
			}
			frame.setThrower(from, newTile);
			/*System.out.println("AAA " + from +" "+ newTile);
			for(int i = 0 ; i < 5; i++)
				if(b[i])
					System.out.println("Choice " + i);*/
			//b[0] = true;
			//tempType = 7;
			frame.setChowOption(tempType, getChewChoice(tempType, newTile));
			if(b[0] || b[1] || b[2] || b[3] || b[4]){
				frame.setSelect(b);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							frame.frameOpen();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				waitOK();
			}
			else if(from != 0){
				action = -1;
				return;
			}
			else
				action = 0;
			
			choice = frame.getChoice();
			selectProcess(tempType, newTile, from);
		}
		private void waitOK()
		{
			while(frame.ok == false){}
			pushTile = new ArrayList<Tile>();
			for(Tile t : frame.push)
				pushTile.add(t.same());
			
			/*for(int i = 0; i < 6; i++)
				if(choice[i])
					System.out.println(i);*/
			/*for(Tile t : frame.push)
				System.out.println(t);*/
			frame.ok = false;
			frame.push = new ArrayList<Tile>();
			frame.changeEnable(false);
		}
		/*private boolean remove(Tile t)
		{
			boolean b = false;
			for(Tile temp : myHand)
				if(temp.equals(t)){
					myHand.remove(temp);
					b = true;
				}
			return b;
		}*/
		
		private void selectProcess(int chewType, Tile newTile, int from)
		{
			discardTile = new ArrayList<Tile>();
			discardTile.add(new Tile(0));
			if(choice[0]){
				action = 1;
				ArrayList<ArrayList<Tile>> chowOption = getChewChoice(chewType, newTile);
				if(chowOption.size() == 1){
					for(int i = 0; i < 3; i++)
						discardTile.add(chowOption.get(0).get(i));
				}
				else{
					for(int i = 0; i < 3; i++)
						discardTile.add(pushTile.get(i).same());
				}
			}
			else if(choice[1]){
				action = 2;
				for(int i = 0; i < 3; i++)
					discardTile.add(newTile.same());
			}
			else if(choice[2]){
				action = 3;
				if(from == 0)
					action = 5;
				for(int i = 0; i < 4; i++)
					discardTile.add(newTile.same());
			}
			else if(choice[3]){
				action = 6;
				discardTile.set(0, pushTile.get(0));
				return;
			}
			else if(choice[4])
			{
				action = 7;
				if(from == 0)
					action = 8;
				getHand();
				discardTile.set(0, myHand.get(0));
				for(int i = 1; i < myHand.size(); i++)
					discardTile.add(myHand.get(i));
				return;
			}
			
			for(int i = 1; i < discardTile.size() - 1; i++)
				hand.discard(discardTile.get(i));
			c.renewGUI();
			
			if(choice[2]){
				discardTile.remove(0);
			}
			else{
				frame.changeEnable(true);
				waitOK();
				discardTile.set(0, pushTile.get(0));
			}
			for(Tile t: discardTile)
				System.out.println(t);
			
			hand.discard(discardTile.get(0));
			frame.resetChoice();
			c.renewGUI();
		}
		private ArrayList<ArrayList<Tile>> getChewChoice(int flag, Tile newTile)
		{
			ArrayList<ArrayList<Tile>> temp = new ArrayList<ArrayList<Tile>>();
			if((flag & 0b001) > 0){
				ArrayList<Tile> temp1 = new ArrayList<Tile>();
				temp1.add(newTile.same(-2));
				temp1.add(newTile.same(-1));
				temp1.add(newTile.same());
				temp.add(temp1);
			}
			if((flag & 0b010) > 0){
				ArrayList<Tile> temp1 = new ArrayList<Tile>();
				temp1.add(newTile.same(-1));
				temp1.add(newTile.same(1));
				temp1.add(newTile.same());
				temp.add(temp1);
			}
			if((flag & 0b100) > 0){
				ArrayList<Tile> temp1 = new ArrayList<Tile>();
				temp1.add(newTile.same(1));
				temp1.add(newTile.same(2));
				temp1.add(newTile.same());
				temp.add(temp1);
			}
			return temp;
		}
		private void getHand()
		{
			myHand = new ArrayList<Tile>();
			for(ArrayList<Tile> temp : hand.getAll())
				for(Tile t : temp)
					for(int i = 0; i < t.getSize(); i++){
						myHand.add(new Tile(t.suit, t.value, t.index));
					}
		}
		
		public void GameOver()
		{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						if(action == 7 || action == 8)
							frame.hu(true);
						else
							frame.hu(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		
		
		
		
		/*public boolean doDraw(Tile tile, ArrayList<ArrayList<Tile>> currentTable)
		{
			newTile = tile.same();
			doSelect(newTile);
			return true;
		}*/
		/*public boolean doChow(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[0];}
		public boolean doPong(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[1];}
		public boolean doKong(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[2];}
		public boolean doReach(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[3];}
		public boolean doHu(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return choice[4];}*/
		
		//public Tile replace(Tile tile, ArrayList<ArrayList<Tile>> currentTable){return new Tile(0);}
		//public Tile kong(ArrayList<ArrayList<Tile>> currentTable){return new Tile(0);}
	}
	
	
	
	
	
	

}