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
class AePlayWave extends Thread  //���������Զ����ź���
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
	private static int enNum=5;//����ÿ�ص���̹������
	private static int myLife=3;//�������ж��ٿ��õ�̹��
	private static int allEnNum=0; //�����������̹�˵�����
	private static FileWriter fw=null;
	private static BufferedWriter bw=null;
	private static FileReader fr=null;
	private static BufferedReader br=null;
	private static Vector<EnemyTank> ets=new Vector<EnemyTank>();
	public static void keepRecAndEnemyTank()  //���沢�˳���Ϸ���������̹������
	{
		try {
			fw=new FileWriter("D:\\A\\baocun\\tanksave.txt");
			bw=new BufferedWriter(fw);
			bw.write(allEnNum+"\r\n");  //��¼����̹������
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
	public static Vector<Node> getNodesAndEnNums()//������Ϸ���ӱ���������ʼ
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
	public static void reduceEnNum()//�����һ������̹�˾ͼ���һ��
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
	//����̹�˵ļ�¼
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
	//�ָ�̹�˵ļ�¼
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
class Tank  //����̹����
{
	int x=0;
	int y=0;//��ʾ̹�˵ĺ�������
	
	int direct=0;//̹�˷���0��ʾ��    1��ʾ��    2��ʾ��    3��ʾ��
	int speed=1;//����̹�˵��ٶȣ�
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
//���˵�̹��
class EnemyTank extends Tank implements Runnable
{
	int times=0;
	int speed=1;
	boolean islive=true;//��ʼ������̹�˻���
	Vector<EnemyTank> ets=new Vector<EnemyTank>();
	Vector<Shot> ss=new Vector<Shot>();//����һ�������ӵ���������Ҫ���ڴ�ŵ���̹�˵��ӵ�
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
		case 0://����
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
		case 1: //����
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
		case 2://����
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
		case 3://����
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
			case 0: //̹�������ƶ�
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
			case 1://̹�������ƶ�
				for(int i=0;i<30;i++)
				{
					if(x<(400-30)&&!this.isTouchOtherEnemy())//����������߽�Ϊ̹�����������   ̹�˻�Խ��ģ���Ҫ��ȥ̹�˵Ŀ�Ȼ򳤶�
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
			case 2://̹�������ƶ�
				for(int i=0;i<30;i++)
				{
					if(y<(300-30)&&!this.isTouchOtherEnemy())//����������߽�Ϊ̹�����������   ̹�˻�Խ��ģ���Ҫ��ȥ̹�˵Ŀ�Ȼ򳤶�
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
			case 3: //̹�������ƶ�
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
			this.direct=(int)(Math.random()*4);//��̹���������һ���µ���ʻ����
			if(this.islive==false)//�ж�̹���Ƿ��Ѿ�����
			{
				break;
			}
			//�õ���̹������µ��ӵ�
			this.times++;
			if(times%2==0)
			{
				if(islive)
				{
					if(ss.size()<5)//����̹�˻����5���ӵ�
					{	
						Shot s=null;
						switch(direct)
						{
						case 0: s=new Shot(x+9,y-6,0,speed); ss.add(s); break;//����
						case 2: s=new Shot(x+9,y+33,2,speed);ss.add(s);break;//����
						case 3: s=new Shot(x-5,y+10,3,speed);ss.add(s);break;//����
						case 1: s=new Shot(x+33,y+9,1,speed);ss.add(s);break;//����
						}
						Thread t=new Thread(s);
						t.start();
					}
				}
			}
		}
	}	
}
//�ҵ�̹��
class Hero extends Tank
{	
	int speedH=2;
	int speedS=5;
	public Hero(int x,int y)
	{
		super(x,y);
	}
	Vector<Shot> ss=new Vector<Shot>();//�����ӵ���������
	Shot s=null;
	public void shotEnemy()
	{
		switch(this.direct)//�����һ���ӵ����ֵ�λ��
		{
		case 0: s=new Shot(x+9,y-6,0,speedS);ss.add(s); break;//����
		case 2: s=new Shot(x+9,y+33,2,speedS);ss.add(s);break;//����
		case 3: s=new Shot(x-5,y+10,3,speedS);ss.add(s);break;//����
		case 1: s=new Shot(x+33,y+9,1,speedS);ss.add(s);break;//����
		}
		//�����ӵ��߳�
		Thread t=new Thread(s);
		t.start();
	}
	public void moveUp()//̹�������ƶ�
	{
		if(y>=0)
		{
		y-=speedH;
		}
	}
	public void moveRight()//̹�������ƶ�
	{
		if(x<=400-32)
		{
		x+=speedH;
		}
	}
	public void moveDown()//̹�������ƶ�
	{
		if(y<=300-32)
		{
		y+=speedH;
		}
	}
	public void moveLeft()//̹�������ƶ�
	{
		if(x>=0)
		{
		x-=speedH;
		}
	}
}
//����ը����
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
//�����ӵ�
class Shot implements Runnable
{
	int x,y;
	int direct;
	int speed=1; //�����ӵ��ٶ�
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
				Thread.sleep(50);//��Ϣ50ms
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(direct) //0 ����   1����   2����  3����
			{
			case 0: y-=speed;break; 
			case 1: x+=speed;break;
			case 2: y+=speed;break;
			case 3: x-=speed;break;
			}
			//�����ӵ��������������߽�����������ںķ��ڴ�
			if(x<0||x>400||y<0||y>300)
			{
				this.isLive=false;
				break;
			}
		}		
	}
}