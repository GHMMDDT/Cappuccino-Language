package cappuccino.Tokenizer;

public abstract class AbstractCappuccinoTokenizer {
	protected abstract void getWhitespace();
	protected abstract void getCommentaryLiteral(boolean isMultipleCommentary);

	protected abstract Token getIdentifier(int startPos);
	protected abstract Token getNumericLiteral(int startPos);
	protected abstract Token getCharacterLiteral(int startPos);
	protected abstract Token getStringLiteral(int startPos);
	protected abstract Token getSymbol(int startPos);

	public abstract void setNextToken();
	public abstract Token getCurrentToken();
}
