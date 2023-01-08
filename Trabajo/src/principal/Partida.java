/*Autores: Miguel Muelas Tenorio e Iker Zubillaga Ruiz.
 * Título del trabajo: Juego resultados Mundial de Qatar 2022.
 */
package principal;

import java.io.FileWriter;

public class Partida {

	private Jugador jugador1;
	private Jugador jugador2;
	private int[] resultado;
	private FileWriter fw;

	public Partida(Jugador j1, Jugador j2, FileWriter fw) {
		this.jugador1 = j1;
		this.jugador2 = j2;
		this.resultado = new int[2];
		this.fw = fw;
	}

	public String ganador() {
		if (this.jugador1.getPuntos() > this.jugador2.getPuntos()) {
			return this.jugador1.getNombre();
		} else if (this.jugador1.getPuntos() < this.jugador2.getPuntos()) {
			return this.jugador2.getNombre();
		} else {
			return "empate";
		}
	}

	public int[] getResultado() {
		return resultado;
	}

	public String pintarResultado() {
		resultado[0]=jugador1.getPuntos();
		resultado[1]=jugador2.getPuntos();
		return resultado[0] + " - " + resultado[1];
	}

	public void setResultado(int[] resultado) {
		this.resultado = resultado;
	}

	public Jugador getJugador1() {
		return jugador1;
	}

	public void setJugador1(Jugador jugador1) {
		this.jugador1 = jugador1;
	}

	public Jugador getJugador2() {
		return jugador2;
	}

	public void setJugador2(Jugador jugador2) {
		this.jugador2 = jugador2;
	}

	public void finPartida() {
		this.resultado[0] = this.jugador1.getPuntos();
		this.resultado[1] = this.jugador2.getPuntos();
	}

	public FileWriter getFw() {
		return fw;
	}

	public void setFw(FileWriter fw) {
		this.fw = fw;
	}

}
