package org.jarb.populator.excel.mapping.excelrow;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.mad.hactar.common.ReflectionUtil;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ClassDefinitionFinder;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.SuperclassRetriever;
import org.jarb.populator.excel.workbook.validator.FieldValidator;

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
    public static void makeForeignRelations(ExcelRow excelRow, Map<ClassDefinition, Map<Integer, ExcelRow>> objectModel) throws NoSuchFieldException {
        for (Entry<PropertyDefinition, Key> entry : excelRow.getValueMap().entrySet()) {
            Key key = entry.getValue();

            //Get foreign Entity or Entities depending on Key Type
            System.out.println(key.getForeignClass().toString());

            ClassDefinition classDefinition = ClassDefinitionFinder.findClassDefinitionByPersistentClass(objectModel.keySet(), key.getForeignClass());
            if (classDefinition == null) {
                Set<Class<?>> superClasses = SuperclassRetriever.getListOfSuperClasses(key.getForeignClass());
                classDefinition = getClassDefinitionFromParents(objectModel, superClasses);
            }

            //Check if the field is really in THIS excelRow. Thus the parents set is empty.
            Object foreignEntity = ForeignExcelRowGrabber.getInstanceValue(key, objectModel.get(classDefinition));
            if (FieldValidator.isExistingField(entry.getKey().getFieldName(), excelRow.getCreatedInstance().getClass())) {
                ReflectionUtil.setFieldValue(excelRow.getCreatedInstance(), entry.getKey().getFieldName(), foreignEntity);
            }

        }
    }

    private static ClassDefinition getClassDefinitionFromParents(Map<ClassDefinition, Map<Integer, ExcelRow>> objectModel, Set<Class<?>> parents) {
        ClassDefinition classDefinition = null;
        for (Class<?> parent : parents) {
            classDefinition = ClassDefinitionFinder.findClassDefinitionByPersistentClass(objectModel.keySet(), parent);
            if (classDefinition != null) {
                break;
            }
        }
        return classDefinition;
    }
}
