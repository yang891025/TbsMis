package NewsTool;

public class NewsContent {

	public static final int parse_flag_header = 0x01;
	public static final int parse_flag_title = 0x02;
	public static final int parse_flag_text = 0x04;
	public static final int parse_flag_full = 0x07;
	public static final int parse_flag_index = 0x10;

	static {
        // common modules
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("TBSLib");
        // interpret modules
        System.loadLibrary("NewsTools");

    }

	public boolean initialize(String ini_file) {
        this.newsInstance = this.initNewsContent(ini_file);
		return this.newsInstance != 0;
	}

	public NewsMessage getNews(long index) {
		long handle = this.getNewsMsg(this.newsInstance, index);
		return new NewsMessage(handle, false);
	}

	public void addNews(NewsMessage newsMsg, long index) {
        this.addNewsMsg(this.newsInstance, newsMsg.getMessageHandle(), index);
	}

	public void addNewsFiled(String full_path, long msg_handle, long index) {
        this.addNewsFile(this.newsInstance, full_path, msg_handle, index);
	}

	public String mergeNewsContent(NewsMessage newsMsg) {
		return this.mergeNewsMsg(this.newsInstance, newsMsg.getMessageHandle());
	}

	public int parseNewsFile(String full_path, long parse_flag, long start,
			long end) {
		return this.parseNewsFile(this.newsInstance, full_path, parse_flag, start, end);
	}

	public String mergeNewsContent(long index) {
		return this.mergeNewsMsgByIndex(this.newsInstance, index);
	}

	public String mergeNewsContent() {
		return this.mergeNewsMsg(this.newsInstance);
	}

	public NewsMessage removeNews(long index) {
		long handle = this.removeNewsMsg(this.newsInstance, index);
		return new NewsMessage(handle, true);
	}

	public long countNews() {
		return this.countNews(this.newsInstance);
	}

	public long mergeNewsFile(String full_path) {
		return this.mergeNewsFile(this.newsInstance, full_path);
	}

	public long countField() {
		return this.countField(this.newsInstance);
	}

	public long removeNewsFile(String full_path, long index) {
		return this.removeNewsFile(this.newsInstance, full_path, index);
	}

	public long modifyNewsFile(String full_path, long msg_handle, long index) {
		return this.modifyNewsFile(this.newsInstance, full_path, msg_handle, index);
	}

	public String getFieldInternalName(long index) {
		return this.getFieldInternalName(this.newsInstance, index);
	}
	public String getFieldName(long index) {
		return this.getFieldName(this.newsInstance, index);
	}
	public String getNewsField(long index, long field) {
		return this.getNewsField(this.newsInstance, index, field);
	}

	public native String getNewsType(long handle);

	public native void reset(long handle);

	public native void loadProfile(long handle, String ini_file);

	public native long countField(long handle);

	public native String getFieldName(long handle, long index);

	public native String getFieldInternalName(long handle, long index);

	public native boolean removeField(long handle, long index);

	public native boolean addField(long handle, String external_name,
			String internal_name, long index);

	public native long countNews(long handle);

	public native String getNewsField(long handle, long index, long field);

	public native String getNewsField(long handle, long index,
			String external_name, String internal_name);

	public native void parseNewsContent(long handle, String content,
			long parse_flag);

	// public native void mergeNewsContent(long handle, long index);
	public native int parseNewsFile(long handle, String full_path,
			long parse_flag, long start, long end);

	public native int mergeNewsFile(long handle, String full_path);

	public native int appendNewsFile(long handle, String full_path);

	public native int removeNewsFile(long handle, String full_path, long index);

	public native int addNewsFile(long handle, String full_path,
			long msg_handle, long index);

	public native int modifyNewsFile(long handle, String full_path,
			long msg_handle, long index);

	public native long getFieldIndex(long handle, String external_name,
			String internal_name);

	private native long getNewsMsg(long handle, long index);

	private native void addNewsMsg(long handle, long newsInst, long index);

	private native String mergeNewsMsg(long handle, long newsInst);

	private native String mergeNewsMsgByIndex(long handle, long index);

	private native String mergeNewsMsg(long handle);

	private native long removeNewsMsg(long handle, long index);

	private native long initNewsContent(String ini_file);

	private long newsInstance;
}
