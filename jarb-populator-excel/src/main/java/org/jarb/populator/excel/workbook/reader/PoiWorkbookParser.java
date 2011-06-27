package org.jarb.populator.excel.workbook.reader;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jarb.populator.excel.workbook.BooleanValue;
import org.jarb.populator.excel.workbook.Cell;
import org.jarb.populator.excel.workbook.CellValue;
import org.jarb.populator.excel.workbook.DateValue;
import org.jarb.populator.excel.workbook.EmptyValue;
import org.jarb.populator.excel.workbook.FormulaValue;
import org.jarb.populator.excel.workbook.NumericValue;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.StringValue;
import org.jarb.populator.excel.workbook.Workbook;

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
            org.apache.poi.ss.usermodel.Workbook workbook = createWorkbook(is);
            return parseWorkbook(workbook);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Build a new workbook, used to access data from an excel file.
     * @param is stream to our excel file
     * @return POI workbook instance to that excel
     * @throws InvalidFormatException whenever the file is of an invalid format
     * @throws IOException whenever another type of read exception occurs
     */
    protected org.apache.poi.ss.usermodel.Workbook createWorkbook(InputStream is) throws InvalidFormatException, IOException {
        return WorkbookFactory.create(is);
    }

    /**
     * Parse an Apache POI workbook into our own {@link Workbook} instance.
     * @param poiWorkbook workbook retrieved from POI
     * @return new workbook instance, containing all sheets
     */
    private Workbook parseWorkbook(org.apache.poi.ss.usermodel.Workbook poiWorkbook) {
        Workbook workbook = new Workbook();
        for (int sheetNo = 0; sheetNo < poiWorkbook.getNumberOfSheets(); sheetNo++) {
            org.apache.poi.ss.usermodel.Sheet poiSheet = poiWorkbook.getSheetAt(sheetNo);
            Sheet sheet = workbook.createSheet(poiSheet.getSheetName());
            copyRows(sheet, poiSheet);
        }
        return workbook;
    }

    /**
     * Parse an Apache POI sheet into our own {@link Sheet} instance.
     * @param sheet sheet model that contains all content
     * @param poiSheet sheet retrieved from POI
     * @return new sheet instance, containing all rows
     */
    private void copyRows(Sheet sheet, org.apache.poi.ss.usermodel.Sheet poiSheet) {
        for (int rowNo = 0; rowNo <= poiSheet.getLastRowNum(); rowNo++) {
            org.apache.poi.ss.usermodel.Row poiRow = poiSheet.getRow(rowNo);
            if (poiRow != null) { // Row is 'null' when not a single cell has been defined
                Row row = sheet.getRowAt(rowNo);
                for (int colNo = 0; colNo < poiRow.getLastCellNum(); colNo++) {
                    Cell cell = row.getCellAt(colNo);
                    cell.setCellValue(parseValue(poiRow.getCell(colNo)));
                }
            }
        }
    }

    /**
     * Parse an Apache POI cell value object, and type, into our own {@link CellValue} instance.
     * @param poiCell cell, including its value, as retrieved from POI
     * @return new cell value instance, never {@code null}
     */
    private CellValue parseValue(org.apache.poi.ss.usermodel.Cell poiCell) {
        CellValue value = null;
        if (poiCell == null) {
            // Unknown cells have an empty value
            value = new EmptyValue();
        } else {
            switch (poiCell.getCellType()) {
            case CELL_TYPE_STRING:
                value = new StringValue(poiCell.getRichStringCellValue().getString());
                break;
            case CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(poiCell)) {
                    value = new DateValue(poiCell.getDateCellValue());
                } else {
                    value = new NumericValue(poiCell.getNumericCellValue());
                }
                break;
            case CELL_TYPE_BOOLEAN:
                value = new BooleanValue(poiCell.getBooleanCellValue());
                break;
            case CELL_TYPE_FORMULA:
                value = new FormulaValue(poiCell.getCellFormula());
                break;
            default:
                // Remaining cell types (blank, error) have an empty value
                value = new EmptyValue();
                break;
            }
        }
        return value;
    }
}
