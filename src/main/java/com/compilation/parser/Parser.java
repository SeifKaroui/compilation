package com.compilation.parser;

import com.compilation.Coloring;
import com.compilation.Grammar;
import com.compilation.lexer.Token;
import com.compilation.lexer.TokenType;
import com.compilation.parser.Action.Accept;
import com.compilation.parser.Action.Reduce;
import com.compilation.parser.Action.Shift;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

public class Parser {

  private Stack<StackItem> excutionStack = new Stack<StackItem>();
  private Stack<Token> inputStack = new Stack<Token>();
  private ArrayList<String> nonterminals = new ArrayList<>();
  private ArrayList<SemanticError> semanticErrors = new ArrayList<>();
  private ArrayList<SyntaxicError> syntaxicErrors = new ArrayList<>();
  private HashMap<String, SemanticType> idMap = new HashMap<>();
  private Stack<String> idStack = new Stack<>();
  private Action currentAction;
  private SlrTable slrTable;
  private Grammar grammar;

  public Parser(SlrTable slrTable, ArrayList<Token> tokens, Grammar grammar) {

    this.grammar = grammar;
    this.slrTable = slrTable;
    // this.tokens =
    excutionStack.add(StackItem.stateItem(0));

    // inputStack.add();
    // tokens.remove(o)
    inputStack.addAll(tokens.reversed());

    for (String s : Grammar.rules) {
      nonterminals.add(s.split("->").toString().replaceAll(" ", ""));
    }
    currentAction = null;
  }

  public void printHeader() {
    System.out.println(
        String.format("%-45s  %-30s %-30s", "ExcutionStack ", "Input sttack", "Action"));
  }

  public void printState() {
    String e = "$ ";
    for (StackItem s : excutionStack) {
      e += s.toString() + " ";
    }

    System.out.print(String.format("%-45s", e));
    System.out.print("| ");

    String ts = "";
    for (Token t : inputStack.reversed()) {

      ts += t.toStackString() + " ";
    }

    System.out.print(String.format("%-30s", ts));
    System.out.print("| ");

    System.out.print(currentAction.toString() + "\n");
  }

  void printIdTable() {

    System.out.println(Coloring.toBlue("Tableau des identificateurs:"));
    for (HashMap.Entry<String, SemanticType> entry : idMap.entrySet()) {
      System.out.println(entry.getKey() + " => " + entry.getValue());
    }
  }

