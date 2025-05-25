package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserByEmail;
import pl.wsb.fitnesstracker.user.api.UserDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    /**
     * Returns a list of all registered users.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Returns a simplified list of users (ID, first name, last name).
     */
    @GetMapping("/simple")
    public List<UserDto> getAllUsersSimple() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
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
                .orElse(ResponseEntity.notFound().build()); // todo exception?
    }

    /**
     * Searches users by email fragment (case-insensitive).
     *
     * @param email partial or full email address
     * @return list of matching users (email-only view)
     */
    @GetMapping("/email")
    public List<UserByEmail> searchUsersByEmail(@RequestParam String email) {
        return userService.findUsersByEmail(email).stream().map(userMapper::toEmail).toList();
    }

    /**
     * Finds users older than the provided date (e.g., birthdate).
     *
     * @param time to compare users' birthdates to
     * @return list of users older than the specified date
     */
    @GetMapping("/older/{time}")
    public List<UserDto> findUsersOlderThan(@PathVariable LocalDate time) {
        return userService.findUsersOlderThan(time)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Creates a new user.
     *
     * @param userDto user data
     * @return created user representation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userMapper.toDto(userService.createUser(userMapper.toEntity(userDto)));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id user identifier
     * @return 204 No Content if deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.removeUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates a user by ID.
     *
     * @param id  user identifier
     * @param dto updated user data
     * @return updated user representation
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        final User updatedUser = userService.updateUser(id, userMapper.toEntity(dto));
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }
}