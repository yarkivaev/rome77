package semantic;

import ir.Expression;
import ir.Function;
import ir.Program;
import ir.Statement;
import ir.simple.IrDeclaration;
import ir.simple.IrOutput;
import ir.simple.IrProgram;
import syntax.SyntaxNode;
import syntax.SyntaxTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Semantic analyzer for Rome77 programs using two-pass analysis.
 *
 * Pass one collects function signatures for forward reference support.
 * Pass two builds IR with full semantic validation by delegating
 * to composable expression and function analysis objects.
 *
 * Example usage:
 * <pre>
 * SyntaxTree tree = new Rome77Syntax("Grafo V").parsed();
 * Program program = new Rome77Analyzer(tree).analyzed();
 * </pre>
 */
public final class Rome77Analyzer implements Analyzer {

    private final SyntaxTree tree;

    /**
     * Primary constructor.
     *
     * @param tree syntax tree to analyze
     */
    public Rome77Analyzer(final SyntaxTree tree) {
        this.tree = tree;
    }

    @Override
    public Program analyzed() throws SemanticException {
        final SyntaxNode root = this.tree.root();
        final Signatures sigs = new SignatureCollection(root).signatures();
        Scope scope = new AnalysisScope();
        final List<Function> functions = new ArrayList<>();
        final List<Statement> statements = new ArrayList<>();
        for (final SyntaxNode stmt : root.children()) {
            if (Objects.equals(stmt.name(), "EOF")) {
                break;
            }
            final List<SyntaxNode> children = new ArrayList<>();
            stmt.children().forEach(children::add);
            final SyntaxNode node = children.get(0);
            switch (node.name()) {
                case "functionDef":
                    functions.add(
                        new FunctionAnalysis(node, sigs, scope).function()
                    );
                    break;
                case "variableDecl":
                    final List<SyntaxNode> decl = new ArrayList<>();
                    node.children().forEach(decl::add);
                    final SyntaxNode nameNode = decl.get(1);
                    final String name = nameNode.text();
                    if (sigs.contains(name)) {
                        throw new SemanticException(
                            nameNode.line(),
                            nameNode.column(),
                            "Function " + name + " is already defined"
                        );
                    }
                    final Expression expr = new DispatchedExpression(
                        decl.get(3), sigs, scope
                    ).expression();
                    scope = scope.with(name, nameNode);
                    statements.add(new IrDeclaration(name, expr));
                    break;
                case "outputStmt":
                    final List<SyntaxNode> out = new ArrayList<>();
                    node.children().forEach(out::add);
                    statements.add(
                        new IrOutput(
                            new DispatchedExpression(
                                out.get(1), sigs, scope
                            ).expression()
                        )
                    );
                    break;
                default:
                    throw new SemanticException(
                        node.line(),
                        node.column(),
                        "Undefined statement: " + node.text()
                    );
            }
        }
        return new IrProgram(functions, statements);
    }
}
