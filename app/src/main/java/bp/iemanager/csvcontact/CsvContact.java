package bp.iemanager.csvcontact;

import java.io.Serializable;

import bp.iemanager.StringUtils;
import bp.iemanager.importer.MatchType;


/**
 * This class represents one CSV contact for row. 
 * @author Viliam Kasala, <xkasal01@stud.fit.vutbr.cz>
 *
 */
public class CsvContact implements Serializable {
	/**
	 * ID sê-ri được tạo tự động
	 */
	private static final long serialVersionUID = -1821639886723668862L;
	/**
	 * Mảng chuỗi các mục CSV.
	 */
	protected String[] csvLine;
	/**
	 * Loại liên hệ CSV - Outlook / Thunderbird
	 */
	protected int csvType;
		
	public CsvContact(String strings[]) {		
		createNewCopy(strings);
		setType();
	}
	
	public CsvContact(int _count) {
		csvLine = new String[_count];
		emptyStringsIfNull();
		setType();
	}
	
	/**
	 * Phương pháp xóa ký tự giọng.
	 */
	public void removeAccentChars() {
		for (int i = 0; i < csvLine.length; i++) {
			csvLine[i] = StringUtils.convertNonAscii(csvLine[i]);
		}
	}
	
	/**
	 * Phương pháp kiểm tra xem đối tượng csvLine có ít nhất một giá trị để xuất
	 * @ trả về true / false theo
	 */
	public boolean hasAtLeastOneValue() {
		for (int i = 0; i < csvLine.length; i++) {
			if(!csvLine[i].equals(""))
				return true;
		}
		return false;
	}
	
	/**
	 * Tạo bản sao chuỗi mới.
	 * @param strings - các mục của liên hệ CSV.
	 */
	protected void createNewCopy(String[] strings) {
		if(strings.length > ThunderbirdConstants.THUDERBIRD_ITEM_COUNT) {
			csvLine = new String[OutlookConstants.OUTLOOK_ITEM_COUNT];
			emptyStringsIfNull();
			for (int i = 0; i < strings.length; i++) {
				csvLine[i] = new String(strings[i]);
			}
		}
		else {
			csvLine = new String[ThunderbirdConstants.THUDERBIRD_ITEM_COUNT];
			emptyStringsIfNull();
			for (int i = 0; i < strings.length; i++) {
				csvLine[i] = new String(strings[i]);
			}
		}
	}
	
	/**
	 *Phương thức null tất cả các mục CSV.
	 */
	public void emptyStringsIfNull() {
		for (int i = 0; i < csvLine.length; i++) {
			if(csvLine[i] == null) {
				csvLine[i] = "";
			}
		}
	}
		
	/**
	 *Phương thức so sánh chuỗi với mục chuỗi trong liên hệ CSV.
	 * @param index - chỉ mục của mục trong liên hệ CSV.
	 * @param s - chuỗi mà chúng tôi muốn so sánh.
	 * @ trả về true / false nếu chúng bằng nhau.
	 */
	public boolean equalTwoStrings(int index, String s) {
		return csvLine[index].equals(s);
	}
	
	/**
	 * Chiều dài trả về của mục được chỉ định theo chỉ mục.
	 * @param index - chỉ mục của mục trong liên hệ CSV.
	 * @return chiều dài của mục.
	 */
	public int getStringLength(int index) {
		return csvLine[index].length();
	}
	
	/**
	 *Phương pháp loại bỏ tất cả các khoảng trắng ở đầu và ở cuối tất cả các mục.
	 */
	public void callTrimOn() {
		for (int i = 0; i < csvLine.length; i++) {
			csvLine[i] = csvLine[i].trim();
		}
	}
	
	public int getLength() {
		return csvLine.length;
	}
	
	public int getType() {
		return csvType;
	}
	
	private void setType() {
		if(csvLine.length == ThunderbirdConstants.THUDERBIRD_ITEM_COUNT)
			csvType = ThunderbirdConstants.THUDERBIRD_ITEM_COUNT;
		else
			csvType = OutlookConstants.OUTLOOK_ITEM_COUNT;
	}
	
	/**
	 * Kiểm tra các mục địa chỉ nhà bưu chính của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục địa chỉ nhà.
	 */
	public boolean checkPostalHomeThunderbird() {
		
		return (!csvLine[ThunderbirdConstants.T_HOME_CITY].equals("") || !csvLine[ThunderbirdConstants.T_HOME_COUNTRY].equals("") ||  !csvLine[ThunderbirdConstants.T_HOME_PSC].equals("") || 
				 !csvLine[ThunderbirdConstants.T_HOME_REGION].equals("") ||  !csvLine[ThunderbirdConstants.T_HOME_STREET].equals("") );
	}
	
