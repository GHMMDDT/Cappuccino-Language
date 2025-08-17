package cappuccino.Tokenizer;

public class Token {
	public final CharSlice value;
	public final SyntaxType type;
	public int line;

	public Token(CharSlice value, SyntaxType type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Token{" +
				"value='" + value.toString() + '\'' +
				", type=" + type +
				", line=" + line +
				'}';
	}

	public enum SyntaxType {
		// Classical:
		IdentifierToken(new CharSlice(new char[] { 'i', 'd', 'e', 'n', 't', 'i', 'f', 'i', 'e', 'r' })),

		// Keywords Control:
		BreakKeywordControlToken(new CharSlice(new char[] { 'b', 'r', 'e', 'a', 'k' })),
		ContinueKeywordControlToken(new CharSlice(new char[]{'c','o','n','t','i','n','u','e'})),

		// keywords:
		ModuleKeywordToken(new CharSlice(new char[]{'m','o','d','u','l','e'})),
		PackageKeywordToken(new CharSlice(new char[]{'p','a','c','k','a','g','e'})),

		ImportKeywordToken(new CharSlice(new char[]{'i','m','p','o','r','t'})),
		FromKeywordToken(new CharSlice(new char[]{'f','r','o','m'})),
		UsingKeywordToken(new CharSlice(new char[]{'u','s','i','n','g'})),
		AsKeywordToken(new CharSlice(new char[]{'a','s'})),

		ProtectedKeywordToken(new CharSlice(new char[]{'p','r','o','t','e','c','t','e','d'})),
		PublicKeywordToken(new CharSlice(new char[]{'p','u','b','l','i','c'})),
		PrivateKeywordToken(new CharSlice(new char[]{'p','r','i','v','a','t','e'})),
		SwitchKeywordToken(new CharSlice(new char[]{'s','w','i','t','c','h'})),
		CaseKeywordToken(new CharSlice(new char[]{'c','a','s','e'})),
		SealedKeywordToken(new CharSlice(new char[]{'s','e','a','l','e','d'})),
		ExternalKeywordToken(new CharSlice(new char[]{'e','x','t','e','r','n','a','l'})),
		InternalKeywordToken(new CharSlice(new char[]{'i','n','t','e','r','n','a','l'})),
		InnerKeywordToken(new CharSlice(new char[]{'i','n','n','e','r'})),
		ExnerKeywordToken(new CharSlice(new char[]{'e','x','n','e','r'})),
		OpenKeywordToken(new CharSlice(new char[]{'o','p','e','n'})),
		CloseKeywordToken(new CharSlice(new char[]{'c','l','o','s','e'})),
		FriendKeywordToken(new CharSlice(new char[]{'f','r','i','e','n','d'})),
		CompanionKeywordToken(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n'})),
		FinalKeywordToken(new CharSlice(new char[]{'f','i','n','a','l'})),
		AbstractKeywordToken(new CharSlice(new char[]{'a','b','s','t','r','a','c','t'})),
		ClassKeywordToken(new CharSlice(new char[]{'c','l','a','s','s'})),
		EnumerateKeywordToken(new CharSlice(new char[]{'e','n','u','m','e','r','a','t','e'})),
		StructureKeywordToken(new CharSlice(new char[]{'s','t','r','u','c','t','u','r','e'})),
		RecordKeywordToken(new CharSlice(new char[]{'r','e','c','o','r','d'})),
		DataKeywordToken(new CharSlice(new char[]{'d','a','t','a'})),
		InterfaceKeywordToken(new CharSlice(new char[]{'i','n','t','e','r','f','a','c','e'})),
		AnnotationKeywordToken(new CharSlice(new char[]{'a','n','n','o','t','a','t','i','o','n'})),
		AttributeKeywordToken(new CharSlice(new char[]{'a','t','t','r','i','b','u','t','e'})),
		TraitKeywordToken(new CharSlice(new char[]{'t','r','a','i','t'})),
		ExtendKeywordToken(new CharSlice(new char[]{'e','x','t','e','n','d'})),
		ExtendsKeywordToken(new CharSlice(new char[]{'e','x','t','e','n','d','s'})),
		ImplementKeywordToken(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t'})),
		ImplementsKeywordToken(new CharSlice(new char[]{'i','m','p','l','e','m','e','n','t','s'})),
		CompanionsKeywordToken(new CharSlice(new char[]{'c','o','m','p','a','n','i','o','n','s'})),
		FriendsKeywordToken(new CharSlice(new char[]{'f','r','i','e','n','d','s'})),
		RestrictionKeywordToken(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n'})),
		RestrictionsKeywordToken(new CharSlice(new char[]{'r','e','s','t','r','i','c','t','i','o','n','s'})),
		PermitKeywordToken(new CharSlice(new char[]{'p','e','r','m','i','t'})),
		PermitsKeywordToken(new CharSlice(new char[]{'p','e','r','m','i','t','s'})),
		WithKeywordToken(new CharSlice(new char[]{'w','i','t','h'})),
		WithsKeywordToken(new CharSlice(new char[]{'w','i','t','h','s'})),

		SetKeywordToken(new CharSlice(new char[]{'s','e','t'})),
		GetKeywordToken(new CharSlice(new char[]{'g','e','t'})),
		LocalKeywordToken(new CharSlice(new char[]{'l','o','c','a','l'})),
		GlobalKeywordToken(new CharSlice(new char[]{'g','l','o','b','a','l'})),
		ReadonlyKeywordToken(new CharSlice(new char[]{'r','e','a','d','o','n','l','y'})),
		ConstantKeywordToken(new CharSlice(new char[]{'c','o','n','s','t','a','n','t'})),
		WriteonlyKeywordToken(new CharSlice(new char[]{'w','r','i','t','e','o','n','l','y'})),
		NativeKeywordToken(new CharSlice(new char[]{'n','a','t','i','v','e'})),
		OverwriteKeywordToken(new CharSlice(new char[]{'o','v','e','r','w','r','i','t','e'})),
		WriteKeywordToken(new CharSlice(new char[]{'w','r','i','t','e'})),
		FunctionKeywordToken(new CharSlice(new char[]{'f','u','n','c','t','i','o','n'})),
		DefinitionKeywordToken(new CharSlice(new char[]{'d','e','f','i','n','i','t','i','o','n'})),
		OperatorKeywordToken(new CharSlice(new char[]{'o','p','e','r','a','t','o','r'})),
		PrefixKeywordToken(new CharSlice(new char[]{'p','r','e','f','i','x'})),
		SuffixKeywordToken(new CharSlice(new char[]{'s','u','f','f','i','x'})),
		ControlKeywordToken(new CharSlice(new char[]{'c','o','n','t','r','o','l'})),
		InfixKeywordToken(new CharSlice(new char[]{'i','n','f','i','x'})),
		ConstructorKeywordToken(new CharSlice(new char[]{'c','o','n','s','t','r','u','c','t','o','r'})),
		ThrowKeywordToken(new CharSlice(new char[]{'t','h','r','o','w'})),
		ThrowsKeywordToken(new CharSlice(new char[]{'t','h','r','o','w','s'})),
		NamespaceKeywordToken(new CharSlice(new char[]{'n','a','m','e','s','p','a','c','e'})),

		InKeywordToken(new CharSlice(new char[]{'i','n'})),
		OutKeywordToken(new CharSlice(new char[]{'o','u','t'})),
		SoKeywordToken(new CharSlice(new char[]{'s','o'})),
		OfKeywordToken(new CharSlice(new char[]{'o','f'})),
		ToKeywordToken(new CharSlice(new char[]{'t','o'})),

		LetKeywordToken(new CharSlice(new char[]{'l','e','t'})),
		TypeKeywordToken(new CharSlice(new char[]{'t','y','p','e'})),

		SuperKeywordToken(new CharSlice(new char[]{'s','u','p','e','r'})),
		BaseKeywordToken(new CharSlice(new char[]{'b','a','s','e'})),
		ThisKeywordToken(new CharSlice(new char[]{'t','h','i','s'})),
		SelfKeywordToken(new CharSlice(new char[]{'s','e','l','f'})),
		NextKeywordToken(new CharSlice(new char[]{'n','e','x','t'})),
		PreviousKeywordToken(new CharSlice(new char[]{'p','r','e','v','i','o','u','s'})),
		ReturnKeywordToken(new CharSlice(new char[]{'r','e','t','u','r','n'})),
		NewKeywordToken(new CharSlice(new char[]{'n','e','w'})),

		// Symbols:
		EqualSymbolOperatorToken(new CharSlice('=')),
		ParenthesisLeftSymbolOperatorToken(new CharSlice('(')),
		ParenthesisRightSymbolOperatorToken(new CharSlice(')')),
		SquareLeftSymbolOperatorToken(new CharSlice('[')),
		SquareRightSymbolOperatorToken(new CharSlice(']')),
		CurlyLeftSymbolOperatorToken(new CharSlice('{')),
		CurlyRightSymbolOperatorToken(new CharSlice('}')),
		AndSymbolOperatorToken(new CharSlice('&')),
		VerticalLineSymbolOperatorToken(new CharSlice('|')),
		NotSymbolOperatorToken(new CharSlice('!')),
		QuestionSymbolOperatorToken(new CharSlice('?')),
		CaretSymbolOperatorToken(new CharSlice('^')),

		PlusSymbolArithmeticalOperatorToken(new CharSlice('+')),
		MinusSymbolArithmeticalOperatorToken(new CharSlice('-')),
		StartSymbolArithmeticalOperatorToken(new CharSlice('*')),
		SlashSymbolArithmeticalOperatorToken(new CharSlice('/')),
		LessThanSymbolArithmeticalOperatorToken(new CharSlice('<')),
		GreaterThanSymbolArithmeticalOperatorToken(new CharSlice('>')),

		SemicolonSymbolDelimiterToken(new CharSlice(';')),
		ColonSymbolDelimiterToken(new CharSlice(':')),
		CommaSymbolDelimiterToken(new CharSlice(',')),
		DotSymbolDelimiterToken(new CharSlice('.')),
		AtSymbolDelimiterToken(new CharSlice('@')),
		LowLineSymbolDelimiterToken(new CharSlice('_')),
		TildeSymbolDelimiterToken(new CharSlice('~')),

		// Literals
		StringLiteralToken(new CharSlice(new char[] { 's', 't', 'r', 'i', 'n', 'g' }), new CharSlice(new char[] { 's', 't', 'r', 'i', 'n', 'g' })),
		StringBlockLiteralToken(new CharSlice(new char[] { 's', 't', 'r', 'i', 'n', 'g', '-', 'b', 'l', 'o', 'c', 'k' }), new CharSlice(new char[] { 's', 't', 'r', 'i', 'n', 'g', '-', 'b', 'l', 'o', 'c', 'k' })),
		CharacterLiteralToken(new CharSlice(new char[] { 'c', 'h', 'a', 'r', 'a', 'c', 't', 'e', 'r' }), new CharSlice(new char[] { 'c', 'h', 'a', 'r', 'a', 'c', 't', 'e', 'r' })),
		DigitLiteralToken(new CharSlice(new char[] { 'd', 'i', 'g', 'i', 't' }), new CharSlice(new char[] { 'd', 'i', 'g', 'i', 't' })),
		NumberLiteralToken(new CharSlice(new char[] { 'n', 'u', 'm', 'b', 'e', 'r' }), new CharSlice(new char[] { 'n', 'u', 'm', 'b', 'e', 'r' })),
		ByteLiteralToken(new CharSlice(new char[] { 'b', 't', 't', 'e' }), new CharSlice(new char[] { 'b', 't', 't', 'e' })),
		ShortLiteralToken(new CharSlice(new char[] { 's', 'h', 'o', 'r', 't' }), new CharSlice(new char[] { 's', 'h', 'o', 'r', 't' })),
		IntegerLiteralToken(new CharSlice(new char[] { 'i', 'n', 't', 'e', 'g', 'e', 'r' }), new CharSlice(new char[] { 'i', 'n', 't', 'e', 'g', 'e', 'r' })),
		LongLiteralToken(new CharSlice(new char[] { 'l', 'o', 'n', 'g' }), new CharSlice(new char[] { 'l', 'o', 'n', 'g' })),
		FloatLiteralToken(new CharSlice(new char[] { 'f', 'l', 'o', 'a', 't' }), new CharSlice(new char[] { 'f', 'l', 'o', 'a', 't' })),
		DoubleLiteralToken(new CharSlice(new char[] { 'd', 'o', 'u', 'b', 'l', 'e' }), new CharSlice(new char[] { 'd', 'o', 'u', 'b', 'l', 'e' })),

		TrueKeywordLiteralToken(new CharSlice(new char[]{'f','a','l','s','e'}), new CharSlice(new char[] { 'b', 'o', 'o', 'l', 'e', 'a', 'n' })),
		FalseKeywordLiteralToken(new CharSlice(new char[]{'t','r','u','e'}), new CharSlice(new char[] { 'b', 'o', 'o', 'l', 'e', 'a', 'n' })),
		IndeterminateKeywordLiteralToken(new CharSlice(new char[]{'i','n','d','e','t','e','r','m','i','n','a','t','e'}), new CharSlice(new char[] { 'b', 'o', 'o', 'l', 'e', 'a', 'n' })),
		UndefinedKeywordLiteralToken(new CharSlice(new char[]{'u','n','d','e','f','i','n','e','d'})),
		NaNKeywordLiteralToken(new CharSlice(new char[]{'N','a','N'})),
		NAKeywordLiteralToken(new CharSlice(new char[]{'N','A'})),
		NullKeywordLiteralToken(new CharSlice(new char[]{'n','u','l','l'})),
		NoneKeywordLiteralToken(new CharSlice(new char[]{'n','o','n','e'})),
		InfinityKeywordLiteralToken(new CharSlice(new char[]{'i','n','f','i','n','i','t','y'})),
		NeutralKeywordLiteralToken(new CharSlice(new char[]{'n','e','u','t','r','a','l'})),

		// Special:
		EndOfInputToken(new CharSlice('\3')),
		BadToken(new CharSlice('\0'));

		public CharSlice assignation;
		public CharSlice type;

		SyntaxType(CharSlice assignation) { this(assignation, new CharSlice(new char[0])); }

		SyntaxType(CharSlice assignation, CharSlice type) {
			this.assignation = assignation;
			this.type = type;
		}
	}
}
