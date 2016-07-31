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
		//�����˵����˵�ѡ��
		jmb=new JMenuBar();
		jm1=new JMenu("��Ϸ(G)");
		//���ÿ�ݷ�ʽ Alt+G�Ϳ��Դ�
		jm1.setMnemonic('G');
		jmi1=new JMenuItem("��ʼ����Ϸ(N)");
		jmi2=new JMenuItem("�˳���Ϸ(E)");
		jmi3=new JMenuItem("�����˳�(C)");
		jmi4=new JMenuItem("�����Ͼ�(S)");
		
		jmi1.setMnemonic('N'); 
		jmi1.addActionListener(this); //ע�������
		jmi1.setActionCommand("newGame");//���ö�Ӧ������
		
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
		this.setSize(560,460);//���û����С
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//��ֹ�ڴ�й©
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("newGame"))
		{
			mp=new MyPanel("newGame");
			//����mp�߳�
			Thread t=new Thread(mp);//��������Ϊ�̣߳����Ҫ���������̲߳�����������
			t.start();
			this.remove(msp);
			this.add(mp);
			
			//ע�����
			this.addKeyListener(mp);
			this.setVisible(true);
		}
		else if(e.getActionCommand().equals("exit"))
		{
			//�û������ �˳�����Ĳ˵�
			Recorder.keepRecording();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("saveExit"))
		{
			//�����˳���Ҫ������˴ݻ�̹�˵�������̹���ӵ�������
			Recorder.setEts(mp.ets);//��Recorder֪��ets��mp��
			Recorder.keepRecAndEnemyTank();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("conGame"))
		{
			mp=new MyPanel("conGame");
			//����mp�߳�
			Thread t=new Thread(mp);//��������Ϊ�̣߳����Ҫ���������̲߳�����������
			t.start();
			this.remove(msp);
			this.add(mp);
			
			//ע�����
			this.addKeyListener(mp);
			this.setVisible(true);
		}
	}
}
//����һ����ʾ���壬��ʾ�ֹ�
class MyStartPanel extends JPanel implements Runnable
{
	int times=0;
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400,300);
		
		//��ʾ��Ϣ
		if(times%2==0)
		{
			g.setColor(Color.yellow);
			Font myFont=new Font("������κ",Font.BOLD,30);
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
				Thread.sleep(500); //500����
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			times++;
			this.repaint();
		}
	}
}
class MyPanel extends JPanel implements KeyListener,Runnable//�����ҵ���� ,�ѻ���Ҳ�����߳�
{
	String flag="newGame";
	
