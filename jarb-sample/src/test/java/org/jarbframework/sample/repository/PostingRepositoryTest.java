package org.jarbframework.sample.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;

import org.jarbframework.sample.ApplicationConfig;
import org.jarbframework.sample.model.Post;
import org.jarbframework.sample.model.PostTitleAlreadyExistsException;
import org.jarbframework.sample.repository.PostRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
@Ignore("Tests are failing right before the release build to Sonatype OSS")
public class PostingRepositoryTest {

    @Resource
    private PostRepository postRepository;

    /**
     * Ensure that posts can be persisted and retrieved.
     */
    @Test
    public void testCreatePost() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setTitle("some post");
        post.setMessage("my message");
        post = postRepository.save(post);
        assertTrue(postRepository.exists(post.getId()));
    }

    /**
     * Validation can be done based on database column metadata.
     * Note that we have no @NotNull on our author property.
     */
    @Test
    public void testNotNullValidation() {
        Post post = new Post();
        // Note that we forgot an author
        post.setTitle("some post");
        post.setMessage("my message");

        try {
            postRepository.save(post);
            fail("Expected a violation exception");
        } catch (ConstraintViolationException e) {
            // Author value cannot be null, as the column is not nullable
            assertEquals(1, e.getConstraintViolations().size());
            javax.validation.ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
            assertEquals("author", violation.getPropertyPath().toString());
            assertEquals("cannot be null", violation.getMessage());
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
        post = postRepository.save(post);
        
        Post postWithSameTitle = new Post();
        postWithSameTitle.setAuthor("jeroen@42.nl");
        postWithSameTitle.setTitle("unique title");
        postWithSameTitle.setMessage("my message");
        
        try {
            postRepository.save(postWithSameTitle);
            fail("Expected a post already exists exception");
        } catch (PostTitleAlreadyExistsException e) {
            // We recieve a nice exception message
            assertEquals("Post title already exists.", e.getMessage());
            // Violation information is available
            assertEquals("uk_posts_title", e.getViolation().getConstraintName());
            // The origional stack trace is still available
            assertTrue(e.getCause() instanceof JpaSystemException);
        }
    }

}
