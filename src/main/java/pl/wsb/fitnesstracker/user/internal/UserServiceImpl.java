package pl.wsb.fitnesstracker.user.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer implementation for user-related operations,
 * handling business logic and delegating persistence to the repository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;

    /**
     * Persists a new {@link User} entity to the database.
     * Throws an exception if the user already has an ID.
     *
     * @param user The user to be saved
     * @return The persisted {@link User}
     */
    @Override
    public User createUser(final User user) {
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }
        return userRepository.save(user);
    }

    /**
     * Removes a user from the system by their unique identifier.
     *
     * @param id The ID of the user to be deleted
     */
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user's data with new values.
     * If the user is not found, a {@link UserNotFoundException} is thrown.
     *
     * @param userId   The ID of the user to update
     * @param userInfo The new user data to apply
     * @return The updated {@link User}
     */
    public User updateUser(final Long userId, User userInfo) {
        if (userInfo.getId() != null && !userId.equals(userInfo.getId())) {
            throw new IllegalArgumentException("User ID in the path and body must match!");
        }
        User userToUpdate = null;
        try {
            userToUpdate = userRepository.getReferenceById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
        userToUpdate.setFirstName(userInfo.getFirstName());
        userToUpdate.setLastName(userInfo.getLastName());
        userToUpdate.setBirthdate(userInfo.getBirthdate());
        userToUpdate.setEmail(userInfo.getEmail());
        return userRepository.save(userToUpdate);
    }

    /**
     * Fetches a user by their database ID.
     *
     * @param userId The unique identifier of the user
     * @return An {@link Optional} containing the user if found
     */
    @Override
    public Optional<User> getUser(final Long userId) {
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
        return userRepository.findByBirthdateOlderThan(time);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list containing all {@link User} entities
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}