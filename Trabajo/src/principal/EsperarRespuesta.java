package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EsperarRespuesta extends Thread {
	public long tiempo;
	public String respuesta;
	public BufferedReader br;
	public BufferedWriter bw;
	public ArrayList<String> equiposGrupo;
	public int n;

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

	public long getTiempo() {
		return this.tiempo;
	}

	public String getRespuesta() {
		return this.respuesta;
	}

	public String resultadoValido(BufferedReader br, BufferedWriter bw, ArrayList<String> equiposGrupo, int m)
			throws IOException, ArrayIndexOutOfBoundsException {
		String respuesta = "", respuesta2 = "";
		String equipo = "";
		boolean igual = false;
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
				if (!igual) {
					bw.write("Vuelve a escribirlo correctamente: \r\n");
					bw.write("ya" + "\r\n");
					bw.flush();
				}
			}
		} else if (n == 2) {
			while (!igual) {
				respuesta = br.readLine();
				String[] resultado = respuesta.split("-");
				int uno = Integer.parseInt(resultado[0]);
				int dos = Integer.parseInt(resultado[1]);

				if (!resultado[0].matches("-?\\d+") || !resultado[1].matches("-?\\d+")) {
					bw.write("Vuelve a escribirlo correctamente en el formato (x-y): \r\n");
					bw.write("ya" + "\r\n");
					bw.flush();
				} else {
					igual = true;
					while ((uno - dos) == 0) {
						bw.write("¿Cuál fue el resultado de los penaltis? \r\n");
						bw.write("ya" + "\r\n");
						bw.flush();
						respuesta2 = br.readLine();
						String[] resultado2 = respuesta2.split("-");
						if (!resultado2[0].matches("-?\\d+") || !resultado2[1].matches("-?\\d+")) {
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
