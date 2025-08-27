package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import java.util.LinkedList;
import java.util.Objects;

import static cappuccino.Tokenizer.Token.SyntaxType.*;
import static cappuccino.Tree.CCTreeUtils.getBuilderCompilationError;

public class CCTVariableStatement extends CCTStatementAbstract {
	public LinkedList<CCTSubVariableStatement> subVariables = new LinkedList<>();
	public CCTSubVariableStatement subVariable;
	public CCTLiteral literal = new CCTLiteral();
	public boolean isSubLet = false;

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
		this.subVariables.forEach(CCTSubVariableStatement::visitor);

		while (parent != null && parent.getKind() == Kind.Block) {
			((CCTBlockStatement) parent).variables.forEach((cctVariableStatement) -> {
				if ((cctVariableStatement.name.value.equals(name.value) && cctVariableStatement != this) && cctVariableStatement.line < line) {
					System.err.println(getBuilderCompilationError(cctVariableStatement, this));
					System.exit(-1);
				}
			});
			parent = parent.parent;
		}

		if (parent != null && parent.getKind() == Kind.CompilationUnit) {
			((CCTCompilationUnit) parent).variables.forEach((cctVariableStatement) -> {
				if (cctVariableStatement.name.value.equals(name.value) && cctVariableStatement != this && cctVariableStatement.line < line) {
					System.err.println(getBuilderCompilationError(cctVariableStatement, this));
					System.exit(-1);
				}
			});
		}

		if (!this.type.value.equals(this.literal.literal.type.type) && this.literal.literal.type != UndefinedKeywordLiteralToken) {
			System.err.println("[Cappuccino Semantic] Type Mismatch (Syntax Error): Variable " + this.name.value.toString() + " cannot be assigned the value " + this.literal.literal.value.toString() + " (" + this.literal.literal.type.type + "), which is not compatible with type '" + this.type.value + "'. Line: " + this.line);
			System.err.println("-----------------------------");
			System.err.println("let " + this.name.value + ": " + this.type.value + " = " + this.literal.literal.value + "; // Incorrect Code; at line: " + this.line);
			System.err.println("-----------------------------");
			System.exit(-1);
		}
	}

	@Override
	public Kind getKind() {
		return Kind.Variable;
	}

	public static class CCTSubVariableStatement extends CCTVariableStatement {
		public CCTLiteral literal = new CCTLiteral();

		public CCTSubVariableStatement(Token type) {
			this.type = type;
			isSubLet = true;
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

			if (parent.parent.getKind() == Kind.CompilationUnit) {
				((CCTCompilationUnit) parent.parent).variables.add(this);
			} else if (parent.parent.getKind() == Kind.Block) {
				((CCTBlockStatement) parent.parent).variables.add(this);
			}
		}

		@Override
		public void visitor() {
			if (parent.getKind() == Kind.Variable) {
				while (parent.getKind() == Kind.Block) {
					((CCTBlockStatement) parent).variables.forEach((cctSubVariable) -> {
						if (cctSubVariable.name.value.equals(name.value) && cctSubVariable != this && cctSubVariable.line < line) {
							System.err.println(getBuilderCompilationError((CCTStatementAbstract) this.parent, this));
							System.exit(-1);
						}
					});
					parent = parent.parent;
				}

				if (parent.getKind() == Kind.CompilationUnit) {
					((CCTCompilationUnit) parent).variables.forEach((cctSubVariable) -> {
						if (cctSubVariable.name.value.equals(name.value) && cctSubVariable != this  && cctSubVariable.line < line) {
							System.err.println(getBuilderCompilationError((CCTStatementAbstract) this.parent, this));
							System.exit(-1);
						}
					});
					parent = parent.parent;
				}

				if (((CCTStatementAbstract) parent).name.value.equals(this.name.value)) {
					System.err.println(getBuilderCompilationError((CCTStatementAbstract) this.parent, this));
					System.exit(-1);
				}

				((CCTVariableStatement) parent).subVariables.forEach(cctSubVariableStatement -> {
					if (cctSubVariableStatement.name.value.equals(name.value) && cctSubVariableStatement != this) {
						System.err.println(getBuilderCompilationError(cctSubVariableStatement, this));
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