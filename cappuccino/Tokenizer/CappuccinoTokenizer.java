package cappuccino.Tokenizer;

import static cappuccino.Tokenizer.Token.SyntaxType.*;

import cappuccino.Tokenizer.Token.SyntaxType ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public final class CappuccinoTokenizer extends AbstractCappuccinoTokenizer {
	private Token token;
	private Token newToken;
	public final Token EndToken = new Token(new CharSlice('\3'), EndOfInputToken);

	private int position = 0;
	public final char[] target;
	private final int length;
	private int line = 1;

	private final HashMap<CharSlice, Token> LARGE_CACHE_TOKEN_PRESENT = new HashMap<>(); { // token pre-procesados
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[] { 'b', 'r', 'e', 'a', 'k' }), new Token(new CharSlice(new char[] { 'b', 'r', 'e', 'a', 'k' }), BreakKeywordControlToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','n','t','i','n','u','e'}), new Token(new CharSlice(new char[]{'c','o','n','t','i','n','u','e'}), ContinueKeywordControlToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'m','o','d','u','l','e'}), new Token(new CharSlice(new char[]{'m','o','d','u','l','e'}), ModuleKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','a','c','k','a','g','e'}), new Token(new CharSlice(new char[]{'p','a','c','k','a','g','e'}), PackageKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','m','p','o','r','t'}), new Token(new CharSlice(new char[]{'i','m','p','o','r','t'}), ImportKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','r','o','m'}), new Token(new CharSlice(new char[]{'f','r','o','m'}), FromKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'u','s','i','n','g'}), new Token(new CharSlice(new char[]{'u','s','i','n','g'}), UsingKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'a','s'}), new Token(new CharSlice(new char[]{'a','s'}), AsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','r','o','t','e','c','t','e','d'}), new Token(new CharSlice(new char[]{'p','r','o','t','e','c','t','e','d'}), ProtectedKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','u','b','l','i','c'}), new Token(new CharSlice(new char[]{'p','u','b','l','i','c'}), PublicKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','r','i','v','a','t','e'}), new Token(new CharSlice(new char[]{'p','r','i','v','a','t','e'}), PrivateKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','w','i','t','c','h'}), new Token(new CharSlice(new char[]{'s','w','i','t','c','h'}), SwitchKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','a','s','e'}), new Token(new CharSlice(new char[]{'c','a','s','e'}), CaseKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','e','a','l','e','d'}), new Token(new CharSlice(new char[]{'s','e','a','l','e','d'}), SealedKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'e','x','t','e','r','n','a','l'}), new Token(new CharSlice(new char[]{'e','x','t','e','r','n','a','l'}), ExternalKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','t','e','r','n','a','l'}), new Token(new CharSlice(new char[]{'i','n','t','e','r','n','a','l'}), InternalKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','n','e','r'}), new Token(new CharSlice(new char[]{'i','n','n','e','r'}), InnerKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'e','x','n','e','r'}), new Token(new CharSlice(new char[]{'e','x','n','e','r'}), ExnerKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'o','p','e','n'}), new Token(new CharSlice(new char[]{'o','p','e','n'}), OpenKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','l','o','s','e'}), new Token(new CharSlice(new char[]{'c','l','o','s','e'}), CloseKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','r','i','e','n','d'}), new Token(new CharSlice(new char[]{'f','r','i','e','n','d'}), FriendKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n'}), new Token(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n'}), CompanionKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','i','n','a','l'}), new Token(new CharSlice(new char[]{'f','i','n','a','l'}), FinalKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'a','b','s','t','r','a','c','t'}), new Token(new CharSlice(new char[]{'a','b','s','t','r','a','c','t'}), AbstractKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','l','a','s','s'}), new Token(new CharSlice(new char[]{'c','l','a','s','s'}), ClassKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'e','n','u','m','e','r','a','t','e'}), new Token(new CharSlice(new char[]{'e','n','u','m','e','r','a','t','e'}), EnumerateKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','t','r','u','c','t','u','r','e'}), new Token(new CharSlice(new char[]{'s','t','r','u','c','t','u','r','e'}), StructureKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'r','e','c','o','r','d'}), new Token(new CharSlice(new char[]{'r','e','c','o','r','d'}), RecordKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'d','a','t','a'}), new Token(new CharSlice(new char[]{'d','a','t','a'}), DataKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','t','e','r','f','a','c','e'}), new Token(new CharSlice(new char[]{'i','n','t','e','r','f','a','c','e'}), InterfaceKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'a','n','n','o','t','a','t','i','o','n'}), new Token(new CharSlice(new char[]{'a','n','n','o','t','a','t','i','o','n'}), AnnotationKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'a','t','t','r','i','b','u','t','e'}), new Token(new CharSlice(new char[]{'a','t','t','r','i','b','u','t','e'}), AttributeKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','r','a','i','t'}), new Token(new CharSlice(new char[]{'t','r','a','i','t'}), TraitKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'e','x','t','e','n','d'}), new Token(new CharSlice(new char[]{'e','x','t','e','n','d'}), ExtendKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'e','x','t','e','n','d','s'}), new Token(new CharSlice(new char[]{'e','x','t','e','n','d','s'}), ExtendsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t'}), new Token(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t'}), ImplementKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t','s'}), new Token(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t','s'}), ImplementsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n','s'}), new Token(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n','s'}), CompanionsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','r','i','e','n','d','s'}), new Token(new CharSlice(new char[]{'f','r','i','e','n','d','s'}), FriendsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n'}), new Token(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n'}), RestrictionKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n','s'}), new Token(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n','s'}), RestrictionsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','e','r','m','i','t'}), new Token(new CharSlice(new char[]{'p','e','r','m','i','t'}), PermitKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','e','r','m','i','t','s'}), new Token(new CharSlice(new char[]{'p','e','r','m','i','t','s'}), PermitsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'w','i','t','h'}), new Token(new CharSlice(new char[]{'w','i','t','h'}), WithKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'w','i','t','h','s'}), new Token(new CharSlice(new char[]{'w','i','t','h','s'}), WithsKeywordToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','e','t'}), new Token(new CharSlice(new char[]{'s','e','t'}), SetKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'g','e','t'}), new Token(new CharSlice(new char[]{'g','e','t'}), GetKeywordToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'l','o','c','a','l'}), new Token(new CharSlice(new char[]{'l','o','c','a','l'}), LocalKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'g','l','o','b','a','l'}), new Token(new CharSlice(new char[]{'g','l','o','b','a','l'}), GlobalKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'r','e','a','d','o','n','l','y'}), new Token(new CharSlice(new char[]{'r','e','a','d','o','n','l','y'}), ReadonlyKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','n','s','t','a','n','t'}), new Token(new CharSlice(new char[]{'c','o','n','s','t','a','n','t'}), ConstantKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'w','r','i','t','e','o','n','l','y'}), new Token(new CharSlice(new char[]{'w','r','i','t','e','o','n','l','y'}), WriteonlyKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','a','t','i','v','e'}), new Token(new CharSlice(new char[]{'n','a','t','i','v','e'}), NativeKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'o','v','e','r','w','r','i','t','e'}), new Token(new CharSlice(new char[]{'o','v','e','r','w','r','i','t','e'}), OverwriteKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'w','r','i','t','e'}), new Token(new CharSlice(new char[]{'w','r','i','t','e'}), WriteKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','u','n','c','t','i','o','n'}), new Token(new CharSlice(new char[]{'f','u','n','c','t','i','o','n'}), FunctionKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'d','e','f','i','n','i','t','i','o','n'}), new Token(new CharSlice(new char[]{'d','e','f','i','n','i','t','i','o','n'}), DefinitionKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'o','p','e','r','a','t','o','r'}), new Token(new CharSlice(new char[]{'o','p','e','r','a','t','o','r'}), OperatorKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','r','e','f','i','x'}), new Token(new CharSlice(new char[]{'p','r','e','f','i','x'}), PrefixKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','u','f','f','i','x'}), new Token(new CharSlice(new char[]{'s','u','f','f','i','x'}), SuffixKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','n','t','r','o','l'}), new Token(new CharSlice(new char[]{'c','o','n','t','r','o','l'}), ControlKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','f','i','x'}), new Token(new CharSlice(new char[]{'i','n','f','i','x'}), InfixKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'c','o','n','s','t','r','u','c','t','o','r'}), new Token(new CharSlice(new char[]{'c','o','n','s','t','r','u','c','t','o','r'}), ConstructorKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','h','r','o','w'}), new Token(new CharSlice(new char[]{'t','h','r','o','w'}), ThrowKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','h','r','o','w','s'}), new Token(new CharSlice(new char[]{'t','h','r','o','w','s'}), ThrowsKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','a','m','e','s','p','a','c','e'}), new Token(new CharSlice(new char[]{'n','a','m','e','s','p','a','c','e'}), NamespaceKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n'}), new Token(new CharSlice(new char[]{'i','n'}), InKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'o','u','t'}), new Token(new CharSlice(new char[]{'o','u','t'}), OutKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','o'}), new Token(new CharSlice(new char[]{'s','o'}), SoKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'o','f'}), new Token(new CharSlice(new char[]{'o','f'}), OfKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','o'}), new Token(new CharSlice(new char[]{'t','o'}), ToKeywordToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'l','e','t'}), new Token(new CharSlice(new char[]{'l','e','t'}), LetKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','y','p','e'}), new Token(new CharSlice(new char[]{'t','y','p','e'}), TypeKeywordToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','u','p','e','r'}), new Token(new CharSlice(new char[]{'s','u','p','e','r'}), SuperKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'b','a','s','e'}), new Token(new CharSlice(new char[]{'b','a','s','e'}), BaseKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','h','i','s'}), new Token(new CharSlice(new char[]{'t','h','i','s'}), ThisKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'s','e','l','f'}), new Token(new CharSlice(new char[]{'s','e','l','f'}), SelfKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','e','x','t'}), new Token(new CharSlice(new char[]{'n','e','x','t'}), NextKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'p','r','e','v','i','o','u','s'}), new Token(new CharSlice(new char[]{'p','r','e','v','i','o','u','s'}), PreviousKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'r','e','t','u','r','n'}), new Token(new CharSlice(new char[]{'r','e','t','u','r','n'}), ReturnKeywordToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','e','w'}), new Token(new CharSlice(new char[]{'n','e','w'}), NewKeywordToken));

		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'f','a','l','s','e'}), new Token(new CharSlice(new char[]{'f','a','l','s','e'}), FalseKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'t','r','u','e'}), new Token(new CharSlice(new char[]{'t','r','u','e'}), TrueKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','d','e','t','e','r','m','i','n','a','t','e'}), new Token(new CharSlice(new char[]{'i','n','d','e','t','e','r','m','i','n','a','t','e'}), IndeterminateKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'u','n','d','e','f','i','n','e','d'}), new Token(new CharSlice(new char[]{'u','n','d','e','f','i','n','e','d'}), UndefinedKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'N','a','N'}), new Token(new CharSlice(new char[]{'N','a','N'}), NaNKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'N','A'}), new Token(new CharSlice(new char[]{'N','A'}), NAKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','u','l','l'}), new Token(new CharSlice(new char[]{'n','u','l','l'}), NullKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','o','n','e'}), new Token(new CharSlice(new char[]{'n','o','n','e'}), NoneKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'i','n','f','i','n','i','t','y'}), new Token(new CharSlice(new char[]{'i','n','f','i','n','i','t','y'}), InfinityKeywordLiteralToken));
		LARGE_CACHE_TOKEN_PRESENT.put(new CharSlice(new char[]{'n','e','u','t','r','a','l'}), new Token(new CharSlice(new char[]{'n','e','u','t','r','a','l'}), NeutralKeywordLiteralToken));
	}

	private final HashMap<Character, Token> SMALL_CACHE_TOKEN_PRESENT = new HashMap<>(); { // token pre-procesados
		// Operators;
		SMALL_CACHE_TOKEN_PRESENT.put('=', new Token(new CharSlice('='), EqualSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('(', new Token(new CharSlice('('), ParenthesisLeftSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put(')', new Token(new CharSlice(')'), ParenthesisRightSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('[', new Token(new CharSlice('['), SquareLeftSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put(']', new Token(new CharSlice(']'), SquareRightSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('{', new Token(new CharSlice('{'), CurlyLeftSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('}', new Token(new CharSlice('}'), CurlyRightSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('&', new Token(new CharSlice('&'), AndSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('|', new Token(new CharSlice('|'), VerticalLineSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('!', new Token(new CharSlice('!'), NotSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('?', new Token(new CharSlice('?'), QuestionSymbolOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('^', new Token(new CharSlice('^'), CaretSymbolOperatorToken));

		// Arithmetical Operators:
		SMALL_CACHE_TOKEN_PRESENT.put('+', new Token(new CharSlice('+'), PlusSymbolArithmeticalOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('-', new Token(new CharSlice('-'), MinusSymbolArithmeticalOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('*', new Token(new CharSlice('*'), StartSymbolArithmeticalOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('/', new Token(new CharSlice('/'), SlashSymbolArithmeticalOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('<', new Token(new CharSlice('<'), LessThanSymbolArithmeticalOperatorToken));
		SMALL_CACHE_TOKEN_PRESENT.put('>', new Token(new CharSlice('>'), GreaterThanSymbolArithmeticalOperatorToken));

		// Delimiters;
		SMALL_CACHE_TOKEN_PRESENT.put(';', new Token(new CharSlice(';'), SemicolonSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put(':', new Token(new CharSlice(':'), ColonSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put(',', new Token(new CharSlice(','), CommaSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put('.', new Token(new CharSlice('.'), DotSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put('@', new Token(new CharSlice('@'), AtSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put('_', new Token(new CharSlice('_'), LowLineSymbolDelimiterToken));
		SMALL_CACHE_TOKEN_PRESENT.put('~', new Token(new CharSlice('~'), TildeSymbolDelimiterToken));
	}

	private final SyntaxType[] suffixNumericSyntax = new SyntaxType[128]; {
		suffixNumericSyntax['n'] = suffixNumericSyntax['N'] = NumberLiteralToken;
		suffixNumericSyntax['b'] = suffixNumericSyntax['B'] = ByteLiteralToken;
		suffixNumericSyntax['s'] = suffixNumericSyntax['S'] = ShortLiteralToken;
		suffixNumericSyntax['i'] = suffixNumericSyntax['I'] = IntegerLiteralToken;
		suffixNumericSyntax['l'] = suffixNumericSyntax['L'] = LongLiteralToken;
		suffixNumericSyntax['f'] = suffixNumericSyntax['F'] = FloatLiteralToken;
		suffixNumericSyntax['d'] = suffixNumericSyntax['D'] = DoubleLiteralToken;
	}

	private final SyntaxType[] suffixNumericDecimalSyntax = new SyntaxType[128]; {
		suffixNumericDecimalSyntax['n'] = suffixNumericDecimalSyntax['N'] = NumberLiteralToken;
		suffixNumericDecimalSyntax['f'] = suffixNumericDecimalSyntax['F'] = FloatLiteralToken;
		suffixNumericDecimalSyntax['d'] = suffixNumericDecimalSyntax['D'] = DoubleLiteralToken;
	}

	public CappuccinoTokenizer(String target) {
		this.target = target.toCharArray();
		this.length = target.length();
	}

	public CappuccinoTokenizer(File file) {
		this.target = this.readFile(file);
		this.length = this.target.length;
	}

	private char[] readFile(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			FileChannel channel = fis.getChannel();
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			Charset charset = StandardCharsets.UTF_8;
			CharBuffer charBuffer = charset.decode(buffer);

			char[] result = new char[charBuffer.remaining()];
			charBuffer.get(result);
			return result;
		} catch (IOException e) {
			return new char[0];
		}
	}

	@Override
	protected void getWhitespace() {
		do {
			if (this.target[this.position] == '\n') {
				this.line++;
			}
			this.position++;
		} while (this.position < this.length && (this.target[this.position] == ' ' || this.target[this.position] == '\n' || this.target[this.position] == '\r' || this.target[this.position] == '\t'));
	}

	@Override
	protected void getCommentaryLiteral(boolean isMultipleCommentary) {
		if (!isMultipleCommentary) {
			do {
				if (this.target[this.position] == '\n') {
					break;
				}
				this.position++;
			} while (this.position < this.length);
		} else {
			do {
				if (this.target[this.position] == '*') {
					this.position++;
					if (this.target[this.position] == '/') {
						this.position++;
						break;
					}
				}
				if (this.target[this.position] == '\n') {
					this.line++;
				}
				this.position++;
			} while (this.position < this.length);
		}
		this.setNextToken();
	}

	@Override
	protected Token getIdentifier(int startPos) {
		boolean isCharacterPrefix;
		boolean isStringPrefix;

		do {
			isCharacterPrefix = this.target[this.position] == 'c';
			isStringPrefix = this.target[this.position] == 's';
			this.position++;
		} while (this.position < this.length && ((this.target[this.position] >= 'a' && this.target[this.position] <= 'z') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'Z') || (this.target[this.position] >= '0' && this.target[this.position] <= '9') || this.target[this.position] == '_' || this.target[this.position] == '$'));

		if (this.position < this.length && this.target[this.position] == '\'' && isCharacterPrefix && (this.position - startPos == 1)) {
			return this.getCharacterLiteral(startPos);
		}

		if (this.position < this.length && this.target[this.position] == '"' && isStringPrefix && (this.position - startPos == 1)) {
			return this.getStringLiteral(startPos);
		}

		this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(new CharSlice(this.target, startPos, this.position - startPos), k -> new Token(k, IdentifierToken));
		this.token.line = this.line;
		this.token.type.assignation = this.token.value;

		return this.token;
	}

	@Override
	protected Token getNumericLiteral(int startPos) {
		boolean isDecimal = false;

		do {
			this.position++;
		} while (this.position < this.length && (this.target[this.position] >= '0' && this.target[this.position] <= '9'));

		if (this.position < this.length && this.target[this.position] == '.') {
			isDecimal = true;
			do {
				this.position++;
			} while (this.position < this.length && (this.target[this.position] >= '0' && this.target[this.position] <= '9'));
		}

		final SyntaxType[] type = new SyntaxType[1];
		if (!isDecimal) {
			if (this.position < this.length && (type[0] = this.suffixNumericSyntax[this.target[this.position]]) != null) {
				this.position++;
			} else {
				type[0] = DigitLiteralToken;
			}
		} else {
			if (this.position < this.length && (type[0] = this.suffixNumericDecimalSyntax[this.target[this.position]]) != null) {
				this.position++;
			} else {
				type[0] = DigitLiteralToken;
			}
		}

		this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(new CharSlice(this.target, startPos, this.position - startPos), k -> new Token(k, type[0]));
		this.token.line = this.line;
		this.token.type.assignation = this.token.value;

		return this.token;
	}

	@Override
	protected Token getCharacterLiteral(int startPos) {
		boolean isEnded = true;
		int isUnicode = 0;
		this.position++;

		if (this.position < this.length && this.target[this.position] == '\\') {
			boolean isValidHexadecimal = false;
			this.position++;

			if (this.position < this.length && (((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) || this.target[this.position] == 'r' || this.target[this.position] == 't' || this.target[this.position] == 'f' || this.target[this.position] == 'n' || this.target[this.position] == '\\' || this.target[this.position] == '\'' || this.target[this.position] == '\"')) {
				this.position++;
				isValidHexadecimal = true;
				isUnicode = 2;
			} else if (this.target[this.position] == 'u') {
				this.position++;
				if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
					this.position++;
					isValidHexadecimal = true;
				}
				if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
					this.position++;
					isValidHexadecimal = true;
				}
				if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
					this.position++;
					isValidHexadecimal = true;
				}
				if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
					this.position++;
					isValidHexadecimal = true;
				}
				isUnicode = 1;
			}

			if (!isValidHexadecimal) {
				throw new RuntimeException("El escapable hexadecimal es incorrecto del de caracter: " + new String(this.target, startPos, this.position - startPos));
			}

		} else {
			this.position++;
		}

		if (this.position > this.length || this.target[this.position] != '\'') {
			isEnded = false;
		}

		if (!isEnded) {
			throw new RuntimeException("Falta en el final el \"'\" para dar por terminado el literal caracter en la linea: " + this.line);
		}

		this.position++;


		if (isUnicode == 1) {
			this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(
					new CharSlice(this.target, this.position - 7, 6),
					k -> new Token(k, CharacterLiteralToken)
			);
		} else if (isUnicode == 2) {
			this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(
					new CharSlice(this.target, this.position - 4, 4),
					k -> new Token(k, CharacterLiteralToken)
			);
		} else {
			this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(
					new CharSlice(this.target, this.position - 3, 3),
					k -> new Token(k, CharacterLiteralToken)
			);
		}
		this.token.line = this.line;
		this.token.type.assignation = this.token.value;

		return this.token;
	}

	@Override
	protected Token getStringLiteral(int startPos) {
		boolean isEnded = true;
		boolean isBlockString;
		this.position++;

		if (this .target[this.position] == '"' && this .target[this.position + 1] == '"') {
			this.position++;
			this.position++;
			isBlockString = true;
		} else {
			isBlockString = false;
		}

		while (this.position < this.length) {
			if (!isBlockString && this.target[this.position] == '\n') {
				isEnded = false;
				break;
			} else if (this.target[this.position] == '"') {
				if (isBlockString && this .target[this.position + 1] == '"' && this .target[this.position + 2] == '"') {
					this.position++;
					this.position++;
					this.position++;
					break;
				} else if (isBlockString) {
					this.position++;
				} else {
					break;
				}
			}

			if (this.target[this.position] == '\\') {
				boolean isValidHexadecimal = false;
				this.position++;

				if (this.position < this.length && (((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) || this.target[this.position] == 'r' || this.target[this.position] == 't' || this.target[this.position] == 'f' || this.target[this.position] == 'n' || this.target[this.position] == '\\' || this.target[this.position] == '\'' || this.target[this.position] == '\"')) {
					this.position++;
					isValidHexadecimal = true;
				} else if (this.target[this.position] == 'u') {
					this.position++;
					if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
						this.position++;
						isValidHexadecimal = true;
					}
					if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
						this.position++;
						isValidHexadecimal = true;
					}
					if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
						this.position++;
						isValidHexadecimal = true;
					}
					if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'f') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'F') || (this.target[this.position] >= '0' && this.target[this.position] <= '9')) {
						this.position++;
						isValidHexadecimal = true;
					}
				}

				if (!isValidHexadecimal) {
					throw new RuntimeException("El escapable hexadecimal es incorrecto del de caracter: " + new String(this.target, startPos, this.position - startPos));
				}

			} else {
				this.position++;
			}
		}

		if (!isEnded) {
			throw new RuntimeException("Falta en el final el '\"' para dar por terminado el literal caracter en la linea: " + this.line);
		}

		this.position++;


		this.token = LARGE_CACHE_TOKEN_PRESENT.computeIfAbsent(
				new CharSlice(this.target, startPos, this.position - startPos),
				k -> new Token(k, isBlockString ? StringBlockLiteralToken : StringLiteralToken)
		);
		this.token.line = this.line;
		this.token.type.assignation = this.token.value;

		return this.token;
	}

	@Override
	protected Token getSymbol(int startPos) {
		if (this.position < this.length) {
			if (this.target[this.position] == '/') {
				if (this.target[this.position + 1] == '/') {
					this.position++;
					this.getCommentaryLiteral(false);
					return this.getCurrentToken();
				} else if (this.target[this.position + 1] == '*') {
					this.position++;
					this.getCommentaryLiteral(true);
					return this.getCurrentToken();
				}
			}

			if (this.target[this.position] == '-' && this.target[this.position + 1] >= '0' && this.target[this.position + 1] <= '9') {
				this.token = this.getNumericLiteral(startPos);
				return this.token;
			}
		}


		this.token = SMALL_CACHE_TOKEN_PRESENT.get(
				this.target[this.position]/*,
				k -> new Token(k, this.symbolsSyntax[this.target[this.position - 1]])*/
		);
		this.token.line = this.line;
		this.token.type.assignation = this.token.value;

		this.position++;

		return this.token;
	}

	@Override
	public void setNextToken() {
		this.token = this.newToken;

		if (this.position >= this.length) {
			this.EndToken.line = line;
			this.newToken = EndToken;
			return;
		}

		if (this.target[this.position] == ' ' || this.target[this.position] == '\n' || this.target[this.position] == '\r' || this.target[this.position] == '\t') {
			this.getWhitespace();

			if (this.position >= this.length) {
				this.EndToken.line = line;
				this.newToken = EndToken;
				return;
			}
		}

		if ((this.target[this.position] >= 'a' && this.target[this.position] <= 'z') || (this.target[this.position] >= 'A' && this.target[this.position] <= 'Z')) {
			this.newToken = this.getIdentifier(this.position);
			return;
		}

		if (this.target[this.position] >= '0' && this.target[this.position] <= '9') {
			this.newToken = this.getNumericLiteral(this.position);
			return;
		}

		if (this.SMALL_CACHE_TOKEN_PRESENT.get(this.target[this.position]) != null) {
			this.newToken = this.getSymbol(this.position);
			return;
		}

		switch (this.target[this.position]) {
			case '"': {
				Token token = getStringLiteral(this.position);
				System.err.println("[Cappuccino Tokenizer] String Matcher Mismatch (Syntax Error): Incorrect matcher detected. Expected: s" + token.value + ", but found: " + token.value + ". Line: " + this.line);
				System.exit(-1);
			}
			case '\'': {
				Token token = getCharacterLiteral(this.position);
				System.err.println("[Cappuccino Tokenizer] Character Matcher Mismatch (Syntax Error): Incorrect matcher detected. Expected: c" + token.value + ", but found: " + token.value + ". Line: " + this.line);
				System.exit(-1);
			}
			default: {
				System.err.println("[Cappuccino Tokenizer] General Matcher Mismatch (Syntax Error): Could not find a suitable place for character: '" + this.target[this.position] + "' within the matchers: Identifier, Symbols and Whitespace; at line: " + this.line);
				System.exit(-1);
			}
		}
	}

	@Override
	public Token getCurrentToken() {
		return this.newToken;
	}
}