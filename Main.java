import cappuccino.Tokenizer.CappuccinoTokenizer;
import cappuccino.Tree.CCTCompilationUnit;
import cappuccino.Tree.CCTreeAbstract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		/*StringBuilder sb = new StringBuilder();

		for (int i = 40_001; i <= 50_000; i++) {
			sb.append("let a").append(i).append(": string = s\"147\";\n");
		}
>=
		System.out.println(sb);*/
		Runtime runtime = Runtime.getRuntime();

		System.gc();

		long memoriaLibre = runtime.freeMemory();
		long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

		long start = System.nanoTime();
		double count = 0;

		CappuccinoTokenizer tokenizer = new CappuccinoTokenizer(Files.readString(Paths.get("test.capp")));

		/*while (tokenizer.getCurrentToken().type != Token.SyntaxType.BadToken) {
			tokenizer.setNextToken();
			count++;
		}*/

		CCTreeAbstract.tokenizer = tokenizer;

		CCTCompilationUnit compilationUnit = new CCTCompilationUnit();
		compilationUnit.parser();
		compilationUnit.visitor();

		long end = System.nanoTime();

		// Segunda recolección para ver cuánta memoria se quedó
		System.gc();
		long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();


		/*System.out.printf("Tiempo: %.3fms%n", (end - start) / 1000.0 / 1000.0);
		System.out.printf("Tamaño del archivo: %.2fMB%n", (tokenizer.target.length / 1024.0 / 1024.0));
		System.out.printf("Tokens en total: %.2f Mtok%n", count / 1_000_000);
		System.out.printf("Tokens por segundo: %.3f Mtok/s%n", (count / ((end - start) / 1_000_000_000.0)) / 1_000_000);*/

		System.out.printf("Tiempo: %.3fms%n", (end - start) / 1000.0 / 1000.0);
		System.out.printf("Tamaño del archivo: %.2fMB%n", (tokenizer.target.length / 1024.0 / 1024.0));

		// Información de memoria
		System.out.printf("Memoria libre: %.2fMB%n", ((memoriaLibre) / 1024.0 / 1024.0));
		System.out.printf("Memoria usada antes: %.2fMB%n", (memoriaAntes / 1024.0 / 1024.0));
		System.out.printf("Memoria usada después: %.2fMB%n", (memoriaDespues / 1024.0 / 1024.0));
		System.out.printf("Memoria consumida por tokenización: %.2fMB%n", ((memoriaDespues - memoriaAntes) / 1024.0 / 1024.0));
		System.out.printf("Tokenizacion, Verificacion de nombre y evaluacion/recorrido del AST");
	}
}

/*
System.out.println("Tokens: " + count);
System.out.printf("Velocidad por tokenizacion en segundos de lectura: %.2f MB/s%n", (tokenizer.target.length / 1024.0 / 1024.0) / ((end - start) / 1_000_000_000.0));
System.out.printf("Velocidad por tokenizacion en segundo de crear tokens: %.0f t/s%n", count / ((end - start) / 1_000_000_000.0));
 */