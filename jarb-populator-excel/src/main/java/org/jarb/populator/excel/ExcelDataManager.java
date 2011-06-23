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
import org.jarb.populator.excel.workbook.reader.ExcelParser;
import org.jarb.populator.excel.workbook.validator.ExcelValidator;
import org.jarb.populator.excel.workbook.validator.WorkbookValidation;
import org.jarb.populator.excel.workbook.writer.ExcelWriter;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Excel data insertion facade.
 * 
 * @author Jeroen van Schagen
 * @since 23-06-2011
 */
public class ExcelDataManager {
    private ExcelParser excelParser;
    private ExcelWriter excelWriter;
    private EntityImporter entityImporter;
    private EntityExporter entityExporter;
    private EntityReader entityReader;
    private EntityWriter entityWriter;
    private ExcelValidator excelValidator;
    private MetaModelGenerator metamodelGenerator;
    
    public WorkbookLoader load(InputStream is) {
        Assert.notNull(is, "Workbook input stream cannot be null");

        Workbook workbook = excelParser.parse(is);
        return new WorkbookLoader(workbook);
    }
    
    public WorkbookLoader load(Resource resource) throws IOException {
        Assert.notNull(resource, "Workbook resource cannot be null");

        return load(resource.getInputStream());
    }
    
    public WorkbookLoader load(String fileName) throws FileNotFoundException {
        Assert.hasText(fileName, "Workbook file name cannot be empty");

        return load(new FileInputStream(fileName));
    }
   
    public class WorkbookLoader {
        private final Workbook workbook;
        private EntityRegistry entities;
        
        private WorkbookLoader(Workbook workbook) {
            Assert.notNull(workbook, "Workbook cannot be null");
            this.workbook = workbook;
        }
        
        protected WorkbookLoader continueIfValid() {
            WorkbookValidation validation = validate();
            if(validation.hasErrors()) {
                throw new InvalidWorkbookException(validation);
            }
            return this;
        }
        
        public WorkbookValidation validate() {
            MetaModel metamodel = metamodelGenerator.generate();
            return excelValidator.validate(workbook, metamodel);
        }
        
        public EntityRegistry entities() {
            continueIfValid();
            if(entities == null) {
                MetaModel metamodel = metamodelGenerator.generate();
                entities = entityImporter.load(workbook, metamodel);
            }
            return entities;
        }
        
        public WorkbookLoader persist() {
            entities = entityWriter.persist(entities());
            return this;
        }
    }
    
    public WorkbookBuilder buildWorkbook() {
        return new WorkbookBuilder();
    }
    
    public class WorkbookBuilder {
        private EntityRegistry entities = new EntityRegistry();
        
        private WorkbookBuilder() {
            // Hide initialization
        }
        
        public WorkbookBuilder includeAllEntities() {
            entities = entityReader.fetchAll();
            return this;
        }
        
        public <T> WorkbookBuilder includeEntities(Class<T> entityClass) {
            Assert.notNull(entityClass, "Entity class cannot be null");
            
            entities.addAll(entityReader.fetchForType(entityClass));
            return this;
        }
        
        public <T> WorkbookBuilder includeEntity(Class<T> entityClass, Object identifier) {
            Assert.notNull(entityClass, "Entity class cannot be null");
            Assert.notNull(identifier, "Entity identifier cannot be null");
            
            entities.add(entityClass, identifier, entityReader.fetch(entityClass, identifier));
            return this;
        }
        
        public <T> WorkbookBuilder includeEntity(Class<T> entityClass, Object identifier, T entity) {
            Assert.notNull(entity, "Entity cannot be null");
            Assert.notNull(entityClass, "Entity class cannot be null");
            Assert.notNull(identifier, "Entity identifier cannot be null");
            
            entities.add(entityClass, identifier, entity);
            return this;
        }
        
        public void write(OutputStream os) {
            Assert.notNull(os, "Workbook output stream cannot be null");
            
            MetaModel metamodel = metamodelGenerator.generate();
            Workbook workbook = entityExporter.export(entities, metamodel);
            excelWriter.write(workbook, os);
        }
        
        public void write(String fileName) throws FileNotFoundException {
            Assert.hasText(fileName, "Workbook file name cannot be empty");

            write(new FileOutputStream(fileName));
        }
    }
    
    public void setExcelParser(ExcelParser excelParser) {
        this.excelParser = excelParser;
    }

    public void setExcelWriter(ExcelWriter excelWriter) {
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

    public void setExcelValidator(ExcelValidator excelValidator) {
        this.excelValidator = excelValidator;
    }

    public void setMetamodelGenerator(MetaModelGenerator metaModelGenerator) {
        this.metamodelGenerator = metaModelGenerator;
    }

}
