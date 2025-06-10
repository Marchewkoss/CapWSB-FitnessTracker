package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserByEmail;
import pl.wsb.fitnesstracker.user.api.UserDto;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * REST controller for user operations.
 * This controller provides endpoints for managing users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
class NewUserController {

    private final SimpleUserService userService;
    private final UserMapper userMapper;

    /**
     * Helper method to map a list of users to DTOs using the provided mapper function.
     *
     * @param users         the list of users to map
     * @param mapperFunction the function to use for mapping each user
     * @return a list of mapped DTOs
     */
    private <T> List<T> mapUsersToDto(List<User> users, Function<User, T> mapperFunction) {
        return users.stream()
                .map(mapperFunction)
                .toList();
    }

    /**
     * Returns a list of all registered users.
     * This is a convenience method that uses the paginated API with default parameters.
     * 
     * @return list of all users with complete details
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.debug("REST request to get all users");
        // Use the paginated method with default parameters (first page, 100 items, sorted by ID)
        List<UserDto> users = mapUsersToDto(userService.findAllUsersPaginated(0, 100, "id", true), userMapper::toDto);
        return ResponseEntity.ok(users);
    }

    /**
     * Returns a paginated list of users with sorting options.
     * This endpoint provides more control over the result set size and order.
     *
     * @param page The page number to retrieve (0-based, defaults to 0)
     * @param size The number of users per page (defaults to 20)
     * @param sortBy The field to sort by (defaults to "id")
     * @param ascending Whether to sort in ascending order (defaults to true)
     * @return A paginated list of users
     */
    @GetMapping("/paginated")
    public ResponseEntity<List<UserDto>> getPaginatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        log.debug("REST request to get paginated users: page={}, size={}, sortBy={}, ascending={}", 
                page, size, sortBy, ascending);

        try {
            List<UserDto> users = mapUsersToDto(
                    userService.findAllUsersPaginated(page, size, sortBy, ascending), 
                    userMapper::toDto);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            log.error("Invalid pagination parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Returns a simplified list of users (ID, first name, last name).
     * 
     * @return list of all users with basic details only
     */
    @GetMapping("/simple")
    public ResponseEntity<List<UserDto>> getAllUsersSimple() {
        log.debug("REST request to get simplified list of all users");
        List<UserDto> users = mapUsersToDto(userService.findAllUsers(), userMapper::toSimpleDto);
        return ResponseEntity.ok(users);
    }

    /**
     * Returns a user by their unique ID.
     *
     * @param id user identifier
     * @return UserDto if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Searches users by email fragment (case-insensitive).
     *
     * @param email partial or full email address
     * @return list of matching users (email-only view)
     */
    @GetMapping("/email")
    public ResponseEntity<List<UserByEmail>> searchUsersByEmail(@RequestParam String email) {
        log.debug("REST request to search users by email: {}", email);
        List<UserByEmail> users = mapUsersToDto(userService.findUsersByEmail(email), userMapper::toEmail);
        return ResponseEntity.ok(users);
    }

    /**
     * Finds users older than the provided date (e.g., birthdate).
     *
     * @param time to compare users' birthdates to
     * @return list of users older than the specified date
     */
    @GetMapping("/older/{time}")
    public ResponseEntity<List<UserDto>> findUsersOlderThan(@PathVariable LocalDate time) {
        log.debug("REST request to find users older than: {}", time);
        List<UserDto> users = mapUsersToDto(userService.findUsersOlderThan(time), userMapper::toDto);
        return ResponseEntity.ok(users);
    }

    /**
     * Creates a new user.
     *
     * @param userDto user data
     * @return created user representation
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        log.debug("REST request to create user: {}", userDto);
        User newUser = userService.createUser(userMapper.toEntity(userDto));
        UserDto result = userMapper.toDto(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id user identifier
     * @return 204 No Content if deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        log.debug("REST request to delete user: {}", id);
        userService.removeUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates a user by ID.
     *
     * @param id  user identifier
     * @param dto updated user data
     * @return updated user representation or 404 if user not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        log.debug("REST request to update user: {}, {}", id, dto);
        return userService.updateUser(id, userMapper.toEntity(dto))
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}