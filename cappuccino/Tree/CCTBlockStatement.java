package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static cappuccino.Tokenizer.Token.SyntaxType.*;

public class CCTBlockStatement extends CCTStatementAbstract{
	public LinkedList<CCTVariableStatement> variables = new LinkedList();
	public LinkedList<CCTBlockStatement> block = new LinkedList<>();

	@Override
	public void parser() {
		validator(CURRENT | CONSUME, CurlyLeftSymbolOperatorToken);

		Token t = validator(CURRENT | SEEK);

		boolean isValid = false;
		while (t.type != CurlyRightSymbolOperatorToken) {
			if (t.type == LetKeywordToken) {
				CCTVariableStatement statement = new CCTVariableStatement();
				statement.parent = this;
				statement.parser();
				this.variables.add(statement);
				isValid = true;
			} else if (t.type == CurlyLeftSymbolOperatorToken) {
				CCTBlockStatement statement = new CCTBlockStatement();
				statement.parent = this;
				statement.parser();
				this.block.add(statement);
				isValid = true;
			}

			if (isValid) {
				t = validator(CURRENT | SEEK);
				isValid = false;
				continue;
			}

			if (t.type == EndOfInputToken) {
				int line = t.line;
				System.err.println("[Cappuccino AATS (ATS)] Syntax Mismatch (Syntax Error): expected '}' but found '" + t.value.toString() + "' (" + t.type.type + ") in statement '" + this.getKind().name() + "' at line " + (line == 0 ? 1 : line));
				System.exit(-1);
			}

			System.exit(-1);
		}

		validator(CURRENT | CONSUME, CurlyRightSymbolOperatorToken);
	}

	@Override
	public void visitor() {
		variables.forEach(CCTVariableStatement::visitor);
		block.forEach(CCTBlockStatement::visitor);
	}

	@Override
	public Kind getKind() {
		return Kind.Block;
	}
}
