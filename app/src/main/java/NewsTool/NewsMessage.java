package NewsTool;

public class NewsMessage {
	
	public NewsMessage(long handle, boolean autoGC) {
        this.messageHandle = handle;
        this.autoFree = autoGC;
	}

	public NewsMessage(boolean autoGC) {
        this.messageHandle = this.createInstance();
        this.autoFree = autoGC;
	}

	public int reset() {
		return this.reset(this.messageHandle);
	}

	public long countValue() {
		return this.countValue(this.messageHandle);
	}

	public String getValue(long index) {
		return this.getValue(this.messageHandle, index);
	}

	public int setValue(long index, String value) {
		return this.setValue(this.messageHandle, index, value);
	}

	public int appendValue(String value) {
		return this.appendValue(this.messageHandle, value);
	}

	public void freeHandle() {
		if (this.messageHandle != 0)
            this.freeInstance(this.messageHandle);
        this.messageHandle = 0;
	}

	public long getMessageHandle() {
		return this.messageHandle;
	}

	public void setAutoFree(boolean autoFree) {
		this.autoFree = autoFree;
	}

	private NewsMessage() {
	}
	private NewsMessage(NewsMessage newsMsg) {
	}

	private long messageHandle;
	private boolean autoFree;

	@Override
	protected void finalize() {
		if (this.messageHandle != 0 && this.autoFree)
            this.freeInstance(this.messageHandle);
        this.messageHandle = 0;
	}

	public native int reset(long handle);
	public native long countValue(long handle);
	public native String getValue(long handle, long index);
	public native int setValue(long handle, long index, String value);
	public native int appendValue(long handle, String value);
	private native long createInstance();
	private native void freeInstance(long instance);
}
