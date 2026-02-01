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

## Build

```bash
mvn clean test
```

## Architecture

```
src/main/java/
├── fp/          # Functional primitives (Either, Left, Right)
├── syntax/      # Syntax tree interfaces (SyntaxTree, SyntaxNode)
└── rome77/      # Rome77-specific parser and ANTLR integration
```
