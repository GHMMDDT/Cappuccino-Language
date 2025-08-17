package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import java.util.LinkedList;

import static cappuccino.Tokenizer.Token.SyntaxType.*;
import static cappuccino.Tree.CCTreeUtils.getBuilderCompilationError;

public class CCTVariableStatement extends CCTStatementAbstract {
	public LinkedList<CCTSubVariableStatement> subVariables = new LinkedList<>();
	public CCTSubVariableStatement subVariable;
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
			subVariable = new CCTSubVariableStatement(this.type);
			subVariable.parent = this;
			subVariable.parser();
			subVariables.add(subVariable);
		}

		validator(CURRENT | CONSUME, SemicolonSymbolDelimiterToken);
	}

	@Override
	public void visitor() {
		if (parent.getKind() == Kind.CompilationUnit) {
			((CCTCompilationUnit) parent).variables.forEach(cctVariableStatement -> {
				if (cctVariableStatement.name.value.equals(name.value) && cctVariableStatement != this) {
					System.err.println(getBuilderCompilationError(this, cctVariableStatement));
					System.exit(-1);
				}
			});
		}

		if (!this.type.value.equals(this.literal.literal.type.type)) {
			System.err.println("[Cappuccino Semantic] Type Mismatch (Syntax Error): Variable " + this.name.value.toString() + " cannot be assigned the value " + this.literal.literal.value.toString() + " (" + this.literal.literal.type.type + "), which is not compatible with type '" + this.type.value + "'. Line: " + this.line);
			System.err.println("-----------------------------");
			System.err.println("let " + this.name.value + ": " + this.type.value + " = " + this.literal.literal.value + "; // Incorrect Code; at line: " + this.line);
			System.err.println("-----------------------------");
			System.exit(-1);
		}

		this.subVariables.forEach(CCTSubVariableStatement::visitor);
	}

	@Override
	public Kind getKind() {
		return Kind.Variable;
	}

	public static class CCTSubVariableStatement extends CCTStatementAbstract {
		public CCTLiteral literal = new CCTLiteral();

		public CCTSubVariableStatement(Token type) {
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
							System.err.println(getBuilderCompilationError((CCTStatementAbstract) this.parent, this));
							System.exit(-1);
						}
					});
				}

				if (((CCTStatementAbstract) parent).name.value.equals(this.name.value)) {
					System.err.println(getBuilderCompilationError((CCTStatementAbstract) this.parent, this));
					System.exit(-1);
				}

				((CCTVariableStatement) parent).subVariables.forEach(cctSubVariableStatement -> {
					if (cctSubVariableStatement.name.value.equals(name.value) && cctSubVariableStatement != this) {
						System.err.println(getBuilderCompilationError(this, cctSubVariableStatement));
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