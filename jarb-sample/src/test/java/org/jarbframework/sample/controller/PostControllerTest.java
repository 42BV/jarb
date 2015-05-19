/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample.controller;

import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;

import org.jarbframework.sample.controller.PostController;
import org.jarbframework.sample.model.Post;
import org.jarbframework.sample.repository.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class PostControllerTest extends AbstractControllerTest {
    
    @Mock
    private PostRepository postRepository;

    @Before
    public void setUp() {
        this.initWebClient(new PostController(postRepository));
    }

    @Test
    public void testList() throws Exception {
        Post post = new Post();
        post.setTitle("Test title");
        
        Mockito.when(postRepository.findAll()).thenReturn(Arrays.asList(post));

        this.webClient.perform(MockMvcRequestBuilders.get("/posts"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test title"));
    }

    @Test
    public void testPost() throws Exception {
        Post post = new Post();
        post.setMessage("My message");
        
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);

        this.webClient.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Test\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("My message"));
    }

}
