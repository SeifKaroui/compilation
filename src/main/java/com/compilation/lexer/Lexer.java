package com.compilation.lexer;

import com.compilation.Coloring;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
  private int line;
  private int pos;
  private int position;
  private char chr;
  private String s;
  private int nbErrors;
  Map<String, TokenType> keywords = new HashMap<>();
  private ArrayList<Token> tokens = new ArrayList<>();
  private ArrayList<String> erreurs = new ArrayList<>();

  public Lexer(String source) {
    this.line = 1;
    this.pos = 0;
    this.position = 0;
    this.s = source;
    this.chr = this.s.charAt(0);
    this.nbErrors = 0;
    this.keywords.put("var", TokenType.Keyword_var);
    this.keywords.put("array", TokenType.Keyword_array);
    this.keywords.put("of", TokenType.Keyword_of);
    this.keywords.put("if", TokenType.Keyword_if);
    this.keywords.put("do", TokenType.Keyword_do);
    this.keywords.put("else", TokenType.Keyword_else);
    this.keywords.put("endif", TokenType.Keyword_endif);
    this.keywords.put("while", TokenType.Keyword_while);
    this.keywords.put("endwhile", TokenType.Keyword_endwhile);
    this.keywords.put("read", TokenType.Keyword_read);
    this.keywords.put("print", TokenType.Keyword_print);
    this.keywords.put("int", TokenType.Type_int);
    this.keywords.put("float", TokenType.Type_float);
  }

  char getNextChar() {
    this.pos++;
    this.position++;
    if (this.position >= this.s.length()) {
      this.chr = '\u0000';
      return this.chr;
    }
    this.chr = this.s.charAt(this.position);
    if (this.chr == '\n') {
      this.line++;
      this.pos = 0;
    }
    return this.chr;
  }

  Token getToken() {
    int line, pos;
    while (Character.isWhitespace(this.chr)) {
      getNextChar();
    }
    line = this.line;
    pos = this.pos;

    switch (this.chr) {
      case '\u0000':
        return new Token(TokenType.End_of_input, "$", this.line, this.pos);
      case '/':
        getNextChar();
        return new Token(TokenType.Op_divide, "", line, pos);
      case '<':
        return follow('=', TokenType.Op_lessequal, TokenType.Op_less, "<=", "<", line, pos);
      case '>':
        return follow('=', TokenType.Op_greaterequal, TokenType.Op_greater, ">=", ">", line, pos);
      case '!':
        return follow('=', TokenType.Op_notequal, TokenType.End_of_input, "!=", "", line, pos);
      case '&':
        return follow('&', TokenType.Op_and, TokenType.End_of_input, "&&", "", line, pos);
      case '|':
        return follow('|', TokenType.Op_or, TokenType.End_of_input, "||", "", line, pos);
      case ':':
        return follow('=', TokenType.Op_assign, TokenType.Colon, ":=", ":", line, pos);
      case '=':
        getNextChar();
        return new Token(TokenType.Op_equal, "=", line, pos);
      case '{':
        getNextChar();
        return new Token(TokenType.LeftBrace, "{", line, pos);
      case '}':
        getNextChar();
        return new Token(TokenType.RightBrace, "}", line, pos);
      case '(':
        getNextChar();
        return new Token(TokenType.LeftParen, "(", line, pos);
      case ')':
        getNextChar();
        return new Token(TokenType.RightParen, ")", line, pos);
      case '[':
        getNextChar();
        return new Token(TokenType.LeftBrace, "[", line, pos);
      case ']':
        getNextChar();
        return new Token(TokenType.RightBrace, "]", line, pos);
      case '+':
        getNextChar();
        return new Token(TokenType.Op_add, "+", line, pos);
      case '-':
        getNextChar();
        return new Token(TokenType.Op_subtract, "-", line, pos);
      case '*':
        getNextChar();
        return new Token(TokenType.Op_multiply, "*", line, pos);
      case '%':
        getNextChar();
        return new Token(TokenType.Op_mod, "%", line, pos);
      case ';':
        getNextChar();
        return new Token(TokenType.Semicolon, ";", line, pos);

      default:
        return identifier_or_integer_or_float(line, pos);
    }
  }

  Token follow(
      char expect,
      TokenType ifyes,
      TokenType ifno,
      String valueYes,
      String valueNo,
      int line,
      int pos) {
    if (getNextChar() == expect) {
      getNextChar();
      return new Token(ifyes, valueYes, line, pos);
    }
    if (ifno == TokenType.End_of_input) {
      error(line, pos, String.format("caractere non reconnu:  '%c'",  this.chr));
    }
    return new Token(ifno, valueNo, line, pos);
  }

  void error(int line, int pos, String msg) {
    this.nbErrors++;
    if (line > 0 && pos > 0) {
      erreurs.add(
          Coloring.toRed("Erreur: ")
              + String.format("%s dans la ligne %d, pos %d", msg, line, pos));
    } else {
      System.out.println(Coloring.toRed("Erreur: ") + msg);
    }
    getNextChar();
    // System.exit(1);
  }

  Token identifier_or_integer_or_float(int line, int pos) {
    boolean is_integer = true;
    boolean is_float = true;
    String text = "";

    while (Character.isAlphabetic(this.chr)
        || Character.isDigit(this.chr)
        || this.chr == '_'
        || this.chr == '.') {
      text += this.chr;
      if (!Character.isDigit(this.chr)) {
        is_integer = false;
        if (this.chr != '.') {
          is_float = false;
        }
      }
      getNextChar();
    }

    if (text.equals("")) {
      error(line, pos, String.format("caractere non reconnu: %c",  this.chr));
      return null;
    }

    if (Character.isDigit(text.charAt(0))) {
      if (is_integer) {
        return new Token(TokenType.IntegerLiteral, text, line, pos);
      } else {
        if (is_float) {

          int firstPoint = text.indexOf('.');
          int secondPoint = text.indexOf('.', firstPoint + 1);
          if (firstPoint > 0 && secondPoint == -1) {
            return new Token(TokenType.FloatLiteral, text, line, pos);
          }
          error(line, pos, String.format("Unité lexicale non reconnue: %s", Coloring.toRed(text)));
        } else {

          error(line, pos, String.format("Unité lexicale non reconnue: %s", Coloring.toRed(text)));
        }
      }
    }

    if (this.keywords.containsKey(text)) {
      return new Token(this.keywords.get(text), text, line, pos);
    }
    return new Token(TokenType.Identifier, text, line, pos);
  }

  public ArrayList<Token> generateTokens() {

    boolean stop = false;
    Token t;
    while (!stop) {
      t = getToken();
      if (t != null) {
        tokens.add(t);
        if (t.tokentype == TokenType.End_of_input) {
          stop = true;
        }
      }
    }
    return this.tokens;
  }

  public void printOutput() {

    String header = String.format("%-5s  %-5s   %-20s", "Ligne", "Pos", "Categorie", "Valeur");

    System.out.println(Coloring.toBlue(header));
    for (Token t : this.tokens) {
      System.out.println(t);
    }

    System.out.println();
    if (this.nbErrors > 0) {
      for (String err : this.erreurs) {
        System.out.println(err);
      }
      System.out.println(Coloring.toRed("Nombre totale d'erreurs lexicales: ") + this.nbErrors);
    } else {
      System.out.println(Coloring.toGreen("L'analyse lexicale est  terminée avec succès."));
    }
  }
}
