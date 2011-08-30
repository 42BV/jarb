package org.jarb.populator.excel.mapping.importer;

import static org.jarb.populator.excel.mapping.importer.ClassDefinitionFinder.findClassDefinitionByPersistentClass;
import static org.jarb.populator.excel.metamodel.generator.SuperclassRetriever.getListOfSuperClasses;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.utils.bean.ModifiableBean;

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
    public static void makeForeignRelations(ExcelRow excelRow, Map<EntityDefinition<?>, Map<Object, ExcelRow>> objectModel) throws NoSuchFieldException {
        for (Entry<PropertyDefinition, Key> entry : excelRow.getValueMap().entrySet()) {
            Key key = entry.getValue();

            EntityDefinition<?> classDefinition = findClassDefinitionByPersistentClass(objectModel.keySet(), key.getForeignClass());
            if (classDefinition == null) {
                Set<Class<?>> superClasses = getListOfSuperClasses(key.getForeignClass());
                classDefinition = getClassDefinitionFromParents(objectModel, superClasses);
            }

            // Check if the field is really in THIS excelRow. Thus the parents set is empty.
            Object foreignEntity = ForeignExcelRowGrabber.getInstanceValue(key, objectModel.get(classDefinition));
            ModifiableBean propertyAccessor = new ModifiableBean(excelRow.getCreatedInstance());
            if (propertyAccessor.isWritableProperty(entry.getKey().getName())) {
                propertyAccessor.setPropertyValue(entry.getKey().getName(), foreignEntity);
            }
        }
    }

    private static EntityDefinition<?> getClassDefinitionFromParents(Map<EntityDefinition<?>, Map<Object, ExcelRow>> objectModel, Set<Class<?>> parents) {
        EntityDefinition<?> classDefinition = null;
        for (Class<?> parent : parents) {
            classDefinition = ClassDefinitionFinder.findClassDefinitionByPersistentClass(objectModel.keySet(), parent);
            if (classDefinition != null) {
                break;
            }
        }
        return classDefinition;
    }
}
