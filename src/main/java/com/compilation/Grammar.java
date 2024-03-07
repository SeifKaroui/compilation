package com.compilation;

import java.util.ArrayList;
import java.util.Arrays;

public class Grammar {
  private ArrayList<String> leftSides = new ArrayList<>();
  private ArrayList<String[]> rightSides = new ArrayList<>();

  public String getLeftSize(int rule) {

    return this.leftSides.get(rule);
  }

  public String[] getRightSide(int rule) {

    return this.rightSides.get(rule);
  }

  public int getRightSidesLength(int rule) {

    return this.rightSides.get(rule).length;
  }

  Grammar() {
    for (String rule : rules) {

      // System.out.println(rule);
      String[] a = rule.split("->");
      //
      String leftSide = a[0].replaceAll(" ", "");
      leftSides.add(leftSide);
      //
      String[] rightSide =
          Arrays.stream(a[1].split(" ")).filter(s -> !s.isEmpty()).toArray(String[]::new);
      rightSides.add(rightSide);
    }
  }

  public static final String[] rules = {
    "PROGRAM -> DEC LI", // 0
    "DEC -> var id : TYPE ; DEC", // 1
    "DEC -> ''", // 2
    "LI -> I LI", // 3
    "LI -> ''", // 4
    "I -> id := E ; ", // 5
    "I -> id [ SIZE ] := E ; ", // 6
    "I -> read ( id ) ; ", // 7
    "I -> write ( E ) ; ", // 8
    "I -> if E then LI endif ", // 9
    "I -> if E then LI else LI endif ", // 10
    "I -> while E do LI endwhile ", // 11
    "E -> E OpArith F", // 12
    "E -> E OpMod F", // 13
    "E -> E OpRel F", // 14
    "E -> E OpLogic F", // 15
    "E -> F", // 16
    "F -> ( E )", // 17
    "F -> id [ SIZE ]", // 18
    "F -> intLiteral ", // 19
    "F -> floatLiteral", // 20
    "F -> id", // 21
    "TYPE -> TS", // 22
    "TYPE -> array [ intLiteral ] of TS", // 23
    "TS -> int", // 24
    "TS -> float", // 25
    "SIZE -> intLiteral", // 26
    "SIZE -> id ", // 27
    "OpArith -> + ", // 28
    "OpArith -> - ", // 29
    "OpArith -> * ", // 30
    "OpArith -> / ", // 31
    "OpMod -> %", // 32
    "OpRel -> >= ", // 33
    "OpRel -> > ", // 34
    "OpRel -> <= ", // 35
    "OpRel -> <  ", // 36
    "OpRel -> = ", // 37
    "OpRel -> !=", // 38
    "OpLogic -> ||", // 39
    "OpLogic -> &&", // 40
  };
}
