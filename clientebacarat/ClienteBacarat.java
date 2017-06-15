package clientebacarat;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClienteBacarat extends JFrame{
	private Socket conexion;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String hostSV; // "SV" mean Server.
	private JTextArea areaPantalla;
	
	public ClienteBacarat(String host){
		initGUI();
		initSC(); //"SC" mean Server Connection.
		
	}
	
	public void initGUI(){
		areaPantalla = new JTextArea( 4, 30 );
		areaPantalla.setEditable( false );
		add( new JScrollPane( areaPantalla ), BorderLayout.SOUTH );
		setSize( 325, 225 );
		setVisible( true );
		setTitle("Cliente");
	}
	public void ejecutar(){
		try {
			while(true){
				
					if(in.getClass().equals(new String())){
					areaPantalla.setText( (String) in.readObject());
					System.out.println((String) in.readObject());
					System.out.println("llego");
					}
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void initSC(){
		try {
			conexion = new Socket(InetAddress.getByName(hostSV), 12345);
			in= new ObjectInputStream(conexion.getInputStream());
			out= new ObjectOutputStream(conexion.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
