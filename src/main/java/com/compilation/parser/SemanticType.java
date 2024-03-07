package com.compilation.parser;

/** SemanticType */
public class SemanticType {
  static final int INT_ID = 0;
  static final int FLOAT_ID = 1;
  static final int ARRAY_ID = 2;
  static final int VOID_ID = 3;
  static final int ERROR_ID = 4;

  static final SemanticType intType = new SemanticType(INT_ID);
  static final SemanticType floatType = new SemanticType(FLOAT_ID);
  static final SemanticType videType = new SemanticType(VOID_ID);
  static final SemanticType errorType = new SemanticType(ERROR_ID);

  final int typeId;
  final int size;
  final int contentType;

  private SemanticType(int id, int size, int contentType) {
    this.typeId = id;
    this.size = size;
    this.contentType = contentType;
  }

  private SemanticType(int id) {
    this.typeId = id;
    this.size = 0;
    this.contentType = 0;
  }

  static   public SemanticType intType() {
    return intType;
  }

  static public SemanticType floatType() {
    return floatType;
  }

  static public SemanticType videType() {
    return videType;
  }

  static public SemanticType errorType() {
    return errorType;
  }

  static public SemanticType intArrayType(int size) {
    return new SemanticType(ARRAY_ID, size, INT_ID);
  }

  static public SemanticType floatArrayType(int size) {
    return new SemanticType(ARRAY_ID, size, FLOAT_ID);
  }

  public SemanticType getArrayContentType() {
    if (typeId == ARRAY_ID) {
      if (contentType == INT_ID) {
        return SemanticType.intType();
      } else {

        return SemanticType.floatType();
      }
    } else {
      System.err.println("Must be an array");
      return SemanticType.intType();
    }
  }

  public boolean isIntType() {
    return typeId == INT_ID;
  }

  public boolean isFloatType() {
    return typeId == FLOAT_ID;
  }

  public boolean isArrayType() {
    return typeId == ARRAY_ID;
  }

  public boolean isVoidType() {
    return typeId == VOID_ID;
  }

  public boolean isErrorType() {
    return typeId == ERROR_ID;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof SemanticType)) {
      return false;
    }

    SemanticType other = (SemanticType) obj;

    if (this.typeId != 2) {
      return this.typeId == other.typeId;

    } else {
      return this.size == other.size && this.contentType == other.contentType;
    }
  }

  @Override
  public String toString() {
    if (this.typeId == INT_ID) {
      return "Int";
    } else if (this.typeId == FLOAT_ID) {

      return "Float";

    } else if (this.typeId == VOID_ID) {

      return "Void";
    } else if (this.typeId == ERROR_ID) {

      return "Erreur";
    } else {
      if (contentType == INT_ID) {

        return "Tableau(size=" + size + ", Int)";
      } else {

        return "Tableau(size=" + size + ", Float)";
      }
    }
  }
}
