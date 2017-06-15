package clientebacarat;

import javax.swing.JFrame;

public class PrincipalCliente {
	 
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			ClienteBacarat aplicacion; // declara la aplicación cliente
			// si no hay argumentos de línea de comandos
			if ( args.length == 0 ){
				aplicacion = new ClienteBacarat("127.0.0.1"); // localhost
			}
			else {
				aplicacion = new ClienteBacarat(args[0]); // usa args
			}
			aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			aplicacion.ejecutar();
		}

}
