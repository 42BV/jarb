/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Provides constraint information on bean types.
 *
 * @author Jeroen van Schagen
 * @since Apr 14, 2015
 */
@Controller
@RequestMapping("/constraint")
public class BeanConstraintController {
    
    private final BeanConstraintDescriptor beanConstraintDescriptor;
    
    public BeanConstraintController(BeanConstraintDescriptor beanConstraintDescriptor) {
        this.beanConstraintDescriptor = beanConstraintDescriptor;
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<BeanConstraintDescription> describeAll() {
        return beanConstraintDescriptor.describeAll();
    }

    @ResponseBody
    @RequestMapping(value = "/{beanType}", method = RequestMethod.GET)
    public BeanConstraintDescription describeBean(@PathVariable String beanType) {
        return beanConstraintDescriptor.describeBean(beanType);
    }

}
