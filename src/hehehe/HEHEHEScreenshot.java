package hehehe;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Dacaitou
 * 截图后双击--保存到桌面
 * @since 10-31-2014
 * @version V0.1.0
 * @GitHub https://github.com/bigcaitou/
 */
public class HEHEHEScreenshot {
	public static boolean flag = true;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SystemTrayDemo().setVisible(false);
			}
		});
	}

	public static class RectD extends JFrame {
		private static final long serialVersionUID = 1L;
		int orgx, orgy, endx, endy;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage image;
		BufferedImage tempImage;
		BufferedImage saveImage;
		Graphics g;

		@Override
		public void paint(Graphics g) {
			RescaleOp ro = new RescaleOp(0.8f, 0, null);
			tempImage = ro.filter(image, null);
			g.drawImage(tempImage, 0, 0, this);
		}

		public RectD() {
			snapshot();
			setVisible(true);
			// setSize(d);//最大化窗口
			/**
			 * 用 JDialog 做一个无控制条的窗口，大小设置成满屏，把截取的这个满屏的图片贴到这个 JDiaglog 里
			 */
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			this.addMouseListener(new MouseAdapter() {// 得到鼠标箭头初始坐标
				@Override
				public void mousePressed(MouseEvent e) {
					orgx = e.getX();
					orgy = e.getY();
				}
			});

			/**
			 * 鼠标圈定区域， 对圈定的区域截屏
			 */
			this.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					endx = e.getX();
					endy = e.getY();
					g = getGraphics();
					g.drawImage(tempImage, 0, 0, RectD.this);
					int x = Math.min(orgx, endx);
					int y = Math.min(orgy, endy);
					int width = Math.abs(endx - orgx) + 1;
					int height = Math.abs(endy - orgy) + 1;
					// 加上1，防止width或height为0
					g.setColor(Color.BLUE);
					g.drawRect(x - 1, y - 1, width + 1, height + 1);
					// 减1，加1都是为了防止图片将矩形框覆盖掉
					saveImage = image.getSubimage(x, y, width, height);
					g.drawImage(saveImage, x, y, RectD.this);
				}
			});
			/**
			 * 双击鼠标截图
			 **/
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					int clickTimes = e.getClickCount();
					if (clickTimes == 2) {
						saveToFile();
					}
				}
			});
		}

		/**
		 * 保存到桌面
		 */
		public void saveToFile() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
			String name = sdf.format(new Date());
			File path = FileSystemView.getFileSystemView().getHomeDirectory();
			// 图片保存在桌面
			String format = "png";
			File f = new File(path + File.separator + name + "." + format);
			try {
				ImageIO.write(saveImage, format, f);
				String srcImgPath = path + "\\" + name + ".png";
				String iconPath = System.getProperty("user.dir") + "\\1.png";
				// URL iconPath = this.getClass().getResource("/image/1.png");
				System.out.println(System.getProperty("user.dir"));
				String targerPath = path + "\\" + name + "呵呵呵.png";
				this.dispose();
				/**
				 * 给图片添加水印
				 */
				HEHEHEScreenshot.markImageByIcon(iconPath, srcImgPath, targerPath, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 截取当前屏幕的满屏图片
		 */
		public void snapshot() {
			try {
				Robot robot = new Robot();
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 给图片添加水印
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));

			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					0, null);

			if (null != degree) {
				// 设置水印旋转
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度

			ImageIcon imgIcon = new ImageIcon(iconPath);
			// ImageIcon imgIcon = new ImageIcon(iconPath);
			// 得到Image对象。
			Image img = imgIcon.getImage();

			float alpha = 1f;
			// 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

			// 表示水印图片的位置
			g.drawImage(img, 50, 50, null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "PNG", os);

			// System.out.println("添加苍蝇完成。。。。。。");
			setClipboardImage(buffImg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存加过水印的图片到剪贴板
	 */
	public static void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	/**
	 * 添加系统托盘
	 */
	public static class SystemTrayDemo extends JFrame {

		private static final long serialVersionUID = 1L;
		
		private TrayIcon trayIcon = null;

		public SystemTrayDemo() {
			if (SystemTray.isSupported()) {// 检查当前系统是否支持系统托盘
				
				// 获取表示桌面托盘区的SystemTray实例。
				SystemTray tray = SystemTray.getSystemTray();
				
				Image image = this.getToolkit().getImage(this.getClass().getResource("/image/logo.png"));
				PopupMenu popupMenu = new PopupMenu();
				MenuItem exitItem = new MenuItem("退出");
				MenuItem menuItema = new MenuItem("截图");
				exitItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							System.exit(0);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});
				popupMenu.add(menuItema);
				popupMenu.add(exitItem);
				trayIcon = new TrayIcon(image, "呵呵呵截图", popupMenu);
				while (flag) {
					flag = false;
					menuItema.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							// 这里调用截图功能
							RectD rd = new RectD();
							GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
									.getDefaultScreenDevice();
							gd.setFullScreenWindow(rd);
						}

					});
					try {
						tray.add(trayIcon);
						// 将 TrayIcon 添加到 SystemTray。
					} catch (AWTException e) {
						System.err.println(e);
					}
				}
			} else {
				System.out.println("你的系统不支持系统托盘");
			}
			try {
			} catch (Exception e) {

			}

		}
	}

}
