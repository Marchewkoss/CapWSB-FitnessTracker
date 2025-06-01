package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingService;

import java.util.Date;
import java.util.List;

/**
 * REST controller for managing training resources.
 */
@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final pl.wsb.fitnesstracker.training.internal.TrainingMapper trainingMapper;

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings as TrainingDto objects
     */
    @GetMapping
    List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings for the specified user.
     *
     * @param userId ID of the user
     * @return list of trainings for the user as TrainingDto objects
     */
    @GetMapping("/{userId}")
    List<TrainingDto> getTrainingsByUserId(@PathVariable Long userId) {
        return trainingService.getTrainingsByUserId(userId)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves trainings that finished after the specified date.
     *
     * @param afterTime date to filter trainings by their end time (format: yyyy-MM-dd)
     * @return list of trainings finished after the given date as TrainingDto objects
     */
    @GetMapping("/finished/{afterTime}")
    List<TrainingDto> findTrainingsFinishedAfter(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date afterTime) {
        return trainingService.getTrainingsWithEndDateAfter(afterTime)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves trainings filtered by activity type.
     *
     * @param activityType type of activity (e.g., running, cycling)
     * @return list of trainings matching the activity type as TrainingDto objects
     */
    @GetMapping("/activityType")
    List<TrainingDto> findTrainingsByActivityType(@RequestParam ActivityType activityType) {
        return trainingService.getTrainingsByActivityType(activityType)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Creates a new training entry.
     *
     * @param trainingDto DTO containing training data to create
     * @return ResponseEntity with the created TrainingDto and HTTP status 201 Created
     */
    @PostMapping
    ResponseEntity<TrainingDto> createTraining(@RequestBody TrainingCreateDTO trainingDto) {
        Training persisted = trainingService.createTraining(trainingMapper.toEntity(trainingDto), trainingDto.getUserId());
        TrainingDto dto = trainingMapper.toDto(persisted);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing training entry.
     *
     * @param trainingId  ID of the training to update
     * @param trainingDto DTO containing updated training data
     * @return ResponseEntity with the updated TrainingDto and HTTP status 200 OK
     */
    @PutMapping("/{trainingId}")
    public ResponseEntity<TrainingDto> updateTraining(@PathVariable Long trainingId, @RequestBody TrainingUpdateDTO trainingDto) {
        Training updated = trainingService.updateTraining(trainingMapper.toEntity(trainingDto), trainingId, trainingDto.getUserId());
        TrainingDto dto = trainingMapper.toDto(updated);
        return ResponseEntity.ok(dto);
    }
}
