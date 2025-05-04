package com.tokioschool.filmweb.core.propertyEditors;

import com.tokioschool.filmapp.dto.artist.ArtistDto;
import com.tokioschool.filmapp.services.artist.ArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArtistDtoEditor extends PropertyEditorSupport {

    private final ArtistService artistService;

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ArtistDto artistDto = null;
        try {
             artistDto = Optional.ofNullable(text)
                    .map(StringUtils::trimToNull)
                    .filter(NumberUtils::isCreatable)
                    .map(NumberUtils::createLong)
                    .map(artistService::findById)
                    .orElseGet(() -> null);
        }catch (NumberFormatException nfe){
            log.error("Error al obtener el artista del editor {}",nfe.getMessage(),nfe);
        }
        setValue(artistDto);
    }
}
