/*Autores: Miguel Muelas Tenorio e Iker Zubillaga Ruiz.
 * Título del trabajo: Juego resultados Mundial de Qatar 2022.
 */
package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;

//Clase que implementa nuestro objeto Judor, es el que desarrolla el juego.
public class Jugador {
	
	private int puntos;
	private BufferedWriter bw;
	private BufferedReader br;
	private String nombre;
	
	//Constructor con los atributos de la clase.
	public Jugador(BufferedWriter bw, BufferedReader br) {
		this.puntos=0;
		this.bw=bw;
		this.br=br;
	}

	//Devuelve el nombre del jugador.
	public String getNombre() {
		return nombre;
	}

	//Cambia el nombre del jugador.
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	//Devuelve los puntos del jugador.
	public int getPuntos() {
		return puntos;
	}
	
	//Suma un punto al jugador.
	public void addPunto() {
		this.puntos = this.puntos+1;
	}
	
	//Resta un punto al jugador.
	public void lowPunto() {
		this.puntos = this.puntos-1;
	}
	
	//Suma puntos al jugador.
	public void addPuntos(int puntos) {
		this.puntos = this.puntos  +puntos;
	}
	
	//Devuelve el BufferedWriter con el que se le escribe al cliente del jugador.
	public BufferedWriter getBw() {
		return bw;
	}
	
	//Cambia el BufferedWriter con el que se le escribe al cliente del jugador.
	public void setBw(BufferedWriter bw) {
		this.bw = bw;
	}
	
	//Devuelve el BufferedReader con el que se lee del cliente del jugador.
	public BufferedReader getBr() {
		return br;
	}
	
	//Cambia el BufferedReader con el que se lee del cliente del jugador.
	public void setBr(BufferedReader br) {
		this.br = br;
	}

	
}
