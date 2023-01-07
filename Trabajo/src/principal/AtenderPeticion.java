package principal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class AtenderPeticion implements Runnable{
	Socket socket1;
	Socket socket2;
	BufferedWriter bw1;
	BufferedWriter bw2;
	public AtenderPeticion(Socket s1, Socket s2, BufferedWriter w1, BufferedWriter w2) {
		this.socket1=s1;
		this.socket2=s2;
		this.bw1=w1;
		this.bw2=w2;
	}
	@Override
	public void run() {
		
		try(BufferedReader br1=new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			BufferedReader br2=new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			DataOutputStream dis1=new DataOutputStream(socket1.getOutputStream());
			DataOutputStream dis2=new DataOutputStream(socket2.getOutputStream());
			) {

			FileWriter fw=new FileWriter("Desarrollo.txt");
			
			Jugador jugador1= new Jugador(bw1, br1);
			Jugador jugador2 = new Jugador(bw2, br2);
			Partida game = new Partida(jugador1, jugador2,fw);
			
			List<String> octavos= new ArrayList<String>();
			List<String> cuartos= new ArrayList<String>();
			List<String> semifinales= new ArrayList<String>();
			List<String> finalistas= new ArrayList<String>();
			List<String> primeros= new ArrayList<String>();
			List<String> segundos= new ArrayList<String>();
			
			faseDeGrupos(game, primeros, segundos, octavos);
			
//			puntuaciones = faseDeGrupos(bw1, bw2, br1, br2, fw, primeros, segundos, octavos);
			generarOctavos(primeros, segundos, octavos);			

			octavos(bw1,br1,fw,octavos,cuartos);
			cuartos(bw1,br1,fw, cuartos,semifinales);
			semifinales(bw1,br1,fw,semifinales,finalistas);
			finales(bw1,br1,fw,finalistas);		
			cerrar(fw,bw1,dis1);

		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	public static String letraGrupo(int i) {
		if(i==0) {return "A";}
		if(i==1) {return "B";}
		if(i==2) {return "C";}
		if(i==3) {return "D";}
		if(i==4) {return "E";}
		if(i==5) {return "F";}
		if(i==6) {return "G";}
		if(i==7) {return "H";}
		return "";
	}
	
	//ESTO HABRÍA QUE METERLO EN EL EsperarRespuesta
	public static String resultadoValido(BufferedReader br, BufferedWriter bw, ArrayList<String> equiposGrupo, int m)throws  IOException{
		String respuesta = "";
		String equipo = "";
		boolean igual=false;
		while(!igual) {
			respuesta=br.readLine();
			for(int n=0 ;n<m ;n++) {
				equipo =equiposGrupo.get(n);
				if(respuesta.equalsIgnoreCase(equipo)){
					igual=true;
					n=4;
				}
			}
			if(!igual) {
				bw.write("Vuelve a escribirlo correctamente: \r\n");
				bw.write("ya" +  "\r\n");
				bw.flush();
			}			
		}
		return equipo;
	}
	
	public static void actualizarPuntuaciones(Partida game, String s1, String s2, long t1, long t2, String respuesta) {
		boolean acertada=false;
		if(s1.equalsIgnoreCase(respuesta)) {
			acertada=true;
			game.getJugador1().addPunto();
		}
		if(s2.equalsIgnoreCase(respuesta)) {
			if(acertada) {
				if(t2<t1) {
					game.getJugador1().lowPunto();
					game.getJugador2().addPunto();
				}
			}
			else {
				game.getJugador2().addPunto();
			}
		}
	}
	
	public static void faseDeGrupos(Partida game, List<String> primeros,List<String> segundos,List<String> octavos) {
		try{

			DocumentBuilderFactory dbf=DocumentBuilderFactory.newDefaultInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.parse(".\\src\\clasificacion.xml");
			
			Element root=doc.getDocumentElement();	
			NodeList grupos=root.getElementsByTagName("grupo");
			NodeList equipos=root.getElementsByTagName("equipo");
			
			game.getJugador1().getBw().write("Empieza el juego, introduce tu nombre:\r\n"); 
			game.getJugador1().getBw().write("ya" +  "\r\n");
			game.getJugador1().getBw().flush();
			
			game.getJugador2().getBw().write("Empieza el juego, introduce tu nombre:\r\n"); 
			game.getJugador2().getBw().write("ya" +  "\r\n"); 
			game.getJugador2().getBw().flush();
			
			String grupo;
			int k=0;
			game.getJugador1().setNombre(game.getJugador1().getBr().readLine());
			game.getJugador2().setNombre(game.getJugador2().getBr().readLine());
			game.getFw().write("Predicciones de los jugadores: " + game.getJugador1().getNombre() + " y "+ game.getJugador2().getNombre() + "\r\n");
			game.getFw().write("Fase de grupos:  \r\n");
			game.getFw().write("\r\n");
			for(int i=0;i<grupos.getLength();i=i+1) {
				
				grupo = letraGrupo(i);
				game.getJugador1().getBw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n"); 
				game.getJugador2().getBw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n"); 
				k=i*4;
				ArrayList<String> equiposGrupo = new ArrayList<String>();
				String equipo,equipoPrimero="",equipoSegundo="";
				for(int j=0; j<4;j++) {
					if(equipos.item(k).hasAttributes()){
				        NamedNodeMap attr = equipos.item(k).getAttributes();
				        Node nodeAttr2 = attr.getNamedItem("id");
				        if(nodeAttr2.getNodeValue().equals("primero")) {
				        	equipoPrimero=equipos.item(k).getTextContent();
				        }else if(nodeAttr2.getNodeValue().equals("segundo")) {
				        	equipoSegundo=equipos.item(k).getTextContent();
				        }
					}
					equipo=equipos.item(k).getTextContent();
					equiposGrupo.add(equipo);
					game.getJugador1().getBw().write(equipo +  "\r\n"); 
					game.getJugador2().getBw().write(equipo +  "\r\n"); 
					k++;
				}
				game.getJugador1().getBw().write("ya" +  "\r\n");
				game.getJugador1().getBw().flush();
				
				game.getJugador2().getBw().write("ya" +  "\r\n");
				game.getJugador2().getBw().flush();
				
				game.getFw().write("Grupo " + grupo + ": \r\n");
				//HASTA AQUÍ FUNCIONA BIEN
				
				//Aquí es donde hay que hacer lo de los hilos
				EsperarRespuesta er1=new EsperarRespuesta(game.getJugador1().getBr(),game.getJugador1().getBw(),equiposGrupo,4);
				EsperarRespuesta er2=new EsperarRespuesta(game.getJugador2().getBr(),game.getJugador2().getBw(),equiposGrupo,4);
				er1.start();
				er2.start();
				er1.join();
				er2.join();
				//Hay que sacar el primero del grupo correspondiente (i).
				
				//respuesta= xml(primero(i)) MIRAR COMO LO SACAS CON EL id MUELAS
				actualizarPuntuaciones(game,er1.getRespuesta(),er2.getRespuesta(),
						er1.getTiempo(),er2.getTiempo(),equipoPrimero);
				
				game.getFw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n");
				game.getFw().write("Respuesta Jugador1: "+er1.getRespuesta()+"\r\n");
				game.getFw().write("Respuesta Jugador2: "+er2.getRespuesta()+"\r\n");
				

				game.getJugador1().getBw().write("Puntuaciones: " + game.getJugador1().getNombre()+ ": "+game.getJugador1().getPuntos()+  "Puntos. "+ game.getJugador2().getNombre()+ ": "+game.getJugador2().getPuntos()+" puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Puntuaciones: "+ game.getJugador1().getNombre()+ ": "+game.getJugador1().getPuntos()+" puntos. "+ game.getJugador2().getNombre()+ ": "+game.getJugador2().getPuntos()+" puntos. \r\n");
				game.getJugador2().getBw().flush();

//				//ESTO HABRÍA QUE METERLO EN EL EsperarRespuesta
//				respuesta1=resultadoValido(game.getJugador1().getBr(), game.getJugador1().getBw(), equiposGrupo, 4);
//				respuesta2=resultadoValido(game.getJugador2().getBr(), game.getJugador2().getBw(), equiposGrupo, 4);
				
				//Esto mantenerlo aquí
				equiposGrupo.remove(equipoPrimero);
				
//				game.getFw().write("Primero según "+nombre1+" y "+nombre2+".\r\n");
//				game.getFw().write(er1.respuesta +" y "+er2.respuesta+".\r\n");
				
				primeros.add(equipoPrimero);
				
				//Hay que meter el hilo y darle los puntos al que haya acertado antes.
				
				game.getJugador1().getBw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getJugador1().getBw().write("ya" +  "\r\n");
				game.getJugador1().getBw().flush();
				
				game.getJugador2().getBw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getJugador2().getBw().write("ya" +  "\r\n");
				game.getJugador2().getBw().flush();
				
				EsperarRespuesta er11=new EsperarRespuesta(game.getJugador1().getBr(),game.getJugador1().getBw(),equiposGrupo,3);
				EsperarRespuesta er22=new EsperarRespuesta(game.getJugador2().getBr(),game.getJugador2().getBw(),equiposGrupo,3);
				er11.start();
				er22.start();
				er11.join();
				er22.join();
				
				actualizarPuntuaciones(game,er11.getRespuesta(),er22.getRespuesta(),
						er11.getTiempo(),er22.getTiempo(),equipoSegundo);
				//Esto mantenerlo aquí
				
				game.getJugador1().getBw().write("Puntuaciones: Jugador1: "+game.getJugador1().getPuntos()+  "Puntos. Jugador2: "+game.getJugador2().getPuntos()+" puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Puntuaciones: Jugador1: "+game.getJugador1().getPuntos()+" puntos. Jugador2: "+game.getJugador2().getPuntos()+" puntos. \r\n");
				game.getJugador2().getBw().flush();
				
				game.getFw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getFw().write("Respuesta Jugador1: "+er11.getRespuesta()+"\r\n");
				game.getFw().write("Respuesta Jugador2: "+er22.getRespuesta()+"\r\n");
				
//				game.getFw().write("Segundo según "+nombre1+" y "+nombre2+".\r\n");
//				game.getFw().write(er11.respuesta+" y "+er22.respuesta+".\r\n");
				
				segundos.add(equipoSegundo);
				
			}
			game.getFw().write("\r\n");
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void generarOctavos(List<String> primeros, List<String> segundos, List<String> octavos) {
		for(int i = 0; i< 8; i=i+2) {
			octavos.add(primeros.get(i));
			octavos.add(segundos.get(i+1));
		}
		for(int i = 0; i< 8; i=i+2) {
			octavos.add(segundos.get(i));
			octavos.add(primeros.get(i+1));
		}
	}
	
	public static void octavos(BufferedWriter bw1,BufferedReader br1,FileWriter fw, List<String> octavos,List<String> cuartos){
		try {
			fw.write("Octavos de final: \r\n");
			fw.write("\r\n");
			bw1.write("¡¡¡Comienzan los octavos!!! \r\n");

			String respuesta1;
			for(int i=0;i<octavos.size();i=i+2) {
				ArrayList<String> encuentro=new ArrayList<>();
				encuentro.add(octavos.get(i));
				encuentro.add(octavos.get(i+1));
				
				bw1.write("¿Cual fue el resultado de este partido? \r\n");
				bw1.write(octavos.get(i) +" vs " + octavos.get(i+1)  + " \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				respuesta1= resultadoValido(br1, bw1, encuentro, 2);
				cuartos.add(respuesta1);
				fw.write(octavos.get(i) +" vs " + octavos.get(i+1)  + " resultado: " +respuesta1 +" \r\n");	
			}
			fw.write("\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void cuartos(BufferedWriter bw1,BufferedReader br1,FileWriter fw, List<String> cuartos,List<String> semifinales){
		try {

			fw.write("Cuartos de final: \r\n");
			fw.write("\r\n");
			bw1.write("¡¡¡Comienzan los cuartos!!! \r\n");
			String respuesta1;
			for(int i=0;i<cuartos.size();i=i+2) {
				ArrayList<String> encuentro=new ArrayList<>();
				encuentro.add(cuartos.get(i));
				encuentro.add(cuartos.get(i+1));
				
				bw1.write("¿Cual fue el resultado de este partido? \r\n");
				bw1.write(cuartos.get(i) +" vs " + cuartos.get(i+1)  + " \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				respuesta1= resultadoValido(br1, bw1, encuentro, 2);
				semifinales.add(respuesta1);
				fw.write(cuartos.get(i) +" vs " + cuartos.get(i+1)  + " resultado: " +respuesta1 +" \r\n");

			}
			fw.write("\r\n");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	
	public static void semifinales(BufferedWriter bw1,BufferedReader br1,FileWriter fw, List<String> semifinales,List<String> finalistas){
		try {
			fw.write("Semifinales: \r\n");
			fw.write("\r\n");
			bw1.write("¡¡¡Comienzan las semifinales!!! \r\n");
			String respuesta1;
			for(int i=0;i<semifinales.size();i=i+2) {
				
				ArrayList<String> encuentro=new ArrayList<>();
				encuentro.add(semifinales.get(i));
				encuentro.add(semifinales.get(i+1));
				
				bw1.write("¿Cual fue el resultado de este partido? \r\n");
				bw1.write(semifinales.get(i) +" vs " + semifinales.get(i+1)  + " \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				respuesta1= resultadoValido(br1, bw1, encuentro, 2);
				finalistas.add(respuesta1);
				fw.write(semifinales.get(i) +" vs " + semifinales.get(i+1)  + " resultado: " +respuesta1 +" \r\n");	
			}
			fw.write("\r\n");

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void finales(BufferedWriter bw1,BufferedReader br1,FileWriter fw,List<String> finalistas){
		try {

			fw.write("Final: \r\n");
			fw.write("\r\n");
			bw1.write("¡¡¡Comienza la final!!! \r\n");
			String respuesta1="";
			for(int i=0;i<finalistas.size();i=i+2) {
				ArrayList<String> encuentro=new ArrayList<>();
				encuentro.add(finalistas.get(i));
				encuentro.add(finalistas.get(i+1));
				
				bw1.write("¿Cual fue el resultado de este partido? \r\n");
				bw1.write(finalistas.get(i) +" vs " + finalistas.get(i+1)  + " \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				respuesta1= resultadoValido(br1, bw1, encuentro, 2);
				fw.write(finalistas.get(i) +" vs " + finalistas.get(i+1)  + " resultado: " +respuesta1 +" \r\n");
	
			}
			bw1.write("¡¡¡Terminaste!!! El ganador es "+respuesta1+" \r\n");
			fw.write("\r\n");
			fw.write("Fin. El ganador es "+respuesta1+" \r\n");
			
			//Hacer recuento de puntos y ver quien ha ganado.
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void cerrar(FileWriter fw,BufferedWriter bw1,DataOutputStream dis1) {
		try {
			fw.close();
		
			bw1.write("END\r\n");
			bw1.flush();
			File fichero=new File("Desarrollo.txt");
			bw1.write(fichero.getName()+":"+fichero.length()+"\r\n");
			bw1.flush();
			try(FileInputStream fin=new FileInputStream(fichero)){
				byte[] buff=new byte[1024];
				int leidos=fin.read(buff);
				while(leidos!=-1) {
					dis1.write(buff, 0, leidos);
				}
			}
		} 	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