  private StackItem semanticAnalysis(int ruleId, ArrayList<StackItem> poppedItems, StackItem left) {

    // Left is void by default
    //
    SemanticType e_type, li_type, f_type;
    String id;
    switch (ruleId) {
      case 0:
        var dec = poppedItems.get(0).getType();
        var li = poppedItems.get(1).getType();
        if (!dec.isVoidType() || !li.isVoidType()) {
          left.setType(SemanticType.errorType());
        }

        break;
      case 1:
        id = poppedItems.get(1).castAsToken().value;
        var type = poppedItems.get(3).getType();
        idMap.put(id, type);
        break;
      case 3:
        var i = poppedItems.get(0).getType();
        if (!i.isVoidType()) {
          left.setType(SemanticType.errorType());
        }
        break;
      case 5:
        id = poppedItems.get(0).castAsToken().value;
        if (idMap.containsKey(id)) {
          var idType = idMap.get(id);

          e_type = poppedItems.get(2).getType();
          if (idType != e_type) {
            if (e_type.isErrorType()) {
              left.setType(SemanticType.errorType());
              break;
            }

            var token = poppedItems.get(1).castAsToken();
            semanticErrors.add(
                new SemanticError(
                    "Cant assign value of type "
                        + e_type.toString()
                        + " to variable "
                        + id
                        + " of type "
                        + idType.toString()
                        + ".",
                    token.line,
                    token.pos));
            left.setType(SemanticType.errorType());
          }

        } else {

          var token = poppedItems.get(1).castAsToken();
          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", token.line, token.pos));
          left.setType(SemanticType.errorType());
        }
        break;
      case 6:
        id = poppedItems.get(0).castAsToken().value;
        if (idMap.containsKey(id)) {
          var idType = idMap.get(id);

          if (idType.isArrayType()) {
            var elementType = idType.getArrayContentType();

            e_type = poppedItems.get(5).getType();
            if (elementType != e_type) {

              var token = poppedItems.get(1).castAsToken();
              semanticErrors.add(
                  new SemanticError(
                      "Can't assign expression of type "
                          + e_type.toString()
                          + " to an array of element of type "
                          + elementType
                          + ".",
                      token.line,
                      token.pos));
              left.setType(SemanticType.errorType());
            }

          } else {

            var token = poppedItems.get(1).castAsToken();
            semanticErrors.add(new SemanticError(id + " is not an array.", token.line, token.pos));
            left.setType(SemanticType.errorType());
          }

        } else {

          var token = poppedItems.get(1).castAsToken();
          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", token.line, token.pos));
          left.setType(SemanticType.errorType());
        }
        break;

      case 7:
        id = poppedItems.get(2).castAsToken().value;
        // String id1 = idStack.pop();
        if (!idMap.containsKey(id)) {
          var token = poppedItems.get(0).castAsToken();
          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", token.line, token.pos));
          left.setType(SemanticType.errorType());
        }
        break;
      case 9:
        var E_type = poppedItems.get(1).getType();
        var LI_type = poppedItems.get(3).getType();
        if (LI_type.isErrorType()) {
          left.setType(SemanticType.errorType());
        }
        if (E_type.isErrorType()) {
          left.setType(SemanticType.errorType());
        } else if (!E_type.isIntType()) {
          var token = poppedItems.get(0).castAsToken();
          semanticErrors.add(
              new SemanticError("if condition must be of type int.", token.line, token.pos));
        }

        break;

      case 10:
        e_type = poppedItems.get(1).getType();
        li_type = poppedItems.get(3).getType();
        var li2_type = poppedItems.get(5).getType();
        if (li_type.isErrorType() || li2_type.isErrorType()) {
          left.setType(SemanticType.errorType());
        }
        if (e_type.isErrorType()) {
          left.setType(SemanticType.errorType());
        } else if (!e_type.isIntType()) {
          var token = poppedItems.get(0).castAsToken();
          semanticErrors.add(
              new SemanticError("if condition must be of type Int.", token.line, token.pos));
        }
        break;

      case 11:
        var EType = poppedItems.get(1).getType();
        var l_type = poppedItems.get(3).getType();
        if (l_type.isErrorType()) {
          left.setType(SemanticType.errorType());
        }
        if (EType.isErrorType()) {
          left.setType(SemanticType.errorType());
        } else if (!EType.isIntType()) {
          var token = poppedItems.get(0).castAsToken();
          semanticErrors.add(
              new SemanticError("while condition must be of type Int.", token.line, token.pos));
        }
        break;
      case 12:
        e_type = poppedItems.get(0).getType();
        f_type = poppedItems.get(2).getType();
        if (e_type.isErrorType() || f_type.isErrorType()) {
          left.setType(SemanticType.errorType());
          break;
        }

        if (e_type.isFloatType() || f_type.isFloatType()) {
          left.setType(SemanticType.floatType());
        } else if (!e_type.isArrayType() && !f_type.isArrayType()) {
          left.setType(SemanticType.intType());
        } else {
          left.setType(SemanticType.errorType());
        }
        break;

      case 13:
        e_type = poppedItems.get(0).getType();
        f_type = poppedItems.get(2).getType();
        if (e_type.isErrorType() || f_type.isErrorType()) {

          left.setType(SemanticType.errorType());
          break;
        }

        if (e_type.isIntType() && f_type.isIntType()) {
          left.setType(SemanticType.intType());
        } else {
          left.setType(SemanticType.errorType());

          semanticErrors.add(
              new SemanticError("Modulus % operands should be both of type int.", -1, -1));
        }
        break;

      case 14:
        e_type = poppedItems.get(0).getType();
        f_type = poppedItems.get(2).getType();
        if (e_type.isErrorType() || f_type.isErrorType()) {
          left.setType(SemanticType.errorType());
          break;
        }
        if (!e_type.isArrayType() && !f_type.isArrayType()) {
          left.setType(SemanticType.intType());
        } else {
          left.setType(SemanticType.errorType());
          semanticErrors.add(
              new SemanticError(
                  "Relational operations should be between Floats and Ints.", -1, -1));
        }
        break;
      case 15:
        e_type = poppedItems.get(0).getType();
        f_type = poppedItems.get(2).getType();

        if (e_type.isErrorType() || f_type.isErrorType()) {
          left.setType(SemanticType.errorType());
          break;
        }

        if (e_type.isIntType() && f_type.isIntType()) {

          left.setType(SemanticType.intType());
        } else {

          left.setType(SemanticType.errorType());
          semanticErrors.add(
              new SemanticError("Logical operations should be between Ints only.", -1, -1));
        }
        break;
      case 16:
        f_type = poppedItems.get(0).getType();
        left.setType(f_type);
        break;

      case 17:
        e_type = poppedItems.get(0).getType();
        left.setType(e_type);
        break;
      case 18:
        id = poppedItems.get(0).castAsToken().value;
        if (idMap.containsKey(id)) {
          var idType = idMap.get(id);

          if (idType.isArrayType()) {
            left.setType(idType.getArrayContentType());
          } else {

            var token = poppedItems.get(1).castAsToken();
            semanticErrors.add(new SemanticError(id + " is not an array.", token.line, token.pos));
            left.setType(SemanticType.errorType());
          }

        } else {

          var token = poppedItems.get(1).castAsToken();
          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", token.line, token.pos));
          left.setType(SemanticType.errorType());
        }
        break;
      case 19:
        left.setType(SemanticType.intType());
        break;
      case 20:
        left.setType(SemanticType.floatType());

        break;
      case 21:
        id = poppedItems.get(0).castAsToken().value;

        if (idMap.containsKey(id)) {
          var idType = idMap.get(id);

          left.setType(idType);

        } else {
          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", -1, -1));
          left.setType(SemanticType.errorType());
        }
        break;
      case 22:
        var ts = poppedItems.get(0).getType();
        left.setType(ts);
        break;

      case 23:
        ts = poppedItems.get(5).getType();
        var intLit = poppedItems.get(2).castAsToken().value;
        int size = Integer.parseInt(intLit);
        if (ts.isIntType()) {

          left.setType(SemanticType.intArrayType(size));
        } else {

          left.setType(SemanticType.floatArrayType(size));
        }
        break;
      case 24:
        left.setType(SemanticType.intType());
        break;
      case 25:
        left.setType(SemanticType.floatType());
        break;

      case 26:
        left.setType(SemanticType.intType());
        break;
      case 27:
        id = poppedItems.get(0).castAsToken().value;
        if (idMap.containsKey(id)) {
          var idType = idMap.get(id);

          if (idType.isIntType()) {

            left.setType(SemanticType.intType());
          } else {
            semanticErrors.add(
                new SemanticError(id + " should be of type Int to access array.", -1, -1));
          }

        } else {

          syntaxicErrors.add(new SyntaxicError(id + " is not declared.", -1, -1));
          left.setType(SemanticType.errorType());
        }
        break;
      default:
        break;
    }
    return left;
  }

