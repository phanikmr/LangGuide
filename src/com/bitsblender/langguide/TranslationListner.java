package com.bitsblender.langguide;

public interface TranslationListner {

	public void onTranslationCompleted(String translatedString);

	public void onTranslationFailed(String causeOfFailure);

	public void onTransaltionStarted();
}
