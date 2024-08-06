package com.ems.application.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportExcelService {
    public HashMap<Integer, ArrayList<Object>> importFileExcelBySheet(MultipartFile file, Integer sheetIndex)
            throws IOException {
        HashMap<Integer, ArrayList<Object>> data = new HashMap<>();
        InputStream fis = file.getInputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            int endCell = sheet.getRow(0).getLastCellNum();
            int endRow = sheet.getLastRowNum();
            System.out.println("endCell: " + endCell);
            System.out.println("endRow: " + endRow);
            int rowIndex = 1;
            for (int i = 1; i <= endRow; i++) {
                Row row = sheet.getRow(i);
                ArrayList<Object> columns = new ArrayList<>();
                int checkNull = 1;
                for (int j = 0; j < endCell; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.STRING) {
                            if (!cell.getStringCellValue().isEmpty()) {
                                columns.add(cell.getStringCellValue());
                            }
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            columns.add(cell.getNumericCellValue());
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                                columns.add(cell.getNumericCellValue());
                            } else if (cell.getCachedFormulaResultType() == CellType.STRING
                                    && !cell.getStringCellValue().isEmpty()) {
                                columns.add(cell.getStringCellValue());
                            } else {
                                columns.add("");
                                checkNull++;
                            }
                        } else {
                            columns.add("");
                            checkNull++;
                        }
                    } else {
                        columns.add("");
                        checkNull++;
                    }
                }
                if (!columns.isEmpty() && (checkNull != endCell)) {
                    data.put(rowIndex, columns);
                    rowIndex++;
                }
            }
        }
        return data;
    }
}
