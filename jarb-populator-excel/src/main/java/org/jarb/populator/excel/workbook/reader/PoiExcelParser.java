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
 * Apache POI implementation of {@link ExcelParser}.
 * @author Jeroen van Schagen
 * @since 06-05-2011
 */
public class PoiExcelParser implements ExcelParser {

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
     * @param poiWorkbook POI workbook
     * @return new workbook instance, containing all sheets
     */
    private Workbook parseWorkbook(org.apache.poi.ss.usermodel.Workbook poiWorkbook) {
        Workbook excel = new Workbook();
        for (int sheetNo = 0; sheetNo < poiWorkbook.getNumberOfSheets(); sheetNo++) {
            excel.addSheet(parseSheet(poiWorkbook.getSheetAt(sheetNo)));
        }
        return excel;
    }

    /**
     * Parse an Apache POI sheet into our own {@link Sheet} instance.
     * @param poiSheet POI sheet
     * @return new sheet instance, containing all rows
     */
    private Sheet parseSheet(org.apache.poi.ss.usermodel.Sheet poiSheet) {
        Sheet sheet = new Sheet(poiSheet.getSheetName());
        for (int rowNo = 0; rowNo <= poiSheet.getLastRowNum(); rowNo++) {
            sheet.setRow(rowNo, parseRow(poiSheet.getRow(rowNo)));
        }
        return sheet;
    }

    /**
     * Parse an Apache POI row into our own {@link Row} instance.
     * @param poiRow POI row
     * @return new row instance, containing all cells
     */
    private Row parseRow(org.apache.poi.ss.usermodel.Row poiRow) {
        Row row = new Row();
        if (poiRow != null) { // Row can be 'null' if not a single cell has been defined
            for (int colNo = 0; colNo < poiRow.getLastCellNum(); colNo++) {
                row.setCell(colNo, parseCell(poiRow.getCell(colNo)));
            }
        }
        return row;
    }

    /**
     * Parse an Apache POI cell into our own {@link Cell} instance.
     * @param poiCell POI cell
     * @return new cell instance, containing a value
     */
    private Cell parseCell(org.apache.poi.ss.usermodel.Cell poiCell) {
        return new Cell(parseValue(poiCell));
    }

    /**
     * Parse an Apache POI cell value object, and type, into our own {@link CellValue} instance.
     * @param poiCell POI cell
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
