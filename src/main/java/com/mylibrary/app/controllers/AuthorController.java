package com.mylibrary.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mylibrary.app.entities.Author;
import com.mylibrary.app.exceptions.ResourceNotFoundException;
import com.mylibrary.app.payloads.ApiResponse;
import com.mylibrary.app.payloads.AuthorDTO;
import com.mylibrary.app.repositories.AuthorRepo;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("api/v1/authors")
public class AuthorController {
    @Autowired
    AuthorRepo authorRepo;
    @Autowired
    ModelMapper modelMapper;


    @GetMapping("")
    public ResponseEntity<List<AuthorDTO>> index() {
        List<Author> authors = this.authorRepo.findAll();
        List<AuthorDTO> authorDTOs = authors.stream().map((author) -> this.modelMapper.map(author, AuthorDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<List<AuthorDTO>>(authorDTOs, HttpStatus.OK);
    }
    
    @PostMapping("")
    public ResponseEntity<AuthorDTO> store(@RequestBody @Valid AuthorDTO authorDTO) {
        Author author = this.modelMapper.map(authorDTO, Author.class);
        author = this.authorRepo.insert(author);

        authorDTO = this.modelMapper.map(author, AuthorDTO.class);
        
        return new ResponseEntity<AuthorDTO>(authorDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> show(@PathVariable String id) {
        Author author = this.authorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", "AuthorId", id));
        return new ResponseEntity<AuthorDTO>(this.modelMapper.map(author, AuthorDTO.class), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> update(@PathVariable String id, @Valid @RequestBody AuthorDTO authorDTO) {
        Author author = this.authorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", "AuthorId", id));
        author.setName(authorDTO.getName());
        author.setStatus(authorDTO.isStatus());
        Author updatedAuthor = this.authorRepo.save(author);
        AuthorDTO updateAuthorDTO = this.modelMapper.map(updatedAuthor, AuthorDTO.class);
        return new ResponseEntity<AuthorDTO>(updateAuthorDTO, HttpStatus.OK);        
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String id){
        Author author = this.authorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", "AuthorId", id));
        this.authorRepo.delete(author);

        return new ResponseEntity<ApiResponse>(new ApiResponse("Author deleted succesfully", true), HttpStatus.OK);
    }
    
}
