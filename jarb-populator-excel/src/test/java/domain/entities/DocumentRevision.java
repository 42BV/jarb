package domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * DocumentRevision holds a reference to the abstract class Document.
 * In this case a circular reference is present with an abstract class and an implementation.
 * This class is there to test ETD's behaviour with this problem.
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "document_revisions")
public class DocumentRevision {

    @Id
    @Column(name = "revision_id")
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "writing_date", nullable = true)
    private Date date;

    /**
     * Returns the document revision's id.
     * @return Document revision's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the document this document revision belongs to.
     * @param document Document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Returns the document this document revision belongs to.
     * @return Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the date of the revision.
     * @param date date
     */
    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

    /**
     * Returns the revision date.
     * @return Date
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

}
