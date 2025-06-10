package pl.wsb.fitnesstracker.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their exact email address.
     * Uses Spring Data JPA's query method naming convention to generate the query.
     *
     * @param email The exact email address to search for
     * @return An {@link Optional} containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Searches for users whose email addresses contain the specified fragment (case-insensitive).
     * Uses a custom JPQL query with the LOWER function for case-insensitive matching.
     *
     * @param email The email fragment to search for
     * @return A list of users whose emails contain the specified fragment
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> findUsersByEmail(@Param("email") String email);

    /**
     * Finds users whose birthdate is before the specified date.
     * Uses a custom JPQL query for more efficient database filtering.
     *
     * @param date The date to compare birthdates against
     * @return A list of users born before the specified date
     */
    @Query("SELECT u FROM User u WHERE u.birthdate < :date")
    List<User> findByBirthdateOlderThan(@Param("date") LocalDate date);
}
