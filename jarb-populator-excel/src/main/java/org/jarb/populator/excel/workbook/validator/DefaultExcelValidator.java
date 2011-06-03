package org.jarb.populator.excel.workbook.validator;

import java.util.List;

import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link ExcelValidator}.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public final class DefaultExcelValidator implements ExcelValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate(Workbook workbook, MetaModel metamodel) {
        try {
            List<String> violations = ExcelFileValidator.verify(workbook, metamodel);
            return new ValidationResult(violations);
        } catch (Exception e) {
            // TODO: Delegating class should not be throwing this many exceptions
            throw new RuntimeException(e);
        }
    }

}
