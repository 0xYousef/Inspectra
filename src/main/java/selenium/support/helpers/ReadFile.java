package support.helpers;

import data.exceptions.FileOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class ReadFile {
    private static final Logger log = LoggerFactory.getLogger(ReadFile.class);

    public static String readTXTFile(String PATH) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(PATH)));

            if (content.isBlank())
                log.info("content is Empty!");

            log.info("Read content from file: {}", PATH);
            return content.replaceAll("[\r\n\b]","");
        }catch (NoSuchFileException e){
            throw new FileOperationException("File NOT FOUND!");
        }
        catch (IOException e) {
            throw new FileOperationException("Error reading file " + PATH, e);
        }
    }

    public static boolean deleteFile(String PATH) {
            File file= new File(PATH);
            if (file.exists()) {
                boolean delete = file.delete();
                log.info("File delete old file successfully!");
                return delete;
            }
            log.info("File NOT FOUND");
            return false;
    }
}
