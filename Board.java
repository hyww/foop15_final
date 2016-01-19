import java.lang.*;
import java.util.*;

public class Board{
	public static final int initScore = 25000;
	public static final int games = 1;	//1東風戰 2東南戰...
	public static int wind;	//0東 1南 2西 3北
	public static String[] actionString = {
	 "",
	 "吃",
	 "碰",
	 "槓",
	 "加槓",
	 "暗槓",
	 "立直",
	 "榮",
	 "胡"
	};
	public static int game;
	public static int dealer; //一開始的莊家
	private static Shuffler shuffler;
	private static comGUI GUI;
	public static void printTiles(ArrayList<Tile> tiles){
		for(Tile t:tiles){
			System.out.print(t.toString()+t.getSize()+",");
		}		
	}
	public static void main(String args[]){
		wind = 0;
		dealer = 0;	//maybe we should decide this randomly?
		game = 0;
		shuffler = new Shuffler();
		GUI = new comGUI();
		Player[] player = new Player[4];
		ArrayList<ArrayList<Tile>> allTiles = new ArrayList<ArrayList<Tile>>();//0萬 1筒 2條 3字
		//GUI.initPlayerGUI("PlayerGUI", initScore);
		//player[0] = GUI.player;
		for(int i = 0; i < 4 ; i++){
			player[i] = new AI("PlayerAI"+i, initScore);
		}
		for(int i = 0 ; i < 4 ; i++){
			allTiles.add(new ArrayList<Tile>());
		}
		while(true){

			//init 4 players' hands
			for(int i = 0 ; i < 4 ; i++){
				for(int j = 0 ; j < 4 ; j++){
					allTiles.get(j).clear();
				}
				for(int j = 0 ; j < 13 ; j++){
					Tile tmpTile = shuffler.getNext();
					allTiles.get(tmpTile.suit).add(tmpTile);
				}
				player[i].initHand(allTiles);
			}
			
			int gameOver = 0;
			int current = (dealer+game)%4;//看第幾局決定輪到誰做莊，莊家開始，抽牌、決定動作
			Tile tile = shuffler.getNext();
			Action action = player[current].doSomething(0, tile);
			while(gameOver == 0){
				System.out.println("DEBUG: "+"wind: "+wind+"game: "+game+player[current]+actionString[action.type]+".");
				switch(action.type){//執行動作
					case 0:	//摸
					case 1:	//吃
					case 2:	//碰
					case 6:	//立直
						tile = action.tiles.get(0);	//打出來的牌
						Action selectAction = null;
						int selectPlayer = null;
						for(int i = 1 ; i < 4 ; i++){//問另外三家有沒有事情要做
							int p = (current+i)%4;
							System.out.println("wait "+p+" "+tile+" "+tile.getSize());
							action = player[p].doSomething(4-p, tile);
							if(action == null) continue;
							System.out.println(p+" "+actionString[action.type]);
							if(selectPlayer == null || action.type > selectAction.type){
								if(selectPlayer != null)player[selectPlayer].failed();
								selectAction = action;
								selectPlayer = p;
							}
							else player[p].failed();
						}
						if(selectAction != null){//執行最優先動作, 榮>碰>吃, 設定好動作、玩家後continue跳到該玩家執行動作，未考慮同時榮的情形:p
							action = selectAction;
							current = selectPlayer;
							continue;
						}
						else{//換下一家，到switch外面抽牌、決定動作
							current = (current+1)%4;
						}
						break;
					case 3:	//槓
					case 4:	//加槓
					case 5:	//暗槓
						shuffler.ackKong();
						break;	//目前玩家補一張，到switch外面抽牌、決定動作
					case 7:	//榮
					case 8:	//自摸
						printTiles(action.tiles);
						if(current != (dealer+game)%4){//當局莊家沒有連莊就要輪莊，進入下一局
							game++;
						}
						shuffler.permuteIndex();
						gameOver = 1;
						//do something
						break;
					default:
						System.out.println("ERROR: "+player[current]+" unknown action "+action.type+".");
						System.exit(1);
				}
				if(gameOver == 1)break;
				tile = shuffler.getNext();//switch外面指的是這裡^^
				if(tile == null){//流局
					gameOver = 1;
					//do something
					break;
				}
				System.out.println("self "+current+" "+tile+" "+tile.getSize());
				action = player[current].doSomething(0, tile);
			}
			if(game == 4){	//打滿4局，南(?入
				wind = wind+1;
				game = 0;
			}
			if(wind == games)	//結束
				break;
		}

	}

}
