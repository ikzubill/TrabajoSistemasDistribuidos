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
			bw1.write("Empieza el juego, eres el Jugador1\r\n");
			bw1.flush();
			NodeList grupos=root.getElementsByTagName("grupo");
			NodeList equipos=root.getElementsByTagName("equipo"); //esto no deberia estar
			int i=0;
			FileWriter fw=new FileWriter("Desarrollo.txt");
			while(i<grupos.getLength()) {
				
				bw1.write("Grupo " + i + "\r\n");
				
				//equipos = grupos.item(i).getchildNodes() me iba mal 
				for(int j=0; j<4;j++) {
					bw1.write(equipos.item(j).getTextContent() +  "\r\n"); //recorre getchildnodes
				}
				bw1.write("ya" +  "\r\n");
				bw1.flush();
				EsperarRespuesta er1=new EsperarRespuesta(br1);
				er1.start();
				er1.join();
//				int[]resultados=actualizarPuntuaciones(er1.getRespuesta(),er1.getTiempo(),respuesta);

				fw.write("Grupo " + i + "\r\n");
				fw.write("Primero: "+ er1.getRespuesta()+"\r\n");
				
				bw1.write("Primero: "+er1.getRespuesta()+"\r\n");
				
				bw1.flush();
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static int[] actualizarPuntuaciones(String s1,long t1,String respuesta) {
		int[]resultados= {0,0,0};

		return resultados;
	}
}
