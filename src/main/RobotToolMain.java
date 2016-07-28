/**
  * @(#)main.RobotToolMain.java  2008-8-20  
  * Copy Right Information	: Tarena
  * Project					: ScreenRobot
  * JDK version used		: jdk1.6.4
  * Comments				: 简单屏幕像素提取器或抓屏器。
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-8-20 	小猪     		新建
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
 * 简单屏幕像素提取器或抓屏器。
 * 2008-8-20
 * @author		达内科技[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(建议) 
 * @author		Administrator
 */
public class RobotToolMain extends JWindow{
	
	private static final long serialVersionUID = 4688016771324191291L;
	
	/** 鼠标指针被按下时的水平位置 */
	private int mouseX = -1;
	/** 鼠标指针被按下时的垂直位置 */
	private int mouseY = -1;
	/** 鼠标指针现在的位置 */
	private int mouseXNow = -1;
	/** 鼠标指针现在的位置 */
	private int mouseYNow = -1;
	/** 正方形方块的宽度 建议为偶数 */
	private int rectWidth = 6;
	
	/** 是否已经按下了鼠标，开始截图 */
	private boolean isPressedMouse = false;
	
	/** 常用工具类对象 */
	private Toolkit tk = null;
	
	/** 机器人类对象 */
	private Robot robot = null;
	
	/** 保存分辨率大小的当前屏幕显示的图像 */
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
			System.out.println("错误"+e.getMessage());
		} catch (HeadlessException e) {
			System.out.println("错误"+e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			System.out.println("错误"+e.getMessage());
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		setSize(new Double(tk.getScreenSize().getWidth()).intValue(),new Double(tk.getScreenSize().getHeight()).intValue());
		
		MouseEventImpl listener = new MouseEventImpl();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addKeyListener(new KeyEventImpl());
		
		GraphicsEnvironment environment=GraphicsEnvironment.getLocalGraphicsEnvironment();//通过this对象取得GraphicsDevice
		GraphicsDevice device=environment.getDefaultScreenDevice();
		if(device.isFullScreenSupported()){
			device.setFullScreenWindow(this);
		}else{
			JOptionPane.showMessageDialog(null, "抱歉，您的设备不支持全屏");
			setVisible(true);
		}
			
	}
	
	
	
	/**
	 * 绘制函数。
	 */
	public void paint(Graphics g) {
		//super.paint(g);
		Color c = g.getColor();
		g.drawImage(image, 0,0,tk.getScreenSize().width,tk.getScreenSize().height,this);
		if(isPressedMouse){
			//填充矩形
			g.setColor(new Color(10,100,130,10));
			g.fillRect(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
			//绘制矩形
			g.setColor(new Color(10,100,130));
			g.drawRect(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
			//绘制矩形边框各个脚上的小矩形
			int width = (mouseXNow-mouseX)/2;
			int height = (mouseYNow-mouseY)/2;
			for(int i=0;i<3;i++)
				for(int j=0;j<3;j++)
					if(i!=1||j!=1)
						g.fillRect(mouseX+i*width-rectWidth/2, mouseY+j*height-rectWidth/2, rectWidth, rectWidth);
			//绘制图小大小提示
			String sizeTip = Math.abs(mouseX-mouseXNow)+"x"+Math.abs(mouseY-mouseYNow);
			g.setColor(Color.BLACK);
			g.fillRect((mouseX>mouseXNow?mouseXNow:mouseX), (mouseY>mouseYNow?mouseYNow:mouseY)-20, sizeTip.length()*7, 18);
			g.setColor(Color.WHITE);
			g.drawString(sizeTip, (mouseX>mouseXNow?mouseXNow:mouseX), (mouseY>mouseYNow?mouseYNow:mouseY)-7);
		}
		g.setColor(c);
	}
	
	/**
	 * 内部类：继承适配器类，实现鼠标事件。
	 * 2008-8-22
	 * @author		达内科技[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(建议) 
	 * @author		Administrator
	 */
	private class MouseEventImpl extends MouseAdapter{
		
		/*
		 * 鼠标移动后坐标的变化。
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
		 * 鼠标点击后开始移动截图。
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
		 * 截图完成后，保存图像。
		 */
		public void mouseReleased(MouseEvent e) {
//			super.mouseReleased(e);
			
			RobotToolMain.this.setVisible(false);;
			if(mouseX>=0 && mouseXNow>=0 && mouseY>=0 && mouseYNow>=0){
				subImage = image.getSubimage(mouseX>mouseXNow?mouseXNow:mouseX, mouseY>mouseYNow?mouseYNow:mouseY, Math.abs(mouseXNow-mouseX), Math.abs(mouseYNow-mouseY));
				JFileChooser chooser = new JFileChooser();
				//chooser.setName("Java未命名截图"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				chooser.setCurrentDirectory(new File("."+File.separator+"ss"));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new FileNameExtensionFilter("GIF文件 (*.gif)",".gif"));
				chooser.setFileFilter(new FileNameExtensionFilter("JPEG文件 (*.jpg;*.jpeg)",".jpg",".jpeg"));
				chooser.setFileFilter(new FileNameExtensionFilter("BMP文件 (*.bmp)",".bmp"));
				int op = chooser.showSaveDialog(null);
				if(op == JFileChooser.APPROVE_OPTION ){
					
					File file = chooser.getSelectedFile();
					String[] exname = ((FileNameExtensionFilter)chooser.getFileFilter()).getExtensions();
					
					//System.out.println(file.getAbsolutePath());
					try {
						ImageIO.write(subImage,"gif",new File(file.getAbsoluteFile()+exname[0]));
						JOptionPane.showMessageDialog(RobotToolMain.this, "Save Success");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(RobotToolMain.this, "保存图片时发生错误:"+e1.getMessage());
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
