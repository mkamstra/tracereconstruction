package nl.martijnkamstra.simscale.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by Martijn Kamstra on 02/04/2017.
 */
public class TraceTests {
    private TraceElement rootElement;
    private TraceElement parentElement;
    private TraceElement childElement1;
    private TraceElement childElement2;

    @Before
    public void setUp() throws Exception {
        LocalDateTime startChild1 = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 339);
        LocalDateTime endChild1 = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 342);
        childElement1 = new TraceElement(startChild1, endChild1, "service1", "22buxmqp", "nhxtegwv");
        LocalDateTime startChild2 = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 345);
        LocalDateTime endChild2 = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 361);
        childElement2 = new TraceElement(startChild2, endChild2, "service5", "22buxmqp", "3wos67cv");
        LocalDateTime startParent = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 318);
        LocalDateTime endParent = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 370);
        parentElement = new TraceElement(startParent, endParent, "service3", "bm6il56t","22buxmqp");
        LocalDateTime startRoot = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 271);
        LocalDateTime endRoot = LocalDateTime.of(2013, 11, 23, 10, 12, 35, 471);
        rootElement = new TraceElement(startRoot, endRoot, "service6", "null","bm6il56t");
    }

    @Test
    public void testTraceElementWithOneChild() {
        parentElement.addChild(childElement1);
        assertEquals("Size of trace element is not correct", 2, parentElement.getSize());
    }

    @Test
    public void testTraceElementWithTwoChildren() {
        parentElement.addChild(childElement1);
        parentElement.addChild(childElement2);
        assertEquals("Size of trace element is not correct", 3, parentElement.getSize());
    }

    @Test
    public void testRootElementWithOneChildAndTwoNestedChildren() {
        parentElement.addChild(childElement1);
        parentElement.addChild(childElement2);
        rootElement.addChild(parentElement);
        assertEquals("Size of trace element is not correct", 4, rootElement.getSize());
    }

    @Test
    public void testTraceWithRoot() {
        parentElement.addChild(childElement1);
        parentElement.addChild(childElement2);
        rootElement.addChild(parentElement);
        Trace trace = new Trace("kfhjgjkf");
        trace.addTraceElement(rootElement);
        assertEquals("Size of trace element is not correct", 4, rootElement.getSize());
        assertEquals("Trace state is not correct", 2, trace.getState());
    }

    @Test
    public void testTraceWithoutRoot() {
        parentElement.addChild(childElement1);
        parentElement.addChild(childElement2);
        Trace trace = new Trace("kfhjgjkf");
        assertEquals("Trace state is not correct", 0, trace.getState());
        trace.addTraceElement(parentElement);
        assertEquals("Size of trace element is not correct", 3, parentElement.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        assertEquals("Trace size is not correct", 3, trace.getSize());
    }

    @Test
    public void testTraceWithRootAddElementsInDifferentOrder() {
        Trace trace = new Trace("kfhjgjkf");
        assertEquals("Trace state is not correct", 0, trace.getState());
        trace.addTraceElement(childElement1);
        assertEquals("Size of trace element is not correct", 1, trace.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        trace.addTraceElement(childElement2);
        assertEquals("Size of trace element is not correct", 2, trace.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        trace.addTraceElement(parentElement);
        assertEquals("Size of trace element is not correct", 3, trace.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        assertEquals("Size of trace element is not correct", 3, parentElement.getSize());
        trace.addTraceElement(rootElement);
        assertEquals("Size of trace element is not correct", 4, trace.getSize());
        assertEquals("Trace state is not correct", 2, trace.getState());
        assertEquals("Size of trace element is not correct", 4, rootElement.getSize());
    }

    @Test
    public void testTraceWithRootWithNoDirectChildren() { // Parent element should be missing
        Trace trace = new Trace("kfhjgjkf");
        assertEquals("Trace state is not correct", 0, trace.getState());
        trace.addTraceElement(childElement1);
        assertEquals("Size of trace element is not correct", 1, trace.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        trace.addTraceElement(childElement2);
        assertEquals("Size of trace element is not correct", 2, trace.getSize());
        assertEquals("Trace state is not correct", 1, trace.getState());
        trace.addTraceElement(rootElement);
        assertEquals("Size of trace element is not correct", 3, trace.getSize());
        assertEquals("Trace state is not correct", 2, trace.getState());
        assertEquals("Size of trace element is not correct", 1, rootElement.getSize());
    }
}
