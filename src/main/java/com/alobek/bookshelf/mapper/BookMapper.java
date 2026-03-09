package com.alobek.bookshelf.mapper;

import com.alobek.bookshelf.dto.book.BookPhotoResponseDTO;
import com.alobek.bookshelf.dto.book.BookResponseDTO;
import com.alobek.bookshelf.entity.BookEntity;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    private final String baseURL = "http://localhost:8080/attach/open/";


    // Map single BookEntity to BookResponseDTO
    public BookResponseDTO toDTO(BookEntity book){
        if (book == null) return null;

        BookPhotoResponseDTO photoDTO = null;
        if (book.getPhoto() != null){
            // generate URL using file ID (no path needed)
            String url = baseURL + book.getPhoto().getId();
            photoDTO = new BookPhotoResponseDTO();
            photoDTO.setId(book.getPhoto().getId());
            photoDTO.setUrl(url);
        }

        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setCreatedDate(book.getCreatedDate());
        dto.setPhoto(photoDTO);

        return dto;
    }

    // map a list of BookEntity to list of BookResponseDTO ...
    public List<BookResponseDTO> toDtoList(List<BookEntity> books){

        return books.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


}
