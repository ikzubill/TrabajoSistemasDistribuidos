package principal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	BufferedWriter bw1;
	public AtenderPeticion(Socket s1, BufferedWriter w1) {
		this.socket1=s1;
		this.bw1=w1;
	}
	@Override
	public void run() {
		
		try(BufferedReader br1=new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			DataOutputStream dis1=new DataOutputStream(socket1.getOutputStream());) {
			//Se que con el dataOutputStream no hacia falta el bufferedwriter pero no me habia dado cuenta que habia que hacer uso de ello hasta el final del examen
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newDefaultInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.parse(".\\src\\clasificacion.xml");
			
			Element root=doc.getDocumentElement();
			
			NodeList grupos=root.getElementsByTagName("grupo");
			NodeList equipos=root.getElementsByTagName("equipo");
			NodeList partidos=root.getElementsByTagName("partido");
//			NodeList octavos=root.getElementsByTagName("octavos");
//			NodeList cuartos=root.getElementsByTagName("cuartos");
//			NodeList semifinales=root.getElementsByTagName("semifinales");
//			NodeList finalistas=root.getElementsByTagName("final");
			List<String> octavos= new ArrayList<String>();
			List<String> primeros= new ArrayList<String>();
			List<String> segundos= new ArrayList<String>();
			bw1.write("Empieza el juego, introduce tu nombre:\r\n"); //falta meter algo pa k lo coja bien
			bw1.write("ya" +  "\r\n");
			bw1.flush();
			String nombre;
			nombre=br1.readLine();
			FileWriter fw=new FileWriter("Desarrollo.txt");
			fw.write("Predicciones del jugador:  " + nombre + "\r\n");

			int i=0;
			String respuesta1="",respuesta2="";
			int k=0,p=0;
			String grupo;
//			Node patido= octavos.item(0);
			while(i<grupos.getLength()) {
				
				grupo = letraGrupo(i);

				bw1.write("¿Qué equipo obtendrá la primera plaza del grupo " + grupo + "? \r\n"); 
				k=i*4;
				ArrayList<String> equiposGrupo = new ArrayList<String>();
				String equipo;
				for(int j=0; j<4;j++) {
					equipo=equipos.item(k).getTextContent();
					equiposGrupo.add(equipo);
					bw1.write(equipo +  "\r\n"); //Deberiamos comprobar k lo k se mete es igual al equipo
					k++;
				}
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				fw.write("Grupo " + grupo + ": \r\n");
				respuesta1=resultadoValido(br1, bw1, equiposGrupo, 4);
				equiposGrupo.remove(respuesta1);
				fw.write("Primero: "+ respuesta1+"\r\n");
				primeros.add(respuesta1);
//				fw.write("Octavos prueba: " + patido.getNextSibling().getNodeName()+"\r\n" );
//				patido.getNextSibling().setTextContent(respuesta1);;
//				partidos.item(p).setTextContent(respuesta1); //falta en rival
				
				bw1.write("¿Qué equipo obtendrá la segunda plaza del grupo " + grupo + "? \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				respuesta2 = resultadoValido(br1, bw1, equiposGrupo, 3);
				fw.write("Segundo: "+ respuesta2+"\r\n");
				segundos.add(respuesta2);
				i++;
				p++;
			}
			generarOctavos(primeros, segundos, octavos);	
			
			
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
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
	public static void generarCuartos(List<String> primeros, List<String> segundos, List<String> octavos) {
		for(int i = 0; i< 8; i=i+2) {
			octavos.add(primeros.get(i));
			octavos.add(segundos.get(i+1));
		}
		for(int i = 0; i< 8; i=i+2) {
			octavos.add(segundos.get(i));
			octavos.add(primeros.get(i+1));
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
	public static int generarCuadro() {
		//Hacer una función que lea del desarrollo.txt y escriba en el xml

		return 0;
	}
}
