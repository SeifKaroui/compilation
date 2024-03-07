package com.compilation.lexer;

import com.compilation.Coloring;

public class Token {
  public TokenType tokentype;
  public String value;
  public int line;
  public int pos;

  public Token(TokenType token, String value, int line, int pos) {
    this.tokentype = token;
    this.value = value;
    this.line = line;
    this.pos = pos;
  }

  @Override
  public String toString() {
    String result = String.format("%-5s", this.line);
    String result1 = String.format("  %-5d  ", this.pos);
    String result2 = String.format(" %-20s", this.tokentype);
    switch (this.tokentype) {
      default:
        result2 += String.format(" %s", value);
        break;
    }
    return Coloring.toYellow(result)+ Coloring.toCyan(result1) + result2;
  }

  public String toStackString() {
    switch (this.tokentype) {
      case Identifier:
        return "id";
      case IntegerLiteral:
        return value;
        // return "intLit";
      case FloatLiteral:
        // return "floatLi";
        return value;
      default:
        return this.value;
    }
  }
}
