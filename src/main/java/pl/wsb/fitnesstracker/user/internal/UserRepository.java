package pl.wsb.fitnesstracker.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findFirst();
    }

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return List of users
     */
    default List<User> findUsersByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(toList());
    }

    /**
     * Query searching users by BirthdateOlderThan given date.
     *
     * @param time of the user birthday to search
     * @return List of users
     */
    default List<User> findByBirthdateOlderThan(LocalDate time) {
        return findAll().stream()
                .filter(user -> user.getBirthdate().isBefore(time))
                .collect(toList());
    }
}
