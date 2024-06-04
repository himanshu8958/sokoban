import java.util.*;
import java.io.*;

public class ModelChecker {

    public static void main(String[] args) throws Exception {
        ModelChecker.checkInteractive(new File("Boards/level1.smv"), 10, new File("Boards/level1.out"),
                new File("Boards/level.err"));

    }

    public static void checkInteractive(File smvFile, int bound, File outFile, File errorFile)
            throws IOException, InterruptedException {
        String[] commands = new String[] { "nuXmv", "-bmc", "-bmc_length", Integer.valueOf(bound).toString(),
                smvFile.getPath() };

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.inheritIO();
        pb.redirectOutput(outFile);
        pb.redirectError(errorFile);

        /*
         * new Thread() {
         * public void run() {
         */ try {
                    final Process process = pb.start();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    String line;
                    reader.close();
                    int exitVal = process.waitFor();
                    if (exitVal != 0) {
                        System.out.println("Abnormal Behaviour! Something bad happened.");
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Something went wrong. Here are more details\n" + e.getMessage());
                }
                /*
                 * }
                 * }.start();
                 */

    }

    public static void checkBdd(File smvFile, File outFile) throws IOException, InterruptedException {
        String[] commands = new String[] { "nuXmv", smvFile.getPath() };
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.inheritIO();

        pb.redirectOutput(outFile);
        new Thread() {
            public void run() {
                try {
                    final Process process = pb.start();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    String line;
                    reader.close();
                    int exitVal = process.waitFor();
                    if (exitVal != 0) {
                        System.out.println("Abnormal Behaviour! Something bad happened.");
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Something went wrong. Here are more details\n" + e.getMessage());
                }
            }
        }.start();

    }

}