	/**
	 * Kiểm tra các mục địa chỉ công việc bưu chính của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục địa chỉ cơ quan.
	 */
	public boolean checkPostalWorkThunderbird() {
		
		return (!csvLine[ThunderbirdConstants.T_WORK_CITY].equals("") || !csvLine[ThunderbirdConstants.T_WORK_COUNTRY].equals("") ||  !csvLine[ThunderbirdConstants.T_WORK_PSC].equals("") || 
				 !csvLine[ThunderbirdConstants.T_WORK_REGION].equals("") ||  !csvLine[ThunderbirdConstants.T_WORK_STREET].equals("") );
	}
	
	/**
	 * Kiểm tra các mục tên cấu trúc của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục tên.
	 */
	public boolean checkStructureNameThunderbird() {
		
		return (!csvLine[ThunderbirdConstants.T_GIVEN_NAME].equals("") || !csvLine[ThunderbirdConstants.T_DISPLAY_NAME].equals("") ||  !csvLine[ThunderbirdConstants.T_FAMILY_NAME].equals(""));
	}
	
	/**
	 * Kiểm tra các mục tổ chức của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục tổ chức.
	 */
	public boolean checkOrganizationThunderbird() {
		
		return (!csvLine[ThunderbirdConstants.T_DEPARTMENT].equals("") || !csvLine[ThunderbirdConstants.T_COMPANY].equals("") ||  !csvLine[ThunderbirdConstants.T_JOB_TITLE].equals(""));
	}
	
	/**
	 * Kiểm tra các mục địa chỉ email của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục địa chỉ email.
	 */
	public boolean checkEmailThunderbird() {
		return (!csvLine[ThunderbirdConstants.T_EMAIL1].equals("") || !csvLine[ThunderbirdConstants.T_EMAIL2].equals(""));
	}
	
	/**
	 * Kiểm tra các mục trên trang web của liên hệ thunderbird.
	 * @return true / false nếu có ít nhất một mục trên trang web.
	 */
	public boolean checkWebsiteThunderbird() {
		return (!csvLine[ThunderbirdConstants.T_WEB_ADDRESS].equals("") || !csvLine[ThunderbirdConstants.T_WEB_ADDRESS2].equals(""));
	}
	
	/**
	 *Kiểm tra các mục địa chỉ nhà bưu điện của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục địa chỉ nhà.
	 */
	public boolean checkPostalHomeOutlook() {
		return (!csvLine[OutlookConstants.O_HOME_CITY].equals("") || !csvLine[OutlookConstants.O_HOME_COUNTRY].equals("") ||  !csvLine[OutlookConstants.O_HOME_PSC].equals("") || 
				 !csvLine[OutlookConstants.O_HOME_REGION].equals("") ||  !csvLine[OutlookConstants.O_HOME_STREET].equals("") );
	}
	
	/**
	 * Kiểm tra các mục địa chỉ công việc bưu chính của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục địa chỉ cơ quan.
	 */
	public boolean checkPostalWorkOutlook() {
		return (!csvLine[OutlookConstants.O_WORK_CITY].equals("") || !csvLine[OutlookConstants.O_WORK_COUNTRY].equals("") ||  !csvLine[OutlookConstants.O_WORK_PSC].equals("") || 
				 !csvLine[OutlookConstants.O_WORK_REGION].equals("") ||  !csvLine[OutlookConstants.O_WORK_STREET].equals("") );
	}
	
	/**

	 * Kiểm tra các mục địa chỉ bưu điện khác của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục địa chỉ khác.
	 */
	public boolean checkPostalOtherOutlook() {
		return (!csvLine[OutlookConstants.O_OTHER_CITY].equals("") || !csvLine[OutlookConstants.O_OTHER_COUNTRY].equals("") ||  !csvLine[OutlookConstants.O_OTHER_PSC].equals("") || 
			 !csvLine[OutlookConstants.O_OTHER_REGION].equals("") ||  !csvLine[OutlookConstants.O_OTHER_STREET].equals("") );
	}
	
	/**

	 * Kiểm tra các mục tên cấu trúc của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục tên.
	 */
	public boolean checkStructureNameOutlook() {
		return (!csvLine[OutlookConstants.O_GIVEN_NAME].equals("") || !csvLine[OutlookConstants.O_MIDDLE_NAME].equals("") ||  !csvLine[OutlookConstants.O_FAMILY_NAME].equals("") ||  !csvLine[OutlookConstants.O_TITLE].equals("") ||  !csvLine[OutlookConstants.O_SUFFIX].equals(""));
	}
	
