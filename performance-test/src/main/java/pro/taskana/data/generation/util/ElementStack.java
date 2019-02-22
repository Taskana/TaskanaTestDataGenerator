package pro.taskana.data.generation.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class wraps a list of elements and provides the pop functionality of stack.
 * 
 * @author fe
 *
 */
public class ElementStack<E> {

    private List<E> elements;

    public ElementStack() {
        elements = new ArrayList<>();
    }

    public ElementStack(List<E> elements) {
        this.elements = elements;
    }

    /**
     * Adds a new item to the wrapped list.
     * 
     * @param item
     *            to be added.
     */
    public void add(E item) {
        this.elements.add(item);
    }

    /**
     * Checks if the contained list is null or empty.
     * 
     * @return <code>true</code> if the contained list is null or empty, otherwise
     *         <code>false</code>
     */
    public boolean isEmpty() {
        return elements == null || elements.isEmpty();
    }

    /**
     * Removes a number of elements starting at index 0 and return this as list.
     * 
     * @param amount
     *            number of elements which will be removed from the list
     * @return list of removed elements
     */
    public List<E> pop(int amount) {
        List<E> subList = elements.subList(0, amount);
        elements = elements.subList(amount, elements.size());
        return subList;
    }

    /**
     * Supplies the number of elements in the stack.
     * 
     * @return size of the stack
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Supplies a list representation for the contained elements.
     * 
     * @return List of elements
     */
    public List<E> toList() {
        return elements;
    }

}
