package com.orderwire.tests;

import com.orderwire.api.IngestEdiFileAPI;
import com.orderwire.data.DataEdi;
import com.orderwire.edi.EDIFileScanner;
import com.orderwire.excp.NoEDIFileFoundException;
import com.orderwire.logging.UtilLogger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class TestEdi {
    private final static String owThreadName = "TestEDIThread";

    public static void main(String[] args) {

        Boolean procStatus = ReadEdiFile();
    }

    private static Boolean ReadEdiFile(){
        Boolean procStatus = true;
        EDIFileScanner scanEdi = new EDIFileScanner(owThreadName);
        String ediFolderPath = "src/main/edi/";
        String ediArchivePath = "src/main/ediarchive/";
        try {
            String ediFileName = scanEdi.ediFileScan(ediFolderPath);
            if (ediFileName.isEmpty()){
                throw new NoEDIFileFoundException();
            }
            System.out.println("pause");

            // read file lines into List
            Path filePath = Paths.get(ediFolderPath, ediFileName);
            List<String> fileLines = Files.readAllLines(filePath);

            //insert into database table (filename, file contents);
            DataEdi de = new DataEdi(owThreadName);
            Integer ediFileId = de.insertEdi(ediFileName, fileLines);


            // move file to archive
            File archiveFile = new File(filePath.toString());
            archiveFile.renameTo(new File(ediArchivePath + ediFileName));



            /* ******************* Ingest EDI File Process *************************** */
            IngestEdiFileAPI iefApi = new IngestEdiFileAPI(owThreadName, fileLines);

            // BIG Segment
            HashMap<String, String> bigMap = iefApi.getBigMap();
            bigMap.put("ow_orderwire_edi_file_id", ediFileId.toString());
            Integer bigId = de.insertEdiSegmentBig(bigMap);

            // IT1 Segments
            List<HashMap> it1MapList = iefApi.getit1MapList();
            for(HashMap it1Map : it1MapList){
                it1Map.put("ow_orderwire_edi_file_id", ediFileId.toString());
                Integer it1Id = de.insertEdiSegmentIt1(it1Map);
            }

        } catch (NoEDIFileFoundException neffe) {
            UtilLogger.setDbStatus(owThreadName, "TestEdi", "ReadEdiFile", "", "No EDI File Found Exception", neffe.getMessage());
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "TestEdi", "ReadEdiFile", "Exception", exce.getMessage(), exce.getMessage());
        }

        return procStatus;
    }

}
