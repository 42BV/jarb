package org.jarb.sample.controller;

import org.jarb.constraint.BeanConstraintMetadata;
import org.jarb.constraint.BeanConstraintMetadataGenerator;
import org.jarb.sample.domain.Post;
import org.jarb.sample.service.PostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("posts")
public class PostingController {

    @Autowired
    private PostingService postingService;
    
    @Autowired
    private BeanConstraintMetadataGenerator constraintDescriptor;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("posts/index");
        mav.addObject("posts", postingService.getAllPosts());
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Post post(@RequestParam("post") Post post) {
        postingService.createPost(post);
        return post;
    }
    
    @RequestMapping(value = "structure", method = RequestMethod.GET)
    public BeanConstraintMetadata<Post> structure() {
        return constraintDescriptor.describe(Post.class);
    }

}
