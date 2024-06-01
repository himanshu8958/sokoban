import java.util.*;
import java.io.*;

public class ModelChecker {
    public static void main(String[] args)  throws Exception{
        checkInteractive(new File("Boards/level1"), 10);
        
    }
    
public static void checkInteractive(File smvFile, int bound) throws IOException, InterruptedException, Exception{                   
        String[] commands = new String[]{ "nuXmv", "-bmc", "-bmc_length", Integer.valueOf(bound).toString(),smvFile.getPath() + ".smv"};
        ProcessBuilder pb = new ProcessBuilder(commands);    
        pb.inheritIO();
        
        pb.redirectOutput(new File(smvFile.getParent() + ".out"));
    
        new Thread() {
            public void run() {
                try{
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
                    System.out.println("Something went wrong. Here are more details\n"+e.getMessage());
                }
            }
        }.start();

    }
}
