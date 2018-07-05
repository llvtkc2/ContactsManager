package bp.iemanager.exporter;

import java.util.ArrayList;
import bp.iemanager.MessageObject;
import bp.iemanager.csvcontact.CsvContact;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;

/**
 * Lớp trừu tượng chứa tất cả các phương thức cần thiết, thuộc tính để xuất danh bạ thành các tệp CSV khác nhau.
 * @author Viliam Kasala, <xkasal01@stud.fit.vutbr.cz>
 *
 */
public abstract class Exporter {
	/*
	 * Gắn cờ để xuất liên hệ trùng lặp.
	 */
	protected boolean fitFields;
	protected static final String TAG = "Export";
	/*
	 * Thuộc tính để ghi liên hệ CSV vào tệp.
	 */
	protected MyFileWriter fileWriter;
	
	/*
	 * Tên tệp xuất tệp.
	 */
	protected String newFileName;
	
	/*
	 * Danh sách liên hệ CSV.
	 */
	protected ArrayList<CsvContact> listOfCsvContacts;
	
	protected Context context;
	
	/*
	 * Chọn tùy chọn.
	 */
	protected boolean[] options;
	
	/*
	 * Tên duy nhất cho các liên hệ trùng lặp.
	 */
	protected final String noteName = "#NOTE@DUPLICATE@CONTACT#";
	
	/*
	 * ID cho các tùy chọn.
	 */
	protected static final int OPTIONS_PHONE = 0;
	protected static final int OPTIONS_EMAIL = 1;
	protected static final int OPTIONS_ADDRESS = 2;
	protected static final int OPTIONS_ORGANIZATION = 7;
	protected static final int OPTIONS_IM = 4;
	protected static final int OPTIONS_NOTE = 5;
	protected static final int OPTIONS_NICKNAME = 6;
	protected static final int OPTIONS_WEBPAGE = 3;
	
	/*
	 *Lựa chọn cho truy vấn.
	 */
	protected String selection;
	
	/*
	 *Đối số lựa chọn cho truy vấn.
	 */
	protected String[] selectionArgs;
	
	/*
	 * Trình xử lý cho hộp thoại tiến trình.
	 */
	protected Handler mHandler;
	
	/*
	 * Số lượng địa chỉ liên hệ đã xuất.
	 */
	protected int numberOfExportedContacts;
	/*
	 * Tên hiển thị.
	 */
	protected String displayName;
	/*
	 * Gắn cờ để xóa ký tự dấu trọng âm.
	 */
	protected boolean removeFlag;
	
	public Exporter(Context _ctx, boolean[] _options, boolean _removeFlag) {
		context = _ctx;
		options = _options;
		newFileName = null;
		listOfCsvContacts = new ArrayList<CsvContact>();
		removeFlag = _removeFlag;
		fileWriter = null;
		selection = null;
		selectionArgs = null;
		mHandler = null;
		numberOfExportedContacts = 0;
		displayName = "";
	}
	
	/**
	 * Bắt đầu nhập tất cả các số liên lạc được lưu trong cơ sở dữ liệu của hệ điều hành Android.
	 */
	public void startExport() {
		queryAllRawContacts();
	}
	
	/**
	 *Phương thức nhận liên hệ CSV đầu tiên.
	 * @return CsvContact
	 */
	public CsvContact getFirstCsvLine() {
		return ((listOfCsvContacts.isEmpty() == true) ?  null : listOfCsvContacts.get(0));
	}
	
	/**
	 *Phương thức trả về tất cả CsvContacts.
	 * @return danh sách của CsvContacts.
	 */
	public ArrayList<CsvContact> getArrayList() {
		return (listOfCsvContacts.isEmpty() == true) ? null : listOfCsvContacts;
	}
	
	/**
	 * Phương thức tạo và trả về con trỏ của tất cả RawContacts.
	 * @quay lại con trỏ của tất cả các địa chỉ liên hệ thô được tìm thấy.
	 */
	protected Cursor getCursorOfAllRawContacts() {
		return context.getContentResolver().query(RawContacts.CONTENT_URI, 
				new String[] { RawContacts._ID }, 
				RawContacts.DELETED + " = ? AND " + " ( " + selection + " ) ", 
				concat(new String[] {"0"}, selectionArgs), 
				null);
	}
	
	/**
	 * Phương pháp tìm ra số lượng liên lạc xuất khẩu.
	 * @quay số số liên lạc đã xuất.
	 */
	public int getExportedContactNumber() {
		Cursor c = null;
		try{
			c = getCursorOfAllRawContacts();
			return c.getCount();
		} finally {
			// Close the used cursor
			if(c != null)
				c.close();
		}
	}
	
	/**
	 * Xây dựng và gửi tin nhắn đến trình xử lý (Progressdialog)
	 */
	protected void sendMessageToHandler() {
		Message msg = mHandler.obtainMessage();
		msg.obj = (MessageObject) new MessageObject(++numberOfExportedContacts, "Exporting:\n " + displayName);
		mHandler.sendMessage(msg);
	}
	
