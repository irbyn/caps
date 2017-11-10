package documents;
/*
 * Storeage of Filenames & filepaths for application for easy alteration
 */
public class FileSystem {
	//File Names
	private String producerStatementForm;
	private String ps3Filed;

	private String consentForm;
	private String consentFiled;

	private String siteCheckForm;
	private String sc_Filed;

	private String invoiceFiled;
	private String siteCheckFiled;	
	private String quoteFiled;
	private String photoFiled;
	private String photoFolder;
	private String photoPrefix;
	

	public FileSystem() {

		producerStatementForm = "//C:/WFS/Forms/PS3.pdf";			//Location of Form Template
		ps3Filed = 				"//C:/WFS/Files/PS3/PS3_";			//Location where files are created

		consentForm = 			"//C:/WFS/Forms/Consent.pdf";		//Location of Form Template
		consentFiled = 			"//C:/WFS/Files/Consent/Consent_";	//Location where files are created
		
		siteCheckForm = 		"//C:/WFS/Forms/";			// + filename from DB (InstalType FilePath)
		sc_Filed = 				"//C:/WFS/Files/SCBlank";				//Location where files are created
		
		invoiceFiled = 			"//C:/WFS/Files/INV/INV_";			//Location where files are stored
		siteCheckFiled = 		"//C:/WFS/Files/SChk/SC_";			//Location where files are stored	
		quoteFiled = 			"//C:/WFS/Files/Quote/QT_";			//Location where files are stored	
		photoFiled = 			"//C:/WFS/Files/Photo/PH_";			//Location where files are stored
		photoFolder = 			"//C:/WFS/Files/Photo/";
		photoPrefix = 			"PH_";
	}

	public String getInvoice() {
		return invoiceFiled;
	}
	public String getSiteChk() {
		return siteCheckFiled;
	}
	public String getQuote() {
		return quoteFiled;
	}
	public String getPhotos() {
		return photoFiled;
	}
	public String getPhotoFolder() {
		return photoFolder;
	}
	public String getPhotoPrefix() {
		return photoPrefix;
	}

	
	public String getSiteCheckForm() {
		return siteCheckForm;
	}

	public String fileSC_ZC() {
		return sc_Filed;
	}
	public String getPS3Form() {
		return producerStatementForm;
	}

	public String getPS3Storage() {
		return ps3Filed;
	}

	public String getConsentForm() {
		return consentForm;
	}

	public String getConsentStorage() {
		return consentFiled;
	}
}
