package principal;
import java.io.BufferedReader;
import java.io.IOException;

public class EsperarRespuesta extends Thread {
	public long tiempo;
	public String respuesta;
	public BufferedReader br;
	public EsperarRespuesta(BufferedReader r) {
		tiempo=0;
		respuesta="";
		this.br=r;
	}
	public void run() {
		try {
			long t=System.currentTimeMillis();
			respuesta=br.readLine();
			this.tiempo=System.currentTimeMillis()-t;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public long getTiempo() {
		return this.tiempo;
	}
	public String getRespuesta() {
		return this.respuesta;
	}
}
