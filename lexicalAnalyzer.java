import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class lexicalAnalyzer {
    public static void main(String[] args) throws IOException {
        String filePath = args[0];         // Use file path of source.tks as command line argument
        File file = new File(filePath);   
        ArrayList<ArrayList<String>> lines = new ArrayList<>(); // Stores each line of code separately
        String token = new String();

        // Read each line of code
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                ArrayList<String> lexemeLine = extractLexemes(line);  // Separate each lexeme
                lines.add(lexemeLine);
            }
        }

        // Retrieve each lexeme individually and the corresponding token
        for (int i = 0; i < lines.size(); i++) {
            for (String lexeme : lines.get(i)) {
                token = tokenize(lexeme, i + 1);
                System.out.print("Lexeme: " + lexeme);
                System.out.println("\tToken: " + token);
            }
        }

   // Create a symbolic table file
FileWriter symbolicTableFile = new FileWriter("symbolic_table.tks");

// Write the header to the symbolic table file
symbolicTableFile.write(String.format("%-20s %-20s\n", "LEXEMES", "TOKENS"));

// Retrieve each lexeme individually and the corresponding token
for (int i = 0; i < lines.size(); i++) {
    for (String lexeme : lines.get(i)) {
        token = tokenize(lexeme, i + 1);
        String substring = "";

        if(lexeme.startsWith("\"") && lexeme.endsWith("\"") && lexeme.length() > 2) {
            substring = lexeme.substring(1, lexeme.length() - 1);
            System.out.print(String.format("%-20s", "Lexeme: " + substring));
        } else {
            System.out.print(String.format("%-20s", "Lexeme: " + lexeme));
        }

        System.out.println(String.format("%-20s", "\tToken: " + token));

        if(lexeme.startsWith("\"") && lexeme.endsWith("\"") && lexeme.length() > 2) {
            symbolicTableFile.write(String.format("%-20s %-20s\n", substring, token));
        } else {
            symbolicTableFile.write(String.format("%-20s %-20s\n", lexeme, token));
        }
    }
}

// Close the symbolic table file
symbolicTableFile.close();

System.out.println("Symbolic table file created: symbolic_table.tks");
    }


    // Helper method to check if a lexeme is a multi-character operator
public static boolean isMultiCharOperator(String lexeme) {
    Set<String> multiCharOperators = Set.of("++", "--", "==", "!=", "<=", ">=", "&&", "||", "+=", "-=");
    return multiCharOperators.contains(lexeme);
}

