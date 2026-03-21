package rome77.antlr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import syntax.SyntaxNode;

/**
 * Immutable SyntaxNode wrapper for ANTLR tree nodes.
 *
 * This final class adapts ANTLR's ParseTree nodes to the
 * immutable SyntaxNode interface. Supports both rule and terminal nodes.
 *
 * Example usage:
 * <pre>
 * SyntaxNode node = new AntlrNode(parseTree, parser);
 * </pre>
 */
public final class AntlrNode implements SyntaxNode {

    /**
     * Wrapped ANTLR parse tree.
     */
    private final ParseTree tree;

    /**
     * Parser for rule name lookup.
     */
    private final Parser parser;

    /**
     * Primary constructor.
     *
     * @param pt ANTLR parse tree node
     * @param prs Parser instance for rule names
     */
    public AntlrNode(final ParseTree pt, final Parser prs) {
        this.tree = pt;
        this.parser = prs;
    }

    @Override
    public String name() {
        final String result;
        if (this.tree instanceof ParserRuleContext) {
            final String classname = this.tree.getClass().getSimpleName();
            final String suffix = "Context";
            final String stripped;
            if (classname.endsWith(suffix)) {
                stripped = classname.substring(0, classname.length() - suffix.length());
            } else {
                stripped = classname;
            }
            if (stripped.isEmpty()) {
                result = stripped;
            } else {
                result = Character.toLowerCase(stripped.charAt(0)) + stripped.substring(1);
            }
        } else if (this.tree instanceof TerminalNode) {
            final int type = ((TerminalNode) this.tree).getSymbol().getType();
            result = this.parser.getVocabulary().getSymbolicName(type);
        } else {
            result = this.tree.getClass().getSimpleName();
        }
        return result;
    }

    @Override
    public String text() {
        return this.tree.getText();
    }

    @Override
    public int line() {
        final int result;
        if (this.tree instanceof ParserRuleContext) {
            result = ((ParserRuleContext) this.tree).getStart().getLine();
        } else if (this.tree instanceof TerminalNode) {
            result = ((TerminalNode) this.tree).getSymbol().getLine();
        } else {
            result = 1;
        }
        return result;
    }

    @Override
    public int column() {
        final int result;
        if (this.tree instanceof ParserRuleContext) {
            result = ((ParserRuleContext) this.tree).getStart().getCharPositionInLine();
        } else if (this.tree instanceof TerminalNode) {
            result = ((TerminalNode) this.tree).getSymbol().getCharPositionInLine();
        } else {
            result = 0;
        }
        return result;
    }

    @Override
    public Iterable<SyntaxNode> children() {
        final int count = this.tree.getChildCount();
        final List<SyntaxNode> nodes = new ArrayList<>(count);
        for (int idx = 0; idx < count; idx = idx + 1) {
            nodes.add(new AntlrNode(this.tree.getChild(idx), this.parser));
        }
        return Collections.unmodifiableList(nodes);
    }
}
