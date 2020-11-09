package ru.ancndz.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class Book {
    private Integer id = null;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private Boolean isAdultOnly;
    private Integer count;

    public Book(String title, String author, LocalDate publicationDate, boolean isAdultOnly, int count) {
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.isAdultOnly = isAdultOnly;
        this.count = count;
    }

    public Book(Integer id, String title, String author, LocalDate publicationDate, Boolean isAdultOnly, Integer count) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.isAdultOnly = isAdultOnly;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Boolean isAdultOnly() {
        return isAdultOnly;
    }

    public void setAdultOnly(Boolean adultOnly) {
        isAdultOnly = adultOnly;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return isAdultOnly == book.isAdultOnly &&
                title.equals(book.title) &&
                author.equals(book.author) &&
                Objects.equals(publicationDate, book.publicationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publicationDate, isAdultOnly);
    }
}
