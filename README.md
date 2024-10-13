# NIO Java Client-Server Application - Numbers API

This project is a Java-based NIO (Non-blocking I/O) client-server application that interacts with the [Numbers API](http://numbersapi.com/#batching) to retrieve interesting facts about numbers, dates, and years. The server can handle multiple clients simultaneously, providing various commands to fetch random or specific facts.

## Features

The application supports six commands that clients can use:

1. **get-math-fact `<number>`**  
   Retrieves a random math fact about the specified number.
2. **get-trivia-fact `<number>`**  
   Retrieves a random trivia fact about the specified number.
3. **get-date-fact `<day>` `<month>`**  
   Retrieves a random fact about the specified day and month (e.g., a notable event on that day).
4. **get-year-fact `<year>`**  
   Retrieves a random fact about the specified year.
5. **get-random-fact**  
   Returns a random fact from one of the above categories (math, trivia, date, year).
6. **disconnect**  
   Disconnects the client from the server.

## Technologies Used

- **Java NIO** for non-blocking I/O operations
- **HTTP client** for sending requests to the Numbers API
- **Concurrent client support** using Java's NIO framework

## How it works:

- The server listens for incoming client connections and processes requests using non-blocking I/O.
- Clients can issue one of the six commands to retrieve facts about numbers, dates, or years.
- The server uses the Numbers API to fetch relevant facts and send them back to the clients.

## Usage:

- **get-year-fact 1969**
- **Response:** 'an estimated 500 million people worldwide watch Neil Armstrong take his historic first steps on the Moon.'
