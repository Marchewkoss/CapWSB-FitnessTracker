package pl.wsb.fitnesstracker.training.api;

public interface TrainingService extends TrainingProvider {
    /**
     * Creates a new training for the specified user.
     *
     * @param training the training entity to create (without ID)
     * @param userId   ID of the user who owns the training
     * @return the created Training entity with assigned ID
     */
    Training createTraining(Training training, Long userId);

    /**
     * Updates an existing training with the given training ID and user ID.
     *
     * @param training   the training entity containing updated data
     * @param trainingId ID of the training to update
     * @param userId     ID of the user owning the training
     * @return the updated Training entity
     */
    Training updateTraining(Training training, Long trainingId, Long userId);
}
