package com.orderwire.threads;

import com.orderwire.api.IngestEdiFileAPI;
import com.orderwire.data.DataCommon;
import com.orderwire.data.DataEdi;
import com.orderwire.edi.EDIFileScanner;
import com.orderwire.excp.EDIDuplicateFileFoundException;
import com.orderwire.excp.NoEDIFileFoundException;
import com.orderwire.logging.UtilLogger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class IngestEdiFileThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;

    public IngestEdiFileThread(HashMap threadMap){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }
    
    @Override
    public void run() {
        Boolean procStatus = true;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);

        try {
            /* *******************  Get EDI File Path ******************************* */
            String ediFolderPath = "";
            String ediArchivePath = "";

            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")) {
                ediFolderPath = "src/test/edi/";
                ediArchivePath = "src/test/ediarchive/";
            } else if (os.toLowerCase().contains("linux")) {
                ediFolderPath = dc.getEdiFilePath();
                ediArchivePath = dc.getEdiArchivePath();
            }

            /* ******************* Get EDI File if Present *************************** */
            EDIFileScanner scanEdi = new EDIFileScanner(owThreadName);
            String ediFileName = scanEdi.ediFileScan(ediFolderPath);
            if (ediFileName.isEmpty()) {
                throw new NoEDIFileFoundException();
            }


            /* *********************   Start Task  **********************************  */
            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();
            UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run()", "", "Start Task Subscribed", owTaskSubscribeId.toString());


            /* ******************* Get EDI File *************************** */
            Path filePath = Paths.get(ediFolderPath, ediFileName);
            File originalFile = new File(filePath.toString());
            UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run", "", "EDI Original File", originalFile.getAbsolutePath());
            File archiveFile = new File(ediArchivePath + ediFileName);
            UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run", "", "EDI Archive File", archiveFile.getAbsolutePath());


            DataEdi de = new DataEdi(owThreadName);

            //Is EDI File Duplicate
            Integer ediOrderwireFileId = de.isEdiFileDuplicate(ediFileName);
            if (!ediOrderwireFileId.equals(-1)) {
                Boolean deleteDuplicateStatus = originalFile.delete();
                throw new EDIDuplicateFileFoundException(ediFileName);
            }

            // read file lines into List
            List<String> fileLines = Files.readAllLines(filePath);

            //insert into database table (filename, file contents);
            Integer ediFileId = de.insertEdi(ediFileName, fileLines);


            // move file to archive
            try {
              Files.copy(originalFile.toPath(), archiveFile.toPath());
              Boolean deleteStatus = originalFile.delete();

              UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run", "", "EDI File Move Success", "Success");
            } catch (Exception exce) {
                UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run", "", "EDI File Move Error", exce.getMessage());
            }


            /* ******************* Ingest EDI File Process *************************** */
            IngestEdiFileAPI iefApi = new IngestEdiFileAPI(owThreadName, fileLines);

            // BIG Segment
            HashMap<String, String> bigMap = iefApi.getBigMap();
            bigMap.put("ow_orderwire_edi_file_id", ediFileId.toString());
            Integer bigId = de.insertEdiSegmentBig(bigMap);

            // IT1 Segments
            List<HashMap> it1MapList = iefApi.getit1MapList();
            for (HashMap it1Map : it1MapList) {
                it1Map.put("ow_orderwire_edi_file_id", ediFileId.toString());
                Integer it1Id = de.insertEdiSegmentIt1(it1Map);
            }
        } catch (NoEDIFileFoundException neffe) {
            procStatus = false;
        } catch (EDIDuplicateFileFoundException edffe){
            UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run", "", "EDI Duplicate File Found Exception", edffe.getMessage());
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());
        }finally {
            /* **************** End the Journal Process ********************* */
            if (procStatus) {
                Boolean endTaskJournalStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
                UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run()", "", "End Task Subscribed", owTaskSubscribeId.toString());
            }
            Boolean endTaskStatus = dc.endTaskSubscribedStatus();
            if (procStatus) {
                UtilLogger.setDbStatus(owThreadName, "IngestEDIFileThread", "run()", "", "End Task Status", owTaskSubscribeId.toString());
            }
        }
    }
    
    public void start () {
        if (t == null){
            t = new Thread (this, owThreadName);
            t.start ();
        }
    }  
}
