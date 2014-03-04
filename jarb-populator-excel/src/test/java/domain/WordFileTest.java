package domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jarbframework.utils.bean.FlexibleBeanWrapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.DocumentRevision;
import domain.entities.Employee;
import domain.entities.WordFile;

public class WordFileTest {

    private WordFile wordFile;

    @Before
    public void setupWordFile() {
        wordFile = new WordFile();
    }

    @Test
    public void testGetID() {
        Long id = new Long(1);
        FlexibleBeanWrapper.wrap(wordFile).setPropertyValue("id", id);
        assertEquals(id, wordFile.getId());
    }

    @Test
    public void testSetGetTitle() {
        wordFile.setTitle("Year report");
        assertEquals("Year report", wordFile.getTitle());
    }

    @Test
    public void testSetGetWriter() {
        Employee employee = new Employee();
        employee.setName("Peter");
        wordFile.setWriter(employee);
        assertEquals(employee, wordFile.getWriter());
    }

    @Test
    public void testSetGetDocumentRevisions() {
        List<DocumentRevision> docRevs = new ArrayList<DocumentRevision>();
        DocumentRevision docRev = new DocumentRevision();
        docRev.setDate(new Date());
        docRevs.add(docRev);
        wordFile.setDocumentRevisions(docRevs);
        assertEquals(docRevs.get(0), wordFile.getDocumentRevisions().get(0));
    }

    @Test
    public void testSetGetCurrentDocumentRevision() {
        DocumentRevision docRev = new DocumentRevision();
        docRev.setDate(new Date());
        wordFile.setCurrentDocumentRevision(docRev);
        assertEquals(docRev, wordFile.getCurrentDocumentRevision());
    }

    @Test
    public void testSetGetSummary() {
        wordFile.setSummary("Boring");
        assertEquals("Boring", wordFile.getSummary());
    }

    @Test
    public void testAbstractMethod() {
        wordFile.abstractMethod();
    }
}
