package pl.wsb.fitnesstracker.user.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserProvider {

    /**
     * Retrieves a user based on their ID.
     * If the user with given ID is not found, then {@link Optional#empty()} will be returned.
     *
     * @param userId id of the user to be searched
     * @return An {@link Optional} containing the located user, or {@link Optional#empty()} if not found
     */
    Optional<User> getUser(Long userId);

    /**
     * Retrieves a user based on their email.
     * If the user with given email is not found, then {@link Optional#empty()} will be returned.
     *
     * @param email The email of the user to be searched
     * @return An {@link Optional} containing the located user, or {@link Optional#empty()} if not found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Fetches a complete list of all users available in the system.
     *
     * @return A list containing all registered users
     */
    List<User> findAllUsers();

    /**
     * Fetches a paginated list of users with optional sorting.
     * This method provides more control over the result set size and order.
     *
     * @param page The page number to retrieve (0-based)
     * @param size The number of users per page
     * @param sortBy The field to sort by (e.g., "firstName", "lastName", "email", "birthdate")
     * @param ascending Whether to sort in ascending (true) or descending (false) order
     * @return A list containing the requested page of users
     */
    List<User> findAllUsersPaginated(int page, int size, String sortBy, boolean ascending);

    /**
     * Searches for users whose email addresses contain the specified fragment.
     * Useful for partial matching and filtering user emails.
     *
     * @param email Substring to search for within user email addresses
     * @return A list of users whose emails match the given fragment
     */
    List<User> findUsersByEmail(String email);

    /**
     * Finds users who were born before the specified date.
     * Typically used to filter users by age threshold.
     *
     * @param time Date to compare user birthdates against
     * @return A list of users older than the specified date
     */
    List<User> findUsersOlderThan(LocalDate time);
}
