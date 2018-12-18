package nl._42.jarb.utils.predicates;

import java.util.function.Predicate;

public class AndPredicate<T> implements Predicate<T> {
    
    private final Predicate<T> left;
    private final Predicate<T> right;
    
    public AndPredicate(Predicate<T> left, Predicate<T> right) {
        this.left = left;
        this.right = right;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(T t) {
        return left.test(t) && right.test(t);
    }
    
}
