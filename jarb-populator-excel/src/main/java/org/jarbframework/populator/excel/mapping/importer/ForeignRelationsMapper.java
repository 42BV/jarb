package org.jarbframework.populator.excel.mapping.importer;

import static org.jarbframework.populator.excel.metamodel.generator.SuperclassRetriever.getListOfSuperClasses;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.utils.bean.ModifiableBean;

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
