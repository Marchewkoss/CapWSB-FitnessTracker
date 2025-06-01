package pl.wsb.fitnesstracker.training.api;

import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Provides operations for managing Training entities.
 */
public interface TrainingProvider {

    /**
     * Retrieves a training by its ID.
     *
     * @param trainingId ID of the training to retrieve
     * @return Optional containing the found Training or empty if not found
     */
    Optional<Training> getTraining(Long trainingId);

    /**
     * Retrieves all trainings.
     *
     * @return list of all Training entities
     */
    List<Training> getAllTrainings();

    /**
     * Retrieves all trainings for a specified user.
     *
     * @param userId ID of the user whose trainings to retrieve
     * @return list of trainings belonging to the user
     */
    List<Training> getTrainingsByUserId(Long userId);

    /**
     * Retrieves all trainings matching the given activity type.
     *
     * @param activityType type of activity to filter trainings by
     * @return list of trainings matching the activity type
     */
    List<Training> getTrainingsByActivityType(ActivityType activityType);

    /**
     * Retrieves all trainings that ended after the specified date.
     *
     * @param date the date to compare training end times to
     * @return list of trainings with end time after the specified date
     */
    List<Training> getTrainingsWithEndDateAfter(Date date);
}
