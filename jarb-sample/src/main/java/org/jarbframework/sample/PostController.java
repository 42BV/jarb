package org.jarbframework.sample;

import javax.validation.Valid;

import org.jarbframework.constraint.metadata.BeanConstraintDescription;
import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BeanConstraintDescriptor beanConstraintDescriptor;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("posts");
        mav.addObject("posts", postRepository.findAll());
        return mav;
    }
    
    @RequestMapping(value = "/constraints", method = RequestMethod.GET)
    public BeanConstraintDescription structure() {
        return beanConstraintDescriptor.describe(Post.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public PostCreateResult post(@Valid Post post) {
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
