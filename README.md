# Rome77 Compiler

A compiler for Rome77, a programming language that uses Roman numerals and Latin-inspired keywords.

## Language Features

| Feature | Syntax | Example |
|---------|--------|---------|
| Roman numerals | `[MDCLXVI]+` or `N` for zero | `XIV`, `MMXXIV`, `N` |
| Variables | `As name = expr` | `As x = V` |
| Input | `Anagnosi` | `As n = Anagnosi` |
| Output | `Grafo expr` | `Grafo x` |
| Functions | `Munus name params = body` | `Munus sum a b = a + b` |
| Conditional | `Sinon cond then else` | `Sinon n I II` |
| Arithmetic | `+ - * /` | `a + b * c` |

## Example Program

Fibonacci sequence:

```
Munus fib n = Sinon n I ((fib n - I) + (fib n - II))
As n = Anagnosi
Grafo fib n
```

## Processing Pipeline

```
Source → Lexer → Syntax → Analyzer → IR Program ─┬→ Interpreter
                                                  └→ LLVM IR → Executable
```

1. **Lexical analysis** — `Lexer` tokenizes source code into an immutable `Listing` of `Token` objects, each classified by `TokenCategory`
2. **Syntactic analysis** — `Syntax` parses the token stream into an immutable `SyntaxTree` of `SyntaxNode` elements
3. **Semantic analysis** — `Analyzer` validates the syntax tree (symbol resolution, arity checking) and produces an IR `Program` with `Function` and `Statement` definitions
4. **Interpretation** — IR `Program` is walked and evaluated directly at runtime
5. **Compilation** — IR `Program` is translated into LLVM IR and compiled into a native executable

## Build

```bash
mvn clean test
```

## Architecture

```
src/main/java/
├── parsing/     # ParsingException
├── lexical/     # Token, Listing, Lexer, LexicalException, TokenCategory
├── syntax/      # Syntax, SyntaxException, SyntaxNode, SyntaxTree
├── semantic/    # Analyzer, SemanticException
├── ir/          # Expression, Statement, Program, Function, and related interfaces
├── ir/simple/   # Simple implementations of IR interfaces
└── rome77/
    └── antlr/   # Rome77Syntax, Rome77Lexer, AntlrToken, AntlrListing, AntlrTree, AntlrNode, Rome77Errors
```
