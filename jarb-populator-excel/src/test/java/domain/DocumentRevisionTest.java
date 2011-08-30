package domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jarb.utils.bean.ModifiableBean;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Document;
import domain.entities.DocumentRevision;
import domain.entities.WordFile;

public class DocumentRevisionTest {

    private DocumentRevision documentRevision;

    @Before
    public void setupDocumentRevision() {
        documentRevision = new DocumentRevision();
    }

    @Test
    public void testSetGetID() {
        Long id = new Long("1");
        new ModifiableBean<DocumentRevision>(documentRevision).setPropertyValue("id", id);
        assertEquals(id, documentRevision.getId());
    }

    @Test
    public void testSetGetDocument() {
        Document document = new WordFile();
        document.setTitle("Document");
        documentRevision.setDocument(document);
        assertEquals(document, documentRevision.getDocument());
    }

    @Test
    public void testSetGetDate() {
        Date date = new Date();
        documentRevision.setDate(date);
        assertEquals(date, documentRevision.getDate());
    }
}
