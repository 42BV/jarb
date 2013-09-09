package org.jarbframework.populator.excel.workbook.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.jarbframework.populator.excel.workbook.Cell;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;

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
        final Object cellValue = cell.getValue();
        if (cellValue != null) {
        	if (cellValue instanceof Boolean) {
                poiCell.setCellValue(((Boolean) cellValue).booleanValue());
            } else if (cellValue instanceof Date) {
                poiCell.setCellValue((Date) cellValue);
                poiCell.setCellStyle(getDateFormatStyle(poiCell));
            } else if (cellValue instanceof Number) {
                poiCell.setCellValue(((Number) cellValue).doubleValue());
            } else {
                poiCell.setCellValue(ObjectUtils.toString(cellValue, ""));
            }
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
