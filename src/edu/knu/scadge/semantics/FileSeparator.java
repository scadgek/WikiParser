package edu.knu.scadge.semantics;

//import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSeparator implements PageHandler {
//    private Logger log = Logger.getLogger("Processor");
    private String path = "D:/wiki_files/";
    private static int counter = 1;
    public static String UNIQUE_SEPARATOR = "#SCADGE_DELIM#";


    @Override
    public void handle(String title, String text) {
        try {
            File file = new File(path.concat("Article-"+counter+".zip"));
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
            ZipEntry zipEntry = new ZipEntry("Article-"+counter+".txt");
            zipOutputStream.putNextEntry(zipEntry);

            String fill = title.concat(UNIQUE_SEPARATOR).concat(text);
            byte[] data = fill.getBytes();
            zipOutputStream.write(data, 0, data.length);
            zipOutputStream.closeEntry();

            zipOutputStream.close();

            if (counter % 100 == 0)
//                log.info(""+counter+" articles have been written");

            counter++;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
