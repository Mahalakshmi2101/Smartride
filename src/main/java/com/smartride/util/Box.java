package com.smartride.util;

import com.smartride.model.entity.Booking;

public class Box<T> {
	private T content;
	
	public void put(T item) {
		this.content=item;
	}

	
	public T get() {
		return content;
	}

public static void main(String args[]) {
	Box<String> stringBox=new Box<>();
	stringBox.put("Hello");
	String s=stringBox.get();
	System.out.println(s);

	Box<Booking> bookingBox=new Box<>();
	bookingBox.put(new Booking(null,null,2));
	Booking b=bookingBox.get();
	System.out.println(b.getNumberOfSeats());
}
}