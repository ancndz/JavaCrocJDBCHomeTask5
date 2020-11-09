package ru.ancndz.Repository;

import org.apache.derby.jdbc.EmbeddedDataSource;
import ru.ancndz.Model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    
    private static final String TABLE_NAME = "BOOK";

    private EmbeddedDataSource dataSource;

    public BookRepository(EmbeddedDataSource dataSource) {
        this.dataSource = dataSource;
        initTable();
    }

    /**
     * Инициализация таблицы, в случае, если ее еще нет
     */
    private void initTable() {
        System.out.println(String.format("Start initializing %s table", TABLE_NAME));
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(
                    null,
                    null,
                    // Несмотря на то, что мы создаем таблицу в нижнем регистре (и дальше к ней так же обращаемся),
                    // поиск мы осуществляем в верхнем. Такие вот приколы
                    TABLE_NAME.toUpperCase(),
                    new String[]{"TABLE"});
            if (resultSet.next()) {
                System.out.println("Table has already been initialized");
            } else {
                statement.executeUpdate(
                        "create table "
                                + TABLE_NAME
                                + " ("
                                + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY primary key, "
                                + "title varchar(255),"
                                + "author varchar(255)," +
                                "publication_date date," +
                                "adult_only boolean," +
                                "count integer"
                                + ")");
                System.out.println("Table was successfully initialized");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred during table initializing: " + e.getMessage());
        } finally {
            System.out.println("=========================");
        }
    }

    /**
     * добавление новой книги в таблицу
     * @param book - @see ru.ancndz.Model.Book
     */
    public void addBook(Book book) {
        String sqlQuery = "insert into " + TABLE_NAME + "(title, author, publication_date, adult_only, count)" +
                " values (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            setStatementValues(book, statement);
            statement.execute();
        } catch (SQLException e) {
            printQueryException(e);
        }
    }

    private void setStatementValues(Book book, PreparedStatement statement) throws SQLException {
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setDate(3, Date.valueOf(book.getPublicationDate()));
        statement.setBoolean(4, book.isAdultOnly());
        statement.setInt(5, book.getCount());
    }

    /**
     * добавление списка книг
     * @param bookList - list of @see ru.ancndz.Model.Book
     */
    public void addAllBooks(List<Book> bookList) {
        for (Book eachBook: bookList) {
            addBook(eachBook);
        }
    }

    /**
     * Обновление книги @see ru.ancndz.Model.Book
     * Если у книги есть id != null => она взята из базы, происходит процедура обновления
     * Если id == null => такой книги в базе нет, и ее добавляем как новую
     * @param book
     */
    public void updateBook(Book book) {
        if (book.getId() == null) {
            addBook(book);
            return;
        }
        String sqlQuery = "update " + TABLE_NAME + " set title = ?," +
                "author = ?, " +
                "publication_date = ?, " +
                "adult_only = ?, " +
                "count = ? where id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            setStatementValues(book, statement);
            statement.setInt(6, book.getId());
            statement.execute();
        } catch (SQLException e) {
            printQueryException(e);
        }
    }

    /**
     * удаление по названию и автору
     * @param title - название
     * @param author - автор
     */
    public void deleteByNameAndAuthor(String title, String author) {
        String sqlQuery = "delete from " + TABLE_NAME + " where title = ? and author = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.execute();
        } catch (SQLException e) {
            printQueryException(e);
        }
    }

    public List<Book> findAll() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
            return fillBookListWithResult(resultSet);
        } catch (Exception e) {
            printQueryException(e);
        }
        return new ArrayList<>();
    }

    /**
     * поиск по названию и автору
     * @param title - название
     * @param author - автор
     * @return @see ru.ancndz.Model.Book
     */
    public List<Book> findByTitleAndAuthor(String title, String author) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format(
                    "select * from %s where title='%s' and author='%s'", TABLE_NAME, title, author
            ));
            return fillBookListWithResult(resultSet);
        } catch (SQLException e) {
            printQueryException(e);
        }
        return new ArrayList<>();
    }

    private List<Book> fillBookListWithResult(ResultSet resultSet) throws SQLException {
        List<Book> bookList = new ArrayList<>();
        while (resultSet.next()) {
            bookList.add(new Book(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDate(4).toLocalDate(),
                    resultSet.getBoolean(5),
                    resultSet.getInt(6)
            ));
        }
        return bookList;
    }

    /**
     * поиск всех 18- книг
     * @return @see ru.ancndz.Model.Book
     */
    public List<Book> findByIsNotAdultOnly() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format(
                    "select * from %s where adult_only = false", TABLE_NAME
            ));
            return fillBookListWithResult(resultSet);
        } catch (SQLException e) {
            printQueryException(e);
        }
        return new ArrayList<>();
    }

    public void trimTable() {
        String sqlQuery = "delete from " + TABLE_NAME;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.execute();
        } catch (SQLException e) {
            printQueryException(e);
        }
    }

    private void printQueryException(Exception e) {
        System.out.println("Ошибка выполнения запроса: " + e.getMessage());
    }
}
