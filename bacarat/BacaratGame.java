package bacarat;


import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BacaratGame /*implements Runnable*/ extends JFrame{
	private int coins;
	private int cartas[]= new int[3];//las cartas de el crupier;
	private ServerSocket server;
	private Socket conexion;
	private ObjectInputStream in;// reservado para la transformacion cliente servidor.
	private ObjectOutputStream out;// reservado para la transformacion cliente servidor.
	private Jugador player; // representa el estado del jugador.
	private boolean plantado;
	private int Total;// represente el valor real en la partida, pues si este supera el 9 empieza en 1 de nuevo. es decir, es circunavegable.
	private JTextArea areaPantalla;
	
	public BacaratGame(/*Socket connection*/){
		initGUI();
		try {
			server= new ServerSocket(12345, 1);
			conexion= server.accept();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iniciarsv();
		
	}
	
	public void initGUI(){
		areaPantalla = new JTextArea( 4, 30 );
		areaPantalla.setEditable( false );
		add( new JScrollPane( areaPantalla ), BorderLayout.SOUTH );
		setSize( 325, 225 );
		setVisible( true );
		setTitle("Server");
	}
	public void iniciarsv(){
		try {
			in = new ObjectInputStream(conexion.getInputStream()); 
			out= new ObjectOutputStream(conexion.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void execute(){
		//play();	
		try {
				if(((String) in.readObject()).equals("Ready?")){
					out.writeChars("Conexion exitosa");
				}
			out.flush();
			out.writeObject(new String("Start the game"));
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		
		
	
	
	public void run(){
		play();
	}

	public void revisar(){// revisa la siguiente jugada del crupier. y ademas usa estados del jugador.
		int terceraC= pedirCarta(); //representa la tercera carta asignada al jugador.

		if(cartas[0]+cartas[1] >9){Total= (cartas[0] + cartas[1])- 10;}

		if(! player.getNatural()){ // entrega la tercera carta al jugador.

			if(! player.plantado){
				int f[]=player.getCards();
				f[2]= terceraC;
				player.setCards(f);


				if(Total >= 7 ){
					plantado=true;

				}else if(Total == 6 && (terceraC== 7 || terceraC == 6)){
					cartas[2]=pedirCarta();

				}else if(Total == 5 && (terceraC== 7 || terceraC == 4)){
					cartas[2]=pedirCarta();

				}else if(Total == 4 && (terceraC== 7 || terceraC == 2)){
					cartas[2]=pedirCarta();

				}else  if(Total == 3 && (terceraC != 8)){
					cartas[2]=pedirCarta();

				}else  if(Total == 0 || Total == 1 || Total==2){
					cartas[2]=pedirCarta();

				}

			}

		}

	}
	public void ganador(){
		if(Total + cartas[2]>9){
			int crupier = (Total + cartas[2])/9;
		}
		if(Total + (player.getCards()[2]) >9){
			int jugador=(Total + (player.getCards()[2]) ) /9;
		}

		if(Total < player.Total){
			System.out.println("Gana el Player");
			try {
				out.writeChars("Gana el Player");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if(Total > player.Total){
			System.out.println("Gana el Crupier");
			try {
				out.writeChars("Gana el Crupier");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public int pedirCarta(){
		int ran=  (int) (Math.random() * 13) + 1; // Numero random entre 1-13.
		return ran; // retorna una carta aleatoria.
	}
	public void play(){

		cartas[0]= pedirCarta(); // se pide la primera carta del crupier.
		cartas[1]= pedirCarta();// se pide la seguunda carta del crupier.
		int f[]= player.getCards();
		f[0]= pedirCarta();// se pide la primera carta del crupier.
		f[1]=pedirCarta();// se pide la segunda carta del
		player.setCards(f);
		player.revisar();
		revisar();
		ganador();

	}


	public String asignador(int cartaID){ // Junto a un Random, Retorna una carta. // en replanteamineto // queda inutilizado por el momento. debe ser util mas adelante.

		switch(cartaID){
		case 1:{return "A";}
		case 2:{return "2";}
		case 3:{return "3";}
		case 4:{return "4";}
		case 5:{return "5";}
		case 6:{return "6";}
		case 7:{return "7";}
		case 8:{return "8";}
		case 9:{return "9";}
		case 10:{return "10";}
		case 11:{return "REY";}
		case 12:{return "REINA";}
		case 13:{return "JACK";}
		default:{System.out.println("ERROR, el Asignador a entrado en Default"); return "EstoEsUnError"; }
		}
	}


	public class Jugador{//clase que representa al jugador.
		private int coins;
		private int cartas[]=new int[3];//Cartas del jugador
		private boolean plantado;  // determina si el jugador esta plantado.
		private int Total;
		private boolean natural;

		public void revisar(){ // verifica si el jugador se planta o pide otra carta

			if(cartas[0] + cartas[0]>9){Total = (cartas[0] + cartas[1]) -10;} // esto puede limitare a una funcion // por modificar.

			if(Total> 7 ){
				plantado=true;
				natural=true;

			}else if(Total >= 6 ){
				plantado=true; // voy a suponer que estar plantado no significa nada, pues no comprendo el rol que lleva en una partida.
			}else if(Total < 6 && Total > 0 ){

			}


		}

		public void setPlantado(boolean plant){
			plantado=plant;

		}
		public boolean getPlantado(){
			return plantado;
		}
		public boolean getNatural(){
			return natural;
		}
		public void setNatural(boolean j){
			natural = j;

		}

		public int[] getCards(){// devuelve arreglo de enteros.

			return cartas;
		}

		public void setCoins( int coins){
			this.coins=coins;
		}

		public void setCards(int card[]){
			cartas= card;
		}


	}


}
