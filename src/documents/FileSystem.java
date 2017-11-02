package documents;

public class FileSystem {
	//File Names
	private String producerStatement;
	private String ps3Storage;
	
	private String consent;
	private String consentStorage;

	
	
	public FileSystem() {
		
		producerStatement = "//C:/WFS/Forms/PS3.pdf";
		ps3Storage = 		"//C:/WFS/Files/Consent";
		
		consent = 			"//C:/WFS/Forms/Consent.pdf";
		consentStorage = 	"//C:/WFS/Files/Consent";
	}

	public String getPS3Form() {
		return producerStatement;
	}

	public String getPS3Storage() {
		return ps3Storage;
	}
	
	public String getConsentForm() {
		return consent;
	}

	public String getConsentStorage() {
		return consentStorage;
	}
}
