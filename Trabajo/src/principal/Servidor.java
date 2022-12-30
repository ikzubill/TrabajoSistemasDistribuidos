package principal;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
	public static void main(String[]args) {
		ExecutorService pool=Executors.newCachedThreadPool();
		try(ServerSocket ss=new ServerSocket(8000)){
			while(true) {
				Socket s1=ss.accept();
				BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
				bw1.write("Jugador conectado, espere a que se conecten todos los jugadores\r\n");
				bw1.flush();
				pool.execute(new AtenderPeticion(s1,bw1));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
