package com.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.menu.AddOrderCommand;
import com.library.menu.Command;
import com.library.menu.ExitCommand;
import com.library.model.Book;
import com.library.model.BookOrder;
import com.library.model.Reader;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApplicationLibrary {
    private static final String BOOKS_FILE_NAME = "books.json";
    private static final String READERS_FILE_NAME = "readers.json";
    private static final String ORDERS_FILE_NAME = "book_orders.json";

    private static Properties properties;
    private static Path dbPath;

    private static ObjectMapper mapper = new ObjectMapper();
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private static List<Book> books;
    private static List<Reader> readers;
    private static List<BookOrder> orders;

    private static List<Command> menu;

    public static void main(String[] args) throws IOException {

        readProperties();

        readLists();

        runNewEntitiesProcessing();

        runMenu();

        cleanUp();

//        books.forEach(System.out::println);
//        readers.forEach(System.out::println);
    }

    private static void cleanUp() throws IOException {
        br.close();
    }

    private static void runMenu() throws IOException {
        initMenu();
        while (true) {
            printMenu();
            Command command = getCommand();
            command.execute();
            if (command.isExit()) {
                break;
            }
        }
    }

    private static void printMenu() {

        for (int i = 0; i < menu.size(); i++) {
            System.out.println(String.format("%d. %s",
                    i + 1,
                    menu.get(i).getName()));
        }

        System.out.println("Choose number:");
    }

    /**
     * Waits an input from user and returns chosen command
     */
    private static Command getCommand() throws IOException {
        int n;
        Command command;

        while (true) {
            try {
                n = Integer.parseInt(br.readLine());
                command = menu.get(n - 1);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Input is not integer. Try again.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Number is incorrect. Try again");
            }
        }

        return command;
    }

    /**
     * Creates all menu items and adds them to the list
     */
    private static void initMenu() {
        menu = new ArrayList<>();
        menu.add(new AddOrderCommand());
        menu.add(new ExitCommand());
    }


    /**
     * Method is looking for new readers and books
     * and adds them to the storage.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void runNewEntitiesProcessing() {
        String inputPath = properties.getProperty("inputpath");

        Thread thread = new Thread(() -> {
            while (true) {
                File inputDir = new File(inputPath);
                File[] files = inputDir.listFiles();

                if (files == null || files.length == 0) {
                    continue;
                }

                try {
                    for (File file : files) {
                        List<String> strings = FileUtils.readLines(file, "UTF-8");
                        String fileContent = String.join("", strings);

                        if (file.getName().endsWith(".book")) {
                            Book book = mapper.readValue(fileContent, Book.class);
                            book.setId(books.size());
                            books.add(book);
                            serializeList(books, DataType.BOOK);
                        } else if (file.getName().endsWith(".reader")) {
                            Reader reader = mapper.readValue(fileContent, Reader.class);
                            reader.setId(readers.size());
                            readers.add(reader);
                            serializeList(readers, DataType.READER);
                        }

                        file.delete();
                    }
                } catch (IOException ignored) {
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @SneakyThrows
    private static void serializeList(List list, DataType type) {
        mapper.writeValue(new File(getPath(type).toString()), list);
    }

    @SuppressWarnings("unchecked")
    private static void readLists() {
        dbPath = Paths.get(properties.getProperty("databasepath"));

        books = initList(getPath(DataType.BOOK), new TypeReference<List<Book>>(){});
        readers = initList(getPath(DataType.READER), new TypeReference<List<Reader>>(){});
        orders = initList(getPath(DataType.ORDER), new TypeReference<List<BookOrder>>(){});
    }

    private static Path getPath(DataType type) {
        Path ret = null;

        switch (type) {
            case BOOK:
                ret = dbPath.resolve(BOOKS_FILE_NAME);
                break;
            case READER:
                ret = dbPath.resolve(READERS_FILE_NAME);
                break;
            case ORDER:
                ret = dbPath.resolve(ORDERS_FILE_NAME);
                break;
        }

        return ret;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    private static List initList(Path path, TypeReference typeReference) {

        File file = new File(path.toString());

        if (!file.isFile()) {
            file.createNewFile();
        }

        List list = null;
        try {
            list = mapper.readValue(file, typeReference);
        } catch (IOException ignored) {
        }

        if (list == null) {
            list = new ArrayList();
        }

        return list;
    }

    @SneakyThrows
    private static void readProperties() {
        properties = new Properties();
        InputStream is = App.class.getResourceAsStream("/application.properties");
        properties.load(is);
    }
}
