package cappuccino.Tree;

import cappuccino.Tokenizer.Token;

import static cappuccino.Tokenizer.Token.SyntaxType.UndefinedKeywordLiteralToken;

public class CCTLiteral extends CCTreeAbstract {
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