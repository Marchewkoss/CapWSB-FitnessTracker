package pl.wsb.fitnesstracker.user.api;
import jakarta.annotation.Nullable;

public record UserByEmail(@Nullable Long id, String email) {

}