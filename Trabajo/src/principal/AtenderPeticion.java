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
			bw1.write("Empieza el juego, introduce tu nombre:\r\n"); //falta meter algo pa k lo coja bien
			bw1.write("ya" +  "\r\n");
			bw1.flush();
			String nombre;
			nombre=br1.readLine();
			FileWriter fw=new FileWriter("Desarrollo.txt");
			fw.write("Predicciones del jugador:  " + nombre + "\r\n");
			NodeList grupos=root.getElementsByTagName("grupo");
			NodeList equipos=root.getElementsByTagName("equipo");
			int i=0;
			String respuesta1,respuesta2;
			int k=0;
			String grupo;
			while(i<grupos.getLength()) {
				
				grupo = letraGrupo(i);
				bw1.write("¿Qué equipo obtendrá la primera plaza del grupo " + grupo + "? \r\n"); 
				k=i*4;
				for(int j=0; j<4;j++) {
					bw1.write(equipos.item(k).getTextContent() +  "\r\n"); //Deberiamos comprobar k lo k se mete es igual al equipo
					k=k+1;
				}
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				
				fw.write("Grupo " + grupo + ": \r\n");
				respuesta1=br1.readLine();
				fw.write("Primero: "+ respuesta1+"\r\n");
				bw1.write("¿Qué equipo obtendrá la segunda plaza del grupo " + grupo + "? \r\n");
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				respuesta2=br1.readLine();
				fw.write("Segundo: "+ respuesta2+"\r\n");

				i++;
			}
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
	
	public static int generarCuadro() {
		//Hacer una función que lea del desarrollo.txt y escriba en el xml

		return 0;
	}
}
