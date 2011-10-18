package org.jarbframework.populator.excel;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.jarbframework.populator.excel.entity.persist.EntityWriter;
import org.jarbframework.populator.excel.entity.query.EntityReader;
import org.jarbframework.populator.excel.mapping.exporter.EntityExporter;
import org.jarbframework.populator.excel.mapping.importer.EntityImporter;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.WorkbookParser;
import org.jarbframework.populator.excel.workbook.validator.MutableWorkbookValidationResult;
import org.jarbframework.populator.excel.workbook.validator.WorkbookValidationResult;
import org.jarbframework.populator.excel.workbook.validator.WorkbookValidator;
import org.jarbframework.populator.excel.workbook.writer.WorkbookWriter;
import org.springframework.core.io.Resource;

/**
 * Allows an Excel workbook to be used to insert entities into
 * the database. Below are some examples of usage:
 * 
 * <p>
 * 
 * <code>
 *  manager.persist(manager.loadWorkbook("input.xls"));<br>
 *  manager.newWorkbook().readAndIncludeAll().write("output.xls");
 * </code>
 * 
 * @author Jeroen van Schagen
 * @since 23-06-2011
 */
public class ExcelDataManager {
    private WorkbookParser excelParser;
    private WorkbookWriter excelWriter;
    private EntityImporter entityImporter;
    private EntityExporter entityExporter;
    private EntityReader entityReader;
    private EntityWriter entityWriter;
    private WorkbookValidator excelValidator;
    private MetaModelGenerator metamodelGenerator;

    /**
     * Load an Excel workbook based on some input stream.
     * @param is input stream to the excel resource
     * @return loaded instance of the workbook
     */
    public WorkbookLoader load(InputStream is) {
        Workbook workbook = excelParser.parse(notNull(is, "Workbook input stream cannot be null"));
        return new WorkbookLoader(workbook);
    }

    /**
     * Load an Excel workbook based on some resource.
     * @param resource descriptor of the excel resource
     * @return loaded instance of the workbook
     * @throws IOException if the stream could not be opened
     */
    public WorkbookLoader load(Resource resource) throws IOException {
        return load(notNull(resource, "Workbook resource cannot be null").getInputStream());
    }

    /**
     * Load an Excel workbook based on some file name.
     * @param fileName the system-dependent excel file name
     * @return loaded instance of the workbook
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason
     * cannot be opened for reading. 
     */
    public WorkbookLoader load(String fileName) throws FileNotFoundException {
        return load(new FileInputStream(hasText(fileName, "Workbook file name cannot be empty")));
    }

    /**
     * Provides access to an excel workbook, enabling its content
     * to be validated, mapped to entities, and persisted.
     */
    public class WorkbookLoader {
        private final Workbook workbook;
        private EntityRegistry entities;

        /**
         * Construct a new {@link WorkbookLoader}.
         * @param workbook the workbook being accessed
         */
        private WorkbookLoader(Workbook workbook) {
            this.workbook = notNull(workbook, "Workbook cannot be null");
        }

        /**
         * Validate the workbook, and return the validation result.
         * @see MutableWorkbookValidationResult
         * @return validation result
         */
        public WorkbookValidationResult validate() {
            MetaModel metamodel = metamodelGenerator.generate();
            return excelValidator.validate(workbook, metamodel);
        }

        /**
         * Retrieve the Java entities contained in our workbook.
         * @see EntityRegistry
         * @return entities contained to our workbook
         */
        public EntityRegistry entities() {
            continueIfValid();
            if (entities == null) {
                MetaModel metamodel = metamodelGenerator.generate();
                entities = entityImporter.load(workbook, metamodel);
            }
            return entities;
        }

