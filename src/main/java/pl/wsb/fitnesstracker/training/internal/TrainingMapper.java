package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.internal.UserMapper;

/**
 * Mapper class to convert between Training entities and DTOs.
 */
@Component
@RequiredArgsConstructor
public class TrainingMapper {

    private final UserMapper trainingUserMapper;

    /**
     * Converts Training entity to TrainingDto.
     *
     * @param training Training entity
     * @return TrainingDto object
     */
    public TrainingDto toDto(Training training) {
        return new TrainingDto(
                training.getId(),
                trainingUserMapper.toDto(training.getUser()),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }

    /**
     * Converts TrainingCreateDTO to Training entity.
     * The entity's id is set to null, as it is a new instance to be persisted.
     *
     * @param dto TrainingCreateDTO object
     * @return Training entity
     */
    public Training toEntity(TrainingCreateDTO dto) {
        return new Training(
                null,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getActivityType(),
                dto.getDistance(),
                dto.getAverageSpeed()
        );
    }

    /**
     * Converts TrainingUpdateDTO to Training entity.
     * The entity's id is set to null here and should be managed externally.
     *
     * @param dto TrainingUpdateDTO object
     * @return Training entity
     */
    public Training toEntity(TrainingUpdateDTO dto) {
        return new Training(
                null,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getActivityType(),
                dto.getDistance(),
                dto.getAverageSpeed()
        );
    }
}
