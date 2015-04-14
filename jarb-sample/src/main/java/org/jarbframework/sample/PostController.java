package org.jarbframework.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    
    @Autowired
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Post> index() {
        return postRepository.findAll();
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
