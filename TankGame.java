package tank6;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
public class TankGame extends JFrame implements ActionListener{
	MyPanel mp=null;
	MyStartPanel msp=null;
	JMenuBar jmb=null;
	JMenu jm1=null;
	JMenuItem jmi1=null;
	JMenuItem jmi2=null;
	JMenuItem jmi3=null;
	JMenuItem jmi4=null;
	public static void main(String[] args)
	{
		TankGame tg=new TankGame();
	}
	public TankGame()
	{		
		//创建菜单及菜单选项
		jmb=new JMenuBar();
		jm1=new JMenu("游戏(G)");
		//设置快捷方式 Alt+G就可以打开
		jm1.setMnemonic('G');
		jmi1=new JMenuItem("开始新游戏(N)");
		jmi2=new JMenuItem("退出游戏(E)");
		jmi3=new JMenuItem("保存退出(C)");
		jmi4=new JMenuItem("继续上局(S)");
		
		jmi1.setMnemonic('N'); 
		jmi1.addActionListener(this); //注册监听，
		jmi1.setActionCommand("newGame");//设置对应的命令
		
		jmi2.setMnemonic('E');
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		
		jmi3.setMnemonic('C');
		jmi3.addActionListener(this);
		jmi3.setActionCommand("saveExit");
		
		jmi4.addActionListener(this);
		jmi4.setActionCommand("conGame");
		
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jm1.add(jmi4);
		
		jmb.add(jm1);
		this.setJMenuBar(jmb);
		
		msp=new MyStartPanel();
		Thread t=new Thread(msp);
		t.start();
		
		this.add(msp);
		this.setSize(560,460);//设置画板大小
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//防止内存泄漏
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("newGame"))
		{
			mp=new MyPanel("newGame");
			//启动mp线程
			Thread t=new Thread(mp);//将画板设为线程，因此要启动画板线程才能正常运作
			t.start();
			this.remove(msp);
			this.add(mp);
			
			//注册监听
			this.addKeyListener(mp);
			this.setVisible(true);
		}
		else if(e.getActionCommand().equals("exit"))
		{
			//用户点击了 退出命令的菜单
			Recorder.keepRecording();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("saveExit"))
		{
			//存盘退出，要保存敌人摧毁坦克的数量和坦克子弹的坐标
			Recorder.setEts(mp.ets);//让Recorder知道ets在mp中
			Recorder.keepRecAndEnemyTank();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("conGame"))
		{
			mp=new MyPanel("conGame");
			//启动mp线程
			Thread t=new Thread(mp);//将画板设为线程，因此要启动画板线程才能正常运作
			t.start();
			this.remove(msp);
			this.add(mp);
			
			//注册监听
			this.addKeyListener(mp);
			this.setVisible(true);
		}
	}
}
//定义一个提示画板，提示分关
class MyStartPanel extends JPanel implements Runnable
{
	int times=0;
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400,300);
		
		//提示信息
		if(times%2==0)
		{
			g.setColor(Color.yellow);
			Font myFont=new Font("华文新魏",Font.BOLD,30);
			g.setFont(myFont);
			g.drawString("Stage: 1", 150, 150);
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try {
				Thread.sleep(500); //500毫秒
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			times++;
			this.repaint();
		}
	}
}
class MyPanel extends JPanel implements KeyListener,Runnable//定义我的面板 ,把画板也做成线程
{
	String flag="newGame";
	
