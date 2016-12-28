package org.tdc.model;

/**
 * State shared between models and the nodes (for that corresponding model).
 */
public class ModelSharedState {
	private boolean isImmutable = false;
	
	public final void setImmutable() {
		isImmutable = true;
	}
	
	public final boolean isImmutable() {
		return isImmutable;
	}
	
	public final void throwExceptionIfImmutable(String methodName) {
		if (isImmutable) {
			throw new UnsupportedOperationException("Unable to call '" + methodName + "'; model is immutable");
		}
	}
}
