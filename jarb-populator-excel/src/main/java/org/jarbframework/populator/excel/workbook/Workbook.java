package org.jarbframework.populator.excel.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Workbook is an internal representation of the data structure of an Excel file.
 * A Workbook has Sheets (just like an Excel file),
 * and each of these Sheets has Rows which are made out of Cells.
 * Changes made to the state of a Workbook have no effect on the Excel file it was loaded from.
 * @author Unknown
 *
 */
public class Workbook implements Iterable<Sheet> {
	
    /**
     * The Sheetsmap is a map holding the Sheets of the workbook.
     * The key for the Sheets is their name.
     * Adding to this Sheetsmap is done by createSheet,
     * removing is done by removeSheet.
     */
    private final Map<String, Sheet> sheetsMap = new TreeMap<String, Sheet>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Sheet> iterator() {
        return sheetsMap.values().iterator();
    }

    /**
     * Retrieves the Sheet with the given name.
     * Part of the interface towards the Sheetsmap.
     * @param name The name of the Sheet
     * @return The Sheet with the name provided, or null if the Sheet couldn't be found.
     */
    public Sheet getSheet(String name) {
        return sheetsMap.get(name);
    }

    /**
     * Gives a list of all Sheets in this Workbook.
     * Part of the interface towards the Sheetsmap.
     * @return a read-only List of the Sheets in this Workbook.
     */
    public List<Sheet> getSheets() {
        List<Sheet> sheets = new ArrayList<Sheet>(sheetsMap.values());
        return Collections.unmodifiableList(sheets);
    }

    /**
     * Gives a Set of all Sheet names in this Workbook.
     * Part of the interface towards the Sheetsmap.
     * @return a read-only Set of the names of the Sheets in this Workbook
     */
    public Set<String> getSheetNames() {
        return Collections.unmodifiableSet(sheetsMap.keySet());
    }

    public boolean containsSheet(String name) {
        return sheetsMap.containsKey(name);
    }

    public int getSheetCount() {
        return sheetsMap.size();
    }

    /**
     * Creates and adds a Sheet to this workbook.
     * @param name The name of the new Sheet.
     * @return The created Sheet.
     * @throws IllegalStateException if the supplied name is already in use by a Sheet in this Workbook
     */
    public Sheet createSheet(String name) {
        if (containsSheet(name)) {
            throw new IllegalStateException("Sheet '" + name + "' already exists");
        }
        Sheet sheet = new Sheet(this, name);
        sheetsMap.put(name, sheet);
        return sheet;
    }

    /**
     * Removes the Sheet that has the supplied name from this Workbook.
     * @param name The name of the Sheet to be removed from this Workbook.
     */
    public void removeSheet(String name) {
        sheetsMap.remove(name);
    }

}
