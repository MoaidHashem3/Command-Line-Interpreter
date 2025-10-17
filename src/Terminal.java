import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Terminal {
     private File currentDir = new File(System.getProperty("user.dir"));
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
        if (cmd.equals("cp") && args.length > 0 && args[0].equals("-r")) {
            // Remove the "-r" from args and call cp_r
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            cp_r(newArgs);
            return;
        }

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
        System.out.println(currentDir.getAbsolutePath());
    }

   void cd(String[] args) {
        if (args.length == 0) {

            currentDir = new File(System.getProperty("user.home"));
        } else if (args[0].equals("..")) {

            currentDir = currentDir.getParentFile() != null ? currentDir.getParentFile() : currentDir;
        } else {

            File newDir = new File(args[0]);
            if (!newDir.isAbsolute()) {
                newDir = new File(currentDir, args[0]);
            }
            if (newDir.exists() && newDir.isDirectory()) {
                currentDir = newDir;
            } else {
                System.out.println("Invalid path: " + args[0]);
            }
        }
    }

    void mkdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: mkdir <directory_name>");
            return;
        }

        for (String dirName : args) {
            File newDir = new File(dirName);
            if (!newDir.isAbsolute()) {
                newDir = new File(currentDir, dirName);
            }
            if (newDir.exists()) {
                System.out.println("Directory already exists: " + newDir.getName());
            } else if (newDir.mkdirs()) {
                System.out.println("Directory created: " + newDir.getName());
            } else {
                System.out.println("Failed to create: " + newDir.getName());
            }
        }
    }

    void rmdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: rmdir <directory_name> or rmdir *");
            return;
        }

        if (args[0].equals("*")) {
            File[] files = currentDir.listFiles(File::isDirectory);
            if (files != null) {
                for (File dir : files) {
                    if (Objects.requireNonNull(dir.list()).length == 0 && dir.delete()) {
                        System.out.println("Removed empty directory: " + dir.getName());
                    }
                }
            }
        } else {
            File dir = new File(args[0]);
            if (!dir.isAbsolute()) {
                dir = new File(currentDir, args[0]);
            }
            if (dir.exists() && dir.isDirectory()) {
                if (Objects.requireNonNull(dir.list()).length == 0 && dir.delete()) {
                    System.out.println("Directory removed: " + dir.getName());
                } else {
                    System.out.println("Directory not empty or cannot be deleted.");
                }
            } else {
                System.out.println("Directory not found: " + args[0]);
            }
        }
    }

      void ls() {
        File[] files = currentDir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("(empty directory)");
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File f : files) {
            System.out.println(f.getName());
        }
    }

    void cp(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: cp <source_file> <destination_file>");
            return;
        }

        File sourceFile = new File(args[0]);
        File destFile = new File(args[1]);

        if (!sourceFile.exists()) {
            System.out.println("Source file not found: " + args[0]);
            return;
        }

        if (sourceFile.isDirectory()) {
            System.out.println("Source is a directory. Use 'cp -r' for directories.");
            return;
        }
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            System.out.println("File copied successfully: " + args[0] + " -> " + args[1]);

        } catch (IOException e) {
            System.out.println("Error copying file: " + e.getMessage());
        }
    }

    void cp_r(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: cp -r <source_directory> <destination_directory>");
            return;
        }

        File sourceDir = new File(args[0]);
        File destDir = new File(args[1]);

        if (!sourceDir.exists()) {
            System.out.println("Source directory not found: " + args[0]);
            return;
        }

        if (!sourceDir.isDirectory()) {
            System.out.println("Source is not a directory: " + args[0]);
            return;
        }

        try {
            if (destDir.getCanonicalPath().startsWith(sourceDir.getCanonicalPath() + File.separator)) {
                System.out.println("Error: Cannot copy directory into itself");
                return;
            }
        } catch (IOException e) {
            System.out.println("Error checking paths: " + e.getMessage());
            return;
        }

        try {
            copyDirectory(sourceDir, destDir);
            System.out.println("Directory copied recursively: " + args[0] + " -> " + args[1]);
        } catch (IOException e) {
            System.out.println("Error copying directory: " + e.getMessage());
        }
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

    private void copyDirectory(File source, File destination) throws IOException {
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                throw new IOException("Failed to create directory: " + destination.getPath());
            }
        }

        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destination, file.getName());

                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    copyFile(file, destFile);
                }
            }
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
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
