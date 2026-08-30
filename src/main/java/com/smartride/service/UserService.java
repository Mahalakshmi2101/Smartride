package com.smartride.service;

import com.smartride.model.User;
import com.smartride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User registerNewUser(String name, String email, String rawPassword, String role,
            String carModel, String licensePlate, Integer vehicleSeats, String vehicleType) {
if (userRepository.findByEmail(email).isPresent()) {
throw new RuntimeException("Email already exists.");
}
User user = new User();
user.setName(name);
user.setEmail(email);
user.setPassword(passwordEncoder.encode(rawPassword));
user.setRoles(role != null ? role : "PASSENGER");

if ("DRIVER".equals(role)) {
user.setCarModel(carModel);
user.setLicensePlate(licensePlate);
user.setVehicleSeats(vehicleSeats);
user.setVehicleType(vehicleType);
}

return userRepository.save(user);
}
}