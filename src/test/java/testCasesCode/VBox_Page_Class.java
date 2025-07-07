package testCasesCode;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class VBox_Page_Class {

    private static final String VBOX_MANAGE_PATH = "C:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe";

    public static void startVM(String vmName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(VBOX_MANAGE_PATH, "startvm", vmName, "--type", "gui");
        Process p = pb.start();
        p.waitFor();
        System.out.println("✅ VM Started.");
    }

    public static File getLatestExeFromDownloads() {
        String downloadFolderPath = System.getProperty("user.home") + "\\Downloads";
        File[] files = new File(downloadFolderPath).listFiles();
        if (files == null) return null;

        return Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".exe"))
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    public static boolean copyToVM(String vmName, String username, String password, File sourceFile, String guestTargetDir) throws IOException, InterruptedException {
        String[] copyFileCommand = {
                VBOX_MANAGE_PATH, "guestcontrol", vmName, "copyto",
                "--username", username, "--password", password,
                "--target-directory", guestTargetDir, sourceFile.getAbsolutePath()
        };

        ProcessBuilder pb = new ProcessBuilder(copyFileCommand);
        Process p = pb.start();
        return p.waitFor() == 0;
    }

    public static boolean executeFileInVM(String vmName, String username, String password, String guestFilePath) throws IOException, InterruptedException {
        String[] executeCommand = {
                VBOX_MANAGE_PATH, "guestcontrol", vmName,
                "--username", username, "--password", password,
                "run", "--exe", "cmd.exe", "--", "/c", guestFilePath
        };

        ProcessBuilder pb = new ProcessBuilder(executeCommand);
        Process p = pb.start();
        return p.waitFor() == 0;
    }
}
