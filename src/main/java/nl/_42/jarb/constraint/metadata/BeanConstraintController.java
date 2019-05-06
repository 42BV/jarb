package nl._42.jarb.constraint.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/constraints")
public class BeanConstraintController {

    private final BeanConstraintService constraintService;

    @Autowired
    public BeanConstraintController(BeanConstraintService constraintService) {
        this.constraintService = constraintService;
    }

    @GetMapping
    public Map<String, Map<String, PropertyConstraintDescription>> describeAll() {
        return constraintService.describeAll();
    }

    @ResponseBody
    @GetMapping("/{beanType}")
    public Map<String, PropertyConstraintDescription> describeBean(@PathVariable String beanType) {
        return constraintService.describe(beanType);
    }

}