        /**
         * Validate the workbook, if validation has errors, we throw
         * a runtime exception. This method is, for example, used in
         * {@link #entities()} to ensure the workbook is always valid.
         * @throws InvalidWorkbookException if validation fails
         * @return this instance, for chaining
         */
        protected WorkbookLoader continueIfValid() {
            WorkbookValidationResult validation = validate();
            if (validation.hasViolations()) {
                throw new InvalidWorkbookException(validation);
            }
            return this;
        }

        /**
         * Persist all loaded entities inside our database.
         * @return this instance, for chaining
         */
        public WorkbookLoader persist() {
            ExcelDataManager.this.persist(entities());
            return this;
        }
    }

    /**
     * Persist the entities inside our database.
     * @param entities all entities being persisted
     * @return this instance, for chaining
     */
    public EntityRegistry persist(EntityRegistry entities) {
        return entityWriter.persist(entities);
    }

    /**
     * Start building a new Excel workbook.
     * @return new Excel workbook builder
     */
    public WorkbookBuilder buildNew() {
        return buildWith(new EntityRegistry());
    }

    /**
     * Start building a new Excel workbook.
     * @param entities the entities that should be included
     * @return new Excel workbook builder
     */
    public WorkbookBuilder buildWith(EntityRegistry entities) {
        return new WorkbookBuilder(entities);
    }

    /**
     * Capable of building an excel template for our meta-model,
     * (database) entities can also be included.
     */
    public class WorkbookBuilder {
        private EntityRegistry entities;

        /**
         * Construct a new {@link WorkbookBuilder}.
         */
        private WorkbookBuilder(EntityRegistry entities) {
            this.entities = entities;
        }

        /**
         * Include all entities currently stored in the database.
         * @return this builder, for chaining
         */
        public WorkbookBuilder includeAll() {
            entities = entityReader.readAll();
            return this;
        }

        /**
         * Include a new entity, not (yet) stored in the database.
         * @param <T> type of entity being included
         * @param entityClass class of the entity type
         * @param identifier entity identifier, can be anything
         * @param entity reference to the new entity
         * @return this builder, for chaining
         */
        public <T> WorkbookBuilder include(Class<T> entityClass, Object identifier, T entity) {
            notNull(entityClass, "Entity class cannot be null");
            notNull(identifier, "Entity identifier cannot be null");
            notNull(entity, "Entity cannot be null");

            entities.add(entityClass, identifier, entity);
            return this;
        }

        /**
         * Write our newly created workbook template to an output stream.
         * @param os stream being written to
         */
        public void write(OutputStream os) {
            notNull(os, "Workbook output stream cannot be null");

            MetaModel metamodel = metamodelGenerator.generate();
            Workbook workbook = entityExporter.export(entities, metamodel);
            excelWriter.write(workbook, os);
        }

        /**
         * Write our newly created workbook template to a file.
         * @param fileName the system-dependent excel file name
         * @throws FileNotFoundException if the file exists but is a directory
         * rather than a regular file, does not exist but cannot be created,
         * or cannot be opened for any other reason 
         */
        public void write(String fileName) throws FileNotFoundException {
            write(new FileOutputStream(hasText(fileName, "Workbook file name cannot be empty")));
        }
    }

    public void setExcelParser(WorkbookParser excelParser) {
        this.excelParser = excelParser;
    }

    public void setExcelWriter(WorkbookWriter excelWriter) {
        this.excelWriter = excelWriter;
    }

    public void setEntityImporter(EntityImporter entityImporter) {
        this.entityImporter = entityImporter;
    }

    public void setEntityExporter(EntityExporter entityExporter) {
        this.entityExporter = entityExporter;
    }

    public void setEntityReader(EntityReader entityReader) {
        this.entityReader = entityReader;
    }

    public void setEntityWriter(EntityWriter entityWriter) {
        this.entityWriter = entityWriter;
    }

    public void setExcelValidator(WorkbookValidator excelValidator) {
        this.excelValidator = excelValidator;
    }

    public void setMetamodelGenerator(MetaModelGenerator metaModelGenerator) {
        this.metamodelGenerator = metaModelGenerator;
    }

}
