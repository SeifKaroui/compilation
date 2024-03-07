package com.compilation.parser;

import com.compilation.Coloring;

/** SemanticError */
public class SemanticError {
  private int line;
  private int pos;
  private String message;

  public SemanticError(String message, int line, int pos) {
    this.message = message;
    this.line = line;
    this.pos = pos;
  }

  @Override
  public String toString() {
    if (pos == -1) {
      return Coloring.toRed("Error: ") + message;
    }
    return Coloring.toRed("Error: ")
        + message
        + Coloring.toBlue(" [line=" + line + ", pos=" + pos + "]");
  }
}
