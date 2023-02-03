package com.orderwire.edi;


import com.orderwire.excp.NoEDIFileFoundException;
import com.orderwire.logging.UtilLogger;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class EDIFileScanner {
    private final String owThreadName;

    public EDIFileScanner(String _threadName){
        owThreadName = _threadName;
    }

    public String ediFileScan(String ediFolderPath){
        String ediFileName = "";

        try {
            // look for file to process   (fileExist)
            File dir = new File(ediFolderPath);
            File[] files = dir.listFiles(ediFilter);

            if (files.length == 0) {
                throw new NoEDIFileFoundException();
            } else {
                ediFileName = files[0].getName();
            }

        } catch (NoEDIFileFoundException neffe) {
            ediFileName = "";
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "IngestEdiFileAPI", "IngestEdiFile", "", "Exception", exce.getMessage());
        }

        return ediFileName;
    }

    FilenameFilter ediFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".edi");
        }
    };

}
