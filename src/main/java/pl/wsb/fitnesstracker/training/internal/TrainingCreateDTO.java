package pl.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Date;

/**
 * Data Transfer Object used for creating a new Training.
 * Contains all necessary fields to create a Training record.
 */
public class TrainingCreateDTO {
    private final Long id;

    private final Long userId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @NotNull
    private final ActivityType activityType;

    @PositiveOrZero
    private final double distance;

    @PositiveOrZero
    private final double averageSpeed;

    /**
     * Constructs a new TrainingCreateDTO.
     *
     * @param id           the unique identifier of the training (should be null when creating new training)
     * @param userId       the ID of the user associated with the training
     * @param startTime    the start date of the training, cannot be null
     * @param endTime      the end date of the training, cannot be null
     * @param activityType the type of physical activity, cannot be null
     * @param distance     the distance covered during training, must be zero or positive
     * @param averageSpeed the average speed during training, must be zero or positive
     */
    TrainingCreateDTO(Long id, Long userId, Date startTime, Date endTime, ActivityType activityType, double distance, double averageSpeed) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public double getDistance() {
        return distance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }
}
