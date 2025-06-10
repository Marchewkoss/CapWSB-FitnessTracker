package pl.wsb.fitnesstracker.user.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserService;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Simplified service implementation for all user operations.
 * This class provides a clean, straightforward implementation of the UserService and UserProvider interfaces.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleUserService implements UserService, UserProvider {

    private final UserRepository userRepository;

    // UserProvider methods

    @Override
    public Optional<User> getUser(Long userId) {
        log.debug("Getting user by ID: {}", userId);
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllUsersPaginated(int page, int size, String sortBy, boolean ascending) {
        log.debug("Getting users with pagination: page={}, size={}, sortBy={}, ascending={}", 
                page, size, sortBy, ascending);
        
        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }

        // Normalize sort field
        String normalizedSortField = normalizeSortField(sortBy);
        
        // Create sort direction
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        // Create pageable request
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));
        
        // Execute query
        return userRepository.findAll(pageRequest).getContent();
    }

    @Override
    public List<User> findUsersByEmail(String email) {
        log.debug("Finding users by email fragment: {}", email);
        return userRepository.findUsersByEmail(email);
    }

    @Override
    public List<User> findUsersOlderThan(LocalDate date) {
        log.debug("Finding users older than: {}", date);
        return userRepository.findByBirthdateOlderThan(date);
    }

    // UserService methods

    @Override
    public User createUser(User user) {
        log.info("Creating user: {}", user);
        
        // Validate user doesn't have an ID
        if (user.getId() != null) {
            throw new IllegalArgumentException("User already has an ID, create not allowed");
        }
        
        // Validate required fields
        validateRequiredFields(user);
        
        // Check if email is already in use
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }
        
        // Save user
        return userRepository.save(user);
    }

    @Override
    public void removeUser(Long id) {
        log.info("Removing user with ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
        log.info("Updating user with ID: {}", id);
        
        // Validate ID consistency
        if (user.getId() != null && !id.equals(user.getId())) {
            throw new IllegalArgumentException("ID in path and body must match");
        }
        
        // Find user
        return findUser(id).map(existingUser -> {
            // Validate email if changed
            validateEmailIfChanged(existingUser, user);
            
            // Update fields
            updateUserFields(existingUser, user);
            
            // Save and return
            return userRepository.save(existingUser);
        });
    }

    // Helper methods

    private Optional<User> findUser(Long id) {
        try {
            User user = userRepository.getReferenceById(id);
            return Optional.of(user);
        } catch (EntityNotFoundException e) {
            log.debug("User with ID {} not found", id);
            return Optional.empty();
        }
    }

    private void validateRequiredFields(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getBirthdate() == null) {
            throw new IllegalArgumentException("Birthdate is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateEmailIfChanged(User existingUser, User updatedUser) {
        // Skip validation if email is not being changed or is null
        if (updatedUser.getEmail() == null || updatedUser.getEmail().equals(existingUser.getEmail())) {
            return;
        }
        
        // Validate email format
        if (!updatedUser.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Check if email is already in use by another user
        Optional<User> userWithSameEmail = userRepository.findByEmail(updatedUser.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(existingUser.getId())) {
            throw new IllegalArgumentException("Email is already in use: " + updatedUser.getEmail());
        }
    }

    private void updateUserFields(User target, User source) {
        // Update only non-null fields
        if (source.getFirstName() != null) {
            target.setFirstName(source.getFirstName());
        }
        if (source.getLastName() != null) {
            target.setLastName(source.getLastName());
        }
        if (source.getBirthdate() != null) {
            target.setBirthdate(source.getBirthdate());
        }
        if (source.getEmail() != null) {
            target.setEmail(source.getEmail());
        }
    }

    private String normalizeSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "id"; // Default sort field
        }

        String normalized = sortBy.trim().toLowerCase();

        // Map to valid entity field names
        return switch (normalized) {
            case "firstname", "first_name", "first" -> "firstName";
            case "lastname", "last_name", "last" -> "lastName";
            case "email", "mail" -> "email";
            case "birthdate", "birth_date", "birth", "dob" -> "birthdate";
            case "id" -> "id";
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };
    }
}