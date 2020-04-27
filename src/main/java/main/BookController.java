package main;

import main.model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import main.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books/")
    public List<Book> list() {
        Iterable<Book> bookIterable = bookRepository.findAll();
        ArrayList<Book> books = new ArrayList<>();
        for(Book book : bookIterable) {
            books.add(book);
        }
        return books;
    }

    @PostMapping("/books/")
    public int add(Book book) {
        Book newBook = bookRepository.save(book);
        return newBook.getId();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity get(@PathVariable int id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (!optionalBook.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(optionalBook.get(), HttpStatus.OK);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity update(@PathVariable int id, Book book) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (!optionalBook.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        book.setId(id);
        bookRepository.save(book);
        return new ResponseEntity(id, HttpStatus.OK);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            bookRepository.deleteById(id);
            return new ResponseEntity(id, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
