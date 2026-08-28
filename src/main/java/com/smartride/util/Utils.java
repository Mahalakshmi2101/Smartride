package com.smartride.util;

import java.util.List;
import com.smartride.model.entity.Booking;

public class Utils {
	public static <T> T getFirst(List<T> list) {
		return list.get(0);

	}
	public static <T extends Comparable<T>> T findMax(List<T> list) {
		T max=list.get(0);
		for(T item:list) {
			if(item.compareTo(max)>0) {
				max=item;
			}
		}
		return max;
	}

	public static void main(String[] args) {
		List<String> myStringList = List.of("Steve", "Nancy", "Alice");
		String first = Utils.getFirst(myStringList);
		System.out.println(first);

		List<Booking> bookingsList = List.of(new Booking(null, null, 2), new Booking(null, null, 1));
		Booking firstBooking = Utils.getFirst(bookingsList);

		System.out.println(firstBooking.getNumberOfSeats());
		List<String> names=List.of("Steve", "Nancy", "Alice");
		System.out.println(Utils.getFirst(names));
		List<Integer> nums=List.of(1,2,3,4,5);
		System.out.println(Utils.getFirst(nums));

	}

}