package tfWebControllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.mail.EmailException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;


public class MyRunner {
	public static  String report1;


    @Test
    public void BA() throws Throwable {
    	Tfwebflow keys = new Tfwebflow();

        // Specify the Keywords file location
        FileInputStream fi = new FileInputStream("data/testData.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(fi);
        XSSFSheet ws = wb.getSheet("Runner");

        // Find the number of rows
        int rowCount = ws.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            XSSFRow row = ws.getRow(i);

            // Read the run mode
            String runMode = row.getCell(4).getStringCellValue();
            System.out.println(runMode);

            if (runMode.equals("Y")) {
                // Read the step description
                String keyWord = row.getCell(3).getStringCellValue();
                System.out.println(keyWord);

                switch (keyWord) {
                    case "accessToken":
                        keys.accessToken();
                        break;
                    case "createUniverse":
                        keys.createUniverse();
                        break;
                    case "createSchema":
                        keys.createSchema();
                        break;
                    case "createGroup":
                        keys.createGroup();
                        break;
                    case "createContext":
                        keys.createContext();
                        break;
                    case "createAq":
                        keys.createAq();
                        break;
                    case "getUniverseId":
    					keys.getUniverseId();
    					break;
    				case "getAllUniverseId":
    					keys.getAllUniverseId();
    					break;
    				case "getSchemaId":
    					keys.getSchemaId();
    					break;
    				case "getAllSchemas":
    					keys.getAllSchemas();
    					break;
    				case "getGroupId":
    					keys.getGroupId();
    					break;
    				case "getAllGroups":
    					keys.getAllGroups();
    					break;
    				case "getContextId":
    					keys.getContextId();
    					break;
    				case "getAllContexts":
    					keys.getAllContexts();
    					break;
    				case "getAqId":
    					keys.getAqId();
    					break;
    				case "getAllAqs":
    					keys.getAllAqs();
    					break;
    				case "deleteUniverse":
    					keys.deleteUniverse();
    					break;
    				case "deleteGroup":
    					keys.deleteGroup();
    					break;
    				case "deleteContext":
    					keys.deleteContext();
    					break;
    				case "deleteAq":
    					keys.deleteAq();
    					break;
    					
    					
    					
                
                        
                        
                }
                }
            }
        

        // Close the workbook
        wb.close();
    }



static ExtentTest test;
static ExtentReports report;

@BeforeClass
public static void startTest() {
    Date d = new Date();
    String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".html";
     report1="TF Api Automation test report_"+fileName;
     System.out.println(report1);
    report = new ExtentReports(System.getProperty("user.dir") + "" + report1);
    
    report = new ExtentReports(System.getProperty("user.dir")+ "/"+report1);


//    test = report.startTest("MarketAPI");
	 //report = new ExtentReports(System.getProperty("user.dir")+"/ExtentReportResults.html");
     //test = report.startTest("Market_API");
}
@AfterClass
public static void endTest() throws IOException {
    // End the test and generate the report
    report.endTest(test);
    report.flush();

//
    try {
    	SendEmail.sendTestReportEmail();
       
    } catch (EmailException e) {
        e.printStackTrace();
    }
   
//    
//}

}
}