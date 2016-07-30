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
 * ��ͼ��˫��--���浽����
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
			// setSize(d);//��󻯴���
			/**
			 * �� JDialog ��һ���޿������Ĵ��ڣ���С���ó��������ѽ�ȡ�����������ͼƬ������� JDiaglog ��
			 */
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			this.addMouseListener(new MouseAdapter() {// �õ�����ͷ��ʼ����
				@Override
				public void mousePressed(MouseEvent e) {
					orgx = e.getX();
					orgy = e.getY();
				}
			});

			/**
			 * ���Ȧ������ ��Ȧ�����������
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
					// ����1����ֹwidth��heightΪ0
					g.setColor(Color.BLUE);
					g.drawRect(x - 1, y - 1, width + 1, height + 1);
					// ��1����1����Ϊ�˷�ֹͼƬ�����ο򸲸ǵ�
					saveImage = image.getSubimage(x, y, width, height);
					g.drawImage(saveImage, x, y, RectD.this);
				}
			});
			/**
			 * ˫������ͼ
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
		 * ���浽����
		 */
		public void saveToFile() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd��HHʱmm��ss��");
			String name = sdf.format(new Date());
			File path = FileSystemView.getFileSystemView().getHomeDirectory();
			// ͼƬ����������
			String format = "png";
			File f = new File(path + File.separator + name + "." + format);
			try {
				ImageIO.write(saveImage, format, f);
				String srcImgPath = path + "\\" + name + ".png";
				String iconPath = System.getProperty("user.dir") + "\\1.png";
				// URL iconPath = this.getClass().getResource("/image/1.png");
				System.out.println(System.getProperty("user.dir"));
				String targerPath = path + "\\" + name + "�ǺǺ�.png";
				this.dispose();
				/**
				 * ��ͼƬ���ˮӡ
				 */
				HEHEHEScreenshot.markImageByIcon(iconPath, srcImgPath, targerPath, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * ��ȡ��ǰ��Ļ������ͼƬ
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
	 * ��ͼƬ���ˮӡ
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));

			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// �õ����ʶ���
			Graphics2D g = buffImg.createGraphics();

			// ���ö��߶εľ��״��Ե����
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					0, null);

			if (null != degree) {
				// ����ˮӡ��ת
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}

			// ˮӡͼ���·�� ˮӡһ��Ϊgif����png�ģ�����������͸����

			ImageIcon imgIcon = new ImageIcon(iconPath);
			// ImageIcon imgIcon = new ImageIcon(iconPath);
			// �õ�Image����
			Image img = imgIcon.getImage();

			float alpha = 1f;
			// ͸����
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

			// ��ʾˮӡͼƬ��λ��
			g.drawImage(img, 50, 50, null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// ����ͼƬ
			ImageIO.write(buffImg, "PNG", os);

			// System.out.println("��Ӳ�Ӭ��ɡ�����������");
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
	 * ����ӹ�ˮӡ��ͼƬ��������
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
	 * ���ϵͳ����
	 */
	public static class SystemTrayDemo extends JFrame {

		private static final long serialVersionUID = 1L;
		
		private TrayIcon trayIcon = null;

		public SystemTrayDemo() {
			if (SystemTray.isSupported()) {// ��鵱ǰϵͳ�Ƿ�֧��ϵͳ����
				
				// ��ȡ��ʾ������������SystemTrayʵ����
				SystemTray tray = SystemTray.getSystemTray();
				
				Image image = this.getToolkit().getImage(this.getClass().getResource("/image/logo.png"));
				PopupMenu popupMenu = new PopupMenu();
				MenuItem exitItem = new MenuItem("�˳�");
				MenuItem menuItema = new MenuItem("��ͼ");
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
				trayIcon = new TrayIcon(image, "�ǺǺǽ�ͼ", popupMenu);
				while (flag) {
					flag = false;
					menuItema.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							// ������ý�ͼ����
							RectD rd = new RectD();
							GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
									.getDefaultScreenDevice();
							gd.setFullScreenWindow(rd);
						}

					});
					try {
						tray.add(trayIcon);
						// �� TrayIcon ��ӵ� SystemTray��
					} catch (AWTException e) {
						System.err.println(e);
					}
				}
			} else {
				System.out.println("���ϵͳ��֧��ϵͳ����");
			}
			try {
			} catch (Exception e) {

			}

		}
	}

}
