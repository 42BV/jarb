package org.jarbframework.populator.excel.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jarbframework.populator.excel.entity.EntityRegistry;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class EntityRegistryTest {
    private EntityRegistry registry;

    @Before
    public void setUp() {
        registry = new EntityRegistry();
    }

    /**
     * Ensure that a registry starts empty. However, entities can be
     * added and again retrieved. After removing an entity, it can
     * no longer be retrieved.
     */
    @Test
    public void testGetAddRemove() {
        assertNull(registry.find(Employee.class, 1L));
        final Employee bas = new Employee();
        registry.add(Employee.class, 1L, bas);
        assertEquals(bas, registry.find(Employee.class, 1L));
        assertEquals(bas, registry.remove(Employee.class, 1L));
        assertNull(registry.find(Employee.class, 1L));
    }

    /**
     * Non-existing entities cannot be removed.
     */
    @Test
    public void testRemoveNonExisting() {
        assertNull(registry.find(Employee.class, 1L));
        assertNull(registry.remove(Employee.class, 1L));
    }

    /**
     * Multiple entities can be retrieved at once, as list format.
     */
    @Test
    public void testGetAllMultipleEntities() {
        final Employee bas = new Employee();
        final Employee jeroen = new Employee();
        registry.add(Employee.class, 1L, bas);
        registry.add(Employee.class, 2L, jeroen);
        List<Employee> employees = registry.withClass(Employee.class).list();
        assertEquals(Arrays.asList(bas, jeroen), employees);
    }

    /**
     * Whenever zero entities have been stored, we retrieve an empty list.
     */
    @Test
    public void testGetAllNonExisting() {
        assertTrue(registry.withClass(Employee.class).isEmpty());
    }

    /**
     * String representation of our registry shows all stored entities.
     */
    @Test
    public void testToString() {
        final Employee jeroen = new Employee();
        registry.add(Employee.class, 42L, jeroen);
        assertEquals("{class domain.entities.Employee={42=" + jeroen.toString() + "}}", registry.toString());
    }

}
