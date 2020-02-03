package com.serjer.playground.exception;
public class CannotWaitInQueueException extends RuntimeException{

	  public CannotWaitInQueueException(String message) {
		  super(message);
	  }
}