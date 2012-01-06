package org.jarbframework.populator.excel.mapping.importer;

import static org.jarbframework.populator.excel.metamodel.generator.SuperclassRetriever.getListOfSuperClasses;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.orm.jpa.JpaMetaModelUtils;

/**
 * Used to map foreign relationships between ExcelRows.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class ForeignRelationsMapper {

    /** Private constructor. */
    private ForeignRelationsMapper() {
    }

    /**
     * Adds foreign relations to the Excelrecord, gathered from the objectModel with all excelrecords and classDefinitions in it.
     * @param objectModel Map with all excelrecords and classDefinitions in it
     * @param excelRow Object representing one row in an excel file.
     * @throws NoSuchFieldException 
     */
    public static void makeForeignRelations(ExcelRow excelRow, Map<Definition, Map<Object, ExcelRow>> objectModel) throws NoSuchFieldException {
        for (Entry<PropertyDefinition, Key> entry : excelRow.getValueMap().entrySet()) {
            Key key = entry.getValue();

            Definition classDefinition = DefinitionFinder.findDefinitionByPersistentClass(objectModel.keySet(), key.getForeignClass());
            if (classDefinition == null) {
                Set<Class<?>> superClasses = getListOfSuperClasses(key.getForeignClass());
                classDefinition = getDefinitionOfParent(objectModel, superClasses);
            }

            // Check if the field is really in THIS excelRow. Thus the parents set is empty.
            Object foreignEntity = ForeignExcelRowGrabber.getInstanceValue(key, objectModel.get(classDefinition));
            ModifiableBean<Object> propertyAccessor = ModifiableBean.wrap(excelRow.getCreatedInstance());
            if (propertyAccessor.isWritableProperty(entry.getKey().getName())) {
                propertyAccessor.setPropertyValue(entry.getKey().getName(), foreignEntity);
            }
        }
    }

    /**
     * Retrieves all the ElementCollection that reference to this EntityDefinition and put them in the appropriate field's collection.
     * @param entityDefinition Representation of the table the ElementCollection records may point to
     * @param excelRow ExcelRow ExcelRow the ElementCollections will be stored in
     * @param excelRowIdentifier Temporary identifier of the ExcelRow needed to couple the records
     * @param elementCollectionRowMap Map with all the ElementCollection records in it
     */
    public static void addElementCollectionRows(EntityDefinition<?> entityDefinition, ExcelRow excelRow, Object excelRowIdentifier,
            Map<ElementCollectionDefinition<?>, Map<Object, List<Object>>> elementCollectionRowMap) {
        Object instance = excelRow.getCreatedInstance();
        ModifiableBean<Object> propertyAccessor = ModifiableBean.wrap(instance);

        for (PropertyDefinition propertyDefinition : entityDefinition.properties()) {
            if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.ELEMENT_COLLECTION) {
                fillElementCollectionProperty(entityDefinition, excelRowIdentifier, elementCollectionRowMap, propertyAccessor, propertyDefinition);
            }
        }
    }

    /**
     * Fills the ElementCollection property with objects referencing to the record.
     * @param entityDefinition Representation of the table the ElementCollection records may point to
     * @param excelRowIdentifier Temporary identifier of the ExcelRow needed to couple the records
     * @param elementCollectionRowMap Map with all the ElementCollection records in it
     * @param propertyAccessor Accessible modifiable bean of ExcelRecord
     * @param propertyDefinition The ElementCollection PropertyDefinition
     */
    private static void fillElementCollectionProperty(EntityDefinition<?> entityDefinition, Object excelRowIdentifier,
            Map<ElementCollectionDefinition<?>, Map<Object, List<Object>>> elementCollectionRowMap, ModifiableBean<Object> propertyAccessor,
            PropertyDefinition propertyDefinition) {
        ElementCollectionDefinition<?> key = getElementCollectionDefinitionOfField(propertyDefinition.getField(), elementCollectionRowMap);
        if (propertyAccessor.isWritableProperty(propertyDefinition.getName()) && (key != null)) {
            //Now we have the proper ElementCollection. We need to add all ExcelRows that have the same key as this joincolumns combined.
            Map<String, String> elementCollectionJoinColumns = propertyDefinition.getElementCollectionJoinColumns();
            Map<String, Object> elementCollectionKeys = fillElementCollectionKeys(entityDefinition, excelRowIdentifier, propertyAccessor,
                    elementCollectionJoinColumns);
            propertyAccessor.setPropertyValue(propertyDefinition.getField().getName(), elementCollectionRowMap.get(key).get(elementCollectionKeys));
        }
    }

    /**
     * Fills a map with identifier columnnames and values corresponding with the @JoinColumn values of an ElementCollection's ExcelRow.
     * @param entityDefinition EntityDefinition of the enclosing class is needed to retrieve the referencedField name
     * @param excelRowIdentifier Identifier of current (enclosing) ExcelRow
     * @param propertyAccessor Accessor used to retrieve data from and edit data in an ExcelRow
     * @param elementCollectionJoinColumns Collection of ElementCollectionJoinColumns which hold the referencedColumn names (the actual column names in the Entity class) and the JoinColumn names (columns in ELementCollection class referring to the actual ones)
     * @return Map with identifier columnnames and their values.
     */
    private static Map<String, Object> fillElementCollectionKeys(EntityDefinition<?> entityDefinition, Object excelRowIdentifier,
            ModifiableBean<Object> propertyAccessor,
            Map<String, String> elementCollectionJoinColumns) {
        Map<String, Object> elementCollectionKeys = new HashMap<String, Object>();
        for (Entry<String, String> entry : elementCollectionJoinColumns.entrySet()) {
            String referencedColumnName = getReferencedColumnName(entityDefinition, elementCollectionJoinColumns, entry);
            Object value = getIdentifierValue(entityDefinition, excelRowIdentifier, propertyAccessor, referencedColumnName);
            elementCollectionKeys.put(entry.getKey(), value);
        }
        return elementCollectionKeys;
    }

    /**
     * Gets the identifier value of a referenced column name by either getting the property from the proper field or using the ExcelRowIdentifier in case it's an @Id column.
     * @param entityDefinition EntityDefinition of the enclosing class is needed to retrieve the referencedField name
     * @param excelRowIdentifier Identifier of current (enclosing) ExcelRow
     * @param propertyAccessor Accessor used to retrieve data from and edit data in an ExcelRow
     * @param referencedColumnName The name of the referenced column which will be used to retrieve the field name.
     * @return Value of identifier
     */
    private static Object getIdentifierValue(EntityDefinition<?> entityDefinition, Object excelRowIdentifier, ModifiableBean<Object> propertyAccessor,
            String referencedColumnName) {
        Object value = null;
        String referencedFieldName = entityDefinition.fieldNameByColumnName(referencedColumnName);
        if (entityDefinition.propertyByColumnName(referencedColumnName).isIdColumn()) {
            value = excelRowIdentifier;
        } else {
            value = propertyAccessor.getPropertyValue(referencedFieldName);
        }
        return value;
    }

    /**
     * Returns the Referenced column name from either the Id column dictionary or get the Id column name from the EntityDefinition if the dictionary is empty AND the CollectionsTable only holds
     * one @JoinColumn annotation. This strategy is based on the JPA's spec standard @JoinColumns description.
     * @param entityDefinition EntityDefinition to get id column for if referencedColumnName cannot otherwised be retrieved.
     * @param joinColumns JoinColumn collection.
     * @param idColumnDictionary Dictionary to translate JoinColumn names to actual Column names of the Entity
     * @return Referenced column name
     */
    private static String getReferencedColumnName(EntityDefinition<?> entityDefinition, Map<String, String> joinColumns,
            Entry<String, String> idColumnDictionary) {
        String referencedColumnName = idColumnDictionary.getValue();
        if ((referencedColumnName == null) && (joinColumns.size() == 1)) {
            //Get the name of the Id annotated column in the entityDefinition
            referencedColumnName = entityDefinition.getIdColumnName();
        }
        return referencedColumnName;
    }

    /**
     * Returns the ElementCollectionDefinition from the ElementCollectionRowMap which corresponds to the passed field.
     * @param field Field needed to retrieve the ElementCollectionDefinition it belongs to. 
     * @param elementCollectionRowMap Map with ElementCollectionRows which hold the elementCollectionDefinitions as keys
     * @return ElementCollectionDefinition belonging to passed field
     */
    private static ElementCollectionDefinition<?> getElementCollectionDefinitionOfField(Field field,
            Map<ElementCollectionDefinition<?>, Map<Object, List<Object>>> elementCollectionRowMap) {
        for (ElementCollectionDefinition<?> elementCollectionDefinition : elementCollectionRowMap.keySet()) {
            if (JpaMetaModelUtils.fieldIsOfClassType(elementCollectionDefinition.getDefinedClass(), field)) {
                return elementCollectionDefinition;
            }
        }
        return null;
    }

    /**
     * Returns the Definition of the parent in the parent set that has one.
     * @param objectModel ObjectModel which holds all the definitions
     * @param parents Parents to retrieve Definitions of
     * @return Definition of parent who has one.
     */
    private static Definition getDefinitionOfParent(Map<Definition, Map<Object, ExcelRow>> objectModel, Set<Class<?>> parents) {
        for (Class<?> parent : parents) {
            Definition classDefinition = DefinitionFinder.findDefinitionByPersistentClass(objectModel.keySet(), parent);
            if (classDefinition != null) {
                return classDefinition;
            }
        }
        return null;
    }
}
