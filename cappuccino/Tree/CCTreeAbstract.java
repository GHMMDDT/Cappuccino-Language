package cappuccino.Tree;

import cappuccino.ArrayDeque;
import cappuccino.Tokenizer.CappuccinoTokenizer;
import cappuccino.Tokenizer.CharSlice;
import cappuccino.Tokenizer.Token;
// import cappuccino.Tree.CCTreeAbstract.CCTVariable.CCTSubVariable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static cappuccino.Tokenizer.Token.SyntaxType.*;
import static cappuccino.Tree.CCTreeUtils.getBuilderCompilationError;

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
	public Token name = null;
	public Token type = null;
	public int line;

	public abstract void parser();
	public abstract void visitor();

	public abstract Kind getKind();

	public static class CCTCompilationUnit extends CCTreeAbstract {
		public LinkedList<CCTVariable> variables = new LinkedList<>();

		@Override
		public Kind getKind() {
			return Kind.CompilationUnit;
		}

		@Override
		public void parser() {
			Token t = validator(CURRENT | SEEK);
			boolean isValid = false;
			while (t.type != EndOfInputToken) {
				if (t.type == LetKeywordToken) {
					CCTVariable variable = new CCTVariable();
					variable.parent = this;
					variable.parser();
					variables.add(variable);
					isValid = true;
				}

				if (isValid) {
					t = validator(CURRENT | SEEK);
					isValid = false;
					continue;
				}

				System.err.println("The value: " + t.value.toString() + " (" + t.type.name() + ") not is valid for the compiler");
				System.exit(-1);
			}
		}

		@Override
		public void visitor() {
			variables.forEach(CCTVariable::visitor);
		}
	}

	public static class CCTVariable extends CCTreeAbstract {
		public LinkedList<CCTSubVariable> subVariables = new LinkedList<>();
		public CCTSubVariable subVariable;
		public CCTLiteral literal = new CCTLiteral();

		@Override
		public void parser() {
			validator(CURRENT | CONSUME, LetKeywordToken);
			name = validator(CURRENT | CONSUME, IdentifierToken);
			line = name.line;

			validator(CURRENT | CONSUME, ColonSymbolDelimiterToken);

			type = validator(CURRENT | CONSUME, IdentifierToken);

			if (validator(CURRENT | SEEK | CONSUME, EqualSymbolOperatorToken).type != BadToken) {
				literal.parent = this;
				literal.parser();
			}

			while (validator(CURRENT | SEEK | CONSUME, CommaSymbolDelimiterToken).type != BadToken) {
				subVariable = new CCTSubVariable(this.type);
				subVariable.parent = this;
				subVariable.parser();
				subVariables.add(subVariable);
			}

			validator(CURRENT | CONSUME, SemicolonSymbolDelimiterToken);
		}

		@Override
		public void visitor() {
			if (parent.getKind() == Kind.CompilationUnit) {
				((CCTCompilationUnit) parent).variables.forEach(cctVariable -> {
					if (cctVariable.name.value.equals(name.value) && cctVariable != this) {
						System.err.println(getBuilderCompilationError(this, cctVariable));
						System.exit(-1);
					}
				});
			}

			if (!this.type.value.equals(this.literal.literal.type.type)) {
				System.err.println("[Cappuccino Semantic] Type Mismatch (Syntax Error): Variable " + this.name.value.toString() + " cannot be assigned the value " + this.literal.literal.value.toString() + " (" + this.literal.literal.type.type + "), which is not compatible with type '" + this.type.value + "'. Line: " + this.line);
				System.exit(-1);
			}

			this.subVariables.forEach(new Consumer<CCTSubVariable>() {
				@Override
				public void accept(CCTSubVariable cctSubVariable) {
					cctSubVariable.visitor();
				}
			});
		}

		@Override
		public Kind getKind() {
			return Kind.Variable;
		}

		public static class CCTSubVariable extends CCTreeAbstract {
			public CCTLiteral literal = new CCTLiteral();

			public CCTSubVariable(Token type) {
				this.type = type;
			}

			@Override
			public void parser() {
				name = validator(CURRENT | CONSUME, IdentifierToken);
				line = name.line;

				if (validator(CURRENT | SEEK | CONSUME, ColonSymbolDelimiterToken).type != BadToken) {
					type = validator(CURRENT | CONSUME, IdentifierToken);
				}

				if (validator(CURRENT | SEEK | CONSUME, EqualSymbolOperatorToken).type != BadToken) {
					literal.parent = this;
					literal.parser();
				}
			}

			@Override
			public void visitor() {
				if (parent.getKind() == Kind.Variable) {
					if (parent.getKind() == Kind.CompilationUnit) {
						((CCTCompilationUnit) parent.parent).variables.forEach(cctSubVariable -> {
							if (cctSubVariable.name.value.equals(name.value)) {
								System.err.println(getBuilderCompilationError(this.parent, this));
								System.exit(-1);
							}
						});
					}

					if (parent.name.value.equals(this.name.value)) {
						System.err.println(getBuilderCompilationError(this.parent, this));
						System.exit(-1);
					}

					((CCTVariable) parent).subVariables.forEach(cctSubVariable -> {
						if (cctSubVariable.name.value.equals(name.value) && cctSubVariable != this) {
							System.err.println(getBuilderCompilationError(this, cctSubVariable));
							System.exit(-1);
						}
					});
				}
			}

			@Override
			public Kind getKind() {
				return Kind.SubVariable;
			}
		}
	}

	public static class CCTLiteral extends CCTreeAbstract {
		public Token literal = new Token(UndefinedKeywordLiteralToken.assignation, UndefinedKeywordLiteralToken);


		@Override
		public void parser() {
			literal = validator(CURRENT | CONSUME);
		}

		@Override
		public void visitor() {

		}

		@Override
		public Kind getKind() {
			return null;
		}
	}

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
