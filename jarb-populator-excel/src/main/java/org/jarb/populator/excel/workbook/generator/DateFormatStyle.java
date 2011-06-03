package org.jarb.populator.excel.workbook.generator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

/**
 * Returns a specified date format style needed for formatting Excel cells into a date.
 * @author Sander Benschop
 *
 */
public final class DateFormatStyle {

    /** Private constructor. */
    private DateFormatStyle() {
    }

    /**
     * Creates a cellStyle for a date so that it's humanly readable. 
     * Also aligns this at the left because it doesnt do that on its own with dates for some reason.
     * @param workbook Excel workbook
     * @return CellStyle with date styled yyyy/mm/dd such as: 2011/03/04
     */
    public static CellStyle getDateFormatStyle(HSSFWorkbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateFormatStyle = workbook.createCellStyle();
        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));
        dateFormatStyle.setAlignment(CellStyle.ALIGN_LEFT);
        return dateFormatStyle;
    }
}
