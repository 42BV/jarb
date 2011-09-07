package org.jarbframework.populator.excel.mapping.importer;
//package org.jarb.populator.excel.mapping.importer;
//
//import org.apache.commons.lang.StringUtils;
//import org.jarb.populator.excel.entity.EntityRegistry;
//import org.jarb.populator.excel.entity.EntityTable;
//import org.jarb.populator.excel.metamodel.ClassDefinition;
//import org.jarb.populator.excel.metamodel.MetaModel;
//import org.jarb.populator.excel.metamodel.PropertyDefinition;
//import org.jarb.populator.excel.workbook.Row;
//import org.jarb.populator.excel.workbook.Sheet;
//import org.jarb.populator.excel.workbook.Workbook;
//import org.jarb.utils.ReflectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Startup to new entity importer implementation.
// * @author Jeroen van Schagen
// * @since 14-06-2011
// */
//public class SimpleEntityImporter implements EntityImporter {
//    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEntityImporter.class);
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public EntityRegistry load(Workbook workbook, MetaModel metamodel) {
//        EntityRegistry registry = new EntityRegistry();
//        for(ClassDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
//            Sheet sheet = workbook.getSheet(classDefinition.getTableName());
//            LOGGER.info("Importing from '{}'...", sheet.getName());
//            registry.addAll(loadEntities(sheet, classDefinition));
//        }
//        return registry;
//    }
//    
//    private <T> EntityTable<T> loadEntities(Sheet sheet, ClassDefinition<T> classDefinition) {
//        EntityTable<T> entities = new EntityTable<T>(classDefinition.getPersistentClass());
//        for (Row row : sheet.getRows()) {
//            T entity = loadEntity(row, classDefinition);
//            entities.add(getIdentifier(entity), entity);
//        }
//        return entities;
//    }
//    
//    private <T> T loadEntity(Row row, ClassDefinition<T> classDefinition) {
//        T entity = ReflectionUtils.instantiate(determineClass(row, classDefinition));
//        for(PropertyDefinition propertyDefinition : classDefinition.getPropertyDefinitions()) {
//            Object propertyValue = loadPropertyValue(row, propertyDefinition);
//            ReflectionUtils.setFieldValue(entity, propertyDefinition.getFieldName(), propertyValue);
//        }
//        return entity;
//    }
//    
//    private Object loadPropertyValue(Row row, PropertyDefinition propertyDefinition) {
//        return null;
//    }
//    
//    private <T> Class<? extends T> determineClass(Row row, ClassDefinition<T> classDefinition) {
//        Class<? extends T> entityClass = classDefinition.getPersistentClass();
//        final String discriminatorColumnName = classDefinition.getDiscriminatorColumnName();
//        if(StringUtils.isNotBlank(discriminatorColumnName)) {
//            String discriminatorValue = row.getCellAt(discriminatorColumnName).getValueAsString();
//            if(StringUtils.isNotBlank(discriminatorValue)) {
//                entityClass = classDefinition.getSubClass(discriminatorValue);
//            }
//        }
//        return entityClass;
//    }
//    
//    private Long getIdentifier(Object entity) {
//        return (Long) ReflectionUtils.getFieldValue(entity, "id");
//    }
//    
//}
