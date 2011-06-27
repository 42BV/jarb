package org.jarb.populator.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.persist.EntityWriter;
import org.jarb.populator.excel.entity.query.EntityReader;
import org.jarb.populator.excel.mapping.exporter.EntityExporter;
import org.jarb.populator.excel.mapping.importer.EntityImporter;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.generator.MetaModelGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.WorkbookParser;
import org.jarb.populator.excel.workbook.validator.MutableWorkbookValidationResult;
import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;
import org.jarb.populator.excel.workbook.validator.WorkbookValidator;
import org.jarb.populator.excel.workbook.writer.WorkbookWriter;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Allows an Excel workbook to be used to insert entities into the database.
 * 
 * <p>
 * 
 * Below are some examples of usage:
 * 
 * <p>
 * 
 * <code>
 *  excelDataManager.loadWorkbook("book.xls").persist();
 *  excelDataManager.buildWorkbook().includeAllEntities().write("book.xls");
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
    public WorkbookAccessor loadWorkbook(InputStream is) {
        Assert.notNull(is, "Workbook input stream cannot be null");
        Workbook workbook = excelParser.parse(is);
        return new WorkbookAccessor(workbook);
    }
    
    /**
     * Load an Excel workbook based on some resource.
     * @param resource descriptor of the excel resource
     * @return loaded instance of the workbook
     * @throws IOException if the stream could not be opened
     */
    public WorkbookAccessor loadWorkbook(Resource resource) throws IOException {
        Assert.notNull(resource, "Workbook resource cannot be null");
        return loadWorkbook(resource.getInputStream());
    }
    
    /**
     * Load an Excel workbook based on some file name.
     * @param fileName the system-dependent excel file name
     * @return loaded instance of the workbook
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason
     * cannot be opened for reading. 
     */
    public WorkbookAccessor loadWorkbook(String fileName) throws FileNotFoundException {
        Assert.hasText(fileName, "Workbook file name cannot be empty");
        return loadWorkbook(new FileInputStream(fileName));
    }
   
    /**
     * Provides access to an excel workbook, enabling its content
     * to be validated, mapped to entities, and persisted.
     */
    public class WorkbookAccessor {
        private final Workbook workbook;
        private EntityRegistry entities;
        
        /**
         * Construct a new {@link WorkbookAccessor}.
         * @param workbook the workbook being accessed
         */
        private WorkbookAccessor(Workbook workbook) {
            Assert.notNull(workbook, "Workbook cannot be null");
            this.workbook = workbook;
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
         * Persist our workbook entities in the database. Whenever
         * the entities are persisted, entity identifiers may be
         * subject to change.
         * @return this instance, for chaining
         */
        public WorkbookAccessor persist() {
            // Update the entity identifiers based on database
            entities = entityWriter.persist(entities());
            return this;
        }
        
        /**
         * Retrieve the Java entities contained in our workbook.
         * @see EntityRegistry
         * @return entities contained to our workbook
         */
        public EntityRegistry entities() {
            continueIfValid();
            if(entities == null) {
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
        protected WorkbookAccessor continueIfValid() {
            WorkbookValidationResult validation = validate();
            if(validation.hasViolations()) {
                throw new InvalidWorkbookException(validation);
            }
            return this;
        }
    }
    
    /**
     * Start building a new Excel workbook.
     * @return new Excel workbook builder
     */
    public WorkbookBuilder newWorkbook() {
        return new WorkbookBuilder();
    }
    
    /**
     * Capable of building an excel template for our metamodel,
     * (database) entities can also be included.
     */
    public class WorkbookBuilder {
        private EntityRegistry entities = new EntityRegistry();
        
        /**
         * Construct a new {@link WorkbookBuilder}.
         */
        private WorkbookBuilder() {
            // Hide initialization
        }
        
        /**
         * Include all entities currently stored in the database.
         * @return this builder, for chaining
         */
        public WorkbookBuilder includeAllEntities() {
            entities = entityReader.readAll();
            return this;
        }
        
        /**
         * Incldue all entities, of a specific type, currently stored in the database.
         * @param <T> type of entities being included
         * @param entityClass class of the entity type
         * @return this builder, for chaining
         */
        public <T> WorkbookBuilder includeEntities(Class<T> entityClass) {
            Assert.notNull(entityClass, "Entity class cannot be null");
            entities.addAll(entityReader.readFrom(entityClass));
            return this;
        }
        
        /**
         * Include a specific entity, currently stored in the database.
         * @param <T> type of entity being included
         * @param entityClass class of the entity type
         * @param identifier entity identifier
         * @return this builder, for chaining
         */
        public <T> WorkbookBuilder includeEntity(Class<T> entityClass, Object identifier) {
            Assert.notNull(entityClass, "Entity class cannot be null");
            Assert.notNull(identifier, "Entity identifier cannot be null");
            entities.add(entityClass, identifier, entityReader.read(entityClass, identifier));
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
        public <T> WorkbookBuilder includeNewEntity(Class<T> entityClass, Object identifier, T entity) {
            Assert.notNull(entity, "Entity cannot be null");
            Assert.notNull(entityClass, "Entity class cannot be null");
            Assert.notNull(identifier, "Entity identifier cannot be null");
            entities.add(entityClass, identifier, entity);
            return this;
        }
        
        /**
         * Write our newly created workbook template to an output stream.
         * @param os stream being written to
         */
        public void write(OutputStream os) {
            Assert.notNull(os, "Workbook output stream cannot be null");
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
            Assert.hasText(fileName, "Workbook file name cannot be empty");
            write(new FileOutputStream(fileName));
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