	Hero hero=null;//定义一个我的坦克
	Vector<EnemyTank > ets=new Vector<EnemyTank >();//定义敌人坦克    Vector 线性安全且同步的
	Vector<Node> nodes=new Vector<Node>();
	int etsSize=5;//定义初始敌人坦克数量
	Vector<Bomb> bombs=new Vector<Bomb>();//定义炸弹集合
	Image image1=null;
	Image image2=null;
	Image image3=null;
	public MyPanel(String flag) //构造函数，主要用于初始化
	{
		Recorder.getRecording();//恢复坦克记录数据 
		if(Recorder.getMyLife()!=0)
		{
			System.out.println(Recorder.getMyLife());
			hero=new Hero(200,250);//定义自己坦克出现的位置
		}
		if(flag.equals("newGame"))
		{
			//播放开战声音
			AePlayWave apw=new AePlayWave("c:\\tank.mp3");
			apw.start();
			
			for(int i=0;i<etsSize;i++)
			{
				//创建一辆敌人坦克对象
				EnemyTank et=new EnemyTank((i+1)*50,0);//每个坦克间隔50,纵坐标为0
				ets.add(et); //加入敌人坦克
				et.setDirect(2);
				et.setColor(0);
				
				et.setEts(ets);
				
				//启动敌人的坦克
				Thread t=new Thread(et);
				t.start();
				Shot s=new Shot(et.x+9,et.y+33,2,1);//创建一颗敌人的子弹
				
				et.ss.add(s);//将创建的子弹加入到敌人子弹向量中
				Thread t2=new Thread(s);
				t2.start();
			}
		}else
		{
			nodes=new Recorder().getNodesAndEnNums();
			for(int i=0;i<nodes.size();i++)
			{
				Node node=nodes.get(i);
				//创建一辆敌人坦克对象
				EnemyTank et=new EnemyTank(node.x, node.y);//每个坦克间隔50,纵坐标为0
				ets.add(et); //加入敌人坦克
				et.setDirect(node.direct);
				et.setColor(0);
				
				et.setEts(ets);
				
				//启动敌人的坦克
				Thread t=new Thread(et);
				t.start();
				Shot s=new Shot(et.x+9,et.y+33,2,1);//创建一颗敌人的子弹
				
				et.ss.add(s);//将创建的子弹加入到敌人子弹向量中
				Thread t2=new Thread(s);
				t2.start();
			}
		}
		//图片的初始化
//		try {//这样能够去掉第一张图片的不显示，但是没有成功
//			image1=ImageIO.read(new File("bomb_1.jpg"));
//			image2=ImageIO.read(new File("bomb_2.jpg"));
//			image3=ImageIO.read(new File("bomb_3.jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//下面的三行初始化   第一张爆炸的效果不是很明显
		image1=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.jpg"));
		image2=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.jpg"));
		image3=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.jpg"));	
		}
	//画出提示信息
	public void showInfo(Graphics g)
	{
		this.drawTank(80,330 , g, 0, 0);
		g.setColor(Color.black);   //显示我的坦克的生命值
		g.drawString(Recorder.getMyLife()+"", 110, 350);//+""可以将int转化为String
		
		this.drawTank(150, 330, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getEnNum()+"", 180, 350);
		
		//显示我的总成绩
		g.setColor(Color.black);
		g.setFont(new Font("宋体",Font.BOLD,20));
		g.drawString("您的总成绩：", 410, 30);
		
		this.drawTank(420, 50, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getAllEnNum()+"", 450, 70);
		
	}
	public void paint(Graphics g)  //重写paint
	{
		super.paint(g);//一定要有，不然就是坦克没行动一步就画一个，就好像是画无数个坦克，没有刷新一样
		g.fillRect(0, 0, 400, 300);//设置坦克的活动区域	
		
		//画出提示信息
		this.showInfo(g);
				
		//画出自己的的坦克
		if(Recorder.getMyLife()!=0)
		{
			if(hero.islive)
			{
				this.drawTank(hero.getX(), hero.getY(), g, this.hero.getDirect(), 0);
			}
		}
		//画出子弹
		for(int i=0;i<hero.ss.size();i++)
		{
			Shot myShot=hero.ss.get(i);
			if(myShot!=null&&myShot.isLive==true)//这样就不会浪费内存了,这样仅此是画出一颗子弹
			{
				g.draw3DRect(myShot.x, myShot.y, 1, 1,false);
			}
			if(myShot.isLive==false)//如果子弹死亡 就把子弹从向量中删除
			{
				hero.ss.remove(myShot);
			}
		}
		//画出炸弹效果图
		for(int i=0;i<bombs.size();i++)
		{
			Bomb b=bombs.get(i);
			if(b.life>6)
			{
				g.drawImage(image3, b.x, b.y, 30, 30,this);
			}
			else if(b.life>3)
			{
				g.drawImage(image2, b.x, b.y, 30, 30,this);
			}
			else 
			{
				g.drawImage(image1, b.x, b.y, 30, 30,this);
			}
			b.lifeDown();
			if(b.life==0)
			{
				bombs.remove(b);
			}
		}
		//画出敌人坦克
		for(int i=0;i<ets.size();i++)
		{
			EnemyTank et=ets.get(i);
			if(et.islive)
			{
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), 1);
				for(int j=0;j<et.ss.size();j++)
				{
					Shot enemyShot=et.ss.get(j);
					if(enemyShot.isLive)
					{
						g.draw3DRect(enemyShot.x, enemyShot.y, 1, 1,false);
					}else
					{
						et.ss.remove(enemyShot);
					}
				}
			}
		}
	}
	public void hitMe()   //判断敌人子弹是否击中我的坦克
	{
		for(int i=0;i<ets.size();i++)
		{
			EnemyTank et=ets.get(i);//取出敌人的坦克
			for(int j=0;j<et.ss.size();j++)
			{
				Shot enemyShot=et.ss.get(j);//取出敌人坦克的子弹
				if(hero.islive)//防止坦克死掉后的二次爆炸
				{
					if(this.hitTank(enemyShot, hero))
					{
						Recorder.reduceMyLife();
					}
				}
			}
		}
	}
	public void hitEnemyTank()    //判断我的子弹是否击中敌人的坦克
	{	
		for(int i=0;i<hero.ss.size();i++)
		{
			Shot myShot=hero.ss.get(i);//取出子弹
			if(myShot.isLive)//判断子弹是否有效
			{
				for(int j=0;j<ets.size();j++) //遍历敌人坦克
				{
					EnemyTank et=ets.get(j);//取出敌人坦克
					if(et.islive)
					{
						if(this.hitTank(myShot, et))
						{
							Recorder.reduceEnNum();
							Recorder.allEnNum();
						}
					}
				}
			}
		}
	}
	//专门判断击中坦克的函数
	public boolean hitTank(Shot s,Tank t)
	{
		boolean b1=false;  //增加b1来判断是否被击中
		switch(t.direct)
		{
		case 0:
		case 2:
			if(s.x>t.x&&s.x<t.x+22&&s.y>t.y&&s.y<t.y+30)
			{
				s.isLive=false;//子弹消失
				t.islive=false; //坦克消失				
					b1=true;			
				ets.remove(t);//需要将击中坦克从向量中移除，不然会造成坦克虚拟存在，产生二次爆炸				
				Bomb b=new Bomb(t.x,t.y);//创建一颗炸弹
				bombs.add(b);//将创建的炸弹加入  炸弹向量中
			}
			break;
		case 1:
		case 3:
			if(s.x>t.x&&s.x<t.x+30&&s.y>t.y&&s.y<t.y+20)
			{
				s.isLive=false;
				t.islive=false;				
					b1=true;				
				ets.remove(t);				
				Bomb b=new Bomb(t.x,t.y);//创建一颗炸弹
				bombs.add(b);//将创建的炸弹加入  炸弹向量中
			}
			break;
		}
		return b1;
	}
	public void drawTank(int x,int y,Graphics g,int direct,int type) //定义一个画出坦克的函数
	{
		switch(type) //判断什么类型的坦克
		{
		case 0:
			g.setColor(Color.CYAN);//如果type=0，表示画我的坦克
			break;
		case 1:
			g.setColor(Color.yellow);//如果type=1，表示画敌方坦克
			break;
		}
		switch(direct) //判断方向
		{
		case 0:
			//画出我的坦克(到时再封装成一个函数)
			//1、画出左边的矩形
			g.fill3DRect(x, y, 5, 30, false);
			//2、画出右边的矩形
			g.fill3DRect(x+16, y, 5, 30, false);
			//3、画出中间矩形
			g.fill3DRect(x+5, y+5, 12, 20, false);
			//4、画出中间圆形
			g.fillOval(x+5, y+10, 10, 10);
			//5、画出线(炮筒)
			g.drawLine(x+10, y+15, x+10, y-2);
			
			//左边齿轮
			g.drawLine(x, y+3, x+3, y+3);
			g.drawLine(x, y+8, x+3, y+8);
			g.drawLine(x, y+13, x+3, y+13);
			g.drawLine(x, y+18, x+3, y+18);
			g.drawLine(x, y+23, x+3, y+23);
			//右边齿轮
			g.drawLine(x+16, y+3, x+20, y+3);
			g.drawLine(x+16, y+8, x+20, y+8);
			g.drawLine(x+16, y+13, x+20, y+13);
			g.drawLine(x+16, y+18, x+20, y+18);
			g.drawLine(x+16, y+23, x+20, y+23);
			
			break;
		//向下走的坦克
		case 2:
			g.fill3DRect(x, y, 5, 30, false);  //左边矩形
			g.fill3DRect(x+16, y, 5, 30, false); //右边矩形
			g.fill3DRect(x+5, y+5, 11, 20, false);  //中间矩形
			g.fillOval(x+5, y+10, 10, 10);//中间圆形
			g.drawLine(x+10, y+15, x+10, y+31);  //炮筒
			//左边齿轮
			g.drawLine(x, y+3, x+3, y+3);
			g.drawLine(x, y+8, x+3, y+8);
			g.drawLine(x, y+13, x+3, y+13);
			g.drawLine(x, y+18, x+3, y+18);
			g.drawLine(x, y+23, x+3, y+23);
			//右边齿轮
			g.drawLine(x+16, y+3, x+20, y+3);
			g.drawLine(x+16, y+8, x+20, y+8);
			g.drawLine(x+16, y+13, x+20, y+13);
			g.drawLine(x+16, y+18, x+20, y+18);
			g.drawLine(x+16, y+23, x+20, y+23);
			
			break;
		//向左走的坦克
		case 3:
			g.fill3DRect(x, y, 30, 5, false); //上边矩形
			g.fill3DRect(x, y+16, 30, 5, false);//下边矩形
			g.fill3DRect(x+5, y+5, 20, 11, false);//中间矩形
			g.fillOval(x+10, y+5, 10, 10); //中间圆形
			g.drawLine(x+15, y+10, x-2, y+10); //炮筒
			//上边齿轮
			g.drawLine(x+3, y, x+3, y+3);
			g.drawLine(x+8, y, x+8, y+3);
			g.drawLine(x+13, y, x+13, y+3);
			g.drawLine(x+18, y, x+18, y+3);
			g.drawLine(x+23, y, x+23, y+3);
			//下边齿轮
			g.drawLine(x+3, y+16, x+3, y+19);
			g.drawLine(x+8, y+16, x+8, y+19);
			g.drawLine(x+13, y+16, x+13, y+19);
			g.drawLine(x+18, y+16, x+18, y+19);
			g.drawLine(x+23, y+16, x+23, y+19);
			break;
		//向右走的坦克
		case 1:
			g.fill3DRect(x, y, 30, 5, false); //上边矩形
			g.fill3DRect(x, y+16, 30, 5, false);//下边矩形
			g.fill3DRect(x+5, y+5, 20, 11, false);//中间矩形
			g.fillOval(x+10, y+5, 10, 10);//中间圆形
			g.drawLine(x+15, y+10, x+31, y+10); //炮筒
			
			//上边齿轮
			g.drawLine(x+3, y, x+3, y+3);
			g.drawLine(x+8, y, x+8, y+3);
			g.drawLine(x+13, y, x+13, y+3);
			g.drawLine(x+18, y, x+18, y+3);
			g.drawLine(x+23, y, x+23, y+3);
			//下边齿轮
			g.drawLine(x+3, y+16, x+3, y+19);
			g.drawLine(x+8, y+16, x+8, y+19);
			g.drawLine(x+13, y+16, x+13, y+19);
			g.drawLine(x+18, y+16, x+18, y+19);
			g.drawLine(x+23, y+16, x+23, y+19);
			break;
		}
	}
		
	
	//键按下，a：表示向左，s：表示向下,w:表示向上，d：表示向右
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub   0向上移动   1向右移动   2 向下移动   3 向左移动
		if(e.getKeyCode()==KeyEvent.VK_W||e.getKeyCode()==KeyEvent.VK_UP)
		{
			//向上移动
			this.hero.setDirect(0);
			this.hero.moveUp();
		}
		else if(e.getKeyCode()==KeyEvent.VK_D||e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			//向右移动
			this.hero.setDirect(1);
			this.hero.moveRight();
		}else if(e.getKeyCode()==KeyEvent.VK_S||e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			//向下移动
			this.hero.setDirect(2);
			this.hero.moveDown();
		}else if(e.getKeyCode()==KeyEvent.VK_A||e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			//向左移动
			this.hero.setDirect(3);
			this.hero.moveLeft();
		}
