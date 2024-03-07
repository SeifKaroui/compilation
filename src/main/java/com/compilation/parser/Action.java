package com.compilation.parser;

/** Action */
public class Action {

  public static class Reduce extends Action {
    public int rule;

    public Reduce(int rule) {
      this.rule = rule;
    }

    @Override
    public String toString() {
      return "Reduire par " + rule;
    }
  }

  public static class Shift extends Action {
    public int state;

    public Shift(int state) {
      this.state = state;
    }

    @Override
    public String toString() {
      return "Décalage par " + state;
    }
  }

  public static class Accept extends Action {
    @Override
    public String toString() {
      return "Accepter";
    }
  }

  public static class ErrorAction extends Action {
    @Override
    public String toString() {
      return "Erreur ( action non trouvé )";
    }
  }
}
