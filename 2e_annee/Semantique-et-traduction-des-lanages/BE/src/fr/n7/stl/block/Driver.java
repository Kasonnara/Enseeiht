package fr.n7.stl.block;

public class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = new Parser(args[0]);
		parser.parse();
	}
	
}