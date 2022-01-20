import java.io.* ;



import java.net.* ;
import java.util.* ;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class client{
	public static void main(String argv[]) throws Exception {
		startGUI();
		
	}
static int count = 1;	
static int port;
static String ip ;	
private static void startGUI() {
	
	
JFrame connectframe = new JFrame();
	
	connectframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	connectframe.setResizable(false); //prevent frame from being resized
	connectframe.setBounds(100,100,300,200);
	
	connectframe.getContentPane().setLayout(null);
	
	//ip address
	JLabel iplbl = new JLabel("Server IP Address:");
	iplbl.setBounds(20,20,150,20);
	connectframe.add(iplbl);
	JTextField iptxt = new JTextField();
	iptxt.setBounds(130,20,120,20);
	connectframe.add(iptxt);
	
	//port
	JLabel portlbl = new JLabel("Port:");
	portlbl.setBounds(98,60,350,20);
	connectframe.add(portlbl);
	JTextField porttxt = new JTextField();
	porttxt.setBounds(130,60,50,20);
	connectframe.add(porttxt);
	
	//connect
	JButton connectbtn = new JButton("Connect");
	connectbtn.setBounds(100,100,90,20);
	connectframe.add(connectbtn);
	connectframe.setVisible(true);
	
	connectbtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
					
			String ipstring = iptxt.getText();
			String portstring = porttxt.getText();
			
			try {
			port = Integer.parseInt(portstring);
			ip = ipstring;
			}catch(Exception e) {
				JLabel errorlbl = new JLabel("Invalid Input. Please try again.");
				errorlbl.setBounds(100,110,90,20);
				connectframe.add(errorlbl);
				connectframe.setVisible(true);
			}
			

			
			if (connectValid(connectframe)){
					//create MAIN JFrame
					JFrame frame = new JFrame(); //creates a frame
					frame.setTitle("Bulletin Board"); //sets title of frame
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setResizable(false); //prevent frame from being resized
					frame.setBounds(100,100,480,580);
					frame.setVisible(true);
					frame.getContentPane().setLayout(null);
					
					//welcome label
					JLabel welcomelbl = new JLabel("Welcome to the bulletin board!");
					welcomelbl.setBounds(10,10,300,20);
					frame.add(welcomelbl);
					
					
					//disconnect
					JButton disconnectbtn = new JButton("Disconnect");
					disconnectbtn.setBounds(350,10,110,20);
					frame.add(disconnectbtn);
					
					disconnectbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							
							System.exit(0);
							
							}
						}
					);
							
			
					//post
					JLabel postlbl = new JLabel("Type a note to post on the bulletin board:");
					postlbl.setBounds(10,75,300,20);
					frame.add(postlbl);
					JTextField posttxt = new JTextField();
					posttxt.setBounds(10,95,370,20);
					frame.add(posttxt);
					JButton postbtn = new JButton("POST");
					postbtn.setBounds(390,95,70,20);
					frame.add(postbtn);
					
					postbtn.addActionListener(new ActionListener() {
					
						public void actionPerformed(ActionEvent ae) {
							
							String posttext = posttxt.getText();
							send("POST "+posttext,count);
							posttxt.setText("");
							count = 0;
							
							}
						}
					);
					
					
					
					//get
					JLabel getlbl = new JLabel("Request a note from the bulletin board:");
					getlbl.setBounds(10,125,300,20);
					frame.add(getlbl);
					JTextField gettxt = new JTextField();
					gettxt.setBounds(10,145,370,20);
					frame.add(gettxt);
					JButton getbtn = new JButton("GET");
					getbtn.setBounds(390,145,70,20);
					frame.add(getbtn);
					
					getbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							
							String gettext = gettxt.getText();
							send("GET "+gettext,count);
							gettxt.setText("");
							count = 0;
							
							
							}
						}
					);
					
					
					//pin/unpin
					JLabel pinunpinlbl = new JLabel("Enter coordinates of note you would like to Pin or Unpin:");
					pinunpinlbl.setBounds(10,180,330,20);
					frame.add(pinunpinlbl);
					JLabel xlbl = new JLabel("X:");
					xlbl.setBounds(10,200,20,20);
					frame.add(xlbl);
					JTextField xtxt = new JTextField();
					xtxt.setBounds(25,200,30,20);
					frame.add(xtxt);
					JLabel ylbl = new JLabel("Y:");
					ylbl.setBounds(60,200,20,20);
					frame.add(ylbl);
					JTextField ytxt = new JTextField();
					ytxt.setBounds(75,200,30,20);
					frame.add(ytxt);
					JButton pinbtn = new JButton("PIN");
					pinbtn.setBounds(110,200,70,20);
					frame.add(pinbtn);
					
					pinbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							
							String X = xtxt.getText();
							String Y = ytxt.getText();
							send("PIN "+X+" "+Y,count);
							 xtxt.setText("");
							 ytxt.setText("");
							count = 0; 
							
							
							};
						}
					);
					
					JButton unpinbtn = new JButton("UNPIN");
					unpinbtn.setBounds(190,200,70,20);
					frame.add(unpinbtn);
					
					unpinbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							
							String X = xtxt.getText();
							String Y = ytxt.getText();
							send("UNPIN "+X+" "+Y,count);
							 xtxt.setText("");
							 ytxt.setText("");
							 count = -0;
							 
							
							
							};
						}
					);
					
					//clear
					JLabel clearlbl = new JLabel("Clear all notes off the board:");
					clearlbl.setBounds(10,280,300,20);
					frame.add(clearlbl);
					JButton clearbtn = new JButton("CLEAR");
					clearbtn.setBounds(10,300,80,20);
					frame.add(clearbtn);
					
					clearbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							send("CLEAR",count);
							count = 0;
						}
					});
					
					//shake
					JLabel shakelbl = new JLabel("Shake all unpinned notes off the board:");
					shakelbl.setBounds(10,230,300,20);
					frame.add(shakelbl);
					JButton shakebtn = new JButton("SHAKE");
					shakebtn.setBounds(10,250,80,20);
					frame.add(shakebtn);
					
					shakebtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							send("SHAKE",count);
							count = 0;
						}
					});
					
					//result box
					JLabel resultlbl = new JLabel("Result:");
					resultlbl.setBounds(10,330,100,20);
					frame.add(resultlbl);
					JTextArea resulttxt = new JTextArea();					
				
					resulttxt.setLineWrap(true);
					PrintStream printStream = new PrintStream(new CustomOutputStream(resulttxt));
					JScrollPane scroll = new JScrollPane(resulttxt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					scroll.setBounds(10,350,450,180);
								
					
					System.setOut(printStream);
			        System.setErr(printStream);
			        frame.add(scroll);
				

			        	
			}
			
			
		}
		
	}
			);
	
}
private static void send(String info,int c) {
	
	Scanner sc = null;
	DataInputStream dis= null;
	DataOutputStream dos = null;
	try {
		sc = new Scanner(System.in);
		InetAddress newip = InetAddress.getByName(ip);
		Socket s = new Socket(newip, port);

		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());
		if(c==1) {
			dos.writeUTF("CONNECT");
			System.out.println(dis.readUTF());
		}
		dos.writeUTF(info);
		System.out.println(info);
		System.out.println(dis.readUTF());
		dos.writeUTF("DISCONNECT");
		dis.close();
		dos.close();
	
		
	}catch(Exception e) {
		System.out.println("Error:"+ e);
	}
	
	sc.close();
	
}
private static boolean connectValid(JFrame connectframe){
	DataInputStream dis= null;
	DataOutputStream dos = null;
	try {
		InetAddress newip = InetAddress.getByName(ip);
		Socket s = new Socket(ip, port);
		
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());
		
		dos.writeUTF("CONNECT");
		System.out.println(dis.readUTF());
		dos.writeUTF("DISCONNECT");
		dis.close();
		dos.close();
		return true;
	}catch(Exception e){
		JLabel state = new JLabel("Can not find a server with "+port+"and"+ip);
		state.setBounds(100,110,90,20);
		connectframe.add(state);
		connectframe.setVisible(true);
		return false;
	}
	

}


} 
    	