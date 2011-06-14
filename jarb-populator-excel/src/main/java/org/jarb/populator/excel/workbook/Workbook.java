package org.jarb.populator.excel.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Workbook implements Iterable<Sheet> {
    private final List<Sheet> sheets = new ArrayList<Sheet>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Sheet> iterator() {
        return getSheets().iterator();
    }

    public List<Sheet> getSheets() {
        return Collections.unmodifiableList(sheets);
    }

    public Sheet getSheetAt(int index) {
        return sheets.get(index);
    }

    public boolean containsSheet(String name) {
        boolean foundSheet = false;
        for (Sheet sheet : sheets) {
            if (name.equals(sheet.getName())) {
                foundSheet = true;
                break;
            }
        }
        return foundSheet;
    }

    public Sheet getSheet(String name) {
        Sheet match = null;
        for (Sheet sheet : getSheets()) {
            if (sheet.getName().equalsIgnoreCase(name)) {
                match = sheet;
                break;
            }
        }
        return match;
    }

    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<String>();
        for (Sheet sheet : sheets) {
            sheetNames.add(sheet.getName());
        }
        return sheetNames;
    }

    public int getSheetCount() {
        return sheets.size();
    }

    public Workbook setSheet(int index, Sheet sheet) {
        sheets.set(index, sheet);
        return this;
    }

    public Sheet createSheet(String name) {
        Sheet sheet = new Sheet(this, name);
        sheets.add(sheet);
        return sheet;
    }

    public void removeSheet(Sheet sheet) {
        sheets.remove(sheet);
    }

}
