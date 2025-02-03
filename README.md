# TaskScript-PPL

**Lexical Analysis Tool**

**Overview**

TaskScript is a goal-orientated programming language that aims to make programming less painful by abstracting away the how and only describing what needs to be done. This project provides a developed Lexical Analysis Tool developed in Java that processes source code from a .tks file and generates tokens based on lexical rules. It aids in breaking down source code into meaningful units such as keywords, identifiers, literals, operators, and special symbols.

**Features**
- Reads input from a file (source.tks).
- Identifies and classifies tokens.
- Handles keywords, identifiers, numbers, operators, special symbols, and invalid tokens.
- Displays the extracted tokens and their classifications in a separate .tks file.
- Reports lexical errors (if any).

**Installation**

Prerequisites
- Java Development Kit (JDK) 8 or later

**Steps**

- Clone the repository: git clone https://github.com/ninapaguio/TaskScript-PPL
- Navigate to the project directory
- Compile the Java files
- javac lexicalAnalyzer.java
- javac Tokens.java
- Run the program: java lexicalAnalyzer source.tks

Usage
- Prepare a source code file named source.tks.
- Run the lexical analyzer using the command mentioned above.
- The tool will display the extracted tokens along with their classifications.
- If errors are found, they will be reported with their position in the file.

File Structure

lexical-analyzer/
│── index.html             # Additional interface for entering code and displaying lexical analysis results
│── lexicalAnalyzer.java   # Main program file
│── Tokens.java            # Program containing token types
│── source.tks             # Source code input file
│── README.md              # Documentation

Future Enhancements
- Support for more complex language constructs.
- Integration with a syntax analyzer.
- Enhanced GUI-based interface for better usability.

License
- This project is licensed under the MIT License. See LICENSE for details.

