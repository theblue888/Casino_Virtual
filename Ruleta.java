package clienteRuleta;


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
import java.util.ArrayList;
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

public class Ruleta extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private String mensaje = "";
	private String servidor;
	private Socket cliente;
	private Manejador escucha = new Manejador();
	private Thread timer = new Thread();
	private JPanel panel = new JPanel();
	private JLabel fondo = new JLabel();
	private JLabel rueda = new JLabel();
	private ImageIcon ruedaImagen = new ImageIcon(getClass().getResource("/resources/rueda.gif"));
	private ImageIcon mesa = new ImageIcon(getClass().getResource("/resources/mesa.png"));
	private JButton apostar = new JButton("Apostar");
	private JComboBox<String> lista1,lista2;
	private JTextField valor;
	private DisplayPanel numero;
	private JPanel color;
	private JLabel titulo1, titulo2;
	private DisplayPanel creditos;
	private JPanel fondo2;
	private ArrayList<String> mensajes = new ArrayList<String>();
	private Boolean[] red = { false, true, false, true, false, true, false, true, false, true, false, false, true,false, true, false, true, false, true, true, false, true, false, true, false, true, false, true, false,false, true, false, true, false, true, false, true };
	private String[] jugadas = { "Rojo/Negro", "Par/Impar", "Pasa/Falta", "Docena", "Columna", "Dos Docenas","Dos Columnas", "Seisena", "Cuadro", "Transversal", "Caballo-H", "Caballo-V", "Pleno" };
	private String[][] listas = { { "Rojo", "Negro" }, { "Par", "Impar" }, { "Pasa", "Falta" },{ "Primera", "Segunda", "Tercera" }, { "Primera", "Segunda", "Tercera" }, { "1ra y 2da", "2da y 3ra" },{ "1ra y 2da", "2da y 3ra" }, { "1 al 6", "7 al 12", "13 al 18", "19 al 24", "25 al 30", "31 al 36" },{ "1-5", "2-6", "4-8", "5-9", "7-11", "8-12", "10-14", "11-15", "13-17", "14-18", "16-20", "17-21", "19-23","20-24", "22-26", "23-27", "25-29", "26-30", "28-32", "29-33", "31-35", "32-36" },{ "0,1 y 2", "0,2 y 3" },{ "1", "2", "4", "5", "7", "8", "10", "11", "13", "14", "16", "17", "19", "20", "22", "23", "25", "26","28", "29", "31", "32", "34", "35" },{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19","20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33" },{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18","19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34","35", "36" }};
	
	
	public Ruleta(String host, String jugador) {
		super("Ruleta - " + jugador);
		initGUI();
		servidor= host;
	}

	// Inicializa la interfaz Grafica del servidor
	public void initGUI() {

		rueda.setIcon(ruedaImagen);
		rueda.setSize(ruedaImagen.getIconWidth(), ruedaImagen.getIconHeight());
		rueda.setBorder(BorderFactory.createEmptyBorder());
		ruedaImagen.setImageObserver(rueda);
		rueda.setBounds(85, 124, rueda.getWidth() - 5, rueda.getHeight() - 5);
		rueda.setVisible(false);
		panel.add(rueda);

		apostar.setBounds(640, 350, 180, 30);
		apostar.addActionListener(escucha);
		panel.add(apostar);

		lista1 = new JComboBox<String>(jugadas);
		lista1.addActionListener(escucha);
		lista1.setBounds(490, 350, 135, 30);
		panel.add(lista1);

		lista2 = new JComboBox<String>();
		gestorComboBox(jugadas[0]);
		lista2.setBounds(340, 350, 135, 30);
		panel.add(lista2);

		valor = new JTextField();
		valor.setBounds(190, 350, 135, 30);
		panel.add(valor);

		numero = new DisplayPanel(0, 2);
		numero.setBounds(380, 35, numero.getWidth(), 33);
		numero.setBorder(BorderFactory.createEmptyBorder());
		panel.add(numero);

		color = new JPanel();
		color.setBounds(360, 35, 15, 33);
		color.setBackground(Color.green);
		panel.add(color);

		titulo1 = new JLabel();
		titulo1.setText("Creditos");
		titulo1.setFont(new Font("Monospaced", Font.BOLD, 20));
		titulo1.setBounds(450, 5, 135, 33);
		panel.add(titulo1);

		creditos = new DisplayPanel(0, 5);
		creditos.setBounds(450, 35, numero.getWidth(), 33);
		panel.add(creditos);

		titulo2 = new JLabel();
		titulo2.setText("Tiro");
		titulo2.setFont(new Font("Monospaced", Font.BOLD, 20));
		titulo2.setBounds(360, 5, 135, 33);
		panel.add(titulo2);

		fondo2 = new JPanel();
		fondo2.setBackground(Color.gray);
		fondo2.setBounds(340, 10, 240, 58);
		panel.add(fondo2);

		campoIntroducir = new JTextField();
		campoIntroducir.setEditable(false);
		campoIntroducir.addActionListener(escucha);
		campoIntroducir.setBounds(640, 10, 180, 33);
		panel.add(campoIntroducir);

		areaPantalla = new JTextArea();
		JScrollPane scroll = new JScrollPane(areaPantalla);
		scroll.setBounds(640, 43, 180, 300);
		panel.add(scroll);

		panel.setBackground(Color.DARK_GRAY);
		panel.setLayout(null);

		fondo.setIcon(mesa);
		fondo.setSize(mesa.getIconWidth(), mesa.getIconHeight());
		fondo.setBounds(0, 0, fondo.getWidth(), fondo.getHeight());
		panel.add(fondo);

		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(840, 430);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
	}

	// Cambia el contenido del segundo comboBox
	public void gestorComboBoxAux(int index) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(listas[index]);
		lista2.setModel(model);
	}

	// Define las listas de los ComboBox
	public void gestorComboBox(String combo1) {
		if (combo1.equals("Rojo/Negro"))
			gestorComboBoxAux(0);
		if (combo1.equals("Par/Impar"))
			gestorComboBoxAux(1);
		if (combo1.equals("Pasa/Falta"))
			gestorComboBoxAux(2);
		if (combo1.equals("Docena"))
			gestorComboBoxAux(3);
		if (combo1.equals("Columna"))
			gestorComboBoxAux(4);
		if (combo1.equals("Dos Docenas"))
			gestorComboBoxAux(5);
		if (combo1.equals("Dos Columnas"))
			gestorComboBoxAux(6);
		if (combo1.equals("Seisena"))
			gestorComboBoxAux(7);
		if (combo1.equals("Cuadro"))
			gestorComboBoxAux(8);
		if (combo1.equals("Transversal"))
			gestorComboBoxAux(9);
		if (combo1.equals("Caballo-H"))
			gestorComboBoxAux(10);
		if (combo1.equals("Caballo-V"))
			gestorComboBoxAux(11);
		if (combo1.equals("Pleno"))
			gestorComboBoxAux(12);
	}

	// Metodo para hacer girar la ruleta animada
	public void start() {
		stop();
		timer = new Thread(this);
		timer.start();
	}

	// Metodo para parar el giro la ruleta animada
	public void stop() {
		if (timer != null) {
			timer.interrupt();
			timer = null;
		}
	}

	// Metodo para ejecutar el juego depues de girar la ruleta animada
	public void run() {
		rueda.setVisible(true);
		apostar.setEnabled(false);
		peticiones();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		jugar();
	}

	// Genera las peticiones para hacer una partida
	private void peticiones() {
		enviarDatos(lista1.getSelectedItem().toString());
		enviarDatos(lista2.getSelectedItem().toString());
		enviarDatos(String.valueOf(tomarApuesta()));
		enviarDatos("Apuesta");
	}

	// Efectua las acciones depues de que gira la ruleta
	public void jugar() {
		int tiro = Integer.parseInt(mensajes.get(mensajes.size()-2));
		int premio = Integer.parseInt(mensajes.get(mensajes.size()-1));
		
		colorear(tiro);
		rueda.setVisible(false);
		numero.setPoints(tiro, false);
		apostar.setEnabled(true);
		
		creditos.setPoints(premio, true);
		repaint();
	}

	// toma la el valor de la apuesta del jugador
	public int tomarApuesta() {
		try {
			int out = Integer.parseInt(valor.getText());
			return out;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// recupera donde el jugador hizo su apuesta
	public String[] recuperarEstadoActual() {
		String[] estadoActual = { lista1.getSelectedItem().toString(), lista2.getSelectedItem().toString()};
		return estadoActual;
	}

	// Muestra el color del numero
	public void colorear(int tiro) {
		if (tiro == 0)
			color.setBackground(Color.GREEN);
		if (red[tiro]) {
			color.setBackground(Color.RED);
		} else {
			color.setBackground(Color.BLACK);
		}
	}

	// Se conecta al servidor y procesa los mensajes que este envia
	public void ejecutarCliente() {
		try {
			cliente = new Socket(InetAddress.getByName(servidor), 12345);
			salida = new ObjectOutputStream(cliente.getOutputStream());
			salida.flush();
			entrada = new ObjectInputStream(cliente.getInputStream());
			mostrarMensaje("Flujos de E/S listos", "Cliente");
			procesarConexion();
		} catch (EOFException eofe) {
			mostrarMensaje("Se termino la conexion", "Cliente");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			cerrarConexion();
		}
	}

	// Procesa la conexion
	private void procesarConexion() throws IOException {
		establecerCampoEditable(true);

		do {
			try {
				mensaje = (String) entrada.readObject();
				guardarMensaje(mensaje);
				mostrarMensaje(mensaje, "Servidor");
			} catch (ClassNotFoundException e) {
				mostrarMensaje("Tipo de objecto desconocido", "Cliente");
			}
		} while (!mensaje.equals("SERVIDOR>>>TERMINAR"));
	}

	private void guardarMensaje(String mensaje){
		mensajes.add(mensaje);
	}
	// Cierra flujos y socket
	private void cerrarConexion() {
		mostrarMensaje("Cerrando Conexion", "Cliente");
		establecerCampoEditable(false);
		try {
			salida.close();
			entrada.close();
			cliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Envia un mensaje al servidor
	private void enviarDatos(String mensaje) {
		try {
			salida.writeObject(mensaje);
			salida.flush();
			mostrarMensaje(mensaje, "Cliente");
		} catch (IOException e) {
			areaPantalla.append("\n Error al escribir el Objeto");
		}
	}
	
	

	// Manipula el objeto areaPantalla en el subproceso despachador de eventos
	private void mostrarMensaje(String mensaje, String emisor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				areaPantalla.append("\n" + emisor + ": " + mensaje);
			}
		});
	}

	// manipula al campo de texto en el despachador de eventos
	private void establecerCampoEditable(final Boolean editable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				campoIntroducir.setEditable(editable);
			}
		});
	}

	private class Manejador implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(lista1))
				gestorComboBox(lista1.getSelectedItem().toString());
			if (e.getSource().equals(apostar))
				start();
			else {
				enviarDatos(e.getActionCommand());
				campoIntroducir.setText("");
			}
		}

	}

}
