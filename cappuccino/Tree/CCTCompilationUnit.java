package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import java.util.LinkedList;

import static cappuccino.Tokenizer.Token.SyntaxType.EndOfInputToken;
import static cappuccino.Tokenizer.Token.SyntaxType.LetKeywordToken;

public class CCTCompilationUnit extends CCTreeAbstract {
	public LinkedList<CCTVariableStatement> variables = new LinkedList<>();

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
	}

	@Override
	public void visitor() {
		variables.forEach(CCTVariableStatement::visitor);
	}
}