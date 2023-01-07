package principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Jugador {
	
	private int puntos;
	private BufferedWriter bw;
	private BufferedReader br;
	private FileWriter fw;
	
	public Jugador(BufferedWriter bw, BufferedReader br,FileWriter fw) {
		this.puntos=0;
		this.bw=bw;
		this.br=br;
		this.fw=fw;
	}
	
	public int getPuntos() {
		return puntos;
	}
	public void addPunto() {
		this.puntos = this.puntos+1;
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
	public FileWriter getFw() {
		return fw;
	}
	public void setFw(FileWriter fw) {
		this.fw = fw;
	}
	
}
