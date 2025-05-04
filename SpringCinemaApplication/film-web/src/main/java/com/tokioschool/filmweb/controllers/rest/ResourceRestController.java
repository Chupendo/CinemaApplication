package com.tokioschool.filmweb.controllers.rest;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceRestController {

    private final StoreFacade storeFacade;

    @GetMapping({"","/"})
    public ResponseEntity<byte[]> getResourceContentHandler(@RequestParam(value = "rsc") UUID resourceId){
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found! "));

        return ResponseEntity.ok()
                .contentType( MediaType.parseMediaType( resourceContentDto.contentType() ) )
                .contentLength( resourceContentDto.size() )
                .body( resourceContentDto.content() );
    }

    // handlker para borrar
    @GetMapping("/downloads")
    public ResponseEntity<byte[]> downloadResourceContentHandler(@RequestParam(value = "rsc") UUID resourceId){
        final ResourceContentDto resresourceContentDtoourceDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource not found! "));

        // wrapper para devoler objetos que Spring Serializa y los envía
        final HttpHeaders httpHeaders = new HttpHeaders();
        // fuerza la descarga del cotenido y le ponemos el nombre del archivo
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename= "+ resresourceContentDtoourceDto.resourceName() );
        // infromación del documenot en la cabecera (puede ponerse en el cuerpo)
        httpHeaders.add(HttpHeaders.CONTENT_LENGTH,String.valueOf( resresourceContentDtoourceDto.size() ));
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,resresourceContentDtoourceDto.contentType() );

        return  ResponseEntity.ok()
                .headers(httpHeaders)
                .body(resresourceContentDtoourceDto.content());
    }
}
