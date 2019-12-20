package com.atguigu.core.exception;

/**
 * @author 530
 * @date 2019/12/19
 */
public class CartException extends RuntimeException {

  public CartException() {
    super();
  }

  public CartException(String message) {
    super(message);
  }
}
