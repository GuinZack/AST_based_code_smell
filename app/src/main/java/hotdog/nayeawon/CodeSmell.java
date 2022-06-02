package hotdog.nayeawon;


import Designite.Designite;
import Designite.utils.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CodeSmell {

    public static void main(String[] args) {
        CodeSmell codeSmell = new CodeSmell();
        codeSmell.run(args);
    }

    public void run(String[] args) {
        CLI inputArgs = parseArguments(args);
        try {
            for (String inputPath : inputArgs.getInputFolderPaths()) {
                String designiteInput = "-i " + inputPath + " -o " + inputArgs.getOutputFolderPath();
                System.out.println(designiteInput);
                Designite.main(designiteInput.split(" "));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
    private static CLI parseArguments(String[] args) {
        Options argOptions = new Options();

        Option input = new Option("i", "Input", true, "Input source folder path");
        input.setRequired(true);
        argOptions.addOption(input);

        Option output = new Option("o", "Output", true, "Path to the output folder");
        output.setRequired(true);
        argOptions.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(argOptions, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Designite", argOptions);
            Logger.log("Quitting..");
            System.exit(1);
        }
        if(cmd==null)
        {
            System.out.println("Couldn't parse the command line arguments.");
            formatter.printHelp("Designite", argOptions);
            Logger.log("Quitting..");
            System.exit(2);
        }

        String inputFolderPath = cmd.getOptionValue("Input");
        ArrayList<String> inputPaths = new ArrayList<>();
        if (inputFolderPath.endsWith(".csv")) {
            inputPaths = CLI.csvReader(inputFolderPath);
        } else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputFolderPath))) {
                for (Path path : stream) {
                    if (path.getFileName().toString().startsWith(".")) continue;
                    if (Files.isDirectory(path)) {
                        inputPaths.add(inputFolderPath + "/" + path.getFileName());
                    }
                }
            } catch (IOException e) { }
        }

        String outputFolderPath = cmd.getOptionValue("Output");
        CLI inputArgs= null;
        try
        {
            inputArgs = new CLI(inputPaths, outputFolderPath);
        }
        catch(IllegalArgumentException ex)
        {
            Logger.log(ex.getMessage());
            Logger.log("Quitting..");
            System.exit(3);
        }
        return inputArgs;
    }
}



