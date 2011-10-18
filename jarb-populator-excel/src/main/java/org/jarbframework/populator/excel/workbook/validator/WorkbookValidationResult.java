package org.jarbframework.populator.excel.workbook.validator;

import static java.util.Collections.unmodifiableSet;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarbframework.populator.excel.workbook.validator.export.ValidationExporter;

/**
 * Result of a validation operation.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class WorkbookValidationResult {
    private Set<WorkbookViolation> globalViolations = new HashSet<WorkbookViolation>();
    private Map<String, Set<WorkbookViolation>> sheetViolationsMap = new HashMap<String, Set<WorkbookViolation>>();

    public boolean hasViolations() {
        return !getViolations().isEmpty();
    }

    public boolean hasViolations(ViolationLevel level) {
        return !getViolations(level).isEmpty();
    }

    public Set<WorkbookViolation> getViolations() {
        Set<WorkbookViolation> violations = new HashSet<WorkbookViolation>(globalViolations);
        for (Set<WorkbookViolation> sheetViolations : sheetViolationsMap.values()) {
            violations.addAll(sheetViolations);
        }
        return unmodifiableSet(violations);
    }

    public Set<WorkbookViolation> getViolations(ViolationLevel level) {
        Set<WorkbookViolation> violationsWithLevel = new HashSet<WorkbookViolation>();
        for (WorkbookViolation violation : getViolations()) {
            if (violation.getLevel() == level) {
                violationsWithLevel.add(violation);
            }
        }
        return unmodifiableSet(violationsWithLevel);
    }

    public Set<WorkbookViolation> getGlobalViolations() {
        return unmodifiableSet(globalViolations);
    }

    /**
     * Include a global violation, for example a missing or unknown sheet.
     * @param violation global violation being added
     */
    public void addGlobalViolation(WorkbookViolation violation) {
        globalViolations.add(violation);
    }

    public Set<WorkbookViolation> getSheetViolations(String sheetName) {
        return unmodifiableSet(sheetViolationsMap.get(sheetName));
    }

    /**
     * Include a sheet specific violation, for example a missing or unknown column.
     * @param sheetName name of the sheet
     * @param violation sheet specific violation being added
     */
    public void addSheetViolation(String sheetName, WorkbookViolation violation) {
        Set<WorkbookViolation> sheetViolations = sheetViolationsMap.get(sheetName);
        if (sheetViolations == null) {
            sheetViolations = new HashSet<WorkbookViolation>();
            sheetViolationsMap.put(sheetName, sheetViolations);
        }
        sheetViolations.add(violation);
    }

    public Set<String> getValidatedSheetNames() {
        return unmodifiableSet(sheetViolationsMap.keySet());
    }

    public void export(ValidationExporter exporter, OutputStream os) {
        exporter.export(this, os);
    }

}
