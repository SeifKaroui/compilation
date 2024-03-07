package com.compilation.lexer;

public enum TokenType {
  Identifier,
  IntegerLiteral,
  FloatLiteral,
  //
  LeftParen, // (
  RightParen, // )
  LeftBrace, // ]
  RightBrace, // [
  Colon, // :
  Semicolon, // ;
  
  // Keywords
  Keyword_var,
  Keyword_array,
  Keyword_of,
  Keyword_if,
  Keyword_do,
  Keyword_else,
  Keyword_endif,
  Keyword_while,
  Keyword_endwhile,
  Keyword_read,
  Keyword_print,
  // Types
  Type_int,
  Type_float,

  // Operators
  Op_multiply,
  Op_divide,
  Op_mod,
  Op_add,
  Op_subtract,
  Op_less,
  Op_lessequal,
  Op_greater,
  Op_greaterequal,
  Op_equal,
  Op_notequal,
  Op_assign,
  Op_and,
  Op_or,
  //
  End_of_input,
}
