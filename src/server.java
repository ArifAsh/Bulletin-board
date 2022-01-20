import java.io.*;
import java.net.*;
import java.util.* ;
import java.util.stream.Collectors;



public class server {
	public static List<Details> notes;
	public static List<List> pins;
	
	public static void main(String argv[]) throws Exception {
		// Get the port number from the command line.
	
		
		int port ;
		int width;
		int height;
		List<String> availableColors = new ArrayList<String>();
		if (argv.length > 0)
		{
			try
			{
				port = Integer.valueOf(argv[0]);
			}
			catch (Exception e)
			{
				System.out.println("Invalid port number, defaulting to 5555");
				port = 5555;
			}
			try
			{
				width = Integer.valueOf(argv[1]);
			}
			catch (Exception e)
			{
				System.out.println("Width invalid or not inputted, defaulting to 200");
				width = 200;
			}
			try
			{
				height = Integer.valueOf(argv[2]);
			}
			catch (Exception e)
			{
				System.out.println("Height invalid or not inputted, defaulting to 200");
				height = 200;
			}
			try
			{
				int i = 3;
				while (i < argv.length) {
					availableColors.add(argv[i]);
					i++;
				}
				if(availableColors.isEmpty()) {
					System.out.println("Colors invalid or not inputted, defaulting to red,green,blue");
					availableColors.add("red");
					availableColors.add("green");
					availableColors.add("blue");
				}
			}
			catch (Exception e)
			{
				System.out.println("Colors invalid or not inputted, defaulting to red,green,blue");
				availableColors.add("red");
				availableColors.add("green");
				availableColors.add("blue");
			}
			
		}
		else
		{
			System.out.println("No input entered by user, defaulting to port:5555, width:200, height:200, colors:red,green,blue");
			port = 5555;
			width = 200;
			height = 200;
			availableColors.add("red");
			availableColors.add("green");
			availableColors.add("blue");
			
		}
		List<Details> notes = new ArrayList<Details>();
		List<List> pins= new ArrayList<List>();
		// Establish the listen socket.
		ServerSocket serverSocket;
		try
		{
			serverSocket = new ServerSocket(port);
		} 
		catch (Exception e)
		{
			System.out.println("Invalid port number, defaulting to 5555");
			port = 5555;
			serverSocket = new ServerSocket(port);
		}
	
		System.out.println("Server up and running...");
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				System.out.println("Receieved data." );

				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
	
			
			
			

				Thread t = new clientHandler(socket, dis, dos,notes,pins,width,height,availableColors);
				t.start();
			} catch (Exception e) {
				socket.close();
				serverSocket.close();
				System.out.println(e);
			}
	
		}
	}
	
}

//client handler class
class clientHandler extends Thread {
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket socket;
	final List<Details> notes;
	final List<List> pins;
	final int width;
	final int height;
	final List<String> availableColors;

	// constructor
	public clientHandler(Socket s, DataInputStream dis, DataOutputStream dos, List<Details> notes,List<List> pins, int width,int height,List<String> availableColors) {
		this.socket = s;
		this.dis = dis;
		this.dos = dos;
		this.notes = notes;
		this.pins = pins;
		this.availableColors = availableColors;
		this.width = width;
		this.height = height;

		
	}

