package org.jarb.populator.excel.workbook.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.jarb.populator.excel.workbook.BooleanValue;
import org.jarb.populator.excel.workbook.Cell;
import org.jarb.populator.excel.workbook.CellValue;
import org.jarb.populator.excel.workbook.DateValue;
import org.jarb.populator.excel.workbook.FormulaValue;
import org.jarb.populator.excel.workbook.NumericValue;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.StringValue;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Apache POI implementation of {@link WorkbookWriter}.
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class PoiWorkbookWriter implements WorkbookWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Workbook workbook, OutputStream os) {
        try {
            HSSFWorkbook poiWorkbook = writeWorkbook(workbook);
            poiWorkbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    protected HSSFWorkbook writeWorkbook(Workbook workbook) {
        HSSFWorkbook poiWorkbook = new HSSFWorkbook();
        for (Sheet sheet : workbook) {
            createAndAddSheet(sheet, poiWorkbook);
        }
        return poiWorkbook;
    }

    protected void createAndAddSheet(Sheet sheet, HSSFWorkbook poiWorkbook) {
        HSSFSheet poiSheet = poiWorkbook.createSheet(sheet.getName());
        for (int rowNo = 0; rowNo <= sheet.getLastRowNumber(); rowNo++) {
            createAndAddRow(sheet.getRowAt(rowNo), poiSheet, rowNo);
        }
    }

    protected void createAndAddRow(Row row, HSSFSheet poiSheet, int rowNo) {
        HSSFRow poiRow = poiSheet.createRow(rowNo);
        for (int colNo = 0; colNo <= row.getLastCellNumber(); colNo++) {
            createAndAddCell(row.getCellAt(colNo), poiRow, colNo);
        }
    }

    protected void createAndAddCell(Cell cell, HSSFRow poiRow, int colNo) {
        HSSFCell poiCell = poiRow.createCell(colNo);
        final CellValue cellValue = cell.getCellValue();
        if (cellValue instanceof BooleanValue) {
            poiCell.setCellValue(((BooleanValue) cellValue).getValue());
        } else if (cellValue instanceof DateValue) {
            poiCell.setCellValue(((DateValue) cellValue).getValue());
            poiCell.setCellStyle(getDateFormatStyle(poiCell));
        } else if (cellValue instanceof NumericValue) {
            poiCell.setCellValue(((NumericValue) cellValue).getValue());
        } else if (cellValue instanceof FormulaValue) {
            poiCell.setCellFormula((((FormulaValue) cellValue).getFormula()));
        } else if (cellValue instanceof StringValue) {
            poiCell.setCellValue(((StringValue) cellValue).getValue());
        }
    }

    private CellStyle getDateFormatStyle(HSSFCell poiCell) {
        final HSSFWorkbook poiWorkbook = poiCell.getRow().getSheet().getWorkbook();
        CellStyle dateFormatStyle = poiWorkbook.createCellStyle();
        CreationHelper createHelper = poiWorkbook.getCreationHelper();
        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd hh:MM:ss"));
        dateFormatStyle.setAlignment(CellStyle.ALIGN_LEFT);
        return dateFormatStyle;
    }

}
