package code.ui;

/**
* Honestly cannot believe this works! it's literally just an interface with a random method. But giving it a lambda function lets the random method call the lambda function. What???
*/
public interface UIActionSetter<T> {
  /**
  * A method that performs this action, whatever that may be
  */
  public void set(T v);
}
