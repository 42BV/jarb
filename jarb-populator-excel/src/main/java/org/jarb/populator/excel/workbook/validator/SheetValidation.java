package org.jarb.populator.excel.workbook.validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SheetValidation {
    private Set<String> missingColumns = new HashSet<String>();
    private Set<String> unknownColumns = new HashSet<String>();
    
    public Set<String> getMissingColumns() {
        return Collections.unmodifiableSet(missingColumns);
    }
    
    public void addMissingColumn(String missingColumn) {
        missingColumns.add(missingColumn);
    }
    
    public Set<String> getUnknownColumns() {
        return Collections.unmodifiableSet(unknownColumns);
    }
    
    public void addUnknownColumn(String unknownColumn) {
        unknownColumns.add(unknownColumn);
    }
}