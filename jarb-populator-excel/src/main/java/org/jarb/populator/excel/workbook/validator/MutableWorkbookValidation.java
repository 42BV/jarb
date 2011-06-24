package org.jarb.populator.excel.workbook.validator;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarb.populator.excel.workbook.validator.export.ValidationExporter;

/**
 * Result of a validation operation.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class MutableWorkbookValidation implements WorkbookValidation {
    private Set<WorkbookViolation> violations = new HashSet<WorkbookViolation>();
    private Map<String, Set<WorkbookViolation>> sheetViolationsMap = new HashMap<String, Set<WorkbookViolation>>();

    public boolean hasViolations() {
        boolean violationFound = !violations.isEmpty();
        if(!violationFound) {
            for(Set<WorkbookViolation> sheetViolations : sheetViolationsMap.values()) {
                if(!sheetViolations.isEmpty()) {
                    violationFound = true;
                    break;
                }
            }
        }
        return violationFound;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkbookViolation> getViolations() {
        Set<WorkbookViolation> allViolations = new HashSet<WorkbookViolation>(violations);
        for(Set<WorkbookViolation> sheetViolations : sheetViolationsMap.values()) {
            allViolations.addAll(sheetViolations);
        }
        return Collections.unmodifiableSet(allViolations);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkbookViolation> getGlobalViolations() {
        return Collections.unmodifiableSet(violations);
    }
    
    /**
     * Include a global violation, for example a missing or unknown sheet.
     * @param violation global violation being added
     */
    public void addGlobalViolation(WorkbookViolation violation) {
        violations.add(violation);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkbookViolation> getSheetViolations(String sheetName) {
        return Collections.unmodifiableSet(sheetViolationsMap.get(sheetName));
    }
    
    /**
     * Include a sheet specific violation, for example a missing or unknown column.
     * @param sheetName name of the sheet
     * @param sheetViolation sheet specific violation being added
     */
    public void addSheetViolation(String sheetName, WorkbookViolation sheetViolation) {
        Set<WorkbookViolation> sheetViolations = sheetViolationsMap.get(sheetName);
        if(sheetViolations == null) {
            sheetViolations = new HashSet<WorkbookViolation>();
            sheetViolationsMap.put(sheetName, sheetViolations);
        }
        sheetViolations.add(sheetViolation);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getValidatedSheetNames() {
        return Collections.unmodifiableSet(sheetViolationsMap.keySet());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void export(ValidationExporter exporter, OutputStream os) {
        exporter.export(this, os);
    }
    
}
