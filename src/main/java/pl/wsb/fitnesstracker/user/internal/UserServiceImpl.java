package pl.wsb.fitnesstracker.user.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer implementation for user-related operations,
 * handling business logic and delegating persistence to the repository.
 */
// @Service - Disabled in favor of SimpleUserService
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements pl.wsb.fitnesstracker.user.api.UserService, pl.wsb.fitnesstracker.user.api.UserProvider {

    private final UserRepository userRepository;

    /**
     * Persists a new {@link User} entity to the database with enhanced validation.
     * Performs several validations before saving:
     * - Checks if the user already has an ID (not allowed for creation)
     * - Validates that required fields are not null or empty
     * - Checks if the email is already in use
     *
     * @param user The user to be saved
     * @return The persisted {@link User}
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public User createUser(final User user) {
        log.info("Creating User {}", user);

        // Validate user doesn't have an ID
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }

        // Validate required fields
        validateRequiredFields(user);

        // Check if email is already in use
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }

        // Save the user
        return userRepository.save(user);
    }

    /**
     * Validates that all required user fields are present.
     * 
     * @param user The user to validate
     * @throws IllegalArgumentException if any required field is missing
     */
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

    /**
     * Removes a user from the system by their unique identifier.
     *
     * @param id The ID of the user to be deleted
     */
    @Override
    public void removeUser(Long id) {
        log.info("Removing User with ID: {}", id);
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user's data with new values, supporting partial updates.
     * Only non-null fields in the provided user object will be used to update the existing user.
     * If the user is not found, returns an empty Optional.
     * If the email is being changed, validates that it's not already in use by another user.
     *
     * @param userId   The ID of the user to update
     * @param userInfo The new user data to apply (can contain null fields for fields that shouldn't be updated)
     * @return Optional containing the updated {@link User}, or empty if user not found
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public Optional<User> updateUser(final Long userId, User userInfo) {
        log.info("Updating User with ID: {}", userId);

        // Validate ID consistency
        validateUserIdConsistency(userId, userInfo);

        // Find the user to update
        return findUser(userId).map(userToUpdate -> {
            // Validate email if it's being changed
            validateEmailIfChanged(userToUpdate, userInfo);

            // Update only the non-null fields
            updateUserFields(userToUpdate, userInfo);

            // Save and return the updated user
            return userRepository.save(userToUpdate);
        });
    }

    /**
     * Validates that the email is not already in use by another user if it's being changed.
     * 
     * @param existingUser The existing user
     * @param updatedUser The user with updated data
     * @throws IllegalArgumentException if the email is already in use by another user
     */
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

    /**
     * Validates that the user ID in the path matches the ID in the user object (if present).
     * 
     * @param pathId The ID from the path
     * @param user The user object to validate
     * @throws IllegalArgumentException if IDs don't match
     */
    private void validateUserIdConsistency(Long pathId, User user) {
        if (user.getId() != null && !pathId.equals(user.getId())) {
            throw new IllegalArgumentException("User ID in the path and body must match!");
        }
    }

    /**
     * Finds a user by ID.
     * 
     * @param userId The ID of the user to find
     * @return Optional containing the found user, or empty if not found
     */
    private Optional<User> findUser(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            return Optional.of(user);
        } catch (EntityNotFoundException e) {
            log.debug("User with ID {} not found", userId);
            return Optional.empty();
        }
    }

    /**
     * Fetches a user by their database ID.
     *
     * @param userId The unique identifier of the user
     * @return An {@link Optional} containing the user if found
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        log.debug("Fetching User with ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Retrieves a user entity by their email address.
     *
     * @param email The email address used to look up the user
     * @return An {@link Optional} containing the matched user, if any
     */
    @Override
    public Optional<User> getUserByEmail(final String email) {
        log.debug("Fetching User by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Performs a search for users whose email contains the specified fragment.
     *
     * @param email The partial or full email to filter users by
     * @return A list of users matching the criteria
     */
    @Override
    public List<User> findUsersByEmail(String email) {
        log.debug("Searching Users by email fragment: {}", email);
        return userRepository.findUsersByEmail(email);
    }

    /**
     * Finds users born before a given date, effectively identifying older users.
     *
     * @param time The cutoff date of birth
     * @return A list of users older than the specified date
     */
    @Override
    public List<User> findUsersOlderThan(LocalDate time) {
        log.debug("Finding Users older than: {}", time);
        return userRepository.findByBirthdateOlderThan(time);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list containing all {@link User} entities
     */
    @Override
    public List<User> findAllUsers() {
        log.debug("Fetching all Users");
        return userRepository.findAll();
    }

    /**
     * Retrieves a paginated list of users with sorting options.
     * This implementation provides efficient database-level pagination and sorting.
     *
     * @param page The page number to retrieve (0-based)
     * @param size The number of users per page
     * @param sortBy The field to sort by (e.g., "firstName", "lastName", "email", "birthdate")
     * @param ascending Whether to sort in ascending (true) or descending (false) order
     * @return A list containing the requested page of users
     */
    @Override
    public List<User> findAllUsersPaginated(int page, int size, String sortBy, boolean ascending) {
        log.debug("Fetching paginated Users: page={}, size={}, sortBy={}, ascending={}", page, size, sortBy, ascending);

        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }

        // Validate and normalize sort field
        String normalizedSortField = normalizeSortField(sortBy);

        // Create sort direction
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create pageable request with sorting
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));

        // Execute paginated query
        return userRepository.findAll(pageRequest).getContent();
    }

    /**
     * Normalizes the sort field name to match entity property names.
     * 
     * @param sortBy The field name to normalize
     * @return The normalized field name
     * @throws IllegalArgumentException if the field name is invalid
     */
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

    /**
     * Helper method to update user fields from source to target, supporting partial updates.
     * Only non-null fields in the source object will be used to update the target object.
     * 
     * @param target The user entity to update
     * @param source The user entity containing the new values (can contain null fields)
     */
    private void updateUserFields(User target, User source) {
        // Update firstName if provided
        if (source.getFirstName() != null) {
            target.setFirstName(source.getFirstName());
        }

        // Update lastName if provided
        if (source.getLastName() != null) {
            target.setLastName(source.getLastName());
        }

        // Update birthdate if provided
        if (source.getBirthdate() != null) {
            target.setBirthdate(source.getBirthdate());
        }

        // Update email if provided
        if (source.getEmail() != null) {
            target.setEmail(source.getEmail());
        }
    }
}
