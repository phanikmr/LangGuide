package com.bitsblender.langguide;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.memetix.mst.language.Language;
import com.memetix.mst.language.SpokenDialect;

public class Languages {

	public static HashMap<String, String> OCRLangs;
	public static HashMap<String, Enum<Language>> BingLangs;
	public static HashMap<String, Enum<SpokenDialect>> BingSpeakingLangs;
	static {
		OCRLangs = new HashMap<String, String>();
		OCRLangs.put("Arabic", "ara");
		OCRLangs.put("Bulgarian", "bul");
		OCRLangs.put("Catalan", "cat");
		OCRLangs.put("Chiness Simplified", "chi_sim");
		OCRLangs.put("Chiness Traditional", "chi_tra");
		OCRLangs.put("Danish", "dan");
		OCRLangs.put("Dutch", "deu");
		OCRLangs.put("Estonian", "est");
		OCRLangs.put("English", "eng");
		OCRLangs.put("Finnish", "fin");
		OCRLangs.put("French", "fra");
		OCRLangs.put("Haitian", "hat");
		OCRLangs.put("Hebrew", "heb");
		OCRLangs.put("Hindi", "hin");
		OCRLangs.put("Hungarian", "hun");
		OCRLangs.put("Indonesian", "ind");
		OCRLangs.put("Italian", "ita");
		OCRLangs.put("Japanese", "jpn");
		OCRLangs.put("Korean", "kor");
		OCRLangs.put("Latvian", "lat");
		OCRLangs.put("Lithuanian", "lit");
		OCRLangs.put("Malay", "mal");
		OCRLangs.put("Norwegian", "nor");
		OCRLangs.put("Polish", "pol");
		OCRLangs.put("Portuguese", "por");
		OCRLangs.put("Romanian", "ron");
		OCRLangs.put("Russian", "rus");
		OCRLangs.put("Slovak", "slk");
		OCRLangs.put("Spanish", "spa");
		OCRLangs.put("Thai", "tha");
		OCRLangs.put("Turkish", "tur");
		OCRLangs.put("Ukranian", "ukr");
		OCRLangs.put("Urdu", "urd");
		OCRLangs.put("Vietnamese", "vie");

		
		BingLangs = new HashMap<>();
		BingLangs.put("Arabic", Language.ARABIC);
		BingLangs.put("Bulgarian", Language.BULGARIAN);
		BingLangs.put("Catalan", Language.CATALAN);
		BingLangs.put("Chiness Simplified", Language.CHINESE_SIMPLIFIED);
		BingLangs.put("Chiness Traditional", Language.CHINESE_TRADITIONAL);
		BingLangs.put("Czech", Language.CZECH);
		BingLangs.put("Danish", Language.DANISH);
		BingLangs.put("Dutch", Language.DUTCH);
		BingLangs.put("Estonian", Language.ESTONIAN);
		BingLangs.put("English", Language.ENGLISH);
		BingLangs.put("Finnish", Language.FINNISH);
		BingLangs.put("French", Language.FRENCH);
		BingLangs.put("German", Language.GERMAN);
		BingLangs.put("Greek", Language.GREEK);
		BingLangs.put("Haitian", Language.HAITIAN_CREOLE);
		BingLangs.put("Hebrew", Language.HEBREW);
		BingLangs.put("Hindi", Language.HINDI);
		BingLangs.put("Hmong Daw", Language.HMONG_DAW);
		BingLangs.put("Hungarian", Language.HUNGARIAN);
		BingLangs.put("Indonesian", Language.INDONESIAN);
		BingLangs.put("Italian", Language.ITALIAN);
		BingLangs.put("Japanese", Language.JAPANESE);
		BingLangs.put("Korean", Language.KOREAN);
		BingLangs.put("Latvian", Language.LATVIAN);
		BingLangs.put("Lithuanian", Language.LITHUANIAN);
		BingLangs.put("Malay", Language.MALAY);
		BingLangs.put("Norwegian", Language.NORWEGIAN);
		BingLangs.put("Persian", Language.PERSIAN);
		BingLangs.put("Polish", Language.POLISH);
		BingLangs.put("Portuguese", Language.PORTUGUESE);
		BingLangs.put("Romanian", Language.ROMANIAN);
		BingLangs.put("Russian", Language.RUSSIAN);
		BingLangs.put("Slovak", Language.SLOVAK);
		BingLangs.put("Slovenian", Language.SLOVENIAN);
		BingLangs.put("Spanish", Language.SPANISH);
		BingLangs.put("Thai", Language.THAI);
		BingLangs.put("Turkish", Language.TURKISH);
		BingLangs.put("Ukranian", Language.UKRAINIAN);
		BingLangs.put("Urdu", Language.URDU);
		BingLangs.put("Vietnamese", Language.VIETNAMESE);
		
		
		BingSpeakingLangs = new HashMap<>();
		BingSpeakingLangs.put("Catalan", SpokenDialect.CATALAN_SPAIN);
		BingSpeakingLangs.put("Chiness Simplified", SpokenDialect.CHINESE_SIMPLIFIED_PEOPLES_REPUBLIC_OF_CHINA);
		BingSpeakingLangs.put("Chiness Traditional", SpokenDialect.CHINESE_TRADITIONAL_HONG_KONG_SAR);
		BingSpeakingLangs.put("Danish", SpokenDialect.DANISH_DENMARK);
		BingSpeakingLangs.put("Dutch", SpokenDialect.DUTCH_NETHERLANDS);
		BingSpeakingLangs.put("English", SpokenDialect.ENGLISH_UNITED_STATES);
		BingSpeakingLangs.put("Finnish", SpokenDialect.FINNISH_FINLAND);
		BingSpeakingLangs.put("French", SpokenDialect.FRENCH_FRANCE);
		BingSpeakingLangs.put("German", SpokenDialect.GERMAN_GERMANY);
		BingSpeakingLangs.put("Italian", SpokenDialect.ITALIAN_ITALY);
		BingSpeakingLangs.put("Japanese", SpokenDialect.JAPANESE_JAPAN);
		BingSpeakingLangs.put("Korean", SpokenDialect.KOREAN_KOREA);
		BingSpeakingLangs.put("Norwegian", SpokenDialect.NORWEGIAN_NORWAY);
		BingSpeakingLangs.put("Polish", SpokenDialect.POLISH_POLAND);
		BingSpeakingLangs.put("Portuguese", SpokenDialect.PORTUGUESE_PORTUGAL);
		BingSpeakingLangs.put("Russian", SpokenDialect.RUSSIAN_RUSSIA);
		BingSpeakingLangs.put("Spanish", SpokenDialect.SPANISH_SPAIN);	
	}

	public static HashMap<String, String> getOCRLangs() {
		return OCRLangs;
	}

	public static HashMap<String, Enum<Language>> getBingLangs() {
		return BingLangs;
	}

	public static Set<String> getOCRLangsStrings() {
		return OCRLangs.keySet();
	}

	public static Set<String> getBingLangsStrings() {
		return BingLangs.keySet();
	}

	public static Collection<Enum<Language>> getBingLangsCodes() {
		return BingLangs.values();

	}

	public static Collection<String> getOCRLangsCodes() {
		return OCRLangs.values();
	}
}
