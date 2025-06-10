package pl.wsb.fitnesstracker.user.api;

import java.util.Optional;

/**
 * Interface (API) for modifying operations on {@link User} entities through the API.
 * Implementing classes are responsible for executing changes within a database transaction, whether by continuing an existing transaction or creating a new one if required.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     * Implementations should validate that the user does not already have an assigned ID,
     * then persist it in the database.
     *
     * @param user The user entity to create
     * @return The persisted user including the assigned ID
     * @throws IllegalArgumentException if the user already has an ID (indicating an update instead of create)
     */
    User createUser(User user);

    /**
     * Deletes a user from the system based on their unique identifier.
     * Implementations should handle the case where the user with the given ID does not exist.
     *
     * @param id The ID of the user to delete
     */
    void removeUser(Long id);

    /**
     * Updates an existing user's information.
     * This method should update all relevant fields of the user
     * and persist the changes.
     * If a user with the given ID does not exist, returns an empty Optional.
     *
     * @param id   The ID of the user to update
     * @param user The user object containing updated data
     * @return Optional containing the updated and persisted user entity, or empty if user not found
     */
    Optional<User> updateUser(Long id, User user);
}
