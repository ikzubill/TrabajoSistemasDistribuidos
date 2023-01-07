package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Jugador {
	
	private int puntos;
	private BufferedWriter bw;
	private BufferedReader br;
	private String nombre;
	
	public Jugador(BufferedWriter bw, BufferedReader br) {
		this.puntos=0;
		this.bw=bw;
		this.br=br;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getPuntos() {
		return puntos;
	}
	public void addPunto() {
		this.puntos = this.puntos+1;
	}
	public void lowPunto() {
		this.puntos = this.puntos-1;
	}
	public void addPuntos(int puntos) {
		this.puntos = this.puntos  +puntos;
	}
	public BufferedWriter getBw() {
		return bw;
	}
	public void setBw(BufferedWriter bw) {
		this.bw = bw;
	}
	public BufferedReader getBr() {
		return br;
	}
	public void setBr(BufferedReader br) {
		this.br = br;
	}

	
}