	/**
	 * Kiểm tra các mục tổ chức của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục tổ chức.
	 */
	public boolean checkOrganizationOutlook() {
		return (!csvLine[OutlookConstants.O_DEPARTMENT].equals("") || !csvLine[OutlookConstants.O_COMPANY].equals("") ||  !csvLine[OutlookConstants.O_JOB_TITLE].equals(""));
	}
	
	/**
	 * Kiểm tra các mục địa chỉ email của liên hệ xem.
	 * @return true / false nếu có ít nhất một mục địa chỉ email.
	 */
	public boolean checkEmailOutlook() {
		return (!csvLine[OutlookConstants.O_EMAIL1].equals("") || !csvLine[OutlookConstants.O_EMAIL2].equals("") || !csvLine[OutlookConstants.O_EMAIL3].equals(""));
	}
	
	/**
	 * Kiểm tra các mặt hàng điện thoại của liên hệ xem.
	 * @ trả về true / false nếu có ít nhất một mục điện thoại.
	 */
	public boolean checkPhoneOutlook() {
		return (!csvLine[OutlookConstants.O_HOME_FAX].equals("") || !csvLine[OutlookConstants.O_HOME_PHONE].equals("") 
			|| !csvLine[OutlookConstants.O_HOME_PHONE2].equals("") || !csvLine[OutlookConstants.O_WORK_FAX].equals("") 
			|| !csvLine[OutlookConstants.O_WORK_PHONE].equals("") || !csvLine[OutlookConstants.O_WORK_PHONE2].equals("")
			|| !csvLine[OutlookConstants.O_MOBILE].equals("") || !csvLine[OutlookConstants.O_OTHER_PHONE].equals("") 
			|| !csvLine[OutlookConstants.O_PAGER].equals(""));
	}
	
	/**
	 * Phương pháp tìm kiếm số điện thoại trong các mục điện thoại thunderbird.
	 * @param phoneNumber - chuỗi số điện thoại.
	 * @param matchedType - matchType.
	 * @ trả lại đúng / sai nếu có số điện thoại.
	 */
	public boolean findPhoneThunderbird(String phoneNumber, MatchType matchedType) {
		if(checkPhoneThunderbird()) {
			return ( equal(ThunderbirdConstants.T_HOME_PHONE, phoneNumber, matchedType) 
					|| equal(ThunderbirdConstants.T_WORK_PHONE, phoneNumber, matchedType) 
					|| equal(ThunderbirdConstants.T_FAX, phoneNumber, matchedType) 
					|| equal(ThunderbirdConstants.T_PAGER, phoneNumber, matchedType) 
					|| equal(ThunderbirdConstants.T_MOBILE, phoneNumber, matchedType));
		}
		else 
			return false;
	}
	
	/**
	 * Phương pháp tìm kiếm số điện thoại trong các mục của điện thoại Outlook.
	 * @param phoneNumber - chuỗi số điện thoại.
	 * @param matchedType - matchType.
	 * @ trả lại đúng / sai nếu có số điện thoại.
	 */
	public boolean findPhoneOutlook(String phoneNumber, MatchType matchedType) {
		if(checkPhoneOutlook()) {
			return (equal(OutlookConstants.O_HOME_FAX, phoneNumber, matchedType)  || equal(OutlookConstants.O_HOME_PHONE, phoneNumber, matchedType) 
					|| equal(OutlookConstants.O_HOME_PHONE2, phoneNumber, matchedType) || equal(OutlookConstants.O_WORK_FAX, phoneNumber, matchedType) 
					|| equal(OutlookConstants.O_WORK_PHONE, phoneNumber, matchedType) || equal(OutlookConstants.O_WORK_PHONE2, phoneNumber, matchedType)
					|| equal(OutlookConstants.O_MOBILE, phoneNumber, matchedType) || equal(OutlookConstants.O_OTHER_PHONE, phoneNumber, matchedType) 
					|| equal(OutlookConstants.O_PAGER, phoneNumber, matchedType));
		}
		else 
			return false;
	}
	
	/**
	 * Kiểm tra các mặt hàng điện thoại của liên hệ thunderbird.
	 * @ trả về true / false nếu có ít nhất một mục điện thoại.
	 */
	public boolean checkPhoneThunderbird() {
		return (!csvLine[ThunderbirdConstants.T_HOME_PHONE].equals("") || !csvLine[ThunderbirdConstants.T_WORK_PHONE].equals("") || !csvLine[ThunderbirdConstants.T_FAX].equals("") || !csvLine[ThunderbirdConstants.T_PAGER].equals("") || !csvLine[ThunderbirdConstants.T_MOBILE].equals("") );
	}
	
