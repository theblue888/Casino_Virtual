package clienteRuleta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Ruleta extends JFrame implements Runnable{
	
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private String mensaje="";
	private String servidorChat;
	private Socket cliente;
	private Manejador escucha = new Manejador();
	private Thread timer = new Thread();
	private JPanel panel = new JPanel();
	private JLabel fondo = new JLabel();
	private JLabel rueda = new JLabel();
	private ImageIcon ruedaImagen = new ImageIcon(getClass().getResource("/resources/rueda.gif"));
	private ImageIcon mesa = new ImageIcon(getClass().getResource("/resources/mesa.png"));
	private JButton apostar = new JButton("Apostar");
	private JComboBox lista1, lista2;
	private JTextField valor;
	private DisplayPanel numero;
	private JPanel color;
	private JLabel titulo1,titulo2;
	private DisplayPanel creditos;
	private JPanel fondo2;
	private Ruleta_Control control = new Ruleta_Control();
	private String repuesta = "";
	
		
	public Ruleta(String host,String jugador){
		super("Ruleta - " + jugador);
		initGUI();
		servidorChat = host;
	}
	
	//inicializa la interfaz Grafica del servidor
	public void initGUI(){
		
		rueda.setIcon(ruedaImagen);
		rueda.setSize(ruedaImagen.getIconWidth(),ruedaImagen.getIconHeight());
		rueda.setBorder(BorderFactory.createEmptyBorder());
		ruedaImagen.setImageObserver(rueda);
		rueda.setBounds(85,124,rueda.getWidth()-5,rueda.getHeight()-5);
		rueda.setVisible(false);
		panel.add(rueda);
		 
		apostar.setBounds(500, 350, 90, 30);
		apostar.addActionListener(escucha);
		panel.add(apostar);
		
		lista1 = new JComboBox(control.getJugadas());
		lista1.addActionListener(escucha);
		lista1.setBounds(350, 350,135 ,30);
		panel.add(lista1);
				
		lista2 = new JComboBox();
		gestorComboBox(control.getJugadas()[0]);
		lista2.setBounds(200, 350,135 ,30);
		panel.add(lista2);
		
		valor = new JTextField();
		valor.setBounds(50, 350,135 ,30);
		panel.add(valor);
		
		numero = new DisplayPanel(0,2);
		numero.setBounds(380,35,numero.getWidth(),33);
		numero.setBorder(BorderFactory.createEmptyBorder());
		panel.add(numero);
		
		color = new JPanel();
		color.setBounds(360,35,15,33);
		color.setBackground(Color.green);
		panel.add(color);
		
		titulo1 = new JLabel();
		titulo1.setText("Creditos");
		titulo1.setFont(new Font("Monospaced", Font.BOLD, 20));
		titulo1.setBounds(450, 5, 135, 33);
		panel.add(titulo1);
		
		creditos = new DisplayPanel(0,5);
		creditos.setBounds(450,35,numero.getWidth(),33);
		panel.add(creditos);
		
		titulo2 = new JLabel();
		titulo2.setText("Tiro");
		titulo2.setFont(new Font("Monospaced", Font.BOLD, 20));
		titulo2.setBounds(360, 5, 135, 33);
		panel.add(titulo2);
		
		fondo2 = new JPanel();
		fondo2.setBackground(Color.gray);
		fondo2.setBounds(340,10,240,58);
		panel.add(fondo2);
		
		campoIntroducir = new JTextField();
		campoIntroducir.setEditable(false);
		campoIntroducir.addActionListener(escucha);
		campoIntroducir.setBounds(170,430,300,33);
		panel.add(campoIntroducir);
		
		areaPantalla = new JTextArea();
		JScrollPane scroll = new JScrollPane(areaPantalla);
		scroll.setBounds(170, 463, 300, 200);
		panel.add(scroll);
		
		
		panel.setBackground(Color.DARK_GRAY);
		panel.setLayout(null);
		
		
		fondo.setIcon(mesa);
		fondo.setSize(mesa.getIconWidth(), mesa.getIconHeight());
		fondo.setBounds(0,0,fondo.getWidth(),fondo.getHeight());
		panel.add(fondo);
		
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(650,730);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}

	//Cambia el contenido del segundo comboBox
	public void gestorComboBoxAux(int index){
		DefaultComboBoxModel model = new DefaultComboBoxModel(control.getListas(index));
		lista2.setModel(model);
	}
	
	//Define las listas de los ComboBox
	public void gestorComboBox(String combo1){
		if(combo1.equals("Rojo/Negro"))		gestorComboBoxAux(0);
		if(combo1.equals("Par/Impar"))		gestorComboBoxAux(1);
		if(combo1.equals("Pasa/Falta"))		gestorComboBoxAux(2);
		if(combo1.equals("Docena"))			gestorComboBoxAux(3);
		if(combo1.equals("Columna"))		gestorComboBoxAux(4);
		if(combo1.equals("Dos Docenas"))	gestorComboBoxAux(5);
		if(combo1.equals("Dos Columnas"))	gestorComboBoxAux(6);
		if(combo1.equals("Seisena"))		gestorComboBoxAux(7);
		if(combo1.equals("Cuadro"))			gestorComboBoxAux(8);
		if(combo1.equals("Transversal"))	gestorComboBoxAux(9);
		if(combo1.equals("Caballo-H"))		gestorComboBoxAux(10);
		if(combo1.equals("Caballo-V"))		gestorComboBoxAux(11);
		if(combo1.equals("Pleno"))			gestorComboBoxAux(12);
	}
	
	//Metodo para hacer girar la ruleta animada
	public void start(){
		stop();
		timer= new Thread(this);
		timer.start();
	}
	
	//Metodo para parar el giro la ruleta animada
	public void stop(){
		if(timer!=null){
			timer.interrupt();
			timer = null;
		}
		
	}
	
	//Metodo para ejecutar el juego depues de girar la ruleta animada
	public void run() {
		rueda.setVisible(true);
		apostar.setEnabled(false);
		peticiones();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jugar();
	}
	
	//Genera las peticiones para hacer una partida
	private void peticiones(){
		enviarDatos("tiro");
		
	}
	
	//Efectua las acciones depues de que gira la ruleta
	public void jugar(){
		int tiro = Integer.parseInt(mensaje); 
		colorear(tiro);
		rueda.setVisible(false);
		numero.setPoints(tiro, false);
		apostar.setEnabled(true);
		int premio = control.premio(recuperarEstadoActual(),tomarApuesta());
		creditos.setPoints(premio,true);
		repaint();
	}
	
	//toma la el valor de la apuesta del jugador 
	public int tomarApuesta(){
		try{
			int out = Integer.parseInt(valor.getText());
			return out;
		}
		catch(NumberFormatException e){
			return 0;
		}
	}

	//recupera donde el jugador hizo su apuesta
	public String[] recuperarEstadoActual(){
		String[] estadoActual = {lista1.getSelectedItem().toString(),lista2.getSelectedItem().toString()};
		return estadoActual;
	}
	
	//Muestra el color del numero
	public void colorear(int tiro){
		if(tiro==0)color.setBackground(Color.GREEN);
		if(control.isRed()[tiro]){
			color.setBackground(Color.RED);
		}
		else{
			color.setBackground(Color.BLACK);
		}
	}
	
	//Se conecta al servidor y procesa los mensajes que este envia
	public void ejecutarCliente(){
		try{
			conectarAlServidor();
			obtenerFlujos();
			procesarConexion();
		}
		catch(EOFException eofe){
			mostrarMensaje("\n El Cliente termino la conexion");
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		finally{
			cerrarConexion();
		}
	}
	
	//Hace la conexion al servidor
	private void conectarAlServidor() throws IOException{
		mostrarMensaje(" Intentando realizar conexion \n");
		cliente = new Socket(InetAddress.getByName(servidorChat),12345);
		mostrarMensaje(" Conectando a: " + cliente.getInetAddress().getHostName());
	}
	
	//Obtiene los flujos objetos que se envian
	private void obtenerFlujos() throws IOException{
		salida = new ObjectOutputStream(cliente.getOutputStream());
		salida.flush();
		
		entrada = new ObjectInputStream(cliente.getInputStream());
		mostrarMensaje("\n Se obtuvieron los flujos de E/S");
	}
	
	//Procesa la conexion
	private void procesarConexion() throws IOException{
		establecerCampoEditable(true);

		do{
			try{
				mensaje = (String)entrada.readObject();
				mostrarMensaje(mensaje);
			}
			catch(ClassNotFoundException e){
				mostrarMensaje("\n Tipo de objecto desconocido");
			}
		}
		while(!mensaje.equals("SERVIDOR>>>TERMINAR"));
	}
	
	
	//Cierra flujos y socket
	private void cerrarConexion(){
		mostrarMensaje("\n Cerrando Conexion");
		establecerCampoEditable(false);
		try{
			salida.close();
			entrada.close();
			cliente.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//Envia un mensaje al servidor
	private void enviarDatos(String mensaje){
		try{
			salida.writeObject(mensaje);
			salida.flush();
			mostrarMensaje(mensaje);
		}
		catch(IOException e){
			areaPantalla.append("\n Error al escribir el Objeto");
		}
	}
	
	//Manipula el objeto areaPantalla en el subproceso despachador de eventos
	private void mostrarMensaje(final String mensajeAMostrar){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						areaPantalla.append("\n" + mensajeAMostrar);
					}
				}
		);
	}
	
	//manipula al campo de texto en el despachador de eventos
	private void establecerCampoEditable(final Boolean editable){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						campoIntroducir.setEditable(editable);
					}
		});
	}
	
	private class Manejador implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource().equals(lista1))gestorComboBox(lista1.getSelectedItem().toString());
			if(e.getSource().equals(apostar))start();
			else{
				enviarDatos(e.getActionCommand());
				campoIntroducir.setText("");
			}
		}
		
	}
	
}
