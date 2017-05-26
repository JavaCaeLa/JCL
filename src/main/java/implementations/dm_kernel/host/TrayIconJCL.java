package implementations.dm_kernel.host;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.SystemTray;


public class TrayIconJCL{
	
	private static TrayIcon trayIcon = null;
	private static Map<String, String> metadata;
	private final static SystemTray tray = SystemTray.getSystemTray();
	private static JPopupMenu menu;
    private static JDialog dialog;
    static {
        dialog = new JDialog((Frame) null);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
    }

    public TrayIconJCL(Map<String,String> metadata){
        /* Use an appropriate Look and Feel */
        try {
        	this.metadata = metadata;
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        trayIcon = new TrayIcon(createImage("/images/icon4.png", "tray icon"));                
        
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showJPopupMenu(e);
            }

//            public void mouseReleased(MouseEvent e) {
//            	System.out.println("mouse click 2");
//                showJPopupMenu(e);
//            }
        });
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Java Cá&Lá System Tray");
            }
        });
                
        trayIcon.setImageAutoSize(true);
    }
    
    
    protected static JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();

        // Create a popup menu components
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem hostItem = new JMenuItem("Host");
        
        
        
        //Add components to popup menu
        popup.add(aboutItem);
        popup.add(hostItem);
        popup.addSeparator();
        popup.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
        
        hostItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		String msg="";
     		for(Map.Entry<String,String> meta:metadata.entrySet()){
     			msg = msg+meta.getKey()+": "+meta.getValue()+"\n";
     		}

     		JOptionPane.showMessageDialog(null,msg);
            }
        });
        
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String message = "<html><body><div  width='400px' align='center'>The Java Cá&Lá (or just JCL) is a middleware that integrates high performance computing (HPC) "
                      + "and Internet of things (IoT) in a unique API. It enables asynchronous remote method invocation "
                      + "and distributed shared memory services over multicore computer architectures or over multicomputer clusters. "
                      + "Sensing services are also feasible in JCL IoT version. An API for internal control enables collecting "
                      + "several times and memory usage of tasks submitted to the JCL cluster. Finally, a distributed map implementation "
                      + "adopts de Java Map interface, a common data structure for Java developers. The JCL map enables distributed "
                      + "storage with low refactoring.</div></body></html>";
            	 JLabel messageLabel = new JLabel(message);
            	
                JOptionPane.showMessageDialog(null,messageLabel);
            }
        });
        
//        popup.addFocusListener(new FocusListener() {
//       	 @Override
//       	 public void focusLost(FocusEvent e) {
//       	  System.out.println("LOST FOCUS");
//       	 }
//
//			@Override
//			public void focusGained(FocusEvent e) {
//				// TODO Auto-generated method stub
//				System.out.println("FOCUS");
//				
//			}});
//        
        popup.addPopupMenuListener(popupListener);
 
           
        return popup;
    }
    
    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = TrayIconJCL.class.getResource(path);
        
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
    
    public void showmessage(String msg, TrayIcon.MessageType type){
    	if (SystemTray.isSupported()) {
    	trayIcon.displayMessage("Java Cá&Lá...",msg,type);
    	}
    }
    
    protected static void showJPopupMenu(MouseEvent e) {
     //   if (e.isPopupTrigger()) {
        	menu = createPopupMenu();
            Dimension size = menu.getPreferredSize();
            showJPopupMenu(e.getX(), e.getY() - size.height);
     //   }
    }
    
    protected static void showJPopupMenu(int x, int y) {
        dialog.setLocation(x, y);
        dialog.setVisible(true);
        menu.show(dialog.getContentPane(), 0, 0);
        // popup works only for focused windows
        dialog.toFront();
    }
    
    private static PopupMenuListener popupListener = new PopupMenuListener() {
        
    	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//    		SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    ((JPopupMenu)e.getSource()).setVisible(true);
//                }
//            });
//    		System.out.println("teste1");
        }
        
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            dialog.setVisible(false);
//    		System.out.println("teste2");

        }
        public void popupMenuCanceled(PopupMenuEvent e) {
            dialog.setVisible(false);
//    		System.out.println("teste3");

        }
    };
}
