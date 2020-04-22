package PPRodVic;

// código baseado em um curso da udemy

import java.io.Serializable;

public class Servidor implements Serializable{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int x, y;
	private int bolinhax, bolinhay;
	private int Pontos1 = 0;
	private int Pontos2 = 0;
	private String imessage="";
	private String omessage="";
	private boolean restart = false;
	public boolean isRestart() {
		return restart;
	}

	public void setRestart(final boolean restart) {
		this.restart = restart;
	}

	public Servidor() {
		x = 50;
		y = 200;
		bolinhax = 380;
		bolinhay = 230;
	}

	public String getNome() {
		return name;
	}

	public String getImessage() {
		return imessage;
	}

	public void setImessage(final String imessage) {
		this.imessage = imessage;
	}

	public String getOmessage() {
		return omessage;
	}

	public void setOmessage(final String omessage) {
		this.omessage = omessage;
	}

	public void setNome(final String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public int getBallx() {
		return bolinhax;
	}

	public void setBallx(final int ballx) {
		this.bolinhax = ballx;
	}

	public int getBally() {
		return bolinhay;
	}

	public void setBally(final int bally) {
		this.ball_y = bally;
	}

	public int getPontos1() {
		return Pontos1;
	}

	public void setPontos1(final int Pontos1) {
		this.Pontos1 = Pontos1;
	}

	public int getPontos2() {
		return Pontos2;
	}

	public void setPontos2(final int Pontos2) {
		this.Pontos2 = Pontos2;
	}
}
