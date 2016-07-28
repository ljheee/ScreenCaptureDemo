/**
  * @(#)main.RobotToolMain.java  2008-8-20  
  * Copy Right Information	: Tarena
  * Project					: ScreenRobot
  * JDK version used		: jdk1.6.4
  * Comments				: ����Ļ������ȡ����ץ������
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-8-20 	С��     		�½�
  **/
package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.filechooser.FileNameExtensionFilter;


 /**
 * ����Ļ������ȡ����ץ������
 * 2008-8-20
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 * @author		Administrator
 */
public class RobotToolMain extends JWindow{
	
	private static final long serialVersionUID = 4688016771324191291L;
	
	/** ���ָ�뱻����ʱ��ˮƽλ�� */
	private int mouseX = -1;
	/** ���ָ�뱻����ʱ�Ĵ�ֱλ�� */
	private int mouseY = -1;
	/** ���ָ�����ڵ�λ�� */
	private int mouseXNow = -1;
	/** ���ָ�����ڵ�λ�� */
	private int mouseYNow = -1;
	/** �����η���Ŀ�� ����Ϊż�� */
	private int rectWidth = 6;
	
	/** �Ƿ��Ѿ���������꣬��ʼ��ͼ */
	private boolean isPressedMouse = false;
	
	/** ���ù�������� */
	private Toolkit tk = null;
	
	/** ����������� */
	private Robot robot = null;
	
	/** ����ֱ��ʴ�С�ĵ�ǰ��Ļ��ʾ��ͼ�� */
	private BufferedImage image = null;
	
	private BufferedImage subImage = null;
	
	
	public RobotToolMain() {
		tk = Toolkit.getDefaultToolkit();
		try {
			robot = new Robot();
			image = robot.createScreenCapture(new Rectangle(0,0,tk.getScreenSize().width,tk.getScreenSize().height));
			//setCursor(tk.createCustomCursor(tk.createImage("cursor.gif"), new Point(), "Test"));
			BufferedImage cursor = ImageIO.read(RobotToolMain.class.getResource("cursor.gif"));
			setCursor(tk.createCustomCursor(cursor, new Point(), "gif"));
		} catch (AWTException e) {
			System.out.println("����"+e.getMessage());
		} catch (HeadlessException e) {
			System.out.println("����"+e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("����"+e.getMessage());
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
		setSize(new Double(tk.getScreenSize().getWidth()).intValue(),new Double(tk.getScreenSize().getHeight()).intValue());
		
		MouseEventImpl listener = new MouseEventImpl();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addKeyListener(new KeyEventImpl());
		
		GraphicsEnvironment environment=GraphicsEnvironment.getLocalGraphicsEnvironment();//ͨ��this����ȡ��GraphicsDevice
		GraphicsDevice device=environment.getDefaultScreenDevice();
		if(device.isFullScreenSupported()){
			device.setFullScreenWindow(this);
		}else{
			JOptionPane.showMessageDialog(null, "��Ǹ�������豸��֧��ȫ��");
			setVisible(true);
		}
			
	}
	
	
	
	/**
	 * ���ƺ�����
	 */
	public void paint(Graphics g) {
		//super.paint(g);
		Color c = g.getColor();
		g.drawImage(image, 0,0,tk.getScreenSize().width,tk.getScreenSize().height,this);
		if(isPressedMouse){
			//������
			g.setColor(new Color(10,100,130,10));
			g.fillRect(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
			//���ƾ���
			g.setColor(new Color(10,100,130));
			g.drawRect(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
			//���ƾ��α߿�������ϵ�С����
			int width = (mouseXNow-mouseX)/2;
			int height = (mouseYNow-mouseY)/2;
			for(int i=0;i<3;i++)
				for(int j=0;j<3;j++)
					if(i!=1||j!=1)
						g.fillRect(mouseX+i*width-rectWidth/2, mouseY+j*height-rectWidth/2, rectWidth, rectWidth);
			//����ͼС��С��ʾ
			String sizeTip = Math.abs(mouseX-mouseXNow)+"x"+Math.abs(mouseY-mouseYNow);
			g.setColor(Color.BLACK);
			g.fillRect((mouseX>mouseXNow?mouseXNow:mouseX), (mouseY>mouseYNow?mouseYNow:mouseY)-20, sizeTip.length()*7, 18);
			g.setColor(Color.WHITE);
			g.drawString(sizeTip, (mouseX>mouseXNow?mouseXNow:mouseX), (mouseY>mouseYNow?mouseYNow:mouseY)-7);
		}
		g.setColor(c);
	}
	
	/**
	 * �ڲ��ࣺ�̳��������࣬ʵ������¼���
	 * 2008-8-22
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 * @author		Administrator
	 */
	private class MouseEventImpl extends MouseAdapter{
		
		/*
		 * ����ƶ�������ı仯��
		 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			//super.mouseDragged(e);
			mouseXNow = e.getXOnScreen();
			mouseYNow = e.getYOnScreen();
			repaint();
		}
		
		@Override
		/**
		 * �������ʼ�ƶ���ͼ��
		 */
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			mouseX = e.getXOnScreen();
			mouseY = e.getYOnScreen();
			if(!isPressedMouse)
				isPressedMouse = true;
		}
		
		@Override
		/**
		 * ��ͼ��ɺ󣬱���ͼ��
		 */
		public void mouseReleased(MouseEvent e) {
//			super.mouseReleased(e);
			
			RobotToolMain.this.setVisible(false);;
			if(mouseX>=0 && mouseXNow>=0 && mouseY>=0 && mouseYNow>=0){
				subImage = image.getSubimage(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
				JFileChooser chooser = new JFileChooser();
				//chooser.setName("Javaδ������ͼ"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				chooser.setCurrentDirectory(new File("."+File.separator+"ss"));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new FileNameExtensionFilter("GIF�ļ� (*.gif)",".gif"));
				chooser.setFileFilter(new FileNameExtensionFilter("JPEG�ļ� (*.jpg;*.jpeg)",".jpg",".jpeg"));
				chooser.setFileFilter(new FileNameExtensionFilter("BMP�ļ� (*.bmp)",".bmp"));
				int op = chooser.showSaveDialog(null);
				if(op == JFileChooser.APPROVE_OPTION ){
					
					File file = chooser.getSelectedFile();
					String[] exname = ((FileNameExtensionFilter)chooser.getFileFilter()).getExtensions();
					
					//System.out.println(file.getAbsolutePath());
					try {
						ImageIO.write(subImage,"gif",new File(file.getAbsoluteFile()+exname[0]));
						JOptionPane.showMessageDialog(RobotToolMain.this, "Save Success");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(RobotToolMain.this, "����ͼƬʱ��������:"+e1.getMessage());
						e1.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
		
	}
	
	private class KeyEventImpl extends KeyAdapter{
		@Override
		public void keyTyped(KeyEvent e) {
			//super.keyPressed(e);
			System.out.println(e.getKeyChar());
			if(e.getKeyChar()==KeyEvent.VK_Z)
				System.exit(0);
		}
	}
	
	
	
	public static void main(String[] args){
		new RobotToolMain();
	}
	
}
