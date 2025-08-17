package cappuccino.Tree;

import cappuccino.Tokenizer.CappuccinoTokenizer;
import cappuccino.Tokenizer.CharSlice;
import cappuccino.Tokenizer.Token;

import static cappuccino.Tokenizer.Token.SyntaxType.*;

public abstract class CCTreeAbstract {
	public static final Token BADTOKEN = new Token(new CharSlice(new char[] { '\0' }), BadToken);

	public static final int CURRENT = 1 << 1;

	public static final int CONSUME = 1 << 3;
	public static final int SEEK = 1 << 4;

	public enum Kind {
		CompilationUnit,
		Variable, SubVariable
	}

	public static CappuccinoTokenizer tokenizer;
	public CCTreeAbstract parent;
	public int line;

	public abstract void parser();
	public abstract void visitor();

	public abstract Kind getKind();

	public Token validator(int flag, Token.SyntaxType... types) {
		Token current = null;
		boolean isCurrent = (flag & CURRENT) != 0;
		boolean isPeek = (flag & SEEK) != 0;
		boolean isConsume = (flag & CONSUME) != 0;

		if (types == null || types.length == 0) {
			if (isCurrent) {
				current = tokenizer.getCurrentToken();
				if (current == null) {
					tokenizer.setNextToken();
					current = tokenizer.getCurrentToken();
				}
			}

			if (isConsume) {
				tokenizer.setNextToken();
				return current;
			}

			return current;
		}

		if (isCurrent) {
			current = tokenizer.getCurrentToken();
			if (current == null) {
				tokenizer.setNextToken();
				current = tokenizer.getCurrentToken();
			}
		}

		if (isPeek) {
			for (Token.SyntaxType type : types) {
				if (type == current.type) {
					if (isConsume) tokenizer.setNextToken();
					return current;
				}
			}
			return BADTOKEN;
		}

		if (isConsume) {
			for (Token.SyntaxType type : types) {
				if (type == current.type) {
					tokenizer.setNextToken();
					return current;
				}
			}
			System.err.println("[Cappuccino AATS (ATS)] Syntax Mismatch (Syntax Error): expected " + solution(types) + " but found '" + current.value.toString() + "' (" + current.type.toString() + ") in statement '" + this.getKind().name() + "' at line " + current.line);
			System.exit(-1);
		}

		System.err.println("Without flags!");
		System.exit(-1);
		return null;
	}

	private StringBuilder solution(Token.SyntaxType... types) {
		StringBuilder sb = new StringBuilder();
		for (Token.SyntaxType type : types) {
			sb.append(" '").append(type.assignation.toString()).append("' (").append(type).append(')');
		}
		return sb;
	}
}
