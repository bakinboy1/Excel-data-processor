package emailer;

import javax.swing.JFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.net.URISyntaxException;

//is it convoluted? yes. does it work? somehow, also yes
//author: fabian hucke
//last edit 12/17/2019
public class Main extends JFrame {

	// initialize a bunch of variables
	private static final long serialVersionUID = 1L;

	// jj = sheet #
	int jj = 0;
	String state = "";
	String city = "";
	String department = "";
	String industry = "";
	String title = "";
	String entryLevel = "";
	String midLevel = "";
	String seniorLevel = "";
	String cityState = "";
	String jsonDoc = "";
	String consolidatedJsonDoc = "const data = {";
	String jsonLocation = "";
	String jsonName = "";
	int cellJump = 0;
	int rowJump = 1;
	int arrPosition = 0;
	int lastRow = 0;
	int moduloLastRow = 0;
	int lastSheet = 0;
	int sheetAt = 0;
	boolean isRowEmpty;
	boolean last = false;
	PrintWriter writer;
	FileOutputStream jsonFile;
	FileOutputStream consolidatedJsonFile;
	// delay naming until reader starts
	public static String sheetName;
	public static XSSFSheet sheet;
	public static XSSFSheet consolidatedSheet;

	// connect program to desired excel file
	XSSFWorkbook consolidatedBook = new XSSFWorkbook();
	private static XSSFWorkbook workbook;

	// runs the console, which runs the gogo()

	public static void main(String[] args) throws URISyntaxException {

		Console.run2();
		// TESTING - System.out.println("done");
	}

	public static Main main = new Main();

