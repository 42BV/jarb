package org.jarbframework.populator.excel.workbook.reader;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jarbframework.populator.excel.workbook.BooleanValue;
import org.jarbframework.populator.excel.workbook.Cell;
import org.jarbframework.populator.excel.workbook.CellValue;
import org.jarbframework.populator.excel.workbook.DateValue;
import org.jarbframework.populator.excel.workbook.EmptyValue;
import org.jarbframework.populator.excel.workbook.NumericValue;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.StringValue;
import org.jarbframework.populator.excel.workbook.Workbook;

/**
 * Apache POI implementation of {@link WorkbookParser}.
 * @author Jeroen van Schagen
 * @since 06-05-2011
 */
public class PoiWorkbookParser implements WorkbookParser {

    /**
     * {@inheritDoc}
     */
    @Override
    public Workbook parse(InputStream is) {
        try {
            return parseWorkbook(WorkbookFactory.create(is));
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private Workbook parseWorkbook(org.apache.poi.ss.usermodel.Workbook poiWorkbook) {
        Workbook workbook = new Workbook();
        for (int sheetNo = 0; sheetNo < poiWorkbook.getNumberOfSheets(); sheetNo++) {
            org.apache.poi.ss.usermodel.Sheet poiSheet = poiWorkbook.getSheetAt(sheetNo);
            parseSheet(poiSheet, workbook);
        }
        return workbook;
    }

    private Sheet parseSheet(org.apache.poi.ss.usermodel.Sheet poiSheet, Workbook workbook) {
        Sheet sheet = workbook.createSheet(poiSheet.getSheetName());
        for (int rowNo = 0; rowNo <= poiSheet.getLastRowNum(); rowNo++) {
            org.apache.poi.ss.usermodel.Row poiRow = poiSheet.getRow(rowNo);
            if (poiRow != null) {
                parseRow(poiRow, sheet);
            }
        }
        return sheet;
    }

    private Row parseRow(org.apache.poi.ss.usermodel.Row poiRow, Sheet sheet) {
        Row row = sheet.getRowAt(poiRow.getRowNum());
        for (int colNo = 0; colNo < poiRow.getLastCellNum(); colNo++) {
            org.apache.poi.ss.usermodel.Cell poiCell = poiRow.getCell(colNo);
            if (poiCell != null) {
                parseCell(poiCell, row);
            }
        }
        return row;
    }

    private Cell parseCell(org.apache.poi.ss.usermodel.Cell poiCell, Row row) {
        Cell cell = row.getCellAt(poiCell.getColumnIndex());
        cell.setCellValue(parseValue(poiCell));
        return cell;
    }

    private CellValue parseValue(org.apache.poi.ss.usermodel.Cell poiCell) {
        CellValue cellValue = new EmptyValue();
        switch (poiCell.getCellType()) {
        case CELL_TYPE_STRING:
            cellValue = new StringValue(poiCell.getRichStringCellValue().getString());
            break;
        case CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(poiCell)) {
                cellValue = new DateValue(poiCell.getDateCellValue());
            } else {
                cellValue = new NumericValue(poiCell.getNumericCellValue());
            }
            break;
        case CELL_TYPE_BOOLEAN:
            cellValue = new BooleanValue(poiCell.getBooleanCellValue());
            break;
        case CELL_TYPE_FORMULA:
            CreationHelper creationHelper = poiCell.getRow().getSheet().getWorkbook().getCreationHelper();
            creationHelper.createFormulaEvaluator().evaluateInCell(poiCell);
            cellValue = parseValue(poiCell);
            break;
        }
        return cellValue;
    }

}
