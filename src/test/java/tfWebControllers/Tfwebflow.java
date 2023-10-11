package tfWebControllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Tfwebflow extends MyRunner {
	private String accessToken;
	private String universeid;
	private String schemaId;
	private String groupid;
	private String contextid;
	private String aqid;
	private String id;

	public static HashMap map = new HashMap();
	String generatedString = RestUtils.generateRandomName();

	public void accessToken() throws IOException {

		// http://ingress-gateway.gaiansolutions.com/iam-service/oauth/token
		RestAssured.baseURI = "http://ingress-gateway.gaiansolutions.com";
		String tokenEndpoint = "/iam-service/oauth/token";
		RequestSpecification request = RestAssured.given();

		// Load the Excel file
		FileInputStream file = new FileInputStream("data/testData.xlsx");
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheet("accessToken"); // Replace "Sheet1" with the actual sheet name
		// Read the input values from Excel
		String username = sheet.getRow(1).getCell(0).getStringCellValue();
		String password = sheet.getRow(1).getCell(1).getStringCellValue();
		String grantType = sheet.getRow(1).getCell(2).getStringCellValue();
		String provider = sheet.getRow(1).getCell(3).getStringCellValue();
		String clientId = sheet.getRow(1).getCell(4).getStringCellValue();
		String checkB2B = sheet.getRow(1).getCell(5).getStringCellValue();

		String productId = sheet.getRow(1).getCell(6).getStringCellValue();
//				 DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
//				  Cell cell = sheet.getRow(1).getCell(6);
//				 String checkB2B  = formatter.formatCellValue(cell);
		// String checkB2B = sheet.getRow(1).getCell(6).getStringCellValue();

		// Set the request parameters
		request.param("username", username);
		request.param("password", password);
		request.param("grant_type", grantType);
		request.param("provider", provider);
		request.param("clientId", clientId);
		request.param("checkB2B", checkB2B);
		request.param("productId", productId);

		// Send the request and validate the response
		Response response = request.post(tokenEndpoint);

		response.then().log().all();
		//    Assert.assertEquals(response.statusCode(), 200);

		// Log the test result in the report

		// Parse the response JSON to extract the access token
		accessToken = response.jsonPath().getString("access_token");
		this.accessToken = accessToken;

		System.out.println("accessToken" + " " + accessToken);

		System.out.println("========================" + "AccessToken generated" + "=============================");
		Cell accessTokenCell2 = sheet.getRow(1).createCell(8); // Create a new cell in the access token column
		accessTokenCell2.setCellValue(response.statusCode());

		// LocalDateTime myDateObj = LocalDateTime.now();
		//    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
		// HH:mm:ss");

		//    String formattedDate = myDateObj.format(myFormatObj);

		//    Cell accessTokenCell3 = sheet.getRow(1).createCell(9); // Create a new
		// cell in the access token column
		//    accessTokenCell3.setCellValue(formattedDate);

		// Write the modified workbook back to the file
		FileOutputStream outFile = new FileOutputStream("data/testData.xlsx");
		workbook.write(outFile);

		// Close the workbook and file
		workbook.close();
		file.close();
		outFile.close();
	}

	public void createUniverse() throws IOException {

		test = report.startTest("TF_WEB create Universe");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		String tokenEndpoint = "/650d6b6d3dc39500017d13f3/universes";
		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.PASS, "User Hits create Universe request");
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + tokenEndpoint);

		try {
			// request.header("Authorization", "Bearer " + accessToken);

			// Load the Excel file
			FileInputStream file = new FileInputStream("data/testData.xlsx");
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet("Body"); // Replace "Sheet1" with the actual sheet name

			// Read the input JSON body from Excel
			String bodyString = sheet.getRow(1).getCell(1).getStringCellValue();

			// Convert the JSON string to a JSONObject
			JSONObject bodyJson = new JSONObject(bodyString);

			// Generate a random tenantId using UUID and set it in the JSON object
			String name = UUID.randomUUID().toString();
			bodyJson.put("name", name);
			
			// Set the request JSON body
			request.body(bodyJson.toString());
			request.contentType(ContentType.JSON);
			// test.log(LogStatus.INFO, "Request Body: " + request);

			// test.log(LogStatus.PASS, "User valid Body");
			// Send the request and validate the response
			Response response = request.post(tokenEndpoint);
			response.then().log().all();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);
			universeid = response.jsonPath().getString("id");
			this.universeid = universeid;

			  int statusCode = response.getStatusCode();


		        if (statusCode == 200 || statusCode == 201) {
		            test.log(LogStatus.PASS, "Universe Creation successfull with status code: " + response.getStatusCode());
		        } else {
		            test.log(LogStatus.FAIL, "Universe Creation Failed with status code: " + response.getStatusCode());
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        test.log(LogStatus.FAIL, "Universe Creation  FAILED", e.getMessage());
		    }
		}
		public void getUniverseId() {

			test = report.startTest("TF_WEB apitoretrieveUniverseId");
			RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
			String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/universes/" + universeid;

			RequestSpecification request = RestAssured.given();
			test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

			test.log(LogStatus.PASS, "User Hits Retrieve UniverseId request");
			try {
				// Add the requesterType as a query parameter

				// Send the request and validate the response
				request.header("Authorization", "Bearer " + accessToken);

				Response response = request.get(retrieveEndpoint);

				response.then().log().all();
				int statusCode = response.getStatusCode();
				String responseBody = response.getBody().asString();
				test.log(LogStatus.INFO, "Response Body: " + responseBody);

				if (statusCode == 200) {
					test.log(LogStatus.PASS, "Retrieved UniverseById successfully with status code:" + statusCode);

				} else if (statusCode == 404) {
					// Resource not found (HTTP 404 Not Found)
					// Handle the 404 error here
					test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

				} else if (statusCode == 500) {
					// Internal server error (HTTP 500 Internal Server Error)
					// Handle the 500 error here
					test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
				} else {
					test.log(LogStatus.FAIL, "Failed to Retrieved UniverseId statuscode: " + statusCode);
				}


				System.out.println(response);

			} catch (Exception e) {
				e.printStackTrace();
				// Log the test failure in the report
				test.log(LogStatus.FAIL, "retrieved universeId failed", e.getMessage());

			}
		}
		public void getAllUniverseId() {

			test = report.startTest("TF_WEB apitoAllretrieveUniverses");
			RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
			String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/universes/get";

			RequestSpecification request = RestAssured.given();

			test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

			test.log(LogStatus.PASS, "User Hits Retrieve AllUniverses request");
			try {

				request.header("Authorization", "Bearer " + accessToken);

				Response response = request.get(retrieveEndpoint);

				response.then().log().all();
				int statusCode = response.getStatusCode();
				String responseBody = response.getBody().asString();
				test.log(LogStatus.INFO, "Response Body: " + responseBody);

				if (statusCode == 200) {
					test.log(LogStatus.PASS, "Retrieved allUniverses successfully statuscode:" + statusCode);

				} else if (statusCode == 404) {
					// Resource not found (HTTP 404 Not Found)
					// Handle the 404 error here
					test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

				} else if (statusCode == 500) {
					// Internal server error (HTTP 500 Internal Server Error)
					// Handle the 500 error here
					test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
				} else {
					test.log(LogStatus.FAIL, "Failed to Retrieved allUniverses statuscode: " + statusCode);
				}

				System.out.println(response);

			} catch (Exception e) {
				e.printStackTrace();
				// Log the test failure in the report
				test.log(LogStatus.FAIL, "retrieved universeId failed", e.getMessage());			
			}
		}
			public void deleteUniverse() {
				test = report.startTest("TF_WEB deleteUniverse");
				 String baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
				 String tokenEndpoint = "/siva/universes/siva/delete";
				 test.log(LogStatus.PASS, "DeleteUniverse");
				 Response response = RestAssured.given().log().all().baseUri(baseURI).get(tokenEndpoint);
				 int statusCode = response.getStatusCode();

				 if (statusCode == 200) {
				 test.log(LogStatus.PASS, "DeleteUniverse request successful"+ statusCode);
				 } 
				 else 
				 {
				 test.log(LogStatus.FAIL, "Failed to retrieve DeleteUniverse. Status code: " + statusCode);
				}

			     report.endTest(test);
			}
			


	public void createSchema() throws IOException {

		test = report.startTest("TF_WEB createSchema");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		String tokenEndpoint = "/650d6b6d3dc39500017d13f3/schemas";
		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.PASS, "User Hits create Schema request");
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + tokenEndpoint);

		try {
			// request.header("Authorization", "Bearer " + accessToken);

			// Load the Excel file
			FileInputStream file = new FileInputStream("data/testData.xlsx");
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet("Body"); // Replace "Sheet1" with the actual sheet name

			// Read the input JSON body from Excel
			String bodyString = sheet.getRow(2).getCell(1).getStringCellValue();

			// Convert the JSON string to a JSONObject
			JSONObject bodyJson = new JSONObject(bodyString);

			// Generate a random tenantId using UUID and set it in the JSON object
			String entityName = UUID.randomUUID().toString();
			bodyJson.put("entityName", entityName);
			JSONArray universes = new JSONArray();
			universes.put(universeid);
			bodyJson.put("universes", universes);


			// Set the request JSON body
			request.body(bodyJson.toString());
			request.contentType(ContentType.JSON);
			// test.log(LogStatus.INFO, "Request Body: " + request);

			// test.log(LogStatus.PASS, "User valid Body");
			// Send the request and validate the response
			Response response = request.post(tokenEndpoint);

			response.then().log().all();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);
			schemaId = response.jsonPath().getString("schemaId");
			this.schemaId = schemaId;

			  int statusCode = response.getStatusCode();


		        if (statusCode == 200 || statusCode == 201) {
		            test.log(LogStatus.PASS, "Schema Creation successfull with status code:  " + response.getStatusCode());
		        } else {
		            test.log(LogStatus.FAIL, "Schema Creation Test Failed with status code: " + response.getStatusCode());
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        test.log(LogStatus.FAIL, "Schema Creation  FAILED", e.getMessage());
		    }
		}
		public void getSchemaId() {

			test = report.startTest("TF_WEB api to Retrieve SchemaID");
			RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
			String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/schemas";

			RequestSpecification request = RestAssured.given();

			test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

			test.log(LogStatus.PASS, "User Hits Retrieve UniverseId request");
			try {
				request.param("schemaIDs", "schemaId");
				Response response = request.get(retrieveEndpoint);

				response.then().log().all();
				int statusCode = response.getStatusCode();
				String responseBody = response.getBody().asString();
				test.log(LogStatus.INFO, "Response Body: " + responseBody);

				if (statusCode == 200) {
					test.log(LogStatus.PASS, "Retrieved SchemaId successfully statuscode:" + statusCode);

				} else if (statusCode == 404) {
					// Resource not found (HTTP 404 Not Found)
					// Handle the 404 error here
					test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

				} else if (statusCode == 500) {
					// Internal server error (HTTP 500 Internal Server Error)
					// Handle the 500 error here
					test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
				} else {
					test.log(LogStatus.FAIL, "Failed to Retrieved SchemaId statuscode: " + statusCode);
				}
//				test.log(LogStatus.INFO, "request:" + request);
//				test.log(LogStatus.INFO, "Response Body: " + responseBody);

				// Assert.assertEquals(response.statusCode(), 200);

				// Log the test result in the report

				System.out.println(response);

			} catch (Exception e) {
				e.printStackTrace();
				// Log the test failure in the report
				test.log(LogStatus.FAIL, "retrieved SchemaId failed", e.getMessage());
			}
		}
			public void getAllSchemas() {

				test = report.startTest("TF_WEB apitoretrieveAllSchemas");
				RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
				String retrieveEndpoint = "650d6b6d3dc39500017d13f3/undefined/schemas/list?owner=false&filter=includeDrafts";

				RequestSpecification request = RestAssured.given();

				test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

				test.log(LogStatus.PASS, "User Hits Retrieve AllSchemas request");
				try {

					Response response = request.get(retrieveEndpoint);

					response.then().log().all();
					int statusCode = response.getStatusCode();
					String responseBody = response.getBody().asString();
					test.log(LogStatus.INFO, "Response Body: " + responseBody);

					if (statusCode == 200) {
						test.log(LogStatus.PASS, "Retrieved AllSchemas successfully statuscode:" + statusCode);

					} else if (statusCode == 404) {
						// Resource not found (HTTP 404 Not Found)
						// Handle the 404 error here
						test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

					} else if (statusCode == 500) {
						// Internal server error (HTTP 500 Internal Server Error)
						// Handle the 500 error here
						test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
					} else {
						test.log(LogStatus.FAIL, "Failed to Retrieved AllSchemas statuscode: " + statusCode);
					}
//					test.log(LogStatus.INFO, "request:" + request);

//					test.log(LogStatus.INFO, "Response Body: " + responseBody);


					System.out.println(response);

				} catch (Exception e) {
					e.printStackTrace();
					// Log the test failure in the report
					test.log(LogStatus.FAIL, "retrieved AllSchemas failed", e.getMessage());
				}
			}
			public void deleteSchema() {
				test = report.startTest("TF_WEB deleteSchema");
				 String baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
				 String tokenEndpoint = "/siva/schemas/siva?confirmDelete=true";
				 test.log(LogStatus.PASS, "deleteSchema");
				 Response response = RestAssured.given().log().all().baseUri(baseURI).get(tokenEndpoint);
				 int statusCode = response.getStatusCode();

				 if (statusCode == 200) {
				 test.log(LogStatus.PASS, "deleteSchema request successful"+ statusCode);
				 } 
				 else 
				 {
				 test.log(LogStatus.FAIL, "Failed to  deleteSchema. Status code: " + statusCode);
				}

			     report.endTest(test);
			}
			
			public void createGroup() throws IOException {

				test = report.startTest("TF_WEB createGroup");
				RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
				String tokenEndpoint = "/650d6b6d3dc39500017d13f3/groups";
				RequestSpecification request = RestAssured.given();
				test.log(LogStatus.PASS, "User Hits create Group request");
				test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + tokenEndpoint);

				try {
					// request.header("Authorization", "Bearer " + accessToken);

					// Load the Excel file
					FileInputStream file = new FileInputStream("data/testData.xlsx");
					Workbook workbook = new XSSFWorkbook(file);
					Sheet sheet = workbook.getSheet("Body"); // Replace "Sheet1" with the actual sheet name

					// Read the input JSON body from Excel
					String bodyString = sheet.getRow(3).getCell(1).getStringCellValue();

					// Convert the JSON string to a JSONObject
					JSONObject bodyJson = new JSONObject(bodyString);

					// Generate a random tenantId using UUID and set it in the JSON object
					String name = UUID.randomUUID().toString();
					bodyJson.put("name", name);
//					JSONArray universes = new JSONArray();
//					universes.put(universeid);
//					bodyJson.put("universes", universes);


					// Set the request JSON body
					request.body(bodyJson.toString());
					request.contentType(ContentType.JSON);
					// test.log(LogStatus.INFO, "Request Body: " + request);

					// test.log(LogStatus.PASS, "User valid Body");
					// Send the request and validate the response
					Response response = request.post(tokenEndpoint);

					response.then().log().all();
					String responseBody = response.getBody().asString();
					test.log(LogStatus.INFO, "Response Body: " + responseBody);
					groupid = response.jsonPath().getString("id");
					this.groupid = groupid;

					  int statusCode = response.getStatusCode();


				        if (statusCode == 200 || statusCode == 201) {
				            test.log(LogStatus.PASS, "Group Creation successfull with status code: " + response.getStatusCode());
				        } else {
				            test.log(LogStatus.FAIL, "Group Creation Test Failed with status code: " + response.getStatusCode());
				        }
				    } catch (IOException e) {
				        e.printStackTrace();
				        test.log(LogStatus.FAIL, "Group Creation  FAILED", e.getMessage());
				    }
				}

	public void getGroupId() {

		test = report.startTest("TF_WEB apitoretrieveGroup");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/groups/" + groupid;

		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		test.log(LogStatus.PASS, "User Hits Retrieve GroupId request");
		try {
			// Add the requesterType as a query parameter

			// Send the request and validate the response
			request.header("Authorization", "Bearer " + accessToken);

			Response response = request.get(retrieveEndpoint);

			response.then().log().all();
			int statusCode = response.getStatusCode();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			if (statusCode == 200) {
				test.log(LogStatus.PASS, "Retrieved GroupId successfully statuscode:" + statusCode);

			} else if (statusCode == 404) {
				// Resource not found (HTTP 404 Not Found)
				// Handle the 404 error here
				test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

			} else if (statusCode == 500) {
				// Internal server error (HTTP 500 Internal Server Error)
				// Handle the 500 error here
				test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
			} else {
				test.log(LogStatus.FAIL, "Failed to Retrieved GroupId statuscode: " + statusCode);
			}

//			test.log(LogStatus.INFO, "Response Body: " + responseBody);


			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
			// Log the test failure in the report
			test.log(LogStatus.FAIL, "retrieved groupId failed", e.getMessage());
		}
	}


		public void getAllGroups() {
		    test = report.startTest("TF_WEB apitoretrieveAllGroups");

		    RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		    String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/groups/metadata/list";

		    RequestSpecification request = RestAssured.given();

		    test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		    test.log(LogStatus.PASS, "User Hits Retrieve AllGroups request");
		    try {
		        Response response = request.get(retrieveEndpoint);

		        // Log response details
		        response.then().log().all();
		        
		        int statusCode = response.getStatusCode();
		        String responseBody = response.getBody().asString();
		        test.log(LogStatus.INFO, "Response Body: " + responseBody);

		        if (statusCode == 200) {
		            test.log(LogStatus.PASS, "Retrieved AllGroups successfully statuscode: " + statusCode);
		        } else {
		            test.log(LogStatus.FAIL, "Failed to Retrieve AllGroups, statuscode: " + statusCode);
		        }
		    } catch (Exception e) {
		        // Log any exceptions that occur during the request
		        test.log(LogStatus.ERROR, "An exception occurred: " + e.getMessage());
		        // Handle the exception appropriately (e.g., logging, reporting, re-throwing)
		        // Consider logging the stack trace for debugging purposes
		        e.printStackTrace();
		    }
		}

		
	public void deleteGroup() {
		test = report.startTest("TF_WEB deleteGroup");
		 String baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		 String tokenEndpoint = "/siva/groups/siva";
		 test.log(LogStatus.PASS, "deleteGroup");
		 Response response = RestAssured.given().log().all().baseUri(baseURI).get(tokenEndpoint);
		 int statusCode = response.getStatusCode();

		 if (statusCode == 200) {
		 test.log(LogStatus.PASS, "deleteGroup request successful"+ statusCode);
		 } 
		 else 
		 {
		 test.log(LogStatus.FAIL, "Failed to  deleteGroup. Status code: " + statusCode);
		}

	     report.endTest(test);
	}
	public void createContext() throws IOException {

		test = report.startTest("TF_WEB createContext");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		String tokenEndpoint = "/650d6b6d3dc39500017d13f3/contexts";
		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.PASS, "User Hits create Context request");
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + tokenEndpoint);

		try {
			// request.header("Authorization", "Bearer " + accessToken);

			// Load the Excel file
			FileInputStream file = new FileInputStream("data/testData.xlsx");
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet("Body"); // Replace "Sheet1" with the actual sheet name

			// Read the input JSON body from Excel
			String bodyString = sheet.getRow(5).getCell(1).getStringCellValue();

			// Convert the JSON string to a JSONObject
			JSONObject bodyJson = new JSONObject(bodyString);

			// Generate a random tenantId using UUID and set it in the JSON object
			String name = UUID.randomUUID().toString();
			bodyJson.put("name", name);
//			JSONArray universes = new JSONArray();
//			universes.put(universeid);
//			bodyJson.put("universes", universes);


			// Set the request JSON body
			request.body(bodyJson.toString());
			request.contentType(ContentType.JSON);
			// test.log(LogStatus.INFO, "Request Body: " + request);

			// test.log(LogStatus.PASS, "User valid Body");
			// Send the request and validate the response
			Response response = request.post(tokenEndpoint);

			response.then().log().all();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);
			contextid = response.jsonPath().getString("id");
			this.contextid = contextid;

			  int statusCode = response.getStatusCode();


		        if (statusCode == 200 || statusCode == 201) {
		            test.log(LogStatus.PASS, "Context Creation successfull with status code: " + response.getStatusCode());
		        } else {
		            test.log(LogStatus.FAIL, "Context Creation Test Failed", "Response statuscode: " + response.getStatusCode());
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        test.log(LogStatus.FAIL, "Context Creation  FAILED", e.getMessage());
		    }
		}


	public void getContextId() {

		test = report.startTest("TF_WEB apitoretrieveContextId");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/contexts/" + contextid;

		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		test.log(LogStatus.PASS, "User Hits Retrieve ContextId request");
		try {
			request.header("Authorization", "Bearer " + accessToken);

			Response response = request.get(retrieveEndpoint);

			response.then().log().all();
			int statusCode = response.getStatusCode();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			if (statusCode == 200) {
				test.log(LogStatus.PASS, "Retrieved ContextId successfully statuscode:" + statusCode);

			}  else {
				test.log(LogStatus.FAIL, "Failed to Retrieved ContextId statuscode: " + statusCode);
			}
//			test.log(LogStatus.INFO, "Response Body: " + responseBody);


			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
			// Log the test failure in the report
			test.log(LogStatus.FAIL, "retrieved ContextId failed", e.getMessage());
		}
	}
	public void getAllContexts() {

		test = report.startTest("TF_WEB apitoretrieveAllContexts");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
//    String universeId = "v1.0/b4218e25-b2b0-4789-93f6-382b02516dbc/universes/+universeId";
		// String requesterType = "TENANT";

		// Update the URL to include the universeId as a path parameter
		String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/undefined/contexts/list?ownedOnly=false\n";

		RequestSpecification request = RestAssured.given();

		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		test.log(LogStatus.PASS, "User Hits Retrieve AllContexts request");
		try {
			// Add the requesterType as a query parameter

			// Send the request and validate the response
//			request.header("Authorization", "Bearer " + accessToken);

			Response response = request.get(retrieveEndpoint);

			response.then().log().all();
			int statusCode = response.getStatusCode();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			if (statusCode == 200) {
				test.log(LogStatus.PASS, "Retrieved AllContexts successfully statuscode:" + statusCode);
} 
			else {
				test.log(LogStatus.FAIL, "Failed to Retrieved AllContexts statuscode: " + statusCode);
			}
//			test.log(LogStatus.INFO, "request:" + request);
//
//			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			// Assert.assertEquals(response.statusCode(), 200);

			// Log the test result in the report

			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
			// Log the test failure in the report
			test.log(LogStatus.FAIL, "retrieved contexts failed", e.getMessage());
		}
	}
		public void deleteContext() {
			test = report.startTest("TF_WEB deleteContext");
			 String baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
			 String tokenEndpoint = "/siva/contexts/siva";
			 test.log(LogStatus.PASS, "deleteContext");
			 Response response = RestAssured.given().log().all().baseUri(baseURI).get(tokenEndpoint);
			 int statusCode = response.getStatusCode();

			 if (statusCode == 200) {
			 test.log(LogStatus.PASS, "deleteContext request successful"+ statusCode);
			 } 
			 else 
			 {
			 test.log(LogStatus.FAIL, "Failed to  deleteContext. Status code: " + statusCode);
			}

		     report.endTest(test);
		}

		public void createAq() throws IOException {

			test = report.startTest("TF_WEB createAq");
			RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
			String tokenEndpoint = "/650d6b6d3dc39500017d13f3/analytic-queries";
			RequestSpecification request = RestAssured.given();
			test.log(LogStatus.PASS, "User Hits create Aq request");
			test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + tokenEndpoint);

			try {
				// request.header("Authorization", "Bearer " + accessToken);

				// Load the Excel file
				FileInputStream file = new FileInputStream("data/testData.xlsx");
				Workbook workbook = new XSSFWorkbook(file);
				Sheet sheet = workbook.getSheet("Body"); // Replace "Sheet1" with the actual sheet name

				// Read the input JSON body from Excel
				String bodyString = sheet.getRow(4).getCell(1).getStringCellValue();

				// Convert the JSON string to a JSONObject
				JSONObject bodyJson = new JSONObject(bodyString);

				// Generate a random tenantId using UUID and set it in the JSON object
				String name = UUID.randomUUID().toString();
				bodyJson.put("name", name);
//				JSONArray universes = new JSONArray();
//				universes.put(universeid);
//				bodyJson.put("universes", universes);


				// Set the request JSON body
				request.body(bodyJson.toString());
				request.contentType(ContentType.JSON);
				// test.log(LogStatus.INFO, "Request Body: " + request);

				// test.log(LogStatus.PASS, "User valid Body");
				// Send the request and validate the response
				Response response = request.post(tokenEndpoint);

				response.then().log().all();
				String responseBody = response.getBody().asString();
				test.log(LogStatus.INFO, "Response Body: " + responseBody);
				aqid = response.jsonPath().getString("id");
				this.aqid = aqid;

				  int statusCode = response.getStatusCode();


			        if (statusCode == 200 || statusCode == 201) {
			            test.log(LogStatus.PASS, "Aq Creation successfull with status code: " + response.getStatusCode());
			        } else {
			            test.log(LogStatus.FAIL, "Aq Creation successfull with status code: " + response.getStatusCode());
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			        test.log(LogStatus.FAIL, "Aq Creation  FAILED", e.getMessage());
			    }
			}



	
	public void getAqId() {

		test = report.startTest("TF_WEB retriveAqById");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
//    String universeId = "v1.0/b4218e25-b2b0-4789-93f6-382b02516dbc/universes/+universeId";
		// String requesterType = "TENANT";

		// Update the URL to include the universeId as a path parameter
		String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/analytic-queries/" + aqid;

		RequestSpecification request = RestAssured.given();

		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		test.log(LogStatus.PASS, "User Hits Retrieve AqId request");
		try {
			// Add the requesterType as a query parameter

			// Send the request and validate the response
//			request.header("Authorization", "Bearer " + accessToken);

			Response response = request.get(retrieveEndpoint);

			response.then().log().all();
			int statusCode = response.getStatusCode();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			if (statusCode == 200) {
				test.log(LogStatus.PASS, "Retrieved AqId successfully statuscode:" + statusCode);

			
			} else {
				test.log(LogStatus.FAIL, "Failed to Retrieved AqId statuscode: " + statusCode);
			}
//			test.log(LogStatus.INFO, "request:" + request);
//
//			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			// Assert.assertEquals(response.statusCode(), 200);

			// Log the test result in the report
			//test.log(LogStatus.PASS, "retrieved AQID", "Response statuscode: " + response.statusCode());

			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
			// Log the test failure in the report
			test.log(LogStatus.FAIL, "retrieved AQID failed", e.getMessage());
		}
	}

	public void getAllAqs() {

		test = report.startTest("TF_WEB retriveAllAqs");
		RestAssured.baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
//    String universeId = "v1.0/b4218e25-b2b0-4789-93f6-382b02516dbc/universes/+universeId";
		// String requesterType = "TENANT";

		// Update the URL to include the universeId as a path parameter
		String retrieveEndpoint = "/650d6b6d3dc39500017d13f3/analytic-queries/metadata/list\n";

		RequestSpecification request = RestAssured.given();
		test.log(LogStatus.INFO, "Request URL:" + "   " + RestAssured.baseURI + retrieveEndpoint);

		test.log(LogStatus.PASS, "User Hits Retrieve AllAq's request");
		try {
			// Add the requesterType as a query parameter

			// Send the request and validate the response
			request.header("Authorization", "Bearer " + accessToken);

			Response response = request.get(retrieveEndpoint);

			response.then().log().all();
			int statusCode = response.getStatusCode();
			String responseBody = response.getBody().asString();
			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			if (statusCode == 200) {
				test.log(LogStatus.PASS, "Retrieved AllAqs successfully statuscode:" + statusCode);

			} else if (statusCode == 404) {
				// Resource not found (HTTP 404 Not Found)
				// Handle the 404 error here
				test.log(LogStatus.FAIL, "Resource not found: " + statusCode);

			} else if (statusCode == 500) {
				// Internal server error (HTTP 500 Internal Server Error)
				// Handle the 500 error here
				test.log(LogStatus.FAIL, "Internal server error: " + statusCode);
			} else {
				test.log(LogStatus.FAIL, "Failed to Retrieved AllAqs statuscode: " + statusCode);
			}
//			test.log(LogStatus.INFO, "Response Body: " + responseBody);

			// Assert.assertEquals(response.statusCode(), 200);

			// Log the test result in the report

			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
			// Log the test failure in the report
			test.log(LogStatus.FAIL, "retrieved AllAq's failed", e.getMessage());
		}
	}
	public void deleteAq() {
		test = report.startTest("TF_WEB deleteAq");
		 String baseURI = "https://ig.aidtaas.com/tf-web/v1.0";
		 String tokenEndpoint = "/siva/analytic-queries/siva";
		 test.log(LogStatus.PASS, "deleteAq");
		 Response response = RestAssured.given().log().all().baseUri(baseURI).get(tokenEndpoint);
		 int statusCode = response.getStatusCode();

		 if (statusCode == 200) {
		 test.log(LogStatus.PASS, "deleteAq request successful"+ statusCode);
		 } 
		 else 
		 {
		 test.log(LogStatus.FAIL, "Failed to  deleteAq. Status code: " + statusCode);
		}

	     report.endTest(test);
	}


}
