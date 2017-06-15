package bacarat;

import javax.swing.JFrame;

public class PrincipalBacarat {
	public static void main(String[] args) {
		
		BacaratGame aplicacion = new BacaratGame();
		aplicacion.execute();
		aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
