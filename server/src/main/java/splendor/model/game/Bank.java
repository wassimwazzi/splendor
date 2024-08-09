package splendor.model.game;

/**
 * Generic bank interface to store payment elements. Tokens in this case.
 *
 * @param <T> the type of the payment elements
 */
public interface Bank<T> {
  /**
   * Add an element to the bank.
   *
   * @param element the element to add
   */
  void add(T element);

  /**
   * Supports adding multiple elements at once.
   *
   * @param elements the element to add
   */
  default void add(T... elements) {
    for (T element : elements) {
      add(element);
    }
  }

  /**
   * Remove an element from the bank.
   *
   * @pre count(element) is less than element.maxAmount()
   * @param element the element to remove
   */
  void remove(T element);

  /**
   * Check if the bank contains an element.
   *
   * @pre contains(element) is true
   * @param element the element to check
   * @return true if the bank contains the element, false otherwise
   */
  boolean contains(T element);

  /**
   * Count the number of elements in the bank.
   *
   * @param element the element to count
   * @return the number of elements in the bank
   */
  int count(T element);
}
