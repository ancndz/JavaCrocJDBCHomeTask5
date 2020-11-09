package ru.ancndz.Repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ancndz.Model.Book;
import ru.ancndz.Provider.DataSourceProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class BookRepositoryTest {

    private static BookRepository bookRepository;

    @BeforeAll
    static void prepareConnect() throws IOException {
        DataSourceProvider dataSourceProvider = null;
        try {
            dataSourceProvider = new DataSourceProvider();
        } catch (IOException e) {
            System.out.println("Ошибка создания провайдера: " + e.getMessage());
            throw e;
        }

        bookRepository = new BookRepository(dataSourceProvider.getDataSource());
    }

    @AfterEach
    void trimTable() {
        bookRepository.trimTable();
    }

    @Test
    void addBook() {
        Book testBook = new Book("testTitle", "testAuthor", LocalDate.now(), false, 2);
        bookRepository.addBook(testBook);
        Assertions.assertTrue(bookRepository.findAll().contains(testBook));
    }

    @Test
    void addAllBooks() {
        Book testBook = new Book("testTitle", "testAuthor", LocalDate.now(), false, 2);
        Book testBook2 = new Book("testTitle2", "testAuthor2", LocalDate.now(), false, 22);
        //формируем список
        List<Book> bookList = new ArrayList<>();
        bookList.add(testBook);
        bookList.add(testBook2);

        //добавляем целый список
        bookRepository.addAllBooks(bookList);
        List<Book> foundBooks = bookRepository.findAll();
        Assertions.assertTrue(foundBooks.contains(testBook));
        Assertions.assertTrue(foundBooks.contains(testBook2));
    }

    @Test
    void updateBook() {
        //создаем книгу, добавляем ее
        Book testBook = new Book("testTitle", "testAuthor", LocalDate.now(), false, 2);
        bookRepository.addBook(testBook);
        List<Book> foundBooks = bookRepository.findAll();
        Assertions.assertTrue(foundBooks.contains(testBook));

        //берем ЭТУ же книгу и меняем у нее название, записываем обратно
        Book changedTestBook = bookRepository.findByTitleAndAuthor("testTitle", "testAuthor").get(0);
        changedTestBook.setTitle("new TITLE !!!");
        bookRepository.updateBook(changedTestBook);

        foundBooks = bookRepository.findAll();
        Assertions.assertFalse(foundBooks.contains(testBook));
        Assertions.assertTrue(foundBooks.contains(changedTestBook));
    }

    @Test
    void deleteByNameAndAuthor() {
        Book testBook = new Book("testTitle", "testAuthor", LocalDate.now(), false, 2);
        bookRepository.addBook(testBook);
        List<Book> foundBooks = bookRepository.findAll();
        Assertions.assertTrue(foundBooks.contains(testBook));

        bookRepository.deleteByNameAndAuthor("testTitle", "testAuthor");
        foundBooks = bookRepository.findAll();
        Assertions.assertFalse(foundBooks.contains(testBook));
    }

    @Test
    void findAll() {
        Book testBook = new Book("testTitle", "testAuthor", LocalDate.now(), false, 2);
        Book testBook2 = new Book("testTitle2", "testAuthor2", LocalDate.now(), false, 22);
        bookRepository.addBook(testBook);
        bookRepository.addBook(testBook2);
        List<Book> foundBooks = bookRepository.findAll();
        Assertions.assertTrue(foundBooks.contains(testBook));
        Assertions.assertTrue(foundBooks.contains(testBook2));
    }

    @Test
    void findByNameAndAuthor() {
        //создаем и добавляем книги
        Book testBook = new Book("title", "author", LocalDate.now(), false, 5);
        Book anotherTestBook = new Book("another", "another", LocalDate.now(), false, 5);
        bookRepository.addBook(testBook);
        bookRepository.addBook(anotherTestBook);
        //найдем все книги с названием title и автором author
        List<Book> foundBooks = bookRepository.findByTitleAndAuthor("title", "author");
        //проверяем, что в полученнм списке есть первая книга, но нет второй
        Assertions.assertTrue(foundBooks.contains(testBook));
        Assertions.assertFalse(foundBooks.contains(anotherTestBook));
    }

    @Test
    void findByIsNotAdultOnly() {
        //добавляем книги
        Book testBook = new Book("title", "author", LocalDate.now(), false, 5);
        Book interestingBook = new Book("another", "another", LocalDate.now(), true, 5);
        Book anotherTestBook = new Book("aaandAnother", "aaandAnother", LocalDate.now(), false, 5);

        bookRepository.addBook(testBook);
        bookRepository.addBook(interestingBook);
        bookRepository.addBook(anotherTestBook);

        //берем список цензурных книг и проверяем, что там нет "интересной"
        List<Book> childrenBooks = bookRepository.findByIsNotAdultOnly();

        Assertions.assertTrue(childrenBooks.contains(testBook));
        Assertions.assertTrue(childrenBooks.contains(anotherTestBook));
        Assertions.assertFalse(childrenBooks.contains(interestingBook));
    }
}