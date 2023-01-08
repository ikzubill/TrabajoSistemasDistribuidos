/*Autores: Miguel Muelas Tenorio e Iker Zubillaga Ruiz.
 * Título del trabajo: Juego resultados Mundial de Qatar 2022.
 */
package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

//Clase que implementa Thread para recoger las respuestas de los jugadores al mismo tiempo.
public class EsperarRespuesta extends Thread {
	public long tiempo;
	public String respuesta;
	public BufferedReader br;
	public BufferedWriter bw;
	public ArrayList<String> equiposGrupo;
	public int n;

	//Constructor con los atributos de la clase.
	public EsperarRespuesta(BufferedReader r, BufferedWriter bw, ArrayList<String> equiposGrupo, int n) {
		tiempo = 0;
		respuesta = "";
		this.br = r;
		this.bw = bw;
		this.equiposGrupo = equiposGrupo;
		this.n = n;
	}

	public void run() {
		try {
			long t = System.currentTimeMillis();
			respuesta = resultadoValido(br, bw, equiposGrupo, n);
			this.tiempo = System.currentTimeMillis() - t;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Devuelve el tiempo tardado en responder.
	public long getTiempo() {
		return this.tiempo;
	}

	//Devuelve la respuesta.
	public String getRespuesta() {
		return this.respuesta;
	}

	//Devuelve la respuesta, pero tiene en cuenta el tratamiento de los datos introducidos.
	public String resultadoValido(BufferedReader br, BufferedWriter bw, ArrayList<String> equiposGrupo, int m)
			throws IOException, ArrayIndexOutOfBoundsException {
		String respuesta = "", respuesta2 = "";
		String equipo = "";
		boolean igual = false;
		
		//Creado para la fase de grupos.
		if (n > 2) {
			while (!igual) {
				respuesta = br.readLine();
				for (int n = 0; n < m; n++) {
					equipo = equiposGrupo.get(n);
					if (respuesta.equalsIgnoreCase(equipo)) {
						igual = true;
						n = 4;
					}
				}
				//Así conseguimos evitar excepciones indeseadas y conseguimos que se introduzcan resultados razonables.
				if (!igual) {
					bw.write("Vuelve a escribirlo correctamente: \r\n");
					bw.write("ya" + "\r\n");
					bw.flush();
				}
			}
		}
		//Creado para las eliminatorias.
		else if (n == 2) {
			while (!igual) {
				respuesta = br.readLine();
				String[] resultado = respuesta.split("-");
				int uno = Integer.parseInt(resultado[0]);
				int dos = Integer.parseInt(resultado[1]);
				//Así conseguimos evitar excepciones indeseadas y conseguimos que se introduzcan resultados razonables.
				if (!respuesta.matches("[0-9]*-[0-9]*")) {
					bw.write("Vuelve a escribirlo correctamente en el formato (x-y): \r\n");
					bw.write("ya" + "\r\n");
					bw.flush();
				} else {
					igual = true;
					//Tenemos en cuenta que para el resultado correcto hay que introducir el resultado de los penaltis.
					while ((uno - dos) == 0) {
						bw.write("¿Cuál fue el resultado de los penaltis? \r\n");
						bw.write("ya" + "\r\n");
						bw.flush();
						respuesta2 = br.readLine();
						if (!respuesta2.matches("[0-9]*-[0-9]*")) {
							bw.write("Vuelve a escribirlo correctamente en el formato (x-y): \r\n");
							bw.write("ya" + "\r\n");
							bw.flush();
						} else {
							uno++;
							equipo = respuesta + "(" + respuesta2 + ")";
						}
					}
					if (equipo.equalsIgnoreCase("")) {
						equipo = respuesta;
					}
				}
			}
		}
		return equipo;
	}
}