	/**
	 * Kiểm tra mục trang web của liên hệ xem.
	 * @return true / false nếu có mục trang web.
	 */ 
	public boolean checkWebsiteOutlook() {
		return (!csvLine[OutlookConstants.O_WEB_PAGE].equals(""));
	}
	
	public boolean isNull(int index) {
		return (csvLine[index] == "");
	}
	
	public String getString(int index) {
		if(csvLine.length > index)
			return (csvLine[index] == null) ? "" : csvLine[index];
		else 
			return null;
	}
	
	public void setString(int index, String string) {
		if(index < csvLine.length)
			csvLine[index]= string;
	}
	
	public String[] getArrayOfStrings() {
		return csvLine;
	}
	
	/**
	 * Phương pháp tìm email trong các mục email của liên hệ csv.
	 * @param email tìm kiếm email.
	 * @param matchedType loại đối sánh.
	 * @return true / false nếu nó chứa email này.
	 */
	public boolean findEmailOutlook(String email, MatchType matchedType) {
		if(!csvLine[OutlookConstants.O_EMAIL1].equals(""))
			if(equal(OutlookConstants.O_EMAIL1, email, matchedType))
				return true;
		if(!csvLine[OutlookConstants.O_EMAIL2].equals("")) 
			if(equal(OutlookConstants.O_EMAIL2, email, matchedType))
				return true;
		if(!csvLine[OutlookConstants.O_EMAIL3].equals("")) 
			if(equal(OutlookConstants.O_EMAIL3, email, matchedType))
				return true;
		return false;
	}
	
	/**
	 * Phương pháp tìm email trong các mục email của liên hệ csv.
	 * @param email tìm kiếm email.
	 * @param matchedType loại đối sánh.
	 * @return true / false nếu nó chứa email này.
	 */
	public boolean findEmailUserOutlook(String email, MatchType matchedType) {
		if(!csvLine[OutlookConstants.O_USER1].equals("")) {
			if(equal(OutlookConstants.O_USER1, email, matchedType))
				return true;
		}
		if(!csvLine[OutlookConstants.O_USER2].equals("")) { 
			if(equal(OutlookConstants.O_USER2, email, matchedType))
				return true;
		}
		if(!csvLine[OutlookConstants.O_USER3].equals("")) { 
			if(equal(OutlookConstants.O_USER3, email, matchedType))
				return true;
		}
		return false;
	}
	
	/**
	 * Phương pháp tìm email trong các mục email của liên hệ csv.
	 * @param email tìm kiếm email.
	 * @param matchedType loại đối sánh.
	 * @return true / false nếu nó chứa email này.
	 */
	public boolean findEmailCustomThunderbird(String email, MatchType matchedType) {
		if(!csvLine[ThunderbirdConstants.T_OTHER].equals(""))
			if(equal(ThunderbirdConstants.T_OTHER, email, matchedType))
				return true;
		if(!csvLine[ThunderbirdConstants.T_OTHER2].equals(""))	
			if(equal(ThunderbirdConstants.T_OTHER2, email, matchedType))
				return true;
		return false;
	}
	
	/**
	 *Phương thức tìm email trong mục email của liên hệ csv.
	 * @param email tìm kiếm email.
	 * @param matchedType loại đối sánh.
	 * @return true / false nếu nó chứa email này.
	 */
	public boolean findEmailThunderbird(String email, MatchType type) {
		if(!csvLine[ThunderbirdConstants.T_EMAIL1].equals(""))
			if( equal(ThunderbirdConstants.T_EMAIL1, email, type) )
				return true;
		if(!csvLine[ThunderbirdConstants.T_EMAIL2].equals(""))	
			if(equal(ThunderbirdConstants.T_EMAIL2, email, type))
				return true;
		return false;
	}
	
	/**
	 * So sánh hai loại ond dựa trên chuỗi.
	 * @param position - vị trí của chuỗi đầu tiên.
	 * @param s2 - chuỗi sai.
	 * @param type - loại so sánh.
	 * @ trả về true / false nếu chúng bằng nhau.
	 */
	public boolean equal(int position, String s2, MatchType type) {
		switch (type) {
			case CASE_SENSITIVE:
				return getString(position).equals(s2);
			case IGNORE_CASE:
				return getString(position).equalsIgnoreCase(s2);
			case IGNORE_ACCENTS_CASE_SENSITIVE:
				return StringUtils.convertNonAscii(getString(position)).equals(StringUtils.convertNonAscii(s2));
			case IGNORE_ACCENTS_AND_CASES:
				return StringUtils.convertNonAscii(getString(position)).equalsIgnoreCase(StringUtils.convertNonAscii(s2));
		}
		return false;
	}
	
	public static String removeAccents(String string) {
		return StringUtils.toUpperCaseSansAccent(string);
	}
}
