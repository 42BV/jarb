/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample;

import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class PostControllerTest extends ControllerTest {
    
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
        this.webClient.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Test\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Post was created succesfully!"));
    }
    
    @Test
    public void testPostAlreadyExists() throws Exception {
        Mockito.when(postRepository.save(Mockito.<Post> any())).thenThrow(new PostTitleAlreadyExistsException());
        
        this.webClient.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Test\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Post title 'Test' already exists"));
    }

}
