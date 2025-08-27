package cappuccino.Tree;

public class CCTreeUtils {
	public static final int PARENT = 1 << 1;
	public static final int CHILDREN = 1 << 2;

	public static String getBuilderCompilationError(CCTStatementAbstract parent, CCTStatementAbstract current) {
		StringBuilder error = new StringBuilder();
		error.append("[Cappuccino Semantic] Name Mismatch (Syntax Error): Redefinition in the ");

		switch (parent.getKind()) {
			case CompilationUnit: case Block: {
				error.append("declaration of the name '")
						.append(current.name.value)
						.append("' at line: ")
						.append(current.name.line);
				error.append(" (previously declared at line ");
				error.append(parent.line)
						.append(")\n");
				error.append("--------------------------------------\n");
				break;
			}
			case Variable: {
				error.append(getBuildVariableError(parent, current, PARENT));
				break;
			}
			case SubVariable: {
				error.append(getBuildVariableError(parent, current, CHILDREN));
				break;
			}
		}

		error.append("--------------------------------------\n");
		return error.toString();
	}

	private static String getBuildVariableError(CCTStatementAbstract parent, CCTStatementAbstract current, int flag) {
		StringBuilder subError = new StringBuilder();

		if ((flag & PARENT) != 0) subError.append("Variable ");
		else if ((flag & CHILDREN) != 0) subError.append("SubVariable ");

		subError.append("with the name '")
				.append(current.name.value)
				.append("' at line: ")
				.append(current.line)
				.append(" (previously declared at line ")
				.append(parent.line)
				.append(")\n")
				.append("--------------------------------------\n");

		subError.append(buildVariableCode(parent, "Original Code"));

		subError.append(buildVariableCode(current, "Duplicated Code"));

		return subError.toString();
	}

	private static String buildVariableCode(CCTreeAbstract variable, String label) {
		StringBuilder code = new StringBuilder();

		if (variable instanceof CCTVariableStatement.CCTSubVariableStatement) {
			CCTVariableStatement.CCTSubVariableStatement subVar = (CCTVariableStatement.CCTSubVariableStatement) variable;
			code.append("sublet ")
					.append(subVar.name.value)
					.append(": ")
					.append(subVar.type.value);

			if (subVar.literal.literal != null) {
				code.append(" = ").append(subVar.literal.literal.value);
			}

			code.append("; // ").append(label).append(", line: ").append(subVar.line).append("\n");
		} else if (variable instanceof CCTVariableStatement) {
			CCTVariableStatement var = (CCTVariableStatement) variable;
			if (var.isSubLet) code.append("sub");
			code.append("let ")
					.append(var.name.value)
					.append(": ")
					.append(var.type.value);

			if (var.literal.literal != null) {
				code.append(" = ").append(var.literal.literal.value);
			}

			// SubVariables if exist
			for (CCTVariableStatement.CCTSubVariableStatement subVar : var.subVariables) {
				code.append(", ").append(subVar.name.value);
				if (subVar.literal.literal != null) {
					code.append(" = ").append(subVar.literal.literal.value);
				}
			}

			code.append("; // ").append(label).append(", line: ").append(var.line).append("\n");
		}

		return code.toString();
	}
}
