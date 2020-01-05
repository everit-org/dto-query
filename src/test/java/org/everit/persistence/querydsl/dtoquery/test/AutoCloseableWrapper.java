package org.everit.persistence.querydsl.dtoquery.test;

public class AutoCloseableWrapper<T> implements AutoCloseable {

  public interface CloseAction<T> {
    void close(T wrapped) throws Exception;
  }

  private final CloseAction<T> closeAction;

  private final T wrapped;

  public AutoCloseableWrapper(T wrapped, CloseAction<T> closeAction) {
    this.wrapped = wrapped;
    this.closeAction = closeAction;
  }

  @Override
  public void close() throws Exception {
    this.closeAction.close(this.wrapped);
  }

  public T getWrapped() {
    return this.wrapped;
  }

}
