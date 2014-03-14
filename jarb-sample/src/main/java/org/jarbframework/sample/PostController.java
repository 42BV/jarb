package org.jarbframework.sample;

import org.jarbframework.constraint.metadata.BeanConstraintDescription;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    
    private final BeanConstraintDescriptor beanConstraintDescriptor;

    public PostController(PostRepository postRepository, BeanConstraintDescriptor beanConstraintDescriptor) {
        this.postRepository = postRepository;
        this.beanConstraintDescriptor = beanConstraintDescriptor;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Post> index() {
        return postRepository.findAll();
    }
    
    @ResponseBody
    @RequestMapping(value = "/constraints", method = RequestMethod.GET)
    public BeanConstraintDescription structure() {
        return beanConstraintDescriptor.describe(Post.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public PostCreateResult post(@RequestBody Post post) {
        boolean success = false;
        String message = null;
        try {
            post = postRepository.save(post);
            message = "Post was created succesfully!";
            success = true;
        } catch (PostTitleAlreadyExistsException e) {
            message = "Post title '" + post.getTitle() + "' already exists";
        }
        return new PostCreateResult(success, message, post);
    }

    public static class PostCreateResult {
        
        private final boolean success;
        
        private final String message;
        
        private final Post post;

        public PostCreateResult(boolean success, String message, Post post) {
            this.success = success;
            this.message = message;
            this.post = post;
        }

        public boolean isSuccess() {
            return success;
        }

        public Post getPost() {
            return post;
        }

        public String getMessage() {
            return message;
        }
        
    }

}
