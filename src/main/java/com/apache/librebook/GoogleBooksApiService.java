/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.AddBookControllerD.Book;
import com.apache.librebook.AddBookControllerD.DetailedBook;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author bhavy
 */
public class GoogleBooksApiService {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    ImageCache imageCache = ImageCache.getImageCache();
    private int totalItems;

    public List<Book> searchBooks(String query, int startIndex, int pageSize) throws Exception {
        System.out.println(query);
        List<Book> bookList = new ArrayList<>();
        String searchUrl = API_URL + query.replace(" ", "+") + "&startIndex=" + startIndex + "&maxResults=" + pageSize;
        System.out.println(searchUrl);
        URL url = new URL(searchUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        JSONObject jsonResponse = new JSONObject(content.toString());

        // Update totalItems from the API response
        if (jsonResponse.has("totalItems")) {
            totalItems = jsonResponse.getInt("totalItems");
        }

        if (jsonResponse.has("items")) {
            JSONArray items = jsonResponse.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                String title = volumeInfo.optString("title", "Unknown Title");

                String description = volumeInfo.optString("description");
                String publisher = volumeInfo.optString("publisher");
                // Handle authors as an array
                List<String> authors = new ArrayList<>();
                authors.add("Unknown Author");
                JSONArray authorsArray = volumeInfo.optJSONArray("authors");

// If authors exist, process them
                if (authorsArray != null && authorsArray.length() > 0) {
                    authors.clear(); // Clear the default "Unknown Author"
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authors.add(authorsArray.getString(j));
                    }
                }

                // Handle image links
                String imageUrl = "";
                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
                if (imageLinks != null) {
                    imageUrl = imageLinks.optString("thumbnail", ""); // Default to an empty string if not found
                }

                // Handle ISBN13 from industryIdentifiers
                String ISBN13 = "";
                JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
                if (industryIdentifiers != null) {
                    for (int j = 0; j < industryIdentifiers.length(); j++) {
                        JSONObject identifier = industryIdentifiers.getJSONObject(j);
                        if ("ISBN_13".equals(identifier.optString("type"))) {
                            ISBN13 = identifier.optString("identifier", ""); // Default to an empty string if not found
                            break; // Stop after finding the first ISBN13
                        }
                    }
                }

                // Only add the book if ISBN13 is not empty
                if (!ISBN13.isEmpty()) {
                    Image image = imageCache.getImage(imageUrl);
                    bookList.add(new Book(title, description, publisher, authors, ISBN13, imageUrl, image));
                }
            }
        }
        return bookList;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public DetailedBook displayBook(String isbn) throws Exception {
        System.out.println(isbn);
        DetailedBook book = null;
        String searchUrl = API_URL + "isbn:" + isbn.replace(" ", "+");

        URL url = new URL(searchUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        JSONObject jsonResponse = new JSONObject(content.toString());

        // Update totalItems from the API response
        if (jsonResponse.has("items")) {
            JSONArray items = jsonResponse.getJSONArray("items");

            JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
            String title = volumeInfo.optString("title", "Unknown Title");

            String description = volumeInfo.optString("description");
            String publisher = volumeInfo.optString("publisher");
            String publisherDate = volumeInfo.optString("publishedDate");
            // Handle authors as an array
            List<String> authors = new ArrayList<>();
            authors.add("Unknown Author");
            JSONArray authorsArray = volumeInfo.optJSONArray("authors");

// If authors exist, process them
            if (authorsArray != null && authorsArray.length() > 0) {
                authors.clear(); // Clear the default "Unknown Author"
                for (int j = 0; j < authorsArray.length(); j++) {
                    authors.add(authorsArray.getString(j));
                }
            }

            // Handle image links
            String imageUrl = "";
            JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
            if (imageLinks != null) {
                imageUrl = imageLinks.optString("thumbnail", ""); // Default to an empty string if not found
            }

            // Handle ISBN13 from industryIdentifiers
            String ISBN13 = "";
            JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
            if (industryIdentifiers != null) {
                for (int j = 0; j < industryIdentifiers.length(); j++) {
                    JSONObject identifier = industryIdentifiers.getJSONObject(j);
                    if ("ISBN_13".equals(identifier.optString("type"))) {
                        ISBN13 = identifier.optString("identifier", ""); // Default to an empty string if not found
                        break; // Stop after finding the first ISBN13
                    }
                }
            }

            String price = "";
            JSONObject saleInfo = items.getJSONObject(0).getJSONObject("saleInfo");
            //System.out.println(saleInfo + Boolean.toString(saleInfo.has("listPrice")) );
            if (saleInfo != null && saleInfo.has("listPrice")) {
                JSONObject listPrice = saleInfo.optJSONObject("listPrice");
                int amount = listPrice.optInt("amount", 0);
                price = Integer.toString(amount);
                //System.out.println("price:"+amount);
            }

            Image image = imageCache.getImage(imageUrl);

            book = new DetailedBook(title, description, publisher, publisherDate, authors, ISBN13, price, imageUrl, image);
        }

        return book;
    }
}
