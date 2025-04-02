package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.core.exceptions.NotFoundException;
import com.tokioschool.ratingapp.core.exceptions.ValidacionException;
import com.tokioschool.ratingapp.dtos.AverageRating;
import com.tokioschool.ratingapp.dtos.RatingFilmDto;
import com.tokioschool.ratingapp.dtos.RatingResponseFilmDto;
import com.tokioschool.ratingapp.dtos.RequestRatingFilmDto;
import com.tokioschool.ratingapp.services.RatingFilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingApiController {

    private final RatingFilmService ratingFilmService;

    @GetMapping("/register/films")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RatingFilmDto>> recoverRatingHandler(){
        return ResponseEntity.ok( ratingFilmService.recoverRatingFilms());
    }

    @PostMapping("/register/films")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingResponseFilmDto> registerARatingHandler(@Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body( ratingFilmService.save(requestRatingFilmDto) );
    }

    @PutMapping("/updated/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> updatedARatingHandler(@PathVariable(value="filmId") Long filmId,
                                                               @PathVariable(value="userId") Long userId,
                                                               @Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }
        RatingFilmDto ratingFilmDto = ratingFilmService.update(filmId,userId,requestRatingFilmDto);
        return ResponseEntity.ok(ratingFilmDto);
    }

    @DeleteMapping("/deleted/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletedRatingHandler(@PathVariable(value="filmId") Long filmId,
                                                    @PathVariable(name="userId") Long userId){
        ratingFilmService.deleteByFilmIdAndUserId(filmId,userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> findRatingByFilmAndUserHandler(@PathVariable(name = "filmId") Long filmId,
                                                                        @PathVariable(name="userId") Long userId){
        RatingFilmDto requestRatingFilmDto = ratingFilmService.findRatingByFilmAndUserHandler(filmId,userId);
        return ResponseEntity.ok(requestRatingFilmDto);
    }


    @GetMapping("/ratings-average/films/{filmId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AverageRating> averageRatingsHandler(@PathVariable(name="filmId") Long filmId) throws NotFoundException {

        return ResponseEntity.ok( ratingFilmService.averageRatings(filmId) );
    }
}
