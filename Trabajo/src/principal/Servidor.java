/*Autores: Miguel Muelas Tenorio e Iker Zubillaga Ruiz.
 * Título del trabajo: Juego resultados Mundial de Qatar 2022.
 */
package principal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Servidor básico al que se conectan dos clientes (jugadores).
public class Servidor {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		try (ServerSocket ss = new ServerSocket(8000)) {
			while (true) {
				Socket s1 = ss.accept();
				BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
				bw1.write(
						"Jugador conectado, bienvenido a la competición de recordar los resultados del Mundial de Qatar 2022, esperando a tu oponente... \r\n");
				bw1.flush();
				Socket s2 = ss.accept();
				BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(s2.getOutputStream()));
				bw2.write(
						"Jugador conectado, bienvenido a la competición de recordar los resultados del Mundial de Qatar 2022. \r\n");
				bw2.flush();
				pool.execute(new AtenderPeticion(s1, s2, bw1, bw2));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