	/**
	 *Xây dựng và gửi thông báo thoát cho trình xử lý (Progressdialog)
	 */
	protected void sendExitMessageToHandler() {
		Message msg = mHandler.obtainMessage();
		msg.obj = (MessageObject) new MessageObject(-1,"UNIQUE^@&!EXIT_MEASSAGE");
		mHandler.sendMessage(msg);
	}
	
	/**
	 * Gets tất cả rawContacts và từng bước được các thông tin chi tiết về rawContact và cập nhật tiến trình.
	 */
	protected void queryAllRawContacts() {
		Cursor c = null;
		try {
			c = getCursorOfAllRawContacts();
			if(c.getCount() == 0) {
				sendMessageToHandler();
			}
			// lặp lại từng bước thông qua tất cả chỉ mục và truy xuất thông tin chi tiết về liên hệ và ghi chúng vào tệp
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				listOfCsvContacts.clear();	
				queryAllDetailedInformation(getColumnLong(c, RawContacts._ID));
				sendMessageToHandler();
			}
		} finally {
			//Đóng con trỏ
			if(c != null)
				c.close();
		}
	}
	
	
	/**
	 *Phương thức nhận tất cả các mục của liên hệ thô và ghi chúng vào mảng danh sách.
	 * @param rawContactId - ID RawContact-u
	 */
	public void queryAllDetailedInformation(long rawContactId) {
		listOfCsvContacts.add(createNewCsvContact());
		
		Uri rawContactsUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId);
		Uri entityUri = Uri.withAppendedPath(rawContactsUri, Entity.CONTENT_DIRECTORY);
		Cursor c = null;
		
		try {
			c = context.getContentResolver().query(entityUri, 
					null, 
					null, null, null);

			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {			
				String s = getColumnString(c, Entity.MIMETYPE);
				Log.v(TAG, s);
				
				// Zaznam o Mene
				if(s.equals(StructuredName.CONTENT_ITEM_TYPE)) 
					processStructuredName(c);
				
				else if (s.equals(Organization.CONTENT_ITEM_TYPE)) {
					if(options[OPTIONS_ORGANIZATION])
						processOrganization(c);						
				}
				// Zaznam o Adrese
				else if(s.equals(StructuredPostal.CONTENT_ITEM_TYPE)){
					if(options[OPTIONS_ADDRESS])
						processStructuredPostal(c);
				}
				// Zaznam o Telefone
				else if(s.equals(Phone.CONTENT_ITEM_TYPE)) {
					if(options[OPTIONS_PHONE])
						processPhone(c);
				}
				// Zaznam o Email-och
				else if(s.equals(Email.CONTENT_ITEM_TYPE)) {
					if(options[OPTIONS_EMAIL])
						processEmail(c);
				}
				// Zaznam o Web strankach
				else if(s.equals(Website.CONTENT_ITEM_TYPE)){
					if(options[OPTIONS_WEBPAGE])
						processWebPages(c);
				}
				
				else if(s.equals(Nickname.CONTENT_ITEM_TYPE)) {
					processNickName(c);
				}
				// Zaznam o Poznamkach
				else if(s.equals(Note.CONTENT_ITEM_TYPE)){
					if(options[OPTIONS_NOTE])
						processNote(c);
				}
			}
			if(fileWriter != null) {
				writeContactToFile();
			}
			
		} finally {
			if(c != null)
				c.close();
		}
	}
	
	protected abstract CsvContact createNewCsvContact();
	protected abstract void editArrayListItems();
	protected abstract void processStructuredName(Cursor c);
	protected abstract void processEmail(Cursor c);
	protected abstract void processPhone(Cursor c);
	protected abstract void processNote(Cursor c);
	protected abstract void processWebPages(Cursor c);
	protected abstract void processOrganization(Cursor c);
	protected abstract void processStructuredPostal(Cursor c);
	protected abstract void processNickName(Cursor c);
	
	/**
	 * Writes all contacts to file.
	 */
	protected void writeContactToFile() {
		if(listOfCsvContacts.size() > 1) {
			editArrayListItems();
		}
		if(removeFlag)
			replaceAllAccentChars();
		int i = 0;
		for (CsvContact csvLine : listOfCsvContacts) {
			if(fitFields && i != 0)
				break;
			fileWriter.writeStrings(csvLine.getArrayOfStrings());
			i++;
		}
	}
	
	/**
	 * Method replace all Accent chars with it's relevant char in Ascii code
	 */
	protected void replaceAllAccentChars() {
		for (CsvContact csvLine : listOfCsvContacts) {
			csvLine.removeAccentChars();
		}
	}
	
	protected static String[] concat(String[] A, String[] B) {
		   String[] C = new String[A.length + B.length];
		   System.arraycopy(A, 0, C, 0, A.length);
		   System.arraycopy(B, 0, C, A.length, B.length);
		   return C;
	}
	
	protected static String getColumnString(Cursor c, String columnName) {
		return c.getString(c.getColumnIndex(columnName));
	}
	
	protected static long getColumnLong(Cursor c, String columnName) {
		return c.getLong(c.getColumnIndex(columnName));
	}
	
	protected static boolean isColumnNull(Cursor c, String columnName) {
		return c.isNull(c.getColumnIndex(columnName));
	}
	
	protected static int getColumnInt(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName));
	}
	
}
