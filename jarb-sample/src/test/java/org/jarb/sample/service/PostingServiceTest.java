package org.jarb.sample.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.jarb.sample.domain.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class PostingServiceTest {

    @Resource
    private PostingService postingService;
    
    @Test
    public void testCreatePost() {
        Post post = new Post();
        post.setAuthor("jeroen@42.nl");
        post.setMessage("my message");
        post.setTitle("some post");
        post = postingService.createPost(post);
        List<Post> posts = postingService.getAllPosts();
        assertTrue(posts.contains(post));
    }
    
}
