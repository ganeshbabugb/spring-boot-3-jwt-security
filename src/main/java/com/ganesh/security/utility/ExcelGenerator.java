package com.ganesh.security.utility;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {

    private void writeHeader(Sheet sheet, String[] headers) {
        Row row = sheet.createRow(0);
        CellStyle style = createHeaderStyle(sheet);
        for (int column = 0; column < headers.length; column++) {
            createCell(row, column, headers[column], style);
        }
    }

    private CellStyle createHeaderStyle(Sheet sheet) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createContentStyle(Sheet sheet) {
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer || valueOfCell instanceof Long)
            cell.setCellValue(((Number) valueOfCell).doubleValue());
        else if (valueOfCell instanceof String) cell.setCellValue((String) valueOfCell);
        else if (valueOfCell instanceof Boolean) cell.setCellValue((Boolean) valueOfCell);
        else cell.setCellValue(valueOfCell != null ? valueOfCell.toString() : "");
        cell.setCellStyle(style);
    }

    private void write(Sheet sheet, List<Map<String, Object>> data, CellStyle style, String[] headers) {
        int rowCount = 1;
        for (Map<String, Object> rowMap : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            for (String header : headers) {
                Object cellValue = rowMap.get(header);
                createCell(row, columnCount++, cellValue, style);
            }
        }
    }

    private String generateFileName(String fileName) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(dateFormatter);
        return fileName + "_" + formattedDate + ".xlsx";
    }

    /**
     * Generates an Excel file and sends it as a response.
     *
     * @param response The HttpServletResponse to which the Excel file will be written.
     * @param fileName The desired name of the Excel file.
     * @param headers  The column headers for the Excel file.
     * @param data     The data to be written to the Excel file as a list of maps with header names as keys.
     * @throws IOException If there is an issue with I/O operations while generating the file.
     */
    public void generateExcelFile(HttpServletResponse response,
                                  String fileName,
                                  String[] headers,
                                  List<Map<String, Object>> data) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(fileName);
            writeHeader(sheet, headers);

            CellStyle style = createContentStyle(sheet);
            write(sheet, data, style, headers);

            for (int columnIndex = 0; columnIndex < headers.length; columnIndex++)
                sheet.autoSizeColumn(columnIndex);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String headerKey = "Content-Disposition";
            String fileNameWithDate = generateFileName(fileName);
            String headerValue = "attachment; filename=" + fileNameWithDate;
            response.setHeader(headerKey, headerValue);
            response.flushBuffer();

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // Handle or log the exception
            e.printStackTrace();
        }
    }
}