	// create arraylist to store excel file
	public void gogo() throws IOException {

		// temporary variables to store excel data for 1 row.
		// probably some way to make it more efficient than using nextCell. go by column
		// # or something
		FileInputStream excelFile;
		try {

			// get the file and assign it to excelFile
			excelFile = new FileInputStream(new File(FileName.getFile()));
			
			// open up the excelfile workbook
			workbook = new XSSFWorkbook(excelFile);

			
			// get the # of sheets in the workbook for iterator
			lastSheet = workbook.getNumberOfSheets();

		} catch (FileNotFoundException e1) {

			// Auto-generated catch block
			e1.printStackTrace();
		}

		// while iterator jj is less than lastSheet value, continue iterating through
		// sheets
		while (jj < lastSheet) {

			// creates excel workbook to write data to
			XSSFWorkbook writebook = new XSSFWorkbook();

			// boolean to control print of last line only 1 time
			last = false;

			// set the name of the sheet in the write file to = sheet name at jj positon in
			// the read file
			sheetName = workbook.getSheetName(jj);

			// assign xlsx sheet variable the corresponding name to the sheet in the
			// document being read
			sheet = writebook.createSheet(sheetName);

			// assign xlsx sheet variable the corresponding name to the sheet in the
			// document being read--consolidated file
			consolidatedSheet = consolidatedBook.createSheet(sheetName);

			// gets workbook sheet at position jj
			Sheet datatypeSheet = workbook.getSheetAt(jj);
			arrPosition = 0;
			lastRow = 0;

			
			// initialize row variables for individual documents --r
						

			////////////
			// get the last row #-- pain in the butt, will often fail to differentiate
			//////////// between blank and filled rows
			lastRow = datatypeSheet.getLastRowNum();

			
			// TESTING - System.out.println(lastRow);

			// get modulo position of last row in page
			moduloLastRow = lastRow % 100;

			// TESTING - System.out.println(workbook.getSheetName(jj));

			// initialize iterator for new rows
			Iterator<Row> iterator = datatypeSheet.iterator();
			datatypeSheet.setAutoFilter(new CellRangeAddress(1, lastRow, 1, 1));

			// while there is a row with data it will keep going
			while (iterator.hasNext()) {
				state = "";
				city = "";

				// TESTING - System.out.println(arrPosition + " " + lastRow);

				// if array is under 101 in size or arrayPosition doesnt equal last row, it'll
				// iterate through the cells
				if (Arr.getArr().getList().size()- 1  < 101 || arrPosition != lastRow) {

					// increment var reset. stores iterator position
					cellJump = 0;

					// initialize row iterator
					Row currentRow = iterator.next();

					// initialize cell iterator
					Iterator<Cell> cellIterator = currentRow.iterator();

					// while there is data in a cell, it will keep iterating to the right to the
					// next cell
					while (cellIterator.hasNext()) {

						// increments column/cell value
						cellJump++;

						// create cell object
						Cell currentCell = cellIterator.next();

						// imported function Dataformatter, honestly i forgot what it does. might be
						// vestigial
						DataFormatter df = new DataFormatter();
						String cellValue = df.formatCellValue(currentCell);

						// if cell contains string & isnt null value, will cellJump to subgroup of if
						// statements
						if (currentCell.getCellType() == CellType.STRING
								|| currentCell.getCellType() == CellType.NUMERIC) {

							// //TESTING - System.out.println(cellJump);

							// changes cell data to string
							// depending on position, store in corresponding temp value
							// probably a better way to do this

							// cell 1 = state
							if (cellJump == 1) {
								state = cellValue;
								// capitalizes state
								state = state.replace("d.c.", "D.C.");
								state = capitalizeWord(state);
								

							}

							// cell 2 = city
							if (cellJump == 2) {

								city = cellValue;

								// various data cleanups
								city = city.replace("metro", "").replace("pennsylvania", "").replace(" ,", "")
										.replace("county", "").replace("area", "").replace("/", "/ ")
										.replace("-", "- ").replace("d.c.", "D.C.");

								// capitalizes ' ' seperated words in city
								city = capitalizeWord(city);

								// puts / and - back in original positions after capitalization fix
								city = city.replace("/ ", "/").replace("- ", "-");

								// handles orange county, where they still want county in
								if (city.contains("Orange")) {
									city = city + " County";
								}

							}

							// if both fields are filled put a comma between
							else if (state != "" && city != "") {

								cityState = city + ", " + state;

								// gets rid of blank space between state and comma
								cityState = cityState.replace(" , ", ", ");

							}

							// if one field is empty just combine
							else if (state != null || city != null) {
								cityState = city + state;
								cityState = cityState.replace("metro", "").replace("pennsylvania", "").replace(" ,", "")
										.replace("county", "").replace("area", "");

							}

							// cell 3= industry
							// cell 4 = department
							if (cellJump == 3) {
								industry = cellValue;
							}

							// cell 4 = department
							if (cellJump == 4 && cellValue != "") {
								department = cellValue;
							}

							// cell 5 = title
							if (cellJump == 5) {
								title = cellValue;
							}

							// cell 6 = entrylevel
							if (cellJump == 6) {
								entryLevel = cellValue;
							}

							// cell 7 = midlevel
							if (cellJump == 7) {
								midLevel = cellValue;
							}

							// cell 8 = seniorlevel
							if (cellJump == 8) {
								seniorLevel = cellValue;
							}

						}

						// if cellIncrementer encounters empty cell it breaks loop and moves down to
						// next row
						else if (currentCell.getStringCellValue() == null && cellJump > 4) {

							break;
						}
					}

					// adds temp data to arraylist
					Arr.getArr().getList().add(
							new Person(state, city, industry, department, title, entryLevel, midLevel, seniorLevel, cityState));

					// increment arrPosition- needed?
					arrPosition++;
				}

				// if arrposition -- lastrow or array size is 101 or greater this runs
				else {

					// if rowjump >0 rowjump=0
					if (arrPosition - Arr.getArr().getList().size() < 0) {
						rowJump = 0;
					} else {
						rowJump = arrPosition - Arr.getArr().getList().size();
					}

					// fill array
					// create the row and increment the row index
					printToExcel();

					// reset
				}
			}

			// print last row of source file. janky AF but it works
			// creates the actual file
			try {
				FileOutputStream outputStream = new FileOutputStream(Console.destinationFolderString + "\\" + sheetName + ".xlsx");

				// TESTING - System.out.println(FileName.getFile() + sheetName + ".xlsx");
				datatypeSheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 1));
				datatypeSheet.createFreezePane(0, 1);
				writebook.write(outputStream);
				writebook.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// consolidated file
			try {
				FileOutputStream outputStream = new FileOutputStream(Console.destinationFolderString + "\\" + " ConsolidatedData.xlsx");
				
				// TESTING - System.out.println(FileName.getFile() + sheetName + ".xlsx");
				consolidatedBook.write(outputStream);
				

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// end of page generator
			////////////////////////////////////////////////////////////////////////////////////////////////
			// extra clear needed. clear in the function still leaves last row of previous
			// page for some reason when printing to new page
			Arr.getArr().getList().clear();
			jj++;
		}

		
		
		// closes the consolidated workbook
		consolidatedBook.close();
		
		consolidatedJsonDoc+="\n};";
		consolidatedJsonFile.write(consolidatedJsonDoc.getBytes());
		
		consolidatedJsonFile.close();
	}

