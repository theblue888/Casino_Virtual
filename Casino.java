package casino_virtual;

import java.net.ServerSocket;

public class Casino { //servidor global //tengo la duda de si el hay que hacer dos clientes y dos sv, o hacer un sv central y luego dos sv secundarios
//SEBASTIAN: Yo opino que debemos hacer un solo servidor principal que maneje la logica de cualquiera de los 2 juegos	
	private ServerSocket svSocket;
	private Bacarat bacaratGame; // For Bacarat game
	
	
	Casino(String game){
		if(game == "BACARAT"){
			bacaratGame = new Bacarat(args); // start Bacarat Game
		}else if(game == "RULETA"){
			//Init. Ruleta // RULETA MULTICLIENTE?
		} 
	}
	
	

}
