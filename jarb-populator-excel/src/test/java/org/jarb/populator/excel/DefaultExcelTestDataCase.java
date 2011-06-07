package org.jarb.populator.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test case which provides working instances of each major component.
 * This test case is used to provide easy access, resulting in smaller
 * concrete test case classes.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public abstract class DefaultExcelTestDataCase {
    private static final String GENERATED_FILE_PATH = "src/test/resources/excel/generated/generated.xls";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ExcelDataManagerFactory excelDataManagerFactory;

    private ExcelDataManager excelDataManager;

    @Before
    public void setUpExcelTestData() {
        excelDataManager = excelDataManagerFactory.build();
    }

    // Utilities

    /**
     * Create a new output stream to the generated file path.
     * @return output stream to "generated" file
     */
    public OutputStream createFileOutputStream() {
        try {
            File file = new File(GENERATED_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the generated file, if any. When the file does not exist,
     * a runtime exception will be thrown
     * @return workbook representation of the generated file
     */
    public Workbook readGeneratedFile() {
        try {
            InputStream is = new FileInputStream(GENERATED_FILE_PATH);
            return excelDataManagerFactory.buildExcelParser().parse(is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a new full meta model, containing a definition of each
     * entity class inside our persistence context.
     * @return new "full" meta model
     */
    public MetaModel generateMetamodel() {
        return excelDataManagerFactory.buildMetamodelGenerator().generate();
    }

    // Accessors

    public ExcelDataManager getExcelDataManager() {
        return excelDataManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public ExcelDataManagerFactory getExcelDataManagerFactory() {
        return excelDataManagerFactory;
    }

}
