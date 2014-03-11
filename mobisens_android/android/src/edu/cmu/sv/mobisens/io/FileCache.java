package edu.cmu.sv.mobisens.io;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class FileCache<K, V> {
	
	
	public interface FileSerializer<K, V> {
		HashMap<K, CacheItem<V>> Serialize();
		File Deserialize(HashMap<K, CacheItem<V>> data);
	}
	
	private FileSerializer<K, V> serializer;
	private HashMap<K, CacheItem<V>> data;
	public FileCache(FileSerializer<K, V> serializer){
		this.serializer = serializer;
		data = this.serializer.Serialize();
	}
	
	public void add(K key, V content, long keepPeriod){
		if(data != null){
			synchronized(data){
				data.put(key, new CacheItem<V>(content, System.currentTimeMillis(), keepPeriod));
			}
		}
	}
	
	public V get(K key){
		if(data != null){
			synchronized(data){
				if(data.containsKey(key)){
					return data.get(key).getContent();
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void swapExpired(){
		if(data == null)
			return;
		
		HashMap<K, CacheItem<V>> dataClone = null;
		synchronized(data){
			dataClone = (HashMap<K, CacheItem<V>>) data.clone();
		}
		
		Set<K> keys = dataClone.keySet();
		for(K key:keys){
			if(dataClone.get(key).isExpired()){
				synchronized(data){
					data.remove(key);
				}
			}
		}
	}
	
	public void flush(){
		
		this.swapExpired();
		
		this.serializer.Deserialize(data);
			
	}
}
