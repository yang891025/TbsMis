package com.tbs.tbsmis.headergridview.selectbuttonaction;

public class StickyGridItem
{
	private String path;
	private String name;
    private String action;
    private String pic;
    private String type;
    private String key;
	private int section;

	public StickyGridItem(String path, String time, int section, String action) {
		super();
		this.path = path;
		this.name = time;
        this.section = section;
        this.action = action;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String time) {
		this.name = time;
	}
	
	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
