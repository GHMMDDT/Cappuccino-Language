import cappuccino.Tokenizer.CappuccinoTokenizer;
import cappuccino.Tree.CCTCompilationUnit;
import cappuccino.Tree.CCTreeAbstract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		Runtime runtime = Runtime.getRuntime();

		System.gc();

		long memoriaLibre = runtime.freeMemory();
		long memoriaAntes = runtime.totalMemory() - runtime.freeMemory();

		long start = System.nanoTime();

		CappuccinoTokenizer tokenizer = new CappuccinoTokenizer(Files.readString(Paths.get("test.capp")));

		CCTreeAbstract.tokenizer = tokenizer;

		CCTCompilationUnit compilationUnit = new CCTCompilationUnit();
		compilationUnit.parser();
		compilationUnit.visitor();

		long end = System.nanoTime();

		System.gc();
		long memoriaDespues = runtime.totalMemory() - runtime.freeMemory();

		boolean a = true ^ (false ^ false);

		System.out.println();
		System.out.printf("Tiempo: %.3fms%n", (end - start) / 1000.0 / 1000.0);
		System.out.printf("Tamaño del archivo: %.2fMB%n", (tokenizer.target.length / 1024.0 / 1024.0));

		// Información de memoria
		System.out.printf("Memoria libre: %.2fMB%n", ((memoriaLibre) / 1024.0 / 1024.0));
		System.out.printf("Memoria usada antes: %.2fMB%n", (memoriaAntes / 1024.0 / 1024.0));
		System.out.printf("Memoria usada después: %.2fMB%n", (memoriaDespues / 1024.0 / 1024.0));
		System.out.printf("Memoria consumida por tokenización: %.2fMB%n", ((memoriaDespues - memoriaAntes) / 1024.0 / 1024.0));
		System.out.println("Tokenizacion, Verificacion de nombre y evaluacion/recorrido del AST");

		/*{
			String a = "Hello, World";
			{
				String b = "hi, planet";
			}
			int b = 157;
			int d = 159;
			{
				String d = "Bon-jour!";
			}
		}*/
	}
}

/*
System.out.println("Tokens: " + count);
System.out.printf("Velocidad por tokenizacion en segundos de lectura: %.2f MB/s%n", (tokenizer.target.length / 1024.0 / 1024.0) / ((end - start) / 1_000_000_000.0));
System.out.printf("Velocidad por tokenizacion en segundo de crear tokens: %.0f t/s%n", count / ((end - start) / 1_000_000_000.0));
 */