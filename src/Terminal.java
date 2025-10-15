import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
        System.out.println("Huda's part");
    }

    void rm(String[] args) {
        System.out.println("Huda's part");
    }

    void wc(String[] args) {
        System.out.println("Huda's part");
    }

    void cat(String[] args) {
        System.out.println("Huda's part");
    }

    void zip(String[] args) {

        if (args.length < 2) {
            System.out.println("Wrong Input please use: zip <zip_file_name.zip> <file1> <file2> ...");
            return;
        }

        String zipFile;
        int startIndex = 0;
        boolean recursive = false;

        if (args[0].equals("-r")) {

            if (args.length < 3) {
                System.out.println("Wrong Input please use: zip -r <archive.zip> <directory>");
                return;
            }
            recursive = true;
            zipFile = args[1];
            startIndex = 2;
        } else {
            zipFile = args[0];
            startIndex = 1;
        }

        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (recursive) {
                File dir = new File(args[startIndex]);
                if (!dir.exists()) {
                    System.out.println("Directory not found: " + args[startIndex]);
                    return;
                }
                addDirectoryToZip(dir, dir.getName(), zos);
                System.out.println("Directory compressed: " + dir.getPath());
            } else {

                for (int i = 1; i < args.length; i++) {
                    File fileToZip = new File(args[i]);

                    if (!fileToZip.exists()) {
                        System.out.println(args[i] + " (file not found)");
                        continue;
                    }

                    addFileToZip(fileToZip, fileToZip.getName(), zos);

                }
            }
            System.out.println("Commpressing is complete and Zip file created: " + zipFile);

        } catch (IOException e) {
            System.out.println("Error zipping: " + e.getMessage());
        }
    }

    private void addFileToZip(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isDirectory()) {
            addDirectoryToZip(fileToZip, fileName, zos);
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            zos.putNextEntry(new ZipEntry(fileName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    private void addDirectoryToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {

            zos.putNextEntry(new ZipEntry(parentFolder + "/"));
            zos.closeEntry();
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                addDirectoryToZip(file, parentFolder + "/" + file.getName(), zos);
            } else {
                addFileToZip(file, parentFolder + "/" + file.getName(), zos);
            }
        }
    }

    void unzip(String[] args) {
        if (args.length < 1) {
            System.out.println("Wrong Input please use:");
            System.out.println("  unzip <archive.zip>");
            System.out.println("  unzip <archive.zip> -d <destination_folder>");
            return;
        }

        String zipFile = args[0];
        String destDir = ".";

        if (args.length >= 3 && args[1].equals("-d")) {
            destDir = args[2];
        }

        File dir = new File(destDir);
        if (!dir.exists())
            dir.mkdirs();

        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(dir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    System.out.println("Zip extracted: " + newFile.getPath());
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            System.out.println("Unzip is completed: " + dir.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error while unzipping: " + e.getMessage());
        }
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

        // Split input by spaces into tokens
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
