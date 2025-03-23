package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.core.exceptions.OperationNotAllowException;
import com.tokioschool.ratingapp.core.exceptions.ValidacionException;
import com.tokioschool.ratingapp.dto.ratings.RatingFilmDto;
import com.tokioschool.ratingapp.records.RequestRatingFilmDto;
import com.tokioschool.ratingapp.services.RatingFilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ratings/api")
@RequiredArgsConstructor
public class RatingApiController {

    private final RatingFilmService ratingFilmService;

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> registerARatingHandler(@Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){// TODO ver por que da 500 y no muestra mensaje de validacion
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }

        ratingFilmService.save(requestRatingFilmDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{filmId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> updatedARatingHandler(@PathVariable(value="filmId") Long filmId,
                                                               @Valid @RequestBody RequestRatingFilmDto requestRatingFilmDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){// TODO ver por que da 500 y no muestra mensaje de validacion
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }
        RatingFilmDto ratingFilmDto = ratingFilmService.update(filmId,requestRatingFilmDto);
        return ResponseEntity.ok(ratingFilmDto);
    }

    @DeleteMapping("/{filmId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRatingHandler(@PathVariable(value="filmId") Long filmId){
        ratingFilmService.deleteById(filmId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/films/{filmId}/users/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingFilmDto> findRatingByFilmAndUserHandler(@PathVariable(name = "filmId") Long filmId,
                                                                        @PathVariable(name="userId") String userId){
        RatingFilmDto requestRatingFilmDto = ratingFilmService.findRatingByFilmAndUserHandler(filmId,userId);
        return ResponseEntity.ok(requestRatingFilmDto);

    }
}
