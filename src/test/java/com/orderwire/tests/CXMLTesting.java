package com.orderwire.tests;


import com.orderwire.cxml.dll.CXML;
import com.orderwire.cxml.dll.Header;
import com.orderwire.logging.UtilLogger;
import com.orderwire.logging.UtilParameters;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;


public class CXMLTesting {

    public static void main(String[] args) {
        
        //Boolean procStatus = cxmlTest();
        Boolean procStatus = responseDate();
    }

    public static Boolean cxmlTest(){
        Boolean procStatus = true;
        UtilParameters up = new UtilParameters();
        String baseDirectory = up.getOSFolderString();        
        String xmlFileName = "SK_PO_000000000000203_20191023105117.xml";
        
        try{
            String xmlPOFilePath = baseDirectory + xmlFileName;
            File xmlFile = new File(xmlPOFilePath);
                       
            
            JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            CXML cxmlType = (CXML) jaxbUnmarshaller.unmarshal(xmlFile);
            System.out.println("Pause");
            
            
            JAXBContext jcH = JAXBContext.newInstance(Header.class);
            Unmarshaller umH = jcH.createUnmarshaller();
            CXML cxmlTypeH = (CXML) umH.unmarshal(xmlFile);
            
            
            String _toIdentity = cxmlType.getHeader().getTo().getCredential().getIdentity();
                    
            System.out.println(_toIdentity);
                    
	} catch (JAXBException e) {
            procStatus = false;
            e.printStackTrace();
	} catch (Exception exce){
            procStatus = false;
            UtilLogger.setDbStatus("Testing", "CXMLTesting", "cxmlTest", "Exception", exce.getMessage(), "");
        }
        
        return procStatus;
        
    }
    
    
    public static Boolean responseDate(){
    
          SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	sf.format(new Date());
	String str = sf.format(new Date());
        
        System.out.println("Date: " + str);
        
        
        
        return true;
    
    }
      
}
