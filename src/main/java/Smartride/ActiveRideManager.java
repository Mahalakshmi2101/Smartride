package Smartride;

import com.smartride.model.User;
import com.smartride.util.GenericCache;
import com.smartride.model.entity.Booking;
import com.smartride.repository.BookingRepository;

import java.util.HashMap;
import java.util.Optional;

public class ActiveRideManager {
	public Optional<Booking> findById(String id){
		Booking booking=h1.get(Long.parseLong(id));
		return Optional.ofNullable(booking);
	}
	
		
	
	GenericCache<Long, Booking> h1 = new GenericCache<>();

	public void addBooking(Booking booking) {
		h1.put(booking.getId(), booking);
	}

	public Booking getBooking(Long bookingId) {
		return h1.get(bookingId);
	}

	public void removeBooking(Long bookingId) {
		h1.remove(bookingId);
	}

	public java.util.List<Booking> getActiveBookings() {
		return h1.values().stream().filter(b -> b.getStatus().equals("CONFIRMED"))
				.collect(java.util.stream.Collectors.toList());
	}

	public double getTotalActiveFare() {
		return h1.values().stream().filter(b -> b.getStatus().equals("CONFIRMED"))
				.mapToDouble(b -> b.getRide().getPrice() * b.getNumberOfSeats()).sum();
	}
	public java.util.List<String> getActiveCustomerUsernames(){
		return h1.values().stream().filter(b -> b.getStatus().equals("CONFIRMED")).map(b -> b.getUser().getUsername()).collect(java.util.stream.Collectors.toList());}

	public static void main(String[] args) {
		ActiveRideManager manager = new ActiveRideManager();
		User steve = new User("steve_rider", "pass123");
		User nancy = new User("nancy_rider", "pass456");
		User alice = new User("alice_rider", "pass789");

		Booking b1 = new Booking(steve, null, 1);
		b1.setId(1L);
		Booking b2 = new Booking(nancy, null, 2);
		b2.setId(2L);
		Booking b3 = new Booking(alice, null, 3);
		b3.setId(3L);
		manager.addBooking(b1);
		manager.addBooking(b2);
		manager.addBooking(b3);
		manager.findById("1").ifPresentOrElse(
			booking -> System.out.println("Found : "+booking.getId()),
			() -> System.out.println("Booking not found")
			);
		
		
	

		System.out.println("Booking 2 status: " + manager.getBooking(2L).getStatus());
		System.out.println("Booking 2 rider: " + manager.getBooking(2L).getUser().getUsername());
		manager.removeBooking(2L);
		System.out.println("After removal : " + manager.getBooking(2L));

	}
}