package org.jarb.populator.excel.workbook.validator;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarb.populator.excel.workbook.validator.export.SimpleValidationExporter;
import org.jarb.populator.excel.workbook.validator.export.ValidationExporter;

/**
 * Result of a validation operation.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class WorkbookValidation {
    private Set<String> missingSheets = new HashSet<String>();
    private Set<String> unknownSheets = new HashSet<String>();
    private Map<String, SheetValidation> sheetValidationMap = new HashMap<String, SheetValidation>();
    
    public void export(OutputStream os) {
        export(new SimpleValidationExporter(), os);
    }
    
    public void export(ValidationExporter exporter, OutputStream os) {
        exporter.export(this, os);
    }

    public boolean hasErrors() {
        return !missingSheets.isEmpty() || !unknownSheets.isEmpty();
    }
    
    public Set<String> getMissingSheets() {
        return Collections.unmodifiableSet(missingSheets);
    }
    
    public void addMissingSheet(String missingSheet) {
        missingSheets.add(missingSheet);
    }
    
    public Set<String> getUnknownSheets() {
        return Collections.unmodifiableSet(unknownSheets);
    }
    
    public void addUnknownSheet(String unknownSheet) {
        unknownSheets.add(unknownSheet);
    }
    
    public void includeSheetValidation(String sheetName, SheetValidation sheetValidation) {
        sheetValidationMap.put(sheetName, sheetValidation);
    }
    
    public SheetValidation getSheetValidation(String sheetName) {
        return sheetValidationMap.get(sheetName);
    }
    
    public Set<String> getValidatedSheetNames() {
        return Collections.unmodifiableSet(sheetValidationMap.keySet());
    }
    
    public static class SheetValidation {
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

}
