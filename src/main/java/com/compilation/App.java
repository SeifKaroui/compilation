package com.compilation;

import com.compilation.lexer.Lexer;
import com.compilation.lexer.Token;
import com.compilation.parser.Parser;
import com.compilation.parser.SlrTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/** Hello world! */
public class App {
  /** Learn how SLR works with lexer */
  public static void main(String[] args) {
    System.out.println("Started lexer:");
    if (args.length > 0) {

      try {

        File f = new File(args[0]);
        Scanner s = new Scanner(f);
        String source = " ";
        while (s.hasNext()) {
          source += s.nextLine() + "\n";
        }
        ///
        SlrTable slrTable = new SlrTable();
        Grammar grammar = new Grammar();
        Lexer l = new Lexer(source);
        ArrayList<Token> tokens = l.generateTokens();
        l.printOutput();
        Parser parser = new Parser(slrTable, tokens, grammar);
        System.out.println();
        parser.runSyntaxAnalysis();

        ///
        s.close();
      } catch (FileNotFoundException e) {
        System.out.println("File error!");
      }
    } else {
      System.out.println("No args!");
    }
  }
}
