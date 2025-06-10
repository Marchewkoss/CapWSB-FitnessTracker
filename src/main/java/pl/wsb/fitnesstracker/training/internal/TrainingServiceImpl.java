package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.training.api.TrainingService;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class TrainingServiceImpl implements TrainingProvider, TrainingService {
    private final TrainingRepository trainingRepository;

    private final UserProvider userProvider;

    /**
     * Retrieves a training by its ID.
     *
     * @param trainingId ID of the training to retrieve
     * @return Optional containing the training if found, otherwise empty
     */
    @Override
    public Optional<Training> getTraining(final Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    /**
     * Finds all trainings associated with a specific user ID.
     *
     * @param userId ID of the user
     * @return list of trainings for the given user, or empty list if none found
     */
    @Override
    public List<Training> getTrainingsByUserId(Long userId) {
        return trainingRepository.findByUserId(userId);
    }

    /**
     * Finds all trainings with the specified activity type.
     *
     * @param activityType the activity type to filter trainings by (e.g., running, cycling)
     * @return list of trainings matching the activity type
     */
    @Override
    public List<Training> getTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    /**
     * Finds all trainings that ended after the given date.
     *
     * @param date the date to compare training end times to
     * @return list of trainings with endTime after the given date
     */
    @Override
    public List<Training> getTrainingsWithEndDateAfter(Date date) {
        return trainingRepository.findByEndDateAfter(date);
    }

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings
     */
    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    private Optional<User> findUser(Long userId) {
        return userProvider.getUser(userId);
    }

    private Training buildTrainingWithUser(Training source, User user) {
        return new Training(
                user,
                source.getStartTime(),
                source.getEndTime(),
                source.getActivityType(),
                source.getDistance(),
                source.getAverageSpeed()
        );
    }

    /**
     * Creates a new training assigned to the user with the given ID.
     * <p>
     * Throws {@link IllegalArgumentException} if the training object already has an ID
     * or if the user with the given ID does not exist.
     * </p>
     *
     * @param training the training object to create (without ID)
     * @param userId   the ID of the user to assign the training to
     * @return the saved training with assigned ID
     * @throws IllegalArgumentException if training already has an ID or user not found
     */
    @Override
    public Training createTraining(Training training, Long userId) {
        if (training.getId() != null) {
            throw new IllegalArgumentException("The training already has an ID in the database, creating a new training is not possible");
        }

        User user = findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        Training newTraining = buildTrainingWithUser(training, user);

        return trainingRepository.save(newTraining);
    }

    private void copyTrainingFields(Training existing, Training updated) {
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setActivityType(updated.getActivityType());
        existing.setDistance(updated.getDistance());
        existing.setAverageSpeed(updated.getAverageSpeed());
    }

    /**
     * Updates an existing training identified by trainingId and assigns it to the user identified by userId.
     * <p>
     * Throws {@link TrainingNotFoundException} if no training with the given ID exists,
     * or {@link IllegalArgumentException} if no user with the given ID exists.
     * </p>
     *
     * @param updatedTraining   the training object containing updated fields
     * @param trainingId the ID of the training to update
     * @param userId     the ID of the user to assign the training to
     * @return the updated training object
     * @throws TrainingNotFoundException if the training does not exist
     * @throws IllegalArgumentException if the user does not exist
     */
    @Override
    public Training updateTraining(Training updatedTraining, Long trainingId, Long userId) {
        Training existingTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        User user = findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        copyTrainingFields(existingTraining, updatedTraining);
        existingTraining.setUser(user);

        return trainingRepository.save(existingTraining);
    }
}
