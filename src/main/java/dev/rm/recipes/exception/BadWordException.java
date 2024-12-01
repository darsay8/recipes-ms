package dev.rm.recipes.exception;

public class BadWordException extends RuntimeException {
  private String message;

  public BadWordException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}