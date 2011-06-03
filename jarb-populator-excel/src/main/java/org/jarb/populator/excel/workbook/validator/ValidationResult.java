package org.jarb.populator.excel.workbook.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Result of a validation operation.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class ValidationResult {
    private final List<String> messages = new ArrayList<String>();

    public ValidationResult(Collection<String> messages) {
        this.messages.addAll(messages);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

}
