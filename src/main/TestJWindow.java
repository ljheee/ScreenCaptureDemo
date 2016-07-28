package main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JWindow;

public class TestJWindow {

	public static void main(String[] args) {
		
		JWindow wnd = new JWindow();
		
		wnd.setSize(200, 200);
		wnd.setLocationRelativeTo(null);
		
		wnd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		
		});
		
		
		
		wnd.setVisible(true);
		
		
	}

}
