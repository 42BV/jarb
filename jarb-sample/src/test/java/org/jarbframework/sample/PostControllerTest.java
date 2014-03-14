/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample;

import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;

import org.jarbframework.constraint.metadata.BeanConstraintDescription;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
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
    
    @Mock
    private BeanConstraintDescriptor beanConstraintDescriptor;

    @Before
    public void setUp() {
        this.initWebClient(new PostController(postRepository, beanConstraintDescriptor));
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
    public void testDescribe() throws Exception {
        BeanConstraintDescription description = new BeanConstraintDescription(Post.class);
        
        Mockito.when(beanConstraintDescriptor.describe(Post.class)).thenReturn(description);
        
        this.webClient.perform(MockMvcRequestBuilders.get("/posts/constraints"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.javaType").value(Post.class.getName()));
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