  public boolean runSyntaxAnalysis() {

    printHeader();
    boolean accept = false;
    // int i = 0;
    while (!accept) {
      // i++;
      // System.out.println("i:  " + i);

      Token t = inputStack.peek();
      int topExec = excutionStack.peek().castAsState();
      currentAction = slrTable.getAction(topExec, t);
      // System.out.println("input: " + t.toStackString() + " state: " + topExec);
      // System.out.println(currentAction );

      printState();
      // System.out.println();

      // Print state

      if (currentAction instanceof Shift) {

        Token tt = inputStack.pop();
        excutionStack.push(StackItem.tokenItem(tt));
        excutionStack.push(StackItem.stateItem(((Shift) currentAction).state));
        // Sematic
        if (tt.tokentype == TokenType.Identifier) {
          idStack.push(tt.value);
        }

      } else if (currentAction instanceof Reduce) {

        int ruleId = ((Reduce) currentAction).rule;

        ArrayList<StackItem> poppedItems = new ArrayList<>();
        if (grammar.getRightSide(ruleId)[0].equals("''")) {

        } else {
          int popLen = grammar.getRightSidesLength(ruleId) * 2;
          for (int j = 0; j < popLen; j++) {

            StackItem r = excutionStack.pop();
            if (j % 2 == 1) {
              poppedItems.add(r);
            }
          }
        }

        int state = excutionStack.peek().castAsState();
        String leftSide = grammar.getLeftSize(ruleId);
        int succ = slrTable.getSuccessor(state, leftSide);

        // System.out.println("succ: " + succ);
        // printState();
        //
        var left = StackItem.nonTerminalItem(leftSide);
        Collections.reverse(poppedItems);
        semanticAnalysis(ruleId, poppedItems, left);
        excutionStack.push(left);
        excutionStack.push(StackItem.stateItem(succ));
      } else if (currentAction instanceof Accept) {
        accept = true;

      } else {

        System.out.println("stop");

        break;
      }
    }
    printIdTable();
    System.out.println("Les erreurs syntaxiques:");
    for (SyntaxicError e : syntaxicErrors) {
      System.out.println(e.toString());
    }

    System.out.println("Semantic errors:");
    for (SemanticError e : semanticErrors) {
      System.out.println(e.toString());
    }
    return accept;
  }
}
