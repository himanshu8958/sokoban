package sokoban;
import java.io.*;
public class run_nuXmv {
	public static String runNuxmv(String modelFilename) {
	// Define the command and the output file
		String[] command = {"nuXmv", "-bmc", "-bmc_length", "30", modelFilename};
		String outputFilename = modelFilename.split("\\.")[0] + ".out";

	// Execute the command
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true); // Merge the error stream into the output stream
		//here
		long startTime = System.nanoTime();  // Start timing
	try {
		Process process = processBuilder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder output = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			output.append(line).append(System.lineSeparator());
		}

		process.waitFor(); // Wait for the process to complete
		//here
		long endTime = System.nanoTime();  // End timing
        long duration = (endTime - startTime) / 1_000_000;  // Convert to milliseconds
        System.out.println(duration+" milliseconds");
		// Save output to file
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
			
			writer.write(output.toString());
			writer.close();
		}
		/*catch(Exception e) {
			System.out.println("Exception e "+ e);
		}*/
		System.out.println("Output saved to " + "C:/Users/User/sokoban/"+outputFilename);

		return outputFilename;
	} 
	catch (IOException | InterruptedException e) {
		e.printStackTrace();
		return  null;
	}
}

	public static void main(String[] args) {
		//if (args.length < 1) {
		//	System.out.println("Usage: java NuXmvRunner <model file name>");
			//return;
		//}

		String modelFilename = "board7.smv";
		String outputFilename = runNuxmv(modelFilename);
}
}


