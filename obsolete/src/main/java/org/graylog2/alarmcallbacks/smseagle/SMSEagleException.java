/**
 * 
 */
package org.graylog2.alarmcallbacks.smseagle;

/**
 * @author mdelarosa
 *
 */
public class SMSEagleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SMSEagleException() {
	}

	/**
	 * @param message
	 */
	public SMSEagleException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SMSEagleException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SMSEagleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SMSEagleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