	Hero hero=null;//����һ���ҵ�̹��
	Vector<EnemyTank > ets=new Vector<EnemyTank >();//�������̹��    Vector ���԰�ȫ��ͬ����
	Vector<Node> nodes=new Vector<Node>();
	int etsSize=5;//�����ʼ����̹������
	Vector<Bomb> bombs=new Vector<Bomb>();//����ը������
	Image image1=null;
	Image image2=null;
	Image image3=null;
	public MyPanel(String flag) //���캯������Ҫ���ڳ�ʼ��
	{
		Recorder.getRecording();//�ָ�̹�˼�¼���� 
		if(Recorder.getMyLife()!=0)
		{
			System.out.println(Recorder.getMyLife());
			hero=new Hero(200,250);//�����Լ�̹�˳��ֵ�λ��
		}
		if(flag.equals("newGame"))
		{
			//���ſ�ս����
			AePlayWave apw=new AePlayWave("c:\\tank.mp3");
			apw.start();
			
			for(int i=0;i<etsSize;i++)
			{
				//����һ������̹�˶���
				EnemyTank et=new EnemyTank((i+1)*50,0);//ÿ��̹�˼��50,������Ϊ0
				ets.add(et); //�������̹��
				et.setDirect(2);
				et.setColor(0);
				
				et.setEts(ets);
				
				//�������˵�̹��
				Thread t=new Thread(et);
				t.start();
				Shot s=new Shot(et.x+9,et.y+33,2,1);//����һ�ŵ��˵��ӵ�
				
				et.ss.add(s);//���������ӵ����뵽�����ӵ�������
				Thread t2=new Thread(s);
				t2.start();
			}
		}else
		{
			nodes=new Recorder().getNodesAndEnNums();
			for(int i=0;i<nodes.size();i++)
			{
				Node node=nodes.get(i);
				//����һ������̹�˶���
				EnemyTank et=new EnemyTank(node.x, node.y);//ÿ��̹�˼��50,������Ϊ0
				ets.add(et); //�������̹��
				et.setDirect(node.direct);
				et.setColor(0);
				
				et.setEts(ets);
				
				//�������˵�̹��
				Thread t=new Thread(et);
				t.start();
				Shot s=new Shot(et.x+9,et.y+33,2,1);//����һ�ŵ��˵��ӵ�
				
				et.ss.add(s);//���������ӵ����뵽�����ӵ�������
				Thread t2=new Thread(s);
				t2.start();
			}
		}
		//ͼƬ�ĳ�ʼ��
//		try {//�����ܹ�ȥ����һ��ͼƬ�Ĳ���ʾ������û�гɹ�
//			image1=ImageIO.read(new File("bomb_1.jpg"));
//			image2=ImageIO.read(new File("bomb_2.jpg"));
//			image3=ImageIO.read(new File("bomb_3.jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//��������г�ʼ��   ��һ�ű�ը��Ч�����Ǻ�����
		image1=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.jpg"));
		image2=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.jpg"));
		image3=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.jpg"));	
		}
	//������ʾ��Ϣ
	public void showInfo(Graphics g)
	{
		this.drawTank(80,330 , g, 0, 0);
		g.setColor(Color.black);   //��ʾ�ҵ�̹�˵�����ֵ
		g.drawString(Recorder.getMyLife()+"", 110, 350);//+""���Խ�intת��ΪString
		
		this.drawTank(150, 330, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getEnNum()+"", 180, 350);
		
		//��ʾ�ҵ��ܳɼ�
		g.setColor(Color.black);
		g.setFont(new Font("����",Font.BOLD,20));
		g.drawString("�����ܳɼ���", 410, 30);
		
		this.drawTank(420, 50, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(Recorder.getAllEnNum()+"", 450, 70);
		
	}
	public void paint(Graphics g)  //��дpaint
	{
		super.paint(g);//һ��Ҫ�У���Ȼ����̹��û�ж�һ���ͻ�һ�����ͺ����ǻ�������̹�ˣ�û��ˢ��һ��
		g.fillRect(0, 0, 400, 300);//����̹�˵Ļ����	
		
		//������ʾ��Ϣ
		this.showInfo(g);
				
		//�����Լ��ĵ�̹��
		if(Recorder.getMyLife()!=0)
		{
			if(hero.islive)
			{
				this.drawTank(hero.getX(), hero.getY(), g, this.hero.getDirect(), 0);
			}
		}
		//�����ӵ�
		for(int i=0;i<hero.ss.size();i++)
		{
			Shot myShot=hero.ss.get(i);
			if(myShot!=null&&myShot.isLive==true)//�����Ͳ����˷��ڴ���,���������ǻ���һ���ӵ�
			{
				g.draw3DRect(myShot.x, myShot.y, 1, 1,false);
			}
			if(myShot.isLive==false)//����ӵ����� �Ͱ��ӵ���������ɾ��
			{
				hero.ss.remove(myShot);
			}
		}
		//����ը��Ч��ͼ
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
		//��������̹��
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
	public void hitMe()   //�жϵ����ӵ��Ƿ�����ҵ�̹��
	{
		for(int i=0;i<ets.size();i++)
		{
			EnemyTank et=ets.get(i);//ȡ�����˵�̹��
			for(int j=0;j<et.ss.size();j++)
			{
				Shot enemyShot=et.ss.get(j);//ȡ������̹�˵��ӵ�
				if(hero.islive)//��ֹ̹��������Ķ��α�ը
				{
					if(this.hitTank(enemyShot, hero))
					{
						Recorder.reduceMyLife();
					}
				}
			}
		}
	}
	public void hitEnemyTank()    //�ж��ҵ��ӵ��Ƿ���е��˵�̹��
	{	
		for(int i=0;i<hero.ss.size();i++)
		{
			Shot myShot=hero.ss.get(i);//ȡ���ӵ�
			if(myShot.isLive)//�ж��ӵ��Ƿ���Ч
			{
				for(int j=0;j<ets.size();j++) //��������̹��
				{
					EnemyTank et=ets.get(j);//ȡ������̹��
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
	//ר���жϻ���̹�˵ĺ���
	public boolean hitTank(Shot s,Tank t)
	{
		boolean b1=false;  //����b1���ж��Ƿ񱻻���
		switch(t.direct)
		{
		case 0:
		case 2:
			if(s.x>t.x&&s.x<t.x+22&&s.y>t.y&&s.y<t.y+30)
			{
				s.isLive=false;//�ӵ���ʧ
				t.islive=false; //̹����ʧ				
					b1=true;			
				ets.remove(t);//��Ҫ������̹�˴��������Ƴ�����Ȼ�����̹��������ڣ��������α�ը				
				Bomb b=new Bomb(t.x,t.y);//����һ��ը��
				bombs.add(b);//��������ը������  ը��������
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
				Bomb b=new Bomb(t.x,t.y);//����һ��ը��
				bombs.add(b);//��������ը������  ը��������
			}
			break;
		}
		return b1;
	}
	public void drawTank(int x,int y,Graphics g,int direct,int type) //����һ������̹�˵ĺ���
	{
		switch(type) //�ж�ʲô���͵�̹��
		{
		case 0:
			g.setColor(Color.CYAN);//���type=0����ʾ���ҵ�̹��
			break;
		case 1:
			g.setColor(Color.yellow);//���type=1����ʾ���з�̹��
			break;
		}
		switch(direct) //�жϷ���
		{
		case 0:
			//�����ҵ�̹��(��ʱ�ٷ�װ��һ������)
			//1��������ߵľ���
			g.fill3DRect(x, y, 5, 30, false);
			//2�������ұߵľ���
			g.fill3DRect(x+16, y, 5, 30, false);
			//3�������м����
			g.fill3DRect(x+5, y+5, 12, 20, false);
			//4�������м�Բ��
			g.fillOval(x+5, y+10, 10, 10);
			//5��������(��Ͳ)
			g.drawLine(x+10, y+15, x+10, y-2);
			
			//��߳���
			g.drawLine(x, y+3, x+3, y+3);
			g.drawLine(x, y+8, x+3, y+8);
			g.drawLine(x, y+13, x+3, y+13);
			g.drawLine(x, y+18, x+3, y+18);
			g.drawLine(x, y+23, x+3, y+23);
			//�ұ߳���
			g.drawLine(x+16, y+3, x+20, y+3);
			g.drawLine(x+16, y+8, x+20, y+8);
			g.drawLine(x+16, y+13, x+20, y+13);
			g.drawLine(x+16, y+18, x+20, y+18);
			g.drawLine(x+16, y+23, x+20, y+23);
			
			break;
		//�����ߵ�̹��
		case 2:
			g.fill3DRect(x, y, 5, 30, false);  //��߾���
			g.fill3DRect(x+16, y, 5, 30, false); //�ұ߾���
			g.fill3DRect(x+5, y+5, 11, 20, false);  //�м����
			g.fillOval(x+5, y+10, 10, 10);//�м�Բ��
			g.drawLine(x+10, y+15, x+10, y+31);  //��Ͳ
			//��߳���
			g.drawLine(x, y+3, x+3, y+3);
			g.drawLine(x, y+8, x+3, y+8);
			g.drawLine(x, y+13, x+3, y+13);
			g.drawLine(x, y+18, x+3, y+18);
			g.drawLine(x, y+23, x+3, y+23);
			//�ұ߳���
			g.drawLine(x+16, y+3, x+20, y+3);
			g.drawLine(x+16, y+8, x+20, y+8);
			g.drawLine(x+16, y+13, x+20, y+13);
			g.drawLine(x+16, y+18, x+20, y+18);
			g.drawLine(x+16, y+23, x+20, y+23);
			
			break;
		//�����ߵ�̹��
		case 3:
			g.fill3DRect(x, y, 30, 5, false); //�ϱ߾���
			g.fill3DRect(x, y+16, 30, 5, false);//�±߾���
			g.fill3DRect(x+5, y+5, 20, 11, false);//�м����
			g.fillOval(x+10, y+5, 10, 10); //�м�Բ��
			g.drawLine(x+15, y+10, x-2, y+10); //��Ͳ
			//�ϱ߳���
			g.drawLine(x+3, y, x+3, y+3);
			g.drawLine(x+8, y, x+8, y+3);
			g.drawLine(x+13, y, x+13, y+3);
			g.drawLine(x+18, y, x+18, y+3);
			g.drawLine(x+23, y, x+23, y+3);
			//�±߳���
			g.drawLine(x+3, y+16, x+3, y+19);
			g.drawLine(x+8, y+16, x+8, y+19);
			g.drawLine(x+13, y+16, x+13, y+19);
			g.drawLine(x+18, y+16, x+18, y+19);
			g.drawLine(x+23, y+16, x+23, y+19);
			break;
		//�����ߵ�̹��
		case 1:
			g.fill3DRect(x, y, 30, 5, false); //�ϱ߾���
			g.fill3DRect(x, y+16, 30, 5, false);//�±߾���
			g.fill3DRect(x+5, y+5, 20, 11, false);//�м����
			g.fillOval(x+10, y+5, 10, 10);//�м�Բ��
			g.drawLine(x+15, y+10, x+31, y+10); //��Ͳ
			
			//�ϱ߳���
			g.drawLine(x+3, y, x+3, y+3);
			g.drawLine(x+8, y, x+8, y+3);
			g.drawLine(x+13, y, x+13, y+3);
			g.drawLine(x+18, y, x+18, y+3);
			g.drawLine(x+23, y, x+23, y+3);
			//�±߳���
			g.drawLine(x+3, y+16, x+3, y+19);
			g.drawLine(x+8, y+16, x+8, y+19);
			g.drawLine(x+13, y+16, x+13, y+19);
			g.drawLine(x+18, y+16, x+18, y+19);
			g.drawLine(x+23, y+16, x+23, y+19);
			break;
		}
	}
		
	
	//�����£�a����ʾ����s����ʾ����,w:��ʾ���ϣ�d����ʾ����
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub   0�����ƶ�   1�����ƶ�   2 �����ƶ�   3 �����ƶ�
		if(e.getKeyCode()==KeyEvent.VK_W||e.getKeyCode()==KeyEvent.VK_UP)
		{
			//�����ƶ�
			this.hero.setDirect(0);
			this.hero.moveUp();
		}
		else if(e.getKeyCode()==KeyEvent.VK_D||e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			//�����ƶ�
			this.hero.setDirect(1);
			this.hero.moveRight();
		}else if(e.getKeyCode()==KeyEvent.VK_S||e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			//�����ƶ�
			this.hero.setDirect(2);
			this.hero.moveDown();
		}else if(e.getKeyCode()==KeyEvent.VK_A||e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			//�����ƶ�
			this.hero.setDirect(3);
			this.hero.moveLeft();
		}
//		if(e.getKeyCode()==KeyEvent.VK_J)//J�����ӵ�������ʵ���ӵ�������
//		{
//			this.hero.shotEnemy();
//		}
		if(e.getKeyCode()==KeyEvent.VK_J)//J�����ӵ�,ֻ�ܷ���5���ӵ�
		{
			if(hero.islive)
			{
				if(this.hero.ss.size()<5)
				{
					this.hero.shotEnemy();
				}
			}
		}
//		//��ո��  ��ͣ��Ϸ
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
		this.repaint();//���»��ƣ�����ˢ��
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
		//�ô��߳���Ϣ100ms
		while(true)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.hitEnemyTank();//�ж��ҵ��ӵ��Ƿ���е��˵�̹��	
			this.hitMe();//�жϵ����ӵ��Ƿ�����ҵ�̹��
			this.repaint();//���»���
		}
	}
}
