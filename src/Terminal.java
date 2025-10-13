import java.io.*;
import java.util.*;

public class Terminal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        Terminal terminal = new Terminal();

        System.out.println("Welcome to your Command Line Interpreter");
        System.out.println("you can type 'exit' to close");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Terminal closed");
                break;
            }

            boolean valid = parser.parse(input);

            if (valid) {
                terminal.execute(parser);
            } else {
                System.out.println("Invalid command format");
            }
        }

        scanner.close();
    }

    public void execute(Parser parser) {
        String cmd = parser.getCommandName();
        String[] args = parser.getArgs();

        switch (cmd) {
            case "pwd":
                pwd();
                break;

            case "cd":
                cd(args);
                break;

            case "ls":
                ls();
                break;

            case "mkdir":
                mkdir(args);
                break;

            case "rmdir":
                rmdir(args);
                break;

            case "touch":
                touch(args);
                break;

            case "rm":
                rm(args);
                break;

            case "cp":
                cp(args);
                break;

            case "cat":
                cat(args);
                break;

            case "wc":
                wc(args);
                break;

            case "zip":
                zip(args);
                break;

            case "unzip":
                unzip(args);
                break;

            default:
                System.out.println("Unknown command: '" + cmd + "'");
        }
    }

    void pwd() {
        System.out.println("Nada's part");
    }

    void cd(String[] args) {
        System.out.println("Nada's part");
    }

    void mkdir(String[] args) {
        System.out.println("Nada's part");
    }

    void rmdir(String[] args) {
        System.out.println("Nada's part");
    }

    void ls() {
        System.out.println("Nada's part");
    }

    void cp(String[] args) {
        System.out.println("Rahma's part");
    }

    void touch(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: touch <filename>");
            return;
        }

        File file = new File(args[0]);
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    void rm(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: rm <filename>");
            return;
        }

        File file = new File(args[0]);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted: " + file.getName());
            } else {
                System.out.println("Error deleting file.");
            }
        } else {
            System.out.println("File not found.");
        }
    }

    void cat(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: cat <filename>");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("----- File Content -----");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("------------------------");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    void wc(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: wc <filename>");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        int lines = 0, words = 0, chars = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                words += line.split("\\s+").length;
                chars += line.length();
            }
            System.out.println("Lines: " + lines + " | Words: " + words + " | Characters: " + chars);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    void zip(String[] args) {
        System.out.println("Moaid's part");
    }

    void unzip(String[] args) {
        System.out.println("Moaid's part");
    }
}

class Parser {
    private String commandName;
    private String[] args;
    private boolean appendMode = false;
    private String outputFile = null;

    public boolean parse(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String[] parts = input.trim().split("\\s+");
        commandName = parts[0];

        List<String> arguments = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals(">") || parts[i].equals(">>")) {
                appendMode = parts[i].equals(">>");
                if (i + 1 < parts.length) {
                    outputFile = parts[i + 1];
                    break;
                } else {
                    return false;
                }
            } else {
                arguments.add(parts[i]);
            }
        }

        args = arguments.toArray(new String[0]);
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}