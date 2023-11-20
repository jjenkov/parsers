# Parsers, Tokenizers etc.
This is a repository showing various parts parser and tokenizer designs and implementations. 

## Disclaimer:
I am not an expert in parsers or tokenizers - but I have written a few parsers over the years, and spent
a fair amount of time thinking about their design and implementation. So - while you may not learn how
to write the world's best parser and / or tokenizer, you can still learn to write some useful ones :-)

## Compilers, Parsers and Lexers / Tokenizers

A compiler is typically split up into multiple parts. A "frontend" consisting of the lexer / tokenizer
and the parser, and a "backend" consisting of a code / output generator.

First, the raw language file is passed to the lexer / tokenizer which breaks it up into smaller tokens.

Second, the sequence of tokens is passed to the parser which produces a logical representation of the
language statements and expressions made up by the tokens. This output is often an abstract syntax tree (AST)
or something similar to that. Something that is easy to traverse for the compiler.

Third, the compiler traverses the output from the parser and generates the final output - which could 
be an executable, a compiled class file or something else entirely.


## Tokenizers
So far I am playing with various tokenizer designs to explore different designs, and try to reason
about their characteristics (code size, performance etc.).


- [BasicTokenizer](https://github.com/jjenkov/parsers/blob/main/src/main/java/com/jenkov/parsers/tokenizers/BasicTokenizer.java)
- [BasicTokenizerMethodized](https://github.com/jjenkov/parsers/blob/main/src/main/java/com/jenkov/parsers/tokenizers/BasicTokenizerMethodized.java)



