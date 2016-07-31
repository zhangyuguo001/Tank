package tank6;
import java.io.*;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
class AePlayWave extends Thread  //创建声音自动播放函数
{
	private String filename;
	public AePlayWave(String wavfile)
	{
		filename = wavfile;
	}
	public void run()
	{
		File soundFile=new File(filename);
		AudioInputStream audioInputStream=null;
		try {
			audioInputStream=AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		AudioFormat format=audioInputStream.getFormat();
		SourceDataLine auline=null;
		DataLine.Info info=new DataLine.Info(SourceDataLine.class, format);
		try {
			auline=(SourceDataLine)AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		auline.start();
		int nBytesRead=0;
		byte[] abData=new byte[1024];
		try {
			while(nBytesRead!=-1)
			{
				nBytesRead=audioInputStream.read(abData,0,abData.length);
				if(nBytesRead>=0)
				{
					auline.write(abData, 0, nBytesRead);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}finally
		{
			auline.drain();
			auline.close();
		}
	}
}
class Node
{
	int x=0;
	int y=0;
	int direct=0;
	public Node(int x,int y,int direct)
	{
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
}
class Recorder
{
	private static int enNum=5;//设置每关敌人坦克数量
	private static int myLife=3;//设置我有多少可用的坦克
	private static int allEnNum=0; //设置消灭敌人坦克的总数
	private static FileWriter fw=null;
	private static BufferedWriter bw=null;
	private static FileReader fr=null;
	private static BufferedReader br=null;
	private static Vector<EnemyTank> ets=new Vector<EnemyTank>();
	public static void keepRecAndEnemyTank()  //保存并退出游戏，保存敌人坦克坐标
	{
		try {
			fw=new FileWriter("D:\\A\\baocun\\tanksave.txt");
			bw=new BufferedWriter(fw);
			bw.write(allEnNum+"\r\n");  //记录敌人坦克数量
			for(int i=0;i<ets.size();i++)
			{
				EnemyTank et=ets.get(i);
				if(et.islive)
				{
					String recode=et.x+" "+et.y+" "+et.direct+" ";
					bw.write(recode+"\r\n");
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static Vector<Node> nodes=new Vector<Node>();
	public static Vector<Node> getNodesAndEnNums()//继续游戏，从保存点继续开始
	{
		try {
			fr=new FileReader("D:\\A\\baocun\\tanksave.txt");
			br=new BufferedReader(fr);
			String n="";
			n=br.readLine();
			allEnNum=Integer.parseInt(n);
			while((n=br.readLine())!=null)
			{
				String []xyz=n.split(" ");
				Node node=new Node(Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
				nodes.add(node);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	finally
		{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nodes;
	}
	
	public static Vector<EnemyTank> getEts() {
		return ets;
	}
	public static void setEts(Vector<EnemyTank> ets) {
		Recorder.ets = ets;
	}
	public static int getAllEnNum() {
		return allEnNum;
	}
	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}	
	public static void reduceEnNum()//若打掉一个敌人坦克就减少一个
	{
		enNum--;
	}
	public static void allEnNum()
	{
		allEnNum++;
	}
	public static void reduceMyLife()
	{
		myLife--;
	}
	//保存坦克的记录
	public static void keepRecording()
	{
		try {
			fw=new FileWriter("D:\\A\\baocun\\tanksave.txt");
			bw=new BufferedWriter(fw);
			bw.write(allEnNum+"\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//恢复坦克的记录
	public static void getRecording()
	{
		try {
			fr=new FileReader("D:\\A\\baocun\\tanksave.txt");
			br=new BufferedReader(fr);
			String n=br.readLine();
			allEnNum=Integer.parseInt(n);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	finally
		{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
class Tank  //定义坦克类
{
	int x=0;
	int y=0;//表示坦克的横纵坐标
	
	int direct=0;//坦克方向：0表示上    1表示右    2表示下    3表示左
	int speed=1;//设置坦克的速度，
	int color;
	boolean islive=true;
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Tank(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
}
//敌人的坦克
class EnemyTank extends Tank implements Runnable
{
	int times=0;
	int speed=1;
	boolean islive=true;//初始化敌人坦克活着
	Vector<EnemyTank> ets=new Vector<EnemyTank>();
	Vector<Shot> ss=new Vector<Shot>();//定义一个敌人子弹向量，主要用于存放敌人坦克的子弹
	public EnemyTank (int x,int y)
	{
		super(x,y);
	}
	public void setEts(Vector<EnemyTank> tank)
	{
		this.ets=tank;
	}
	public boolean isTouchOtherEnemy()
	{
		boolean b=false;
		switch(this.direct)
		{
		case 0://向上
			for(int i=0;i<ets.size();i++)
			{
				EnemyTank et=ets.get(i);
				if(et!=this)
				{
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
					}
					if(et.direct==1||et.direct==3)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
					}
				}
			}
			break;
		case 1: //向右
			for(int i=0;i<ets.size();i++)
			{
				EnemyTank et=ets.get(i);
				if(et!=this)
				{
					if(et.direct==0||et.direct==2)
					{
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
						{
							return true;
						}
					}
					if(et.direct==1||et.direct==3)
					{
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20)
						{
							return true;
						}
					}
				}
			}
			break;
		case 2://向下
			for(int i=0;i<ets.size();i++)
			{
				EnemyTank et=ets.get(i);
				if(et!=this)
				{
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
						{
							return true;
						}
					}
					if(et.direct==1||et.direct==3)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
						{
							return true;
						}
					}
				}
			}
			break;
		case 3://向左
			for(int i=0;i<ets.size();i++)
			{
				EnemyTank et=ets.get(i);
				if(et!=this)
				{
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
						{
							return true;
						}
					}
					if(et.direct==1||et.direct==3)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20)
						{
							return true;
						}
					}
				}
			}
			break;
		}
		return b;	
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			switch(this.direct)
			{
			case 0: //坦克向上移动
				for(int i=0;i<30;i++)
				{
					if(y>0&&!this.isTouchOtherEnemy())
					{
						y-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 1://坦克向右移动
				for(int i=0;i<30;i++)
				{
					if(x<(400-30)&&!this.isTouchOtherEnemy())//考虑以区域边界为坦克坐标起点是   坦克会越界的，需要减去坦克的宽度或长度
					{
						x+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 2://坦克向下移动
				for(int i=0;i<30;i++)
				{
					if(y<(300-30)&&!this.isTouchOtherEnemy())//考虑以区域边界为坦克坐标起点是   坦克会越界的，需要减去坦克的宽度或长度
					{
						y+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 3: //坦克向左移动
				for(int i=0;i<30;i++)
				{
					if(x>0&&!this.isTouchOtherEnemy())
					{
						x-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
			this.direct=(int)(Math.random()*4);//让坦克随机产生一个新的行驶方向
			if(this.islive==false)//判断坦克是否已经死亡
			{
				break;
			}
			//让敌人坦克添加新的子弹
			this.times++;
			if(times%2==0)
			{
				if(islive)
				{
					if(ss.size()<5)//敌人坦克会产生5颗子弹
					{	
						Shot s=null;
						switch(direct)
						{
						case 0: s=new Shot(x+9,y-6,0,speed); ss.add(s); break;//上面
						case 2: s=new Shot(x+9,y+33,2,speed);ss.add(s);break;//下面
						case 3: s=new Shot(x-5,y+10,3,speed);ss.add(s);break;//左面
						case 1: s=new Shot(x+33,y+9,1,speed);ss.add(s);break;//右面
						}
						Thread t=new Thread(s);
						t.start();
					}
				}
			}
		}
	}	
}
//我的坦克
class Hero extends Tank
{	
	int speedH=2;
	int speedS=5;
	public Hero(int x,int y)
	{
		super(x,y);
	}
	Vector<Shot> ss=new Vector<Shot>();//定义子弹的向量，
	Shot s=null;
	public void shotEnemy()
	{
		switch(this.direct)//定义第一颗子弹出现的位置
		{
		case 0: s=new Shot(x+9,y-6,0,speedS);ss.add(s); break;//上面
		case 2: s=new Shot(x+9,y+33,2,speedS);ss.add(s);break;//下面
		case 3: s=new Shot(x-5,y+10,3,speedS);ss.add(s);break;//左面
		case 1: s=new Shot(x+33,y+9,1,speedS);ss.add(s);break;//右面
		}
		//启动子弹线程
		Thread t=new Thread(s);
		t.start();
	}
	public void moveUp()//坦克向上移动
	{
		if(y>=0)
		{
		y-=speedH;
		}
	}
	public void moveRight()//坦克向右移动
	{
		if(x<=400-32)
		{
		x+=speedH;
		}
	}
	public void moveDown()//坦克向下移动
	{
		if(y<=300-32)
		{
		y+=speedH;
		}
	}
	public void moveLeft()//坦克向左移动
	{
		if(x>=0)
		{
		x-=speedH;
		}
	}
}
//定义炸弹类
class Bomb
{
	int x,y;
	int life=4;
	boolean isLive=true;
	public Bomb(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	public void lifeDown()
	{
		if(life>0)
		{
			life--;
		}
		else 
		{
			this.isLive=false; 
		}
	}
}
//定义子弹
class Shot implements Runnable
{
	int x,y;
	int direct;
	int speed=1; //定义子弹速度
	boolean isLive=true;
	public Shot(int x,int y,int direct,int speed)
	{
		this.x=x;
		this.y=y;
		this.direct=direct;
		this.speed=speed;
	}
	public void run() {
		while(true)
		{
			try {
				Thread.sleep(50);//休息50ms
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(direct) //0 向上   1向右   2向下  3向左
			{
			case 0: y-=speed;break; 
			case 1: x+=speed;break;
			case 2: y+=speed;break;
			case 3: x-=speed;break;
			}
			//设置子弹的死亡，触碰边界就死亡，不在耗费内存
			if(x<0||x>400||y<0||y>300)
			{
				this.isLive=false;
				break;
			}
		}		
	}
}