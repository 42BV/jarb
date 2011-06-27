package org.jarb.populator.excel.workbook.reader;

import java.io.InputStream;

import org.jarb.populator.excel.workbook.Workbook;

/**
 * Read the content of an excel file.
 * @author Jeroen van Schagen
 * @since 06-05-2011
 */
public interface WorkbookParser {

    /**
     * Read the content of an excel file, and convert it into a {@link Workbook}.
     * @param is input stream to our excel file
     * @return workbook instance, containing all content of our excel file
     */
    Workbook parse(InputStream is);

}