	// print contents of arraylist to the excel file.
	// probably could be made cleaner
	public void printToExcel() throws IOException {

		// the for loop
		for (int i = 0; i <= Arr.getArr().getList().size(); i++) {

			// initialize row variables for individual documents --r
			Row r = sheet.createRow(rowJump);

			// initialize row variables for consolidated document --c
			Row c = consolidatedSheet.createRow(rowJump);

			// If statement-prevents it from filling in blank cells counted from original
			// document with the last value repeatedly
			if (i < Arr.getArr().getList().size() - 1) {

				// checks to make sure row values aren't repeating- repeating indicates it hit
				// the last row
				if (Arr.getArr().getList().get(i).getEntryLevel() != Arr.getArr().getList().get(i + 1).getEntryLevel()
				 || Arr.getArr().getList().get(i).getMidLevel() != Arr.getArr().getList().get(i + 1).getMidLevel()
			     || Arr.getArr().getList().get(i).getSeniorLevel() != Arr.getArr().getList().get(i + 1).getSeniorLevel()) {

//				if(Arr.getArr().getList().get(i+1).toString() != null) {
					// fills individual document

					// fill in values for first row
					if (i == 0) {

						// set values for first row since it's the title row

						Arr.getArr().getList().get(i).setCityState("Location");
						Arr.getArr().getList().get(i).setDepartment("Department");
						Arr.getArr().getList().get(i).setTitle("Title");
						Arr.getArr().getList().get(i).setEntryLevel("Low");
						Arr.getArr().getList().get(i).setMidLevel("Mid");
						Arr.getArr().getList().get(i).setSeniorLevel("High");

						// fills individual document first row
						r.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
						r.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
						r.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
						r.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
						r.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
						r.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());

						// fills consolidated document first row
						c.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
						c.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
						c.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
						c.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
						c.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
						c.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());


					}

					// fills in the rest of the rows
					else {
						r.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
						r.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
						r.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
						r.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
						r.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
						r.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());

						// fills consolidated document
						c.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
						c.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
						c.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
						c.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
						c.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
						c.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());

						// handle adding info to string to print to json

						// "" is for first row check

						// rest of the rows past row 1

						// if i=1 it creates the initial starting header
						if (i == 1) {
							consolidatedJsonDoc+="\n" + "\"" + Arr.getArr().getList().get(i).getIndustry() + "\":[\n";
							jsonDoc += "{\n" + "\"" + Arr.getArr().getList().get(i).getCityState() + "\":[\n";
							
						}
						// if i>1 and city state value does NOT equal previous city state value then put
						// in another keyed header
						else if (i > 1 && 
						!Arr.getArr().getList().get(i - 1).getCityState().contentEquals(Arr.getArr().getList().get(i).getCityState())) {

							// closes the previous header brackets and starts a new keyed header
							jsonDoc += "\n],\n" + "\"" + Arr.getArr().getList().get(i).getCityState() + "\":[\n";
						}

						// fills the json string with the necessary formatting/data
						jsonDoc += "{\n" + "     \"Department\": " + "\""
								+ Arr.getArr().getList().get(i).getDepartment() + "\",";
						jsonDoc += "\n" + "     \"Title\": " + "\"" + Arr.getArr().getList().get(i).getTitle() + "\",";
						jsonDoc += "\n" + "     \"Low\": " + "\"" + Arr.getArr().getList().get(i).getEntryLevel()
								+ "\",";
						jsonDoc += "\n" + "     \"Mid\": " + "\"" + Arr.getArr().getList().get(i).getMidLevel() + "\",";
						jsonDoc += "\n" + "     \"High\": " + "\"" + Arr.getArr().getList().get(i).getSeniorLevel()
								+ "\"}\n";
						jsonLocation = Arr.getArr().getList().get(i).getCityState();

						// if the location is the same then add a comma between body segments (used to
						// avoid comma addition at last body group)
						if (Arr.getArr().getList().get(i + 1).getCityState().contentEquals(Arr.getArr().getList().get(i).getCityState()) ) {

							jsonDoc += ",\n";
							// adds closing ] to location

						}

					}

				} else if (last == false ) {

					// fills individual document
					
					r.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
					r.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
					r.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
					r.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
					r.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
					r.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());

					// fills consolidated document
					c.createCell(0).setCellValue(Arr.getArr().getList().get(i).getCityState());
					c.createCell(1).setCellValue(Arr.getArr().getList().get(i).getDepartment());
					c.createCell(2).setCellValue(Arr.getArr().getList().get(i).getTitle());
					c.createCell(3).setCellValue(Arr.getArr().getList().get(i).getEntryLevel());
					c.createCell(4).setCellValue(Arr.getArr().getList().get(i).getMidLevel());
					c.createCell(5).setCellValue(Arr.getArr().getList().get(i).getSeniorLevel());
					last = true;

					
					// adds data as standard for last row but also adds closing } to the file at the
					// end
					jsonDoc += "{\n" + "     \"Department\": " + "\"" + Arr.getArr().getList().get(i).getDepartment()+ "\",";
					jsonDoc += "\n" + "     \"Title\": " + "\"" + Arr.getArr().getList().get(i).getTitle() + "\",";
					jsonDoc += "\n" + "     \"Low\": " + "\"" + Arr.getArr().getList().get(i).getEntryLevel() + "\",";
					jsonDoc += "\n" + "     \"Mid\": " + "\"" + Arr.getArr().getList().get(i).getMidLevel() + "\",";
					jsonDoc += "\n" + "     \"High\": " + "\"" + Arr.getArr().getList().get(i).getSeniorLevel() + "\"}\n";
					consolidatedJsonDoc+= jsonDoc;
					consolidatedJsonDoc+="\n]\n"; 
					jsonDoc += "\n]\n}\n";
					System.out.println();
					
				}

			}
			
			rowJump++;
		}
		if(jj<lastSheet-1) {
		consolidatedJsonDoc+="\n}\n],"; 
		System.out.println("jj "+jj+" lastsheet " +lastSheet);
		}
		else {consolidatedJsonDoc+="\n}\n]";}
		
		jsonName = new String(sheetName);
		jsonFile = new FileOutputStream(Console.destinationFolderString +"\\" + jsonName + ".json", true);
		jsonFile.write(jsonDoc.getBytes());
		Arr.getArr().getList().clear();
		jsonDoc = "";
		jsonFile.close();
		
		consolidatedJsonFile = new FileOutputStream(Console.destinationFolderString +"\\" + "Consolidated" + ".js", true);
		consolidatedJsonFile.write(consolidatedJsonDoc.getBytes());
		consolidatedJsonDoc="";
		//consolidatedJsonFile.close();
		System.out.println("print");
	}

	// capitalize words function
	public static String capitalizeWord(String str) {

		// splits string into array by ' '
		String words[] = str.split("\\s");
		String capitalizeWord = "";

		// try catch block needed to prevent it from crashing. doesnt actually result in
		// errors in the data for some reason? throws boston?
		try {

			// capitalize each word in 'words' array
			for (String w : words) {

				String first = w.substring(0, 1);
				String afterfirst = w.substring(1);

				// adds capitalized array words into the 'capitalizeWord' string
				capitalizeWord += first.toUpperCase() + afterfirst + " ";

			}
		}
		// prints boston for some reason??? its not even the previous city value before
		// throwing catch
		catch (Exception e) {
			System.out.println(str);
		}

		// trim necessary? probably not but its there
		return capitalizeWord.trim();

	}
}