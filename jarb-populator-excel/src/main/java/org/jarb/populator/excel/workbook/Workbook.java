package org.jarb.populator.excel.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Workbook implements Iterable<Sheet> {
    private final TreeMap<String, Sheet> sheetsMap = new TreeMap<String, Sheet>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Sheet> iterator() {
        return sheetsMap.values().iterator();
    }
    
    public Sheet getSheet(String name) {
        return sheetsMap.get(name);
    }

    public List<Sheet> getSheets() {
        List<Sheet> sheets = new ArrayList<Sheet>(sheetsMap.values());
        return Collections.unmodifiableList(sheets);
    }
    
    public Set<String> getSheetNames() {
        return Collections.unmodifiableSet(sheetsMap.keySet());
    }

    public boolean containsSheet(String name) {
        return sheetsMap.containsKey(name);
    }

    public int getSheetCount() {
        return sheetsMap.size();
    }

    public Sheet createSheet(String name) {
        if(containsSheet(name)) {
            throw new IllegalStateException("Sheet '" + name + "' already exists");
        } 
        Sheet sheet = new Sheet(this, name);
        sheetsMap.put(name, sheet);
        return sheet;
    }

    public void removeSheet(String name) {
        sheetsMap.remove(name);
    }

}
