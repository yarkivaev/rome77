package semantic;

import ir.Expression;
import ir.Function;
import ir.Operator;
import ir.Program;
import ir.Statement;
import ir.simple.IrBinaryOp;
import ir.simple.IrCall;
import ir.simple.IrConditional;
import ir.simple.IrDeclaration;
import ir.simple.IrFunction;
import ir.simple.IrInput;
import ir.simple.IrLiteral;
import ir.simple.IrOutput;
import ir.simple.IrProgram;
import ir.simple.IrUnaryOp;
import ir.simple.IrVariable;
import syntax.SyntaxException;
import syntax.SyntaxNode;
import syntax.SyntaxTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Rome77Analyzer implements Analyzer {

    private final SyntaxTree tree;

    /**
     * Constructor for successful analysis.
     *
     * @param tree Syntax tree to analyze
     */
    Rome77Analyzer(final SyntaxTree tree) {
        this.tree = tree;
    }

    /**
     * Performs full semantic analysis of the Rome77 program.
     * <p>
     * The analysis consists of two passes:
     * <ul>
     *     <li><b>Pass 1:</b> Collect all function signatures (name + arity)</li>
     *     <li><b>Pass 2:</b> Build IR and validate semantic correctness</li>
     * </ul>
     * <p>
     * During analysis the following checks are performed:
     * <ul>
     *     <li>Duplicate function definitions</li>
     *     <li>Duplicate variable declarations</li>
     *     <li>Function existence and correct arity</li>
     *     <li>Variable scope resolution</li>
     * </ul>
     *
     * @return fully constructed IR program
     * @throws SemanticException if any semantic error is encountered
     */
    @Override
    public Program analyzed() {

        SyntaxNode root = this.tree.root();

        // === PASS 1: собираем функции ===
        Map<String, Integer> functionParamsCount = collectFunctions(root);

//        // === PASS 2: строим IR ===
        List<Function> functions = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        Set<String> variables = new HashSet<>();
        for (SyntaxNode stmt : root.children()) {
            if (Objects.equals(stmt.name(), "EOF")) {
                break;
            }
            SyntaxNode node = child(stmt, 0);
            switch (node.name()) {
                case "functionDef":
                    functions.add(analyzeFunction(node, functionParamsCount, variables));
                    break;
                case "variableDecl":
                    statements.add(analyzeVar(node, functionParamsCount, variables));
                    break;

                case "outputStmt":
                    statements.add(analyzeOutput(node, functionParamsCount, variables));
                    break;

                default:
                    semanticException(node, "Undefined statement: " + node.text());
            }
        }

        return new IrProgram(functions, statements);
    }

    /**
     * First pass: collects all function declarations and their arity.
     * <p>
     * Only function signatures are collected here, without analyzing bodies.
     * This allows forward references and recursion.
     *
     * @param root root node of the syntax tree
     * @return map of function name to parameter count
     * @throws SemanticException if duplicate function definitions are found
     */
    private Map<String, Integer> collectFunctions(SyntaxNode root) {
        Map<String, Integer> functionParamsCount = new HashMap<>();
        for (SyntaxNode stmt : root.children()) {
            if (stmt.name().equals("statement")) {
                SyntaxNode node = child(stmt, 0);
                if (node.name().equals("functionDef")) {
                    String name = getFunctionName(node);

                    if (functionParamsCount.containsKey(name)) {
                        semanticException(node, "Function " + name + " is already defined");
                    }

                    int arity = getParamCount(node);

                    functionParamsCount.put(name, arity);
                }
            }
        }
        return functionParamsCount;
    }

    private static String getFunctionName(SyntaxNode node) {
        for (SyntaxNode child : node.children()) {
            if (child.name().equals("IDENTIFIER")) {
                return child.text();
            }
        }
        return null;
    }

    private int getParamCount(SyntaxNode node) {
        for (SyntaxNode child : node.children()) {
            if (child.name().equals("params")) {
                return child.children().size();
            }
        }
        return 0;
    }

    /**
     * Analyzes a function definition node and produces IR representation.
     * <p>
     * Extracts:
     * <ul>
     *     <li>Function name</li>
     *     <li>Parameter list</li>
     *     <li>Body expression</li>
     * </ul>
     * <p>
     * Scope rules:
     * <ul>
     *     <li>Only parameters are visible inside function body</li>
     *     <li>Global variables are NOT accessible inside functions</li>
     * </ul>
     *
     * @param node      functionDef syntax node
     * @param functions known function signatures
     * @param variables global variables (used only for validation conflicts)
     * @return IR function
     * @throws SemanticException if semantic violations occur
     */
    private Function analyzeFunction(SyntaxNode node, Map<String, Integer> functions, Set<String> variables) {
        String name = null;
        List<String> params = Collections.emptyList();
        SyntaxNode expressionNode = null;
        for (SyntaxNode child : node.children()) {
            switch (child.name()) {
                case "MUNUS":
                case "EQUALS":
                    //Ignore
                    break;
                case "IDENTIFIER":
                    name = child.text();
                    if (variables.contains(name)) {
                        semanticException(child, "Variable " + name + " is already defined");
                    }
                    break;
                case "params":
                    params = extractParams(child);
                    break;
                default:
                    expressionNode = child;
            }
        }
        if (expressionNode == null) {
            semanticException(node, "Function body not defined");
        }
        HashSet<String> localVariables = new HashSet<>(params);
//        localVariables.addAll(variables); //Должны ли попадать в метод объявленные ранее пременные?

        Expression expression = analyzeExpr(expressionNode, functions, localVariables);

        return new IrFunction(name, params, expression);
    }

    /**
     * Analyzes a variable declaration statement.
     * <p>
     * Format:
     * <pre>
     * As name = expression
     * </pre>
     * <p>
     * Rules:
     * <ul>
     *     <li>Variable name must not conflict with function names</li>
     *     <li>Variable cannot be redefined</li>
     *     <li>Right-hand side is evaluated in current variable scope</li>
     *     <li>Variable becomes visible only after its declaration</li>
     * </ul>
     *
     * @param node                variableDecl syntax node
     * @param functionParamsCount known functions
     * @param variables           currently defined variables (mutable scope)
     * @return IR declaration
     */
    private Statement analyzeVar(SyntaxNode node, Map<String, Integer> functionParamsCount, Set<String> variables) {
        // As IDENTIFIER = expr

        SyntaxNode nameNode = child(node, 1);
        String name = nameNode.text();
        if (functionParamsCount.containsKey(name)) {
            semanticException(nameNode, "Function " + name + " is already defined");
        }
        if (variables.contains(name)) {
            semanticException(nameNode, "Variable " + name + " is already defined");
        }
        SyntaxNode exprNode = child(node, 3);

        // анализ выражения (можно использовать уже объявленные переменные)
        Expression expr = analyzeExpr(exprNode, functionParamsCount, new HashSet<>(variables));

        // добавляем переменную в scope (после анализа RHS!)
        variables.add(name);

        return new IrDeclaration(name, expr);
    }

    /**
     * Analyzes an output statement.
     * <p>
     * Format:
     * <pre>
     * Grafo expression
     * </pre>
     *
     * @param node                outputStmt syntax node
     * @param functionParamsCount known functions
     * @param variables           current variable scope
     * @return IR output statement
     */
    private Statement analyzeOutput(SyntaxNode node, Map<String, Integer> functionParamsCount, Set<String> variables) {
        // children: [GRAFO, expr]
        SyntaxNode exprNode = child(node, 1);

        Expression expr = analyzeExpr(exprNode, functionParamsCount, variables);

        return new IrOutput(expr);
    }

    private List<String> extractParams(SyntaxNode node) {
        List<String> params = new ArrayList<>(node.children().size());
        for (SyntaxNode child : node.children()) {
            if (child.name().equals("IDENTIFIER")) {
                String name = child.text();
                if (params.contains(name)) {
                    semanticException(child, "Duplicate parameter name: " + name);
                }
                params.add(name);
            }
        }
        return params;
    }

    /**
     * Recursively analyzes an expression node and produces IR.
     * <p>
     * Supports:
     * <ul>
     *     <li>Conditional expressions (Sinon)</li>
     *     <li>Function calls</li>
     *     <li>Arithmetic operations</li>
     *     <li>Unary operations</li>
     *     <li>Variables</li>
     *     <li>Roman literals</li>
     *     <li>Input (Anagnosi)</li>
     * </ul>
     * <p>
     * Semantic checks:
     * <ul>
     *     <li>Function existence</li>
     *     <li>Correct number of arguments</li>
     *     <li>Variable defined in scope</li>
     * </ul>
     *
     * @param node      expression syntax node
     * @param functions map of known functions and their arity
     * @param scope     visible variables in current context
     * @return IR expression
     * @throws SemanticException if semantic errors are found
     */
    private Expression analyzeExpr(SyntaxNode node, Map<String, Integer> functions, Set<String> scope) {
        switch (node.name()) {

            case "conditional":
                return new IrConditional(
                        analyzeExpr(child(node, 1), functions, scope),
                        analyzeExpr(child(node, 2), functions, scope),
                        analyzeExpr(child(node, 3), functions, scope)
                );

            case "funcCall":
                String functionName = child(node, 0).text();

                List<Expression> args = new ArrayList<>();
                for (int i = 1; i < node.children().size(); i++) {
                    args.add(analyzeExpr(child(node, i), functions, scope));
                }

                // проверка функции
                Integer arity = functions.get(functionName);
                if (arity == null) {
                    semanticException(node, "Undefined function: " + functionName);
                }
                if (arity != args.size()) {
                    semanticException(node, "Function " + functionName + " expects " + arity + " arguments, got " + args.size());
                }

                return new IrCall(functionName, args);

            case "addSub":
            case "mulDiv":
                return new IrBinaryOp(
                        mapOperator(child(node, 1)),
                        analyzeExpr(child(node, 0), functions, scope),
                        analyzeExpr(child(node, 2), functions, scope)
                );

            case "unaryOp":
                return new IrUnaryOp(
                        mapOperator(child(node, 0)),
                        analyzeExpr(child(node, 1), functions, scope)
                );

            case "romanLiteral":
                return new IrLiteral(parseRoman(node));

            case "variable":
                String variableName = node.text();

                if (!scope.contains(variableName)) {
                    Integer functionArity = functions.get(variableName);
                    if (functionArity != null) {
                        semanticException(node, "Function " + variableName + " expects " + functionArity + " arguments, got 0");
                    }
                    semanticException(node, "Undefined variable: " + variableName);
                }

                return new IrVariable(variableName);

            case "readInput":
                return new IrInput();

            default:
                // unwrap (toMult, toUnary, parens и т.д.)
                return analyzeExpr(unwrap(node), functions, scope);
        }
    }

    /**
     * Maps syntax token to IR operator.
     *
     * @param node operator token node
     * @return corresponding Operator enum
     * @throws SyntaxException if unknown operator is encountered
     */
    private Operator mapOperator(SyntaxNode node) {
        return switch (node.name()) {
            case "PLUS" -> Operator.ADD;
            case "MINUS" -> Operator.SUB;
            case "MULT" -> Operator.MUL;
            case "DIV" -> Operator.DIV;
            default -> throw new SyntaxException(node.line(), node.column(), "Undefined operator: " + node.text());
        };
    }

    /**
     * Converts a Roman numeral into an integer value.
     * <p>
     * Rules:
     * <ul>
     *     <li>'N' represents zero</li>
     *     <li>Standard subtractive notation is supported (IV, IX, etc.)</li>
     *     <li>Invalid characters cause semantic error</li>
     *     <li>Result is clamped to non-negative values</li>
     * </ul>
     *
     * @param node ROMAN syntax node
     * @return integer value (>= 0)
     * @throws SemanticException if invalid Roman numeral is encountered
     */
    private int parseRoman(SyntaxNode node) {
        String roman = node.text();
        if (roman.equals("N")) {
            return 0;
        }

        int result = 0;
        int prev = 0;

        for (int i = roman.length() - 1; i >= 0; i--) {
            char romanChar = roman.charAt(i);
            Integer value = romanValue(romanChar);

            if (value == null) {
                semanticException(node, "Invalid roman digit: " + romanChar);
            }

            if (value < prev) {
                result -= value;
            } else {
                result += value;
                prev = value;
            }
        }

        return Math.max(result, 0);
    }

    private Integer romanValue(char romanChar) {
        return switch (romanChar) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> null;
        };
    }

    /**
     * Removes intermediate grammar nodes introduced by operator precedence rules.
     * <p>
     * ANTLR generates wrapper nodes like:
     * <ul>
     *     <li>toMult</li>
     *     <li>toUnary</li>
     *     <li>Arithmetic</li>
     * </ul>
     * <p>
     * This method unwraps them to reach the actual semantic node.
     * <p>
     * Also handles parentheses:
     * (expr) → expr
     *
     * @param node syntax node
     * @return unwrapped meaningful node
     */
    private SyntaxNode unwrap(SyntaxNode node) {
        while (true) {
            String name = node.name();

            if (name.equals("toMult")
                    || name.equals("multiplicative")
                    || name.equals("toUnary")
                    || name.equals("toPrimary")
                    || name.equals("arithmetic")) {

                node = child(node, 0);
                continue;
            }

            if (name.equals("parens")) {
                // ( expr ) → expr
                return child(node, 1);
            }

            return node;
        }
    }

    private SyntaxNode child(SyntaxNode node, int i) {
        return node.children().get(i);
    }

    /**
     * Throws a semantic exception at given node position.
     *
     * @param node syntax node where error occurred
     * @param message error description
     * @throws SemanticException always
     */
    private void semanticException(SyntaxNode node, String message) {
        throw new SemanticException(node.line(), node.column(), message);
    }
}
