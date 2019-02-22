package pro.taskana.export.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for creating new files.
 * 
 * @author Felix Eurich
 *
 */
public class FileUtils {

    private static final String DEFAULT_SEPARATOR = ",";

    private final Path outputDir;
    private final FileType fileType;

    public FileUtils(Path outputDir, FileType fileType) {
        super();
        this.outputDir = outputDir;
        this.fileType = fileType;
    }

    /**
     * Create a file with the given name and content.
     * 
     * @param name
     *            of the new file.
     * @param lines
     *            content of the file.
     */
    public void createFile(String fileName, List<List<String>> lines) {
        try {
            File file = new File(outputDir.toString(), fileName + "." + fileType.getExtension());
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            String content = lines.stream().map(line -> line.stream().collect(Collectors.joining(DEFAULT_SEPARATOR)))
                    .collect(Collectors.joining("\n"));

            content.replaceAll(" ", "");
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
