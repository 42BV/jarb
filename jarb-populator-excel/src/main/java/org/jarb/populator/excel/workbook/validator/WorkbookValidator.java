package org.jarb.populator.excel.workbook.validator;

import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Validates the structure and content of an excel {@link Workbook}.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface WorkbookValidator {

    /**
     * Check the validity of an excel workbook.
     * @param workbook contains the content that should be validated
     * @param metamodel describes what is "correct"
     * @return collection of feedback message
     */
    WorkbookValidationResult validate(Workbook workbook, MetaModel metamodel);

}
