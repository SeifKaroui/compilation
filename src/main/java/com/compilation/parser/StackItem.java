package com.compilation.parser;

import com.compilation.lexer.Token;

public class StackItem {

  private final int itemId;
  private Object item;
  private SemanticType type;

  private StackItem(Object item, int type) {
    this.item = item;
    this.itemId = type;
    this.type = SemanticType.videType();
  }

  public SemanticType getType() {
    return type;
  }
  public void setType(SemanticType type) {
    this.type = type;
  }
  public static StackItem stateItem(int state) {
    return new StackItem(state, 0);
  }

  public static StackItem tokenItem(Token t) {
    return new StackItem(t, 1);
  }

  public static StackItem nonTerminalItem(String s) {
    return new StackItem(s, 2);
  }

  public int castAsState() {
    return (Integer) this.item;
  }

  public Token castAsToken() {
    return (Token) this.item;
  }

  public String castAsNonTerminal() {
    return (String) this.item;
  }

  @Override
  public String toString() {
    switch (itemId) {
      case 0:
        return Integer.toString((Integer) item);
      case 1:
        return ((Token) item).toStackString();
      default:
        return (String) item;
    }
  }
}