public static ArrayList<String> extractLexemes(String line) {
    ArrayList<String> lexemeLine = new ArrayList<>();
    StringBuilder word = new StringBuilder();
    boolean inString = false;

    for (int i = 0; i < line.length(); i++) {
        char ch = line.charAt(i);

        // Check for comments (everything after '#' is a comment)
        if (ch == '#') {
            if (word.length() > 0) {
                lexemeLine.add(word.toString());
                word.setLength(0);
            }
            lexemeLine.add(line.substring(i)); // Store the full comment as one lexeme
            break;
        }

        // Handle string literals correctly
        if (ch == '"') {
            if (inString) {
                lexemeLine.add("\""); 

                word.append(ch); // Append closing quote
                lexemeLine.add(word.toString()); // Store full string with quotes
                word.setLength(0);
                inString = false;

                lexemeLine.add("\""); 
            } else {
                if (word.length() > 0) {
                    lexemeLine.add(word.toString());
                    word.setLength(0);
                }
                
                word.append(ch); // Append opening quote
                inString = true;
            }
            continue;
        }

        // If inside a string, keep appending
        if (inString) {
            word.append(ch);
            continue;
        }

        // Handle multi-character operators
        if (i < line.length() - 1) {
            String twoCharOp = "" + ch + line.charAt(i + 1);
            if (isMultiCharOperator(twoCharOp)) {
                if (word.length() > 0) {
                    lexemeLine.add(word.toString());
                    word.setLength(0);
                }
                lexemeLine.add(twoCharOp);
                i++; // Skip next character since it's part of the operator
                continue;
            }
        }

        // Handle numbers (para magkadikit yung numbers!)
        if (Character.isDigit(ch)) {
            while (i < line.length() && Character.isDigit(line.charAt(i))) {
                word.append(line.charAt(i));
                i++;
            }
            lexemeLine.add(word.toString());
            word.setLength(0);
            i--; // Adjust for the extra increment in loop
            continue;
        }
        

        // Check for identifiers, keywords, numbers
        if (Character.isLetter(ch) || ch == '_') {
            word.append(ch);
        } else {
            if (word.length() > 0) {
                lexemeLine.add(word.toString()); // Store previous word
                word.setLength(0);
            }

            // Handle delimiters separately
            if (isDelimiter(ch)) {
                lexemeLine.add(String.valueOf(ch));
            } else if (Character.isDigit(ch)) {
                word.append(ch);
            } else if (!Character.isWhitespace(ch)) {
                lexemeLine.add(String.valueOf(ch));
            }
        }
    }

    // Add last word if it exists
    if (word.length() > 0) {
        lexemeLine.add(word.toString());
    }

    return lexemeLine;
}

    // Check if a character is a delimiter
    public static boolean isDelimiter(char ch) {
        // Define delimiters
        return ";:,(){}[]+-*/=<>!".indexOf(ch) != -1;
    }

    // Method to classify each lexeme as a token
    public static String tokenize(String lexeme, int line) {
        Set<String> keywords = Set.of("task", "input", "result", "definetask", "printresult",
                                      "assign", "condition", "for", "if", "else", "as", "not", "and", "or");
    
        Set<String> reservedWords = Set.of("true", "false");
        Set<String> builtInFunctions = Set.of("FilterData", "FilteredData", "CalculateTotal",
                                              "TotalNumber", "CalculateDiscount", "DiscountedPrice");
        Set<String> noiseWords = Set.of("begin", "endtask", "using", "number", "string", "boolean", "list", "object");
    
        Set<String> arithmeticOperators = Set.of("+", "-", "*", "/", "%", "^");
        Set<String> unaryOperators = Set.of("+", "-", "++", "--");
        Set<String> relationalOperators = Set.of("==", "!=", ">", "<", ">=", "<=");
        Set<String> logicalOperators = Set.of("!", "&&", "||");
        Set<String> assignmentOperators = Set.of("=", "+=", "-=");
    
        // Handle comments
        if (lexeme.startsWith("#")) {
            return Tokens.COMMENT;
        }
    
        // Keywords
        if (keywords.contains(lexeme.toLowerCase())) {
            return Tokens.KEYWORD;
        }
    
        // Reserved words
        if (reservedWords.contains(lexeme.toLowerCase())) {
            return Tokens.RESERVED_WORD;
        }
    
        // Built-in functions
        if (builtInFunctions.contains(lexeme)) {
            return Tokens.BUILT_IN_FUNCTION;
        }
    
        // Noise words
        if (noiseWords.contains(lexeme.toLowerCase())) {
            return Tokens.NOISE_WORD;
        }
    
        // Numbers
        if (isInteger(lexeme) || isFloat(lexeme)) {
            return Tokens.NUMBER;
        }
    
        // **Ensure strings are recognized as a single lexeme**
        if (lexeme.startsWith("\"") && lexeme.endsWith("\"") && lexeme.length() > 2) {
            return Tokens.STRING;
        }
    
        // Operators
        if (arithmeticOperators.contains(lexeme)) {
            return Tokens.ARITHMETIC_OPERATOR;
        }
        if (unaryOperators.contains(lexeme)) {
            return Tokens.UNARY_OPERATOR;
        }
        if (relationalOperators.contains(lexeme)) {
            return Tokens.RELATIONAL_BOOLEAN_OPERATOR;
        }
        if (logicalOperators.contains(lexeme)) {
            return Tokens.LOGICAL_BOOLEAN_OPERATOR;
        }
        if (assignmentOperators.contains(lexeme)) {
            return Tokens.ASSIGNMENT_OPERATOR;
        }
    
        // Tokenize the quotation mark separately
        if (lexeme.equals("\"")) {
            return Tokens.DELIMITER;
        }
    
        // Delimiters
        if (isDelimiter(lexeme.charAt(0))) {
            return Tokens.DELIMITER;
        }
    
        // Identifiers
        if (Character.isLetter(lexeme.charAt(0))) {
            return Tokens.IDENTIFIER;
        }
    
        // Unknown token
        return Tokens.UNKNOWN + " At LINE " + line;
    }
    

    // Helper method to check if a lexeme is an integer (NUMBER)
    public static boolean isInteger(String lexeme) {
        try {
            Integer.parseInt(lexeme);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if a lexeme is a Float (NUMBER)
    public static boolean isFloat(String lexeme) {
        try {
            Double.parseDouble(lexeme);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if a lexeme is a string
    public static boolean isString(String lexeme) {
        return lexeme.startsWith("\"") && lexeme.endsWith("\"");
    }
}