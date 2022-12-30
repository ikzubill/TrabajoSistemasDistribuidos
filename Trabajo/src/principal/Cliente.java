package principal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
	public static void main(String[]args) {
		try(Socket s=new Socket("localhost",8000);
				BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				BufferedReader br1=new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedReader teclado=new BufferedReader(new InputStreamReader(System.in));){
				System.out.println(br1.readLine());
				System.out.println(br1.readLine());
				String pregunta=br1.readLine();
				while(!pregunta.equals("END")) {
					System.out.println(pregunta);
					pregunta=br1.readLine();
					if(pregunta.equals("ya")) {
						String respuesta=teclado.readLine();
						bw1.write(respuesta+"\r\n");
						bw1.flush();
						pregunta=br1.readLine();
					}
				}
				System.out.println("se acabo");
				//Falta que el cliente reciba el fichero y lo muestre
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
