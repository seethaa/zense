package edu.cmu.sv.mobisens.io;

public class CacheItem<T> {
	private long createdTime = 0;
	private long expireation = 0;
	private T content;
	
	public CacheItem(T content, long createdTime, long expireationTimeSpan){
		
		this.expireation = expireationTimeSpan;
		this.createdTime = createdTime;
		this.content = content;
	}

	public long getCreatedTime(){
		return this.createdTime;
	}

	public T getContent() {
		return content;
	}
	
	public boolean isExpired(){
		if(System.currentTimeMillis() - this.expireation < this.createdTime)
			return false;
		
		return true;
	}
}
