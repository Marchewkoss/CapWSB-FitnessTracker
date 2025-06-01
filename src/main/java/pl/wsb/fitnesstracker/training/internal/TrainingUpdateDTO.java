package pl.wsb.fitnesstracker.training.internal;

import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object used for updating an existing Training.
 * Contains fields that can be updated for a training record.
 */
@Data
public class TrainingUpdateDTO {
    private Long userId;
    private Date startTime;
    private Date endTime;
    private ActivityType activityType;
    private Double distance;
    private Double averageSpeed;
}
