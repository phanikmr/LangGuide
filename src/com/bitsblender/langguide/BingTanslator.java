package com.bitsblender.langguide;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


public class BingTanslator  {
	private TranslationListner translationListner;
	private String TranslatingText = "" ;
	private String TranslatedText = "";
	boolean useChoosedLanguage;
	private Language selectedDefaultLanguage,temporarySelectedLanguage;
	public BingTanslator(String clientId, String clientPassKey) {
		Translate.setClientId(clientId);
		Translate.setClientSecret(clientPassKey);
		this.translationListner = null;
		this.selectedDefaultLanguage = null;
		this.useChoosedLanguage = false;
	}

	public BingTanslator(String clientId, String clientPassKey, Language toLanguage) {
		Translate.setClientId(clientId);
		Translate.setClientSecret(clientPassKey);
		this.selectedDefaultLanguage = toLanguage;
		this.useChoosedLanguage = true;
	}

	public void setOnTranslationListner(TranslationListner tempListner) {
		this.translationListner = tempListner;
	}

	public void translate(String translatingText, Language toLanguage) {
		if (this.translationListner != null)
			this.translationListner.onTransaltionStarted();
		this.TranslatingText = translatingText;
		this.temporarySelectedLanguage = toLanguage;
		this.useChoosedLanguage = false;
		this.runBackGround();
	}

	public Language getActiveLanguage() {
		return this.selectedDefaultLanguage;
	}

	public void translate(String translatingText) {
		if (this.translationListner != null)
			this.translationListner.onTransaltionStarted();
		this.TranslatingText = translatingText;
		this.runBackGround();
	}

	public String recentTranslatingText() {
		return this.TranslatingText;
	}

	public String recentTranslatedText() {
		return this.TranslatedText;
	}

	protected Void doInBackground(Language lang) {
		try {
			this.TranslatedText = Translate.execute(this.TranslatingText, lang);
			System.out.println("Started trans:" + this.TranslatedText);
			System.out.println(this.TranslatedText);
		} catch (Exception e) {
			if (this.translationListner != null)
				this.translationListner.onTranslationFailed("Please Check the Network");
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute() {
		this.useChoosedLanguage = true;
		if (this.translationListner != null)
			this.translationListner.onTranslationCompleted(this.TranslatedText);
	}
	public void setDefaultLanguage(Language lang) {
		this.selectedDefaultLanguage = lang;
	}


	public void runBackGround() {
		Thread currentThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (BingTanslator.this.useChoosedLanguage) {
					if (BingTanslator.this.selectedDefaultLanguage == null) {
						throw new NullPointerException("Default Language Not Intialised");
					}
					BingTanslator.this.doInBackground(BingTanslator.this.selectedDefaultLanguage);
				}else{
					BingTanslator.this.doInBackground(BingTanslator.this.temporarySelectedLanguage);
				}

				BingTanslator.this.onPostExecute();
				
			}
		});
		currentThread.start();
		try {
			currentThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} 
}
