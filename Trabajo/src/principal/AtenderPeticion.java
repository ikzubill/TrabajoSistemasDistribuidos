/*Autores: Miguel Muelas Tenorio e Iker Zubillaga Ruiz.
 * Título del trabajo: Juego resultados Mundial de Qatar 2022.
 */
package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

//Clase en la que se implementa el desarrollo del juego.
public class AtenderPeticion implements Runnable {
	Socket socket1;
	Socket socket2;
	BufferedWriter bw1;
	BufferedWriter bw2;

	//Constructor con los atributos de la clase.
	public AtenderPeticion(Socket s1, Socket s2, BufferedWriter w1, BufferedWriter w2) {
		this.socket1 = s1;
		this.socket2 = s2;
		this.bw1 = w1;
		this.bw2 = w2;
	}

	@Override
	public void run() {

		try (BufferedReader br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
				BufferedReader br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));) {
			//Documento .txt donde se guarda el desarrollo del juego.
			FileWriter fw = new FileWriter("Desarrollo.txt");
			Jugador jugador1 = new Jugador(bw1, br1);
			Jugador jugador2 = new Jugador(bw2, br2);
			//Comenzamos la partida con dos jugadores.
			Partida game = new Partida(jugador1, jugador2, fw);

			List<String> primeros = new ArrayList<String>();
			List<String> segundos = new ArrayList<String>();
			List<String> octavos = new ArrayList<String>();
			List<String> cuartos = new ArrayList<String>();
			List<String> semifinales = new ArrayList<String>();
			List<String> finalistas = new ArrayList<String>();

			//Leemos del .xml los grupos con el sorteo realizado (marcará todos los enfrentamientos). En el tenemos los resultados también.
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(".\\src\\clasificacion.xml");
			Element root = doc.getDocumentElement();
			NodeList partidos = root.getElementsByTagName("partido");

			faseDeGrupos(game, primeros, segundos, octavos, root);

			generarOctavos(primeros, segundos, octavos);

			game.getFw().write("Octavos de final: \r\n");
			game.getFw().write("\r\n");
			bw1.write("\r\n");
			bw2.write("\r\n");
			bw1.write("¡¡¡Comienzan los octavos!!! \r\n");
			bw2.write("¡¡¡Comienzan los octavos!!! \r\n");

			enfrentamientos(game, octavos, cuartos, partidos, 0);

			fw.write("Cuartos de final: \r\n");
			fw.write("\r\n");
			bw1.write("\r\n");
			bw2.write("\r\n");
			bw1.write("¡¡¡Comienzan los cuartos!!! \r\n");
			bw2.write("¡¡¡Comienzan los cuartos!!! \r\n");

			enfrentamientos(game, cuartos, semifinales, partidos, 8);

			fw.write("Semifinales: \r\n");
			fw.write("\r\n");
			bw1.write("\r\n");
			bw2.write("\r\n");
			bw1.write("¡¡¡Comienzan las semifinales!!! \r\n");
			bw2.write("¡¡¡Comienzan las semifinales!!! \r\n");

			enfrentamientos(game, semifinales, finalistas, partidos, 12);

			finales(game, finalistas, partidos.item(14).getTextContent());

			game.getJugador1().getBw().write("END\r\n");
			game.getJugador1().getBw().flush();
			game.getJugador2().getBw().write("END\r\n");
			game.getJugador2().getBw().flush();

			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String letraGrupo(int i) {
		if (i == 0) {
			return "A";
		}
		if (i == 1) {
			return "B";
		}
		if (i == 2) {
			return "C";
		}
		if (i == 3) {
			return "D";
		}
		if (i == 4) {
			return "E";
		}
		if (i == 5) {
			return "F";
		}
		if (i == 6) {
			return "G";
		}
		if (i == 7) {
			return "H";
		}
		return "";
	}

	public static String actualizarPuntuaciones(Partida game, String s1, String s2, long t1, long t2,
			String respuesta) {
		boolean acertada = false;
		String actualizado = "";
		if (s1.equalsIgnoreCase(respuesta)) {
			acertada = true;
			game.getJugador1().addPunto();
			actualizado = game.getJugador1().getNombre() + " acertó antes que " + game.getJugador2().getNombre()
					+ ". \r\n \r\n";
		}
		if (s2.equalsIgnoreCase(respuesta)) {
			if (acertada) {
				if (t2 < t1) {
					game.getJugador1().lowPunto();
					game.getJugador2().addPunto();
					actualizado = game.getJugador2().getNombre() + " acertó antes que " + game.getJugador1().getNombre()
							+ ". \r\n \r\n";
				}
			} else {
				game.getJugador2().addPunto();
				actualizado = game.getJugador2().getNombre() + " acertó antes que " + game.getJugador1().getNombre()
						+ ". \r\n \r\n";
			}
		}
		if (actualizado.equalsIgnoreCase("")) {
			actualizado = "Ni " + game.getJugador2().getNombre() + " ni " + game.getJugador1().getNombre()
					+ " acertaron el resultado. \r\n \r\n";
		}
		return actualizado;
	}

	public static void faseDeGrupos(Partida game, List<String> primeros, List<String> segundos, List<String> octavos,
			Element root) {
		try {
			game.getJugador1().getBw().write("Empieza el juego, introduce tu nombre:\r\n");
			game.getJugador1().getBw().write("ya" + "\r\n");
			game.getJugador1().getBw().flush();

			game.getJugador2().getBw().write("Empieza el juego, introduce tu nombre:\r\n");
			game.getJugador2().getBw().write("ya" + "\r\n");
			game.getJugador2().getBw().flush();

			game.getJugador1().setNombre(game.getJugador1().getBr().readLine());
			game.getJugador2().setNombre(game.getJugador2().getBr().readLine());

			game.getFw().write("Predicciones de los jugadores: " + game.getJugador1().getNombre() + " y "
					+ game.getJugador2().getNombre() + "\r\n");
			game.getFw().write("\r\n");
			game.getFw().write("Fase de grupos:  \r\n");
			game.getFw().write("\r\n");

			NodeList grupos = root.getElementsByTagName("grupo");
			NodeList equipos = root.getElementsByTagName("equipo");

			String grupo;
			int k = 0;
			for (int i = 0; i < grupos.getLength(); i = i + 1) {
				grupo = letraGrupo(i);
				game.getJugador1().getBw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n");
				game.getJugador2().getBw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n");

				k = i * 4;
				ArrayList<String> equiposGrupo = new ArrayList<String>();
				String equipo, equipoPrimero = "", equipoSegundo = "";
				for (int j = 0; j < 4; j++) {
					if (equipos.item(k).hasAttributes()) {
						NamedNodeMap attr = equipos.item(k).getAttributes();
						Node nodeAttr2 = attr.getNamedItem("id");
						if (nodeAttr2.getNodeValue().equals("primero")) {
							equipoPrimero = equipos.item(k).getTextContent();
						} else if (nodeAttr2.getNodeValue().equals("segundo")) {
							equipoSegundo = equipos.item(k).getTextContent();
						}
					}
					equipo = equipos.item(k).getTextContent();
					equiposGrupo.add(equipo);
					game.getJugador1().getBw().write(equipo + "\r\n");
					game.getJugador2().getBw().write(equipo + "\r\n");
					k++;
				}
				game.getJugador1().getBw().write("ya" + "\r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("ya" + "\r\n");
				game.getJugador2().getBw().flush();

				game.getFw().write("\r\n");
				game.getFw().write("Grupo " + grupo + ": \r\n");

				EsperarRespuesta er1 = new EsperarRespuesta(game.getJugador1().getBr(), game.getJugador1().getBw(),
						equiposGrupo, 4);
				EsperarRespuesta er2 = new EsperarRespuesta(game.getJugador2().getBr(), game.getJugador2().getBw(),
						equiposGrupo, 4);
				er1.start();
				er2.start();
				er1.join();
				er2.join();

				String ganador = actualizarPuntuaciones(game, er1.getRespuesta(), er2.getRespuesta(), er1.getTiempo(),
						er2.getTiempo(), equipoPrimero);

				game.getJugador1().getBw().write("Respuesta correcta: " + equipoPrimero + ". \r\n");
				game.getJugador1().getBw().write(ganador);
				game.getJugador1().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Respuesta correcta: " + equipoPrimero + ". \r\n");
				game.getJugador2().getBw().write(ganador);
				game.getJugador2().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador2().getBw().flush();

				game.getFw().write("¿Qué equipo obtuvo la primera plaza del grupo " + grupo + "? \r\n");
				game.getFw()
						.write("Respuesta de " + game.getJugador1().getNombre() + ": " + er1.getRespuesta() + "\r\n");
				game.getFw()
						.write("Respuesta de " + game.getJugador2().getNombre() + ": " + er2.getRespuesta() + "\r\n");
				game.getFw().write(ganador);
				game.getFw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");

				//Así no permitimos que se introduzca como respuesta el que ya realmente quedó primero
				equiposGrupo.remove(equipoPrimero);
				primeros.add(equipoPrimero);

				game.getJugador1().getBw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getJugador1().getBw().write("ya" + "\r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getJugador2().getBw().write("ya" + "\r\n");
				game.getJugador2().getBw().flush();

				EsperarRespuesta er11 = new EsperarRespuesta(game.getJugador1().getBr(), game.getJugador1().getBw(),
						equiposGrupo, 3);
				EsperarRespuesta er22 = new EsperarRespuesta(game.getJugador2().getBr(), game.getJugador2().getBw(),
						equiposGrupo, 3);
				er11.start();
				er22.start();
				er11.join();
				er22.join();

				ganador = actualizarPuntuaciones(game, er11.getRespuesta(), er22.getRespuesta(), er11.getTiempo(),
						er22.getTiempo(), equipoSegundo);

				game.getJugador1().getBw().write("Respuesta correcta: " + equipoSegundo + ". \r\n");
				game.getJugador1().getBw().write(ganador);
				game.getJugador1().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Respuesta correcta: " + equipoSegundo + ". \r\n");
				game.getJugador2().getBw().write(ganador);
				game.getJugador2().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador2().getBw().flush();

				game.getFw().write("¿Qué equipo obtuvo la segunda plaza del grupo " + grupo + "? \r\n");
				game.getFw().write("Respuesta " + game.getJugador1().getNombre() + ": " + er11.getRespuesta() + "\r\n");
				game.getFw().write("Respuesta " + game.getJugador2().getNombre() + ": " + er22.getRespuesta() + "\r\n");
				game.getFw().write(ganador);
				game.getFw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");

				segundos.add(equipoSegundo);

			}
			game.getFw().write("\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void generarOctavos(List<String> primeros, List<String> segundos, List<String> octavos) {
		for (int i = 0; i < 8; i = i + 2) {
			octavos.add(primeros.get(i));
			octavos.add(segundos.get(i + 1));
		}
		for (int i = 0; i < 8; i = i + 2) {
			octavos.add(segundos.get(i));
			octavos.add(primeros.get(i + 1));
		}
	}

	public static void enfrentamientos(Partida game, List<String> actual, List<String> siguiente, NodeList partidos,
			int n) {
		try {
			for (int i = 0; i < actual.size(); i = i + 2) {
				ArrayList<String> encuentro = new ArrayList<>();
				encuentro.add(actual.get(i));
				encuentro.add(actual.get(i + 1));

				game.getJugador1().getBw().write("¿Cual fue el resultado exacto de este partido? \r\n");
				game.getJugador1().getBw().write(actual.get(i) + " vs " + actual.get(i + 1) + " \r\n");
				game.getJugador1().getBw().write("ya" + "\r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("¿Cual fue el resultado exacto de este partido? \r\n");
				game.getJugador2().getBw().write(actual.get(i) + " vs " + actual.get(i + 1) + " \r\n");
				game.getJugador2().getBw().write("ya" + "\r\n");
				game.getJugador2().getBw().flush();

				EsperarRespuesta er1 = new EsperarRespuesta(game.getJugador1().getBr(), game.getJugador1().getBw(),
						encuentro, 2);
				EsperarRespuesta er2 = new EsperarRespuesta(game.getJugador2().getBr(), game.getJugador2().getBw(),
						encuentro, 2);
				er1.start();
				er2.start();
				er1.join();
				er2.join();

				String ganadorPartido = partidos.item(n).getAttributes().getNamedItem("id").getNodeValue();
				String resultadoPartido = partidos.item(n).getTextContent();

				siguiente.add(ganadorPartido);

				String ganador = actualizarPuntuaciones(game, er1.getRespuesta(), er2.getRespuesta(), er1.getTiempo(),
						er2.getTiempo(), resultadoPartido);

				game.getJugador1().getBw().write("Respuesta correcta: " + resultadoPartido + ". \r\n");
				game.getJugador1().getBw().write(ganador);
				game.getJugador1().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Respuesta correcta: " + resultadoPartido + ". \r\n");
				game.getJugador2().getBw().write(ganador);
				game.getJugador2().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador2().getBw().flush();

				game.getFw().write(" \r\n");
				game.getFw().write("¿Cual fue el resultado exacto de este partido?  " + actual.get(i) + " vs "
						+ actual.get(i + 1) + " \r\n");
				game.getFw().write("Respuesta " + game.getJugador1().getNombre() + ": " + er1.getRespuesta() + "\r\n");
				game.getFw().write("Respuesta " + game.getJugador2().getNombre() + ": " + er2.getRespuesta() + "\r\n");
				game.getFw().write(ganador);
				game.getFw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ " puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				n++;

			}
			game.getFw().write("\r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void finales(Partida game, List<String> finalistas, String resultadoFinal) {
		try {

			game.getFw().write("Final: \r\n");
			game.getFw().write("\r\n");
			game.getJugador1().getBw().write("¡¡¡Comienza la final!!! \r\n");
			game.getJugador2().getBw().write("¡¡¡Comienza la final!!! \r\n");

			for (int i = 0; i < finalistas.size(); i = i + 2) {
				ArrayList<String> encuentro = new ArrayList<>();
				encuentro.add(finalistas.get(i));
				encuentro.add(finalistas.get(i + 1));

				game.getJugador1().getBw().write("¿Cual fue el resultado exacto de este partido? \r\n");
				game.getJugador1().getBw().write(finalistas.get(i) + " vs " + finalistas.get(i + 1) + " \r\n");
				game.getJugador1().getBw().write("ya" + "\r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("¿Cual fue el resultado exacto de este partido? \r\n");
				game.getJugador2().getBw().write(finalistas.get(i) + " vs " + finalistas.get(i + 1) + " \r\n");
				game.getJugador2().getBw().write("ya" + "\r\n");
				game.getJugador2().getBw().flush();

				EsperarRespuesta er1 = new EsperarRespuesta(game.getJugador1().getBr(), game.getJugador1().getBw(),
						encuentro, 2);
				EsperarRespuesta er2 = new EsperarRespuesta(game.getJugador2().getBr(), game.getJugador2().getBw(),
						encuentro, 2);
				er1.start();
				er2.start();
				er1.join();
				er2.join();

				String ganador = actualizarPuntuaciones(game, er1.getRespuesta(), er2.getRespuesta(), er1.getTiempo(),
						er2.getTiempo(), resultadoFinal);

				game.getJugador1().getBw().write("Respuesta correcta: " + resultadoFinal + ". \r\n");
				game.getJugador1().getBw().write(ganador);
				game.getJugador1().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ "puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador1().getBw().flush();

				game.getJugador2().getBw().write("Respuesta correcta: " + resultadoFinal + ". \r\n");
				game.getJugador2().getBw().write(ganador);
				game.getJugador2().getBw()
						.write("Puntuaciones: " + game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos()
								+ "puntos. " + game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos()
								+ " puntos. \r\n");
				game.getJugador2().getBw().flush();

				game.getFw().write("¿¿Cual fue el resultado exacto de este partido?  " + finalistas.get(i) + " vs "
						+ finalistas.get(i + 1) + " \r\n");
				game.getFw().write("Respuesta " + game.getJugador1().getNombre() + ": " + er1.getRespuesta() + "\r\n");
				game.getFw().write("Respuesta " + game.getJugador2().getNombre() + ": " + er2.getRespuesta() + "\r\n");
				game.getFw().write(ganador);

				game.getJugador1().getBw().write("¡¡¡Terminaste!!! El ganador es: " + game.ganador()
						+ ". El resultado ha sido: " + game.pintarResultado() + " \r\n");
				game.getJugador2().getBw().write("¡¡¡Terminaste!!! El ganador es: " + game.ganador()
						+ ". El resultado ha sido: " + game.pintarResultado() + " \r\n");

			}

			game.getFw().write("\r\n");
			game.getFw().write("Fin. El ganador es: " + game.ganador() + ". El resultado ha sido: "
					+ game.pintarResultado() + " \r\n");
			game.getFw().write("Tabla puntuaciones:  \r\n");
			game.getFw()
					.write(game.getJugador1().getNombre() + ": " + game.getJugador1().getPuntos() + " puntos. \r\n");
			game.getFw()
					.write(game.getJugador2().getNombre() + ": " + game.getJugador2().getPuntos() + " puntos. \r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
