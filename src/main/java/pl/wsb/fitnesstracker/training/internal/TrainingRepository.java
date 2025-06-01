package pl.wsb.fitnesstracker.training.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.fitnesstracker.training.api.Training;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Training entity.
 * Extends JpaRepository to provide CRUD operations.
 */
interface TrainingRepository extends JpaRepository<Training, Long> {

    /**
     * Finds all trainings for a given user ID.
     *
     * @param userId the ID of the user
     * @return list of trainings for the specified user
     */
    List<Training> findByUserId(Long userId);

    /**
     * Finds all trainings with the specified activity type.
     *
     * @param activityType the type of activity
     * @return list of trainings with the given activity type
     */
    List<Training> findByActivityType(ActivityType activityType);

    /**
     * Finds all trainings that have an end time after the specified date.
     * Note: This method fetches all trainings and filters in memory.
     *
     * @param date the date to compare the training's end time to
     * @return list of trainings finished after the specified date
     */
    default List<Training> findByEndDateAfter(Date date) {
        return findAll().stream()
                .filter(training -> training.getEndTime().after(date))
                .toList();
    }
}
