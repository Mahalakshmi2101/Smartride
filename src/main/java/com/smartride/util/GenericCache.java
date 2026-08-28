package com.smartride.util;
import java.util.HashMap;
import java.util.Collection;
public class GenericCache<K,V> {
	private HashMap<K,V> store=new HashMap<>();
	
	public void put(K key, V value) { store.put(key, value);}
	public V get(K key) { return store.get(key);}
	public boolean contains(K key) { return store.containsKey(key);}
	public void remove(K key) { store.remove(key);}
	public Collection<V> values() { return store.values();}
			
	}

	
	