//		if(e.getKeyCode()==KeyEvent.VK_J)//J键发子弹，可以实现子弹的连发
//		{
//			this.hero.shotEnemy();
//		}
		if(e.getKeyCode()==KeyEvent.VK_J)//J键发子弹,只能发出5颗子弹
		{
			if(hero.islive)
			{
				if(this.hero.ss.size()<5)
				{
					this.hero.shotEnemy();
				}
			}
		}
//		//打空格键  暂停游戏
//		
//		if(e.getKeyCode()==KeyEvent.VK_SPACE)
//		{
//			//int n=0;
//			n++;
//			int m=n%2;
//			switch(m)
//			{
//				case 0:				
//					hero.speed=0;
//					for(int j=0;j<hero.ss.size();j++)
//					{
//						Shot s=hero.ss.get(j);
//						if(s.isLive)
//						{
//							s.speed=0;
//						}
//					}
//					for(int i1=0;i1<ets.size();i1++)
//					{
//						EnemyTank et=ets.get(i1);
//						if(et.islive)
//						{
//							et.speed=0;
//							//et.direct=this.getd
//							for(int i2=0;i2<et.ss.size();i2++)
//							{
//								Shot s=et.ss.get(i2);
//								if(s.isLive)
//								{
//									s.speed=0;
//								}
//							}
//						}
//					}
//					break;
//				case 1:break;
//			}
//		}
		this.repaint();//重新绘制，就是刷新
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//让次线程休息100ms
		while(true)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.hitEnemyTank();//判断我的子弹是否击中敌人的坦克	
			this.hitMe();//判断敌人子弹是否击中我的坦克
			this.repaint();//重新绘制
		}
	}
}
