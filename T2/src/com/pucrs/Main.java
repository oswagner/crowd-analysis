package com.pucrs;

import com.pucrs.parsing.Parser;
import com.pucrs.viewer.View;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parseFile();

        new View();

        // print personList content
        Parser.printPersonList();
    }
}
