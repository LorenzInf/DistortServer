package io.github.lorenzinf;

public class Main {
	public static void main (String[] args) {
		ProgramController pc = new ProgramController();
		new DistortServer(25565, pc);
	}
}