	public void run() {
		String received;
	
		
		while (true) {
			
				
			try {
				
				received = dis.readUTF();
				
				if (received.equals("DISCONNECT")) {
			
					this.socket.close();
					this.dis.close();
					this.dos.close();
					System.out.println("Done");
					break;
				}else if(received.equals("CONNECT")){
					dos.writeUTF("List of available colors: " + availableColors +"\n"+"Width of board:" +width +"\n"+"Height of board:" +height);
				
				}else if( received.length()> 4 && received.substring(0,4).equals("POST")) {
					try {
					String text = received.substring(5,received.length());
					Details info = addFields(new Details(),text);
					if (!availableColors.contains(info.color)) {
						dos.writeUTF("Color is not available. Please try one of the available colors " + availableColors);
					}else{
						int xStart = info.coordinates.get(0);
						int xEnd =  info.coordinates.get(2) + xStart;
						int yStart =  info.coordinates.get(1);
						int yEnd =  info.coordinates.get(3) + yStart;
						System.out.println(xStart+" "+xEnd+" "+yStart+" "+yEnd);
						if ((xStart <= width &&  width >= xEnd) && (yStart <= height && height >= yEnd)){
							this.notes.add(info);
							dos.writeUTF("SUCCESSFUL");
						}else {
							dos.writeUTF("The post doesn't fit on the board. The dimension of the board are: "+ width+" "+height);
					}
					}
					}catch(Exception e) {
						dos.writeUTF("Unrecognized format or command please try again.");
					}
					
					
				}else if( received.length()> 3 &&received.substring(0,3).equals("PIN")) {
					try {
						pinner(received.substring(4,received.length()),"PIN");
						dos.writeUTF("SUCCESSFUL");
					}catch(Exception e) {
						dos.writeUTF("Unrecognized format or command please try again.");
					}
					
				}else if(received.length()> 5 && received.substring(0,5).equals("UNPIN")) {
					try {
						pinner(received.substring(6,received.length()),"UNPIN");
						dos.writeUTF("SUCCESSFUL");
					}catch(Exception e) {
						dos.writeUTF("Unrecognized format or command please try again.");
					}
					
				}else if (received.length()>= 5 &&received.substring(0,5).equals("SHAKE")) {
					try {
					int i = notes.size()-1;
					int count = 0;
					while (count <= i) {
						if (!notes.get(count).pinned) {
							notes.remove(count);
							i--;
						}else {
							count++;
						}
						
					}
					dos.writeUTF("SUCCESSFUL");
					
					}catch(Exception e) {
						dos.writeUTF("Unrecognized format or command please try again.");
				}
				}else if(received.length()>= 5 && received.substring(0,5).equals("CLEAR")) {
					try {
					int i = notes.size()-1;
					int count = 0;
					while (count <= i) {
							notes.remove(count);
							count++;
					}
					i = pins.size()-1;
					count = 0;
					while(count <= i) {
						pins.remove(count);
						count++;
					}
					dos.writeUTF("SUCCESSFUL");
					
				}catch(Exception e) {
					dos.writeUTF("Unrecognized format or command please try again.");
				}
					
				}else if(received.length()>= 8 && received.substring(0,8).equals("GET PINS")) {
					String allPins = "";
					for (int i=0;i<pins.size();i++) {
						System.out.println(pins.get(i));
						String x = String.valueOf(pins.get(i).get(0));
						String y = String.valueOf(pins.get(i).get(1));
						allPins+="| "+x+ ", "+y + " |";
					}
					dos.writeUTF(allPins);
					
					
				}else if(received.length()>= 3 && received.substring(0,3).equals("GET")) {
					if(received.substring(3,received.length()).length() > 0) {
						String color = "";
						List<Integer> contains = new ArrayList<Integer>();
						String refersTo = "";
						int i = 0;
						String text = received.substring(3,received.length());
						
						while(i+5 < text.length()) {
							if(text.substring(i,i+5).equals("color")) {
								try {
									
									for (int c= i+5; c< text.length();c++ ) {
										if(Character.isLetter(text.charAt(c))) {
											color+= text.charAt(c);
										}else if(color != "") {
											break;
										}
									}
									
			
								}catch(Exception e) {
									dos.writeUTF("Unrecognized format or command please try again.");
									
								}
								break;
							}
							i++;
							
						}
						i = 0;
						String num = "";
					
						try {
						while(i+8 < text.length()) {
							if(text.substring(i,i+8).equals("contains")) {
							
									int c = i+8;
									while(c < text.length()) {
										if(Character.isDigit(text.charAt(c))) {
											num+=text.charAt(c);
										}else if(num!="" && contains.size() <=2) {
											contains.add(Integer.parseInt(num));
										}else {
											break;
										}
										c++;
										
										
									}
									
								
								break;
								
							}
							i++;
						}
						}catch(Exception e) {
							dos.writeUTF("Unrecognized format or command please try again.");
						}
						
						i = 0;
						while(i+8 < text.length()) {
							if(text.substring(i,i+8).equals("refersTo")) {
								try {
									for (int c= i+9; c< text.length();c++ ) {
										if(!Character.isWhitespace(text.charAt(c))) {
											refersTo+= text.charAt(c);
										}else if(refersTo != "" ) {
											break;
										}
									}
									
			
								}catch(Exception e) {
									dos.writeUTF("Unrecognized format or command please try again.");
									
								}
								break;
							}
							i++;
							
						}

						String all = "";
						for (i = 0; i < notes.size(); i++) {
							
							if (color == ""||(color != "" && notes.get(i).color.equals(color)))  {
								if (refersTo.isEmpty() && contains.size() == 0) {
									all+="NOTE|  PINNED: "+notes.get(i).pinned+ "|| CONTENT: "+notes.get(i).content +"\n";
									
								}else if((!contains.isEmpty() && !refersTo.isEmpty() )|| (!contains.isEmpty() && refersTo=="")){
									
									int xStart = this.notes.get(i).coordinates.get(0);
									int xEnd = this.notes.get(i).coordinates.get(2) + xStart;
									int yStart = this.notes.get(i).coordinates.get(1);
									int yEnd = this.notes.get(i).coordinates.get(3) + yStart;
									int x = contains.get(0);
									int y = contains.get(1);
									boolean isOnBoard = (x >= xStart && x <= xEnd) && (y >= yStart && y <= yEnd);
									if (( isOnBoard && refersTo.isEmpty()) || (isOnBoard && notes.get(i).content.indexOf(refersTo) != -1)){
										all+="NOTE|  PINNED: "+notes.get(i).pinned+ "|| CONTENT: "+notes.get(i).content +"\n";
									}
									
								}else if(!refersTo.isEmpty() && contains.isEmpty()) {
									
									if (notes.get(i).content.indexOf(refersTo) != -1) {
										all+="NOTE|  PINNED: "+notes.get(i).pinned+ "|| CONTENT: "+notes.get(i).content +"\n";
									}
								}
								
								
							}
						}
						dos.writeUTF(all);
						
						
					
					
					}
				}else {
					dos.writeUTF("Unrecognized format or command please try again.");
				}
			

				
					
				
				System.out.println("Response of client: " + received);
			} catch (Exception e) {
				System.out.println(e);
				try {
					this.dis.close();
					this.dos.close();
					this.socket.close();
				} catch (IOException err) {
					System.out.println("Error: " + err);
				
				}
				break;
			
			}
		}
		
	}
	private Details addFields(Details info, String text) {
		List<Integer> coordinates = new ArrayList<Integer>();
		int c = 0;
		int index = 0;
		String curr= "";
		while (c < 4 && index < text.length()) {
			if (Character.isDigit(text.charAt(index))){
				curr += text.charAt(index);
				index++;
				
			}else {
				coordinates.add(Integer.parseInt(curr));
				curr = "";
				c++;
				index++;
			}
			
		}
		String color = "";

		while (true) {
			if (Character.isLetter(text.charAt(index))) {
				color += text.charAt(index);
				index++;
			}else {
				break;
			}
		}
		
		info.coordinates = coordinates;
		info.color = color;
		info.content = text.substring(index,text.length());
		info.pinned = false;
		info.pinCount = 0;
	
		return info;
	}
	private void pinner(String position,String toDo) {
		List<Integer> pos = new ArrayList<Integer>();
		int i = 0;
		int index = 0;
		String curr = "";
		while (i < 2 && index < position.length() ) {
			if (Character.isDigit(position.charAt(index))){
				curr += position.charAt(index);
				index++;
			}else {
				
				pos.add(Integer.parseInt(curr));
				curr= "";
				i++;
				index++;
			}
		}
		
		pos.add(Integer.parseInt(curr));
		
		if (toDo=="PIN") {
			this.pins.add(pos);
		}else {
			i = pins.size()-1;
			int count = 0;
			while (count <= i) {
				if (pins.get(count).equals(pos)) {
					pins.remove(count);
					i--;
				}else {
					count++;
				}
			}
		}
		
		for (int c = 0; c<this.notes.size();c++) {
			int xStart = this.notes.get(c).coordinates.get(0);
			int xEnd = this.notes.get(c).coordinates.get(2) + xStart;
			int yStart = this.notes.get(c).coordinates.get(1);
			int yEnd = this.notes.get(c).coordinates.get(3) + yStart;
			int x = pos.get(0);
			int y = pos.get(1);
			if ((x >= xStart && x <= xEnd) && (y >= yStart && y <= yEnd)){
				if (toDo=="PIN") {
					this.notes.get(c).pinned = true;
					this.notes.get(c).pinCount++;
				}else if (notes.get(c).pinCount > 0 ){
					this.notes.get(c).pinCount--;
					if (this.notes.get(c).pinCount == 0) {
						this.notes.get(c).pinned = false;
				}
				
				}
				
				
			}
			
		}
	}

}

