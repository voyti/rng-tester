package rngTester;

public class Remark {
	public static final int STYLE_NOTIFY = 10;
	public static final int STYLE_IMPORTANT = 11;
	private int remarkLevel;
	private String text;

	public Remark(String text, int remarkLevel) {
		this.text = text;
		this.remarkLevel = remarkLevel;
	}
	
	public int getRemarkLevel() {
		return remarkLevel;
	}

	public void setRemarkLevel(int remarkLevel) {
		this.remarkLevel = remarkLevel;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
