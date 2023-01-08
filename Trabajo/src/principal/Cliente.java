package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
	public static void main(String[] args) {
		try (Socket s = new Socket("localhost", 8000);
				BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				BufferedReader br1 = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));) {
			System.out.println(br1.readLine());
			String resultado = br1.readLine();
			while (!resultado.equals("END")) {
				System.out.println(resultado);
				resultado = br1.readLine();
				if (resultado.equals("ya")) {
					String respuesta1 = teclado.readLine();
					bw1.write(respuesta1 + "\r\n");
					bw1.flush();
					resultado = br1.readLine();
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
