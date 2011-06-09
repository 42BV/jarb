package org.jarb.sample.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.jarb.sample.domain.Post;
import org.jarb.sample.domain.PostTitleAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class PostingServiceTest {

    @Resource
    private PostingService postingService;
    
    /**
     * Ensure that posts can be persisted and retrieved.
     */
    @Test
    public void testCreatePost() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setTitle("some post");
        post.setMessage("my message");
        post = postingService.createPost(post);
        List<Post> posts = postingService.getAllPosts();
        assertTrue(posts.contains(post));
    }
    
    /**
     * Validation can be done based on database column metadata.
     * Note that we have no @NotNull on our author property.
     */
    @Test
    public void testValidationBasedOnDatabaseMetadata() {
        Post post = new Post();
        // Note that we forgot an author
        post.setTitle("some post");
        post.setMessage("my message");
        
        try {
            postingService.createPost(post);
            fail("Expected a violation exception");
        } catch(javax.validation.ConstraintViolationException e) {
            // Author value cannot be null, as the column is not nullable
            assertEquals(1, e.getConstraintViolations().size());
            javax.validation.ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
            assertEquals("author", violation.getPropertyPath().toString());
            assertEquals("cannot be empty", violation.getMessage());
        }
    }
    
    /**
     * Database constraint violations are mapped on java exceptions.
     */
    @Test
    public void testTitleAlreadyExists() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setTitle("unique title");
        post.setMessage("my message");
        post = postingService.createPost(post);
        
        Post postWithSameTitle = new Post();
        postWithSameTitle.setAuthor("jeroen@42.nl");
        postWithSameTitle.setTitle("unique title");
        postWithSameTitle.setMessage("my message");
        
        try {
            postingService.createPost(postWithSameTitle);
            fail("Expected a post already exists exception");
        } catch(PostTitleAlreadyExistsException e) { // JDBC exception is mapped to this
            // We recieve a nice exception message
            assertEquals("Unique key 'uk_posts_title' was violated.", e.getMessage());
            // Violation information is available
            assertEquals("uk_posts_title", e.getViolation().getConstraintName());
            // The origional stack trace is still available
            assertTrue(e.getCause() instanceof PersistenceException);
        }
    }
    
}
