package cappuccino.Tree;

public class CCTreeUtils {
	public static final int PARENT = 1 << 1;
	public static final int CHILDREN = 1 << 2;

	public static String getBuilderCompilationError(CCTreeAbstract parent, CCTreeAbstract current) {
		StringBuilder error = new StringBuilder();
		error.append("[Cappuccino Semantic] Name Mismatch (Syntax Error): Redefinition in the ");

		switch (parent.getKind()) {
			case CompilationUnit: {
				error.append("declaration of the name '")
						.append(current.name.value)
						.append("' at line: ")
						.append(current.name.line);
				error.append(" (previously declared at line ")
						.append(parent.name.line)
						.append(")\n");
				error.append("--------------------------------------\n");
				break;
			}
			case Variable: {
				error.append(getBuildVariableError(parent, current, PARENT, CHILDREN));
				break;
			}
			case SubVariable: {
				error.append(getBuildVariableError(parent, current, CHILDREN, CHILDREN));
				break;
			}
		}

		error.append("--------------------------------------\n");
		return error.toString();
	}

	private static String getBuildVariableError(CCTreeAbstract parent, CCTreeAbstract current, int flag, int flag2) {
		StringBuilder subError = new StringBuilder();

		// Original declaration info
		if ((flag & PARENT) != 0) subError.append("Variable ");
		else if ((flag & CHILDREN) != 0) subError.append("SubVariable ");

		subError.append("with the name '")
				.append(current.name.value)
				.append("' at line: ")
				.append(current.name.line)
				.append(" (previously declared at line ")
				.append(parent.name.line)
				.append(")\n")
				.append("--------------------------------------\n");

		// Show original code
		subError.append(buildVariableCode(parent, "Original Code"));

		// Show duplicated code
		subError.append(buildVariableCode(current, "Duplicated Code"));

		return subError.toString();
	}

	private static String buildVariableCode(CCTreeAbstract variable, String label) {
		StringBuilder code = new StringBuilder();

		if (variable instanceof CCTreeAbstract.CCTVariable) {
			CCTreeAbstract.CCTVariable var = (CCTreeAbstract.CCTVariable) variable;
			code.append("let ")
					.append(var.name.value)
					.append(": ")
					.append(var.type.value);

			if (var.literal.literal != null) {
				code.append(" = ").append(var.literal.literal.value);
			}

			// SubVariables if exist
			for (CCTreeAbstract.CCTVariable.CCTSubVariable subVar : var.subVariables) {
				code.append(", ").append(subVar.name.value);
				if (subVar.literal.literal != null) {
					code.append(" = ").append(subVar.literal.literal.value);
				}
			}

			code.append("; // ").append(label).append(", line: ").append(var.line).append("\n");
		}
		else if (variable instanceof CCTreeAbstract.CCTVariable.CCTSubVariable) {
			CCTreeAbstract.CCTVariable.CCTSubVariable subVar = (CCTreeAbstract.CCTVariable.CCTSubVariable) variable;
			code.append("let ")
					.append(subVar.name.value)
					.append(": ")
					.append(subVar.type.value);

			if (subVar.literal.literal != null) {
				code.append(" = ").append(subVar.literal.literal.value);
			}

			code.append("; // ").append(label).append(", line: ").append(subVar.line).append("\n");
		}

		return code.toString();
	}
}
