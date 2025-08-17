package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import java.util.LinkedList;

import static cappuccino.Tokenizer.Token.SyntaxType.*;

public abstract class CCTBlockAbstract extends CCTreeAbstract {
	private LinkedList<CCTVariableStatement> variables = new LinkedList<>();

	@Override
	public void parser() {
		validator(CURRENT | CONSUME, CurlyLeftSymbolOperatorToken);

		Token t = validator(CURRENT | SEEK);
		boolean isValid = false;
		while (validator(CURRENT | SEEK, CurlyRightSymbolOperatorToken).type == BadToken) {
			if (t.type == LetKeywordToken) {
				CCTVariableStatement variable = new CCTVariableStatement();
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

		validator(CURRENT | CONSUME, CurlyLeftSymbolOperatorToken);
	}
}