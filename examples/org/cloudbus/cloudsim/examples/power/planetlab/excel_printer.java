/**
 * 
 */
package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author Administrator /** To print the values in excel sheet (by Ala'anzy).
 * 
 * @return Printing (no return)
 *
 *
 */
public class excel_printer {

	private String sheetname;
	private HSSFWorkbook workbook;
	private FileOutputStream fileOut;

	public void printer(String s) throws IOException {
		try {
			System.out.println("Suseccfully >> Go to D drive to see the result ");
			FileOutputStream fileOut = new FileOutputStream("D://file.xls");
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Enizi");

			HSSFCellStyle headcellStyle = workbook.createCellStyle();
			HSSFRow row1 = worksheet.createRow((short) 0);

			HSSFCell cellS1 = row1.createCell((short) 0);
			cellS1.setCellValue("Time");
			cellS1.setCellStyle(headcellStyle);

			HSSFCell cellB1 = row1.createCell((short) 1);
			cellB1.setCellValue("Host");
			cellB1.setCellStyle(headcellStyle);

			HSSFCell cellD1 = row1.createCell((short) 2);
			cellD1.setCellValue("Status");
			cellD1.setCellStyle(headcellStyle);

			HSSFCell cellH1 = row1.createCell((short) 3);
			cellH1.setCellValue("result");
			cellH1.setCellStyle(headcellStyle);

			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printer(String sheetname, int itration, double Time, int Hostno, double result)
			throws IOException, InvalidFormatException {
		setSheetname(sheetname);
		if (itration == 1) {
			System.out.println("I am in intilizing phase... ");
			FileOutputStream fileOut = new FileOutputStream("D://ffile.xls");
			setFileOut(fileOut);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet(sheetname);
			setWorkbook(workbook);
			HSSFCellStyle headcellStyle = workbook.createCellStyle();
			HSSFRow Row = worksheet.createRow(0);
			HSSFCell Column = Row.createCell(1);
			Column.setCellStyle(headcellStyle);
			Column.setCellValue("Time");

			Column = Row.createCell(2);
			Column.setCellValue("Host");

			Column = Row.createCell(3);
			Column.setCellValue("Status");

			Column = Row.createCell(4);
			Column.setCellValue("Result");
			System.out.println("I am the header creation phase ");
			workbook.write(fileOut);
			fileOut.close();
			// System.exit(0);
		}

		else {
			System.out.println("I am in workbook is not null phase..  ");
			// String excelFilePath = "JavaBooks.xls";
			try {
				FileInputStream inputStream = new FileInputStream(new File("D://ffile.xls"));
				Workbook WB = WorkbookFactory.create(inputStream);
				Sheet sheet = WB.getSheetAt(0);
				WB.getSheetAt(0);
				Object[][] bookData = { { Time, "Host#" + Hostno, "utilization is", result }, };

				int rowCount = sheet.getLastRowNum();

				for (Object[] aBook : bookData) {
					Row row = sheet.createRow(++rowCount);
					int columnCount = 0;

					Cell cell = row.createCell(columnCount);
					// cell.setCellValue(rowCount);

					for (Object field : aBook) {
						cell = row.createCell(++columnCount);
						if (field instanceof String) {
							cell.setCellValue((String) field);
						} else if (field instanceof Double) {
							cell.setCellValue((Double) field);
						}
					}

				}

				inputStream.close();
				FileOutputStream outputStream = new FileOutputStream("D://ffile.xls");
				WB.write(outputStream);
				WB.close();
				outputStream.close();

			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}

		}

	}
	
	
	/**
	 * @param print the power result 
	 */
	public void printer(String file_name,int repetition_count, String data1, String data2, String data3, String data4, String data5,String data6,String data7)
			throws IOException, InvalidFormatException {
		
		//if (repetition_count == 1 ) {
			//System.out.println("I am in intilizing phase... ");
		//	FileOutputStream fileOut = new FileOutputStream("D://power_algrithm_result.xls");
		Path path = Paths.get(file_name);
		if (Files.notExists(path)==true) {// to print on the end of the current file_name
		//File file=new File();
		FileOutputStream fileOut = new FileOutputStream(file_name);
		setFileOut(fileOut);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet(sheetname);
			setWorkbook(workbook);
			HSSFCellStyle headcellStyle = workbook.createCellStyle();
			HSSFRow Row = worksheet.createRow(0);
			HSSFCell Column = Row.createCell(1);
			Column.setCellStyle(headcellStyle);
			Column.setCellValue(data1);

			Column = Row.createCell(2);
			Column.setCellValue(data2);
			
			Column = Row.createCell(3);
			Column.setCellValue(data3);

			Column = Row.createCell(4);
			Column.setCellValue(data4);

			Column = Row.createCell(5);
			Column.setCellValue(data5);
			
			Column = Row.createCell(6);
			Column.setCellValue(data6);
			
			Column = Row.createCell(7);
			Column.setCellValue(data7);
						//System.out.println("I am the header creation phase ");
			workbook.write(fileOut);
			fileOut.close();	
			}
		
	}
		

	public void printer(String file_name, int repetition_count, double data1, double data2, double data3, double data4, double data5,double data6,double data7)
			throws IOException, InvalidFormatException {
		//if  (repetition_count == -1|| repetition_count>1){
			try {
			//File inputStream= new File("D://power_algrithm_result.xls");
				FileInputStream inputStream = new FileInputStream(new File(file_name));
				Workbook WB = WorkbookFactory.create(inputStream);
				Sheet sheet = WB.getSheetAt(0); // for where u should get the file line // + due to the first repetition will be the headers.
				Object[][] bookData = {{data1,data2, data3,data4,data5,data6,data7}, };
				int rowCount = sheet.getLastRowNum();
				for (Object[] aBook : bookData) {
					Row row = sheet.createRow(++rowCount);
					int columnCount = 0;
					Cell cell = row.createCell(columnCount);
					for (Object field : aBook) {
						cell = row.createCell(++columnCount);
						if (field instanceof String) {
							cell.setCellValue((String) field);
						} else if (field instanceof Double) {
							cell.setCellValue((Double) field);
						}
					}
				}
				inputStream.close();
				FileOutputStream outputStream = new FileOutputStream(file_name);
				WB.write(outputStream);
				WB.close();
				outputStream.close();

			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		//}
	}

	
	/**
	 * @return the sheetname
	 */
	public String getSheetname() {
		return sheetname;
	}

	/**
	 * @param sheetname the sheetname to set
	 */
	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	/**
	 * @return the workbook
	 */
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	/**
	 * @param workbook the workbook to set
	 */
	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	/**
	 * @return the fileOut
	 */
	public FileOutputStream getFileOut() {
		return fileOut;
	}

	/**
	 * @param fileOut the fileOut to set
	 */
	public void setFileOut(FileOutputStream fileOut) {
		this.fileOut = fileOut;
	}

}
