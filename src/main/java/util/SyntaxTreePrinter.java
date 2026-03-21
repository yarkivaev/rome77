package util;

import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreePrinter {

    public static void printTree(SyntaxNode root) {
        printTree(root, "", true);
    }

    private static void printTree(SyntaxNode node, String prefix, boolean isLast) {
        if (node == null) return;

        System.out.println(prefix
                + (isLast ? "└── " : "├── ")
                + "name: " + node.name() + ", text: " + node.text() + ", line: " + node.line() + ", column: " + node.column());

        List<SyntaxNode> children = new ArrayList<>();
        node.children().forEach(children::add);
        for (int i = 0; i < children.size(); i++) {
            boolean last = (i == children.size() - 1);
            printTree(children.get(i),
                    prefix + (isLast ? "    " : "│   "),
                    last);
        }
    }
}
