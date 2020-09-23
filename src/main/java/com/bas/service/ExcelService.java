package com.bas.service;

import com.bas.component.Components;
import com.bas.model.CellAddressCoordinate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelService {

    // - Singleton
    private static ExcelService instance = null;

    private ExcelService() {}

    public static ExcelService getInstance() {
        if (null == instance)
            instance = new ExcelService();
        return instance;
    }
    // - Singleton

    private final StudentService studentService = StudentService.getInstance();

    public Workbook readExcel(String path) {
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            Components.getErrorDialog("Error", "No es posible leer el archivo.\n\nAsegúrese de que el archivo esté cerrado, verifique la ruta e intente nuevamente.\n ").showAndWait();
            return null;
        }
    }

    public Boolean saveExcel(Workbook workbook, String path) {
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            Components.getErrorDialog("Error", "No es posible guardar el archivo.\n\nRevise que el archivo esté cerrado e intente nuevamente.\n ").showAndWait();
            return false;
        }
    }

    public CellAddressCoordinate getCoordinateFor(Workbook workbook, Integer sheet, String keyWord) {
        Sheet workbookSheet = workbook.getSheetAt(sheet);
        Iterator iterator = workbookSheet.iterator();
        DataFormatter formatter = new DataFormatter();

        while (iterator.hasNext()) {
            Row row = (Row) iterator.next();
            Iterator cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = (Cell) cellIterator.next();
                String cellContent = formatter.formatCellValue(cell).toLowerCase();
                if(keyWord.toLowerCase().equals(cellContent)) {
                    return new CellAddressCoordinate(cell.getRowIndex(), cell.getColumnIndex());
                }
            }
        }
        Components.getErrorDialog("Error", "No se encontró la columna a analizar.\n\nRevise que esté bien escrita y asegúrese de haber seleccionado el archivo correcto.\n ").showAndWait();
        return null;
    }

    public void setStudentId(Workbook workbook, Integer sheet, CellAddressCoordinate coordinate, String keyWord) {
        Sheet workbookSheet = workbook.getSheetAt(sheet);
        Iterator iterator = workbookSheet.iterator();
        DataFormatter formatter = new DataFormatter();
        Integer indexIdColumn = null;

        while (iterator.hasNext()) {
            Row row = (Row) iterator.next();
            if(formatter.formatCellValue(row.getCell(coordinate.getColumnIndex())).equals(keyWord)) {
                if(indexIdColumn == null) {
                    indexIdColumn = (int) row.getLastCellNum();
                }
                row.createCell(indexIdColumn);
                row.getCell(indexIdColumn).setCellValue("Legajo");
            } else {
                String cell = formatter.formatCellValue(row.getCell(coordinate.getColumnIndex()));
                Pattern pattern = Pattern.compile("\\b(\\d{11})\\b");
                Matcher matcher = pattern.matcher(cell);
                if (matcher.find()) {
                    String cuil = matcher.group(1);
                    if(indexIdColumn == null) {
                        indexIdColumn = (int) row.getLastCellNum();
                    }
                    row.createCell(indexIdColumn);
                    String legajo = studentService.getStudentIdByParentCuil(cuil);
                    row.getCell(indexIdColumn).setCellValue(legajo);
                }
            }
        }
    }

    public Boolean runCompleteExcel(String path, Integer sheet, String keyWord) {
        Workbook workbook = readExcel(path);
        if(workbook != null) {
            CellAddressCoordinate coordinate = getCoordinateFor(workbook, sheet, keyWord);
            if(coordinate != null) {
                setStudentId(workbook, sheet, coordinate, keyWord);
                return saveExcel(workbook, path);
            }
        }
        return false;
    }

}
