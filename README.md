# course-helper
Java desktop application for students to manage class schedules and assignments. High use of integration with google calandar and use of AI to assist with studying and deadline reminders.


Project Overview:
A Java desktop application designed to help students manage their coursework. The key features will include:

Calendar Creation and Management.
Integration with Google Calendar to sync deadlines and events.
AI-powered Chatbot to assist with studying and deadline reminders.
Set Up Project Structure:
Desktop Application Framework:

Use JavaFX to build the graphical user interface (GUI) for the desktop application.
JavaFX Scene Builder: A visual layout tool to design user interfaces quickly.
Back-End:

Use Spring Boot for the backend logic if needed (for managing Google Calendar integration, databases, or AI processing). Alternatively, for a pure Java desktop app, Java-based back-end logic can be implemented directly using Java Services.
Database:

Use SQLite or H2 Database for local database storage of user data, courses, assignments, and other details.
Authentication & Google Integration:

Use OAuth 2.0 to allow Google authentication (signing in with Google account) and Google Calendar API for syncing data with Google Calendar.
User Authentication and Profile Management:
Authentication Flow:

Implement Google OAuth 2.0 to allow users to log in with their Google accounts.
Use JWT (JSON Web Tokens) or OAuth tokens to maintain session management for authenticated users.
Profile Management:

Allow users to input and edit basic information (name, email, courses) using JavaFX forms.
Profile Data can be stored in a local database such as SQLite or H2 Database.
Creating and Managing Coursework Calendars:
Calendar Interface (JavaFX):
Build a calendar view using JavaFX's JavaFX CalendarView or create a custom calendar layout.
Users can add, update, and delete course events, assignments, and exams using JavaFX input forms.
Google Calendar Sync:
Integrate Google Calendar API for syncing coursework events.
Use Google API Client for Java to handle authentication and API interactions with Google Calendar (bi-directional sync).
Allow users to add, edit, and delete events in both the Java desktop application and Google Calendar.
Implementing the Study Assistance Chatbot:
Designing the Chatbot (AI Integration):

Use a cloud-based AI service (such as Dialogflow, IBM Watson, or Rasa) to build the chatbot.
Chatbot will assist with reminders, studying tips, and handling study-related questions.
Integrating the Chatbot:

Integrate the chatbot interface in the JavaFX application using a WebView component to load the chatbot's web-based interface (if using a cloud AI service).
Alternatively, integrate via REST API calls to an external chatbot service and display the responses in the JavaFX application.
AI Assistance:

The chatbot will provide personalized reminders and assistance based on users' coursework and deadlines, interacting with the Google Calendar integration.
Database Design (SQLite or H2 Database):
Models:

User: Stores user profile information (name, email, Google account data).
Course: Stores course information (course name, professor, schedule).
Assignments/Exams: Stores information about assignments and exams, including deadlines and priority.
Chatbot Interaction: Log user-chatbot interactions for improvement (optional for study suggestions).
Relationships:

One User can have many Courses and Assignments.
Use JDBC or JPA (Java Persistence API) for database interaction.
Google Calendar Integration:
OAuth Authentication:

Implement OAuth 2.0 authentication for seamless Google login.
Use Google API Client for Java to interact with the Google Calendar API.
Google Calendar API Operations:

Sync coursework events (add, update, delete) using Google Calendar API in both directions (Java app ↔ Google Calendar).
Allow users to view and manage calendar events within the Java desktop app.
Front-End Features (JavaFX GUI):
Dashboard:

Display a summary of coursework deadlines and upcoming events using JavaFX components like ListView or TableView.
Use Material Design for JavaFX or JavaFX CSS to style the application.
Calendar View:

Use JavaFX CalendarView or custom JavaFX components to create a visual calendar where users can manage their courses and deadlines.
Implement dynamic forms to input coursework data and render calendar events.
Chatbot Interface:

Embed the chatbot interface in a JavaFX WebView or create a custom chatbot interface in JavaFX using buttons and labels.
Use REST APIs to communicate with the backend chatbot service if required.
Back-End Features:
Course Management:

Build CRUD (Create, Read, Update, Delete) methods for managing courses, assignments, and exams in the back-end.
Google Calendar Sync API:

Use Google API Client for Java to build methods that interact with Google Calendar (create/edit/delete events).
Use Google Calendar API endpoints for syncing coursework events.
Chatbot Data Management:

Store and retrieve chatbot interactions in the local database to help improve the chatbot over time.
Testing:
Unit Testing:

Test Java backend logic (e.g., course creation, calendar sync) using JUnit or TestNG.
Integration Testing:

Test Google Calendar synchronization to ensure changes are reflected across both the Java app and Google Calendar.
User Testing:

Conduct user testing for UI and UX, ensuring that the chatbot provides useful assistance and that the calendar interface is intuitive.
Deployment:
Desktop Application:

Package the Java desktop application as an executable JAR or native application using tools like Launch4J or JavaFX Packager.
Backend Services:

If using a backend service (e.g., Spring Boot), deploy it on cloud platforms like Heroku or AWS.
For local applications, the backend can run locally without a server.
Google OAuth Configuration:

Set up OAuth credentials in the Google Developer Console for access to Google Calendar.
Future Features & Enhancements:
Mobile App:

Extend the desktop application by developing a mobile version using React Native or Flutter (to reuse code across platforms).
Advanced Chatbot:

Integrate machine learning to provide more personalized study schedules and resources based on user data.
Real-time Collaboration:

Enable students to collaborate on assignments using WebSockets or Socket.IO (if extending functionality to a web-based app).
Analytics Dashboard:

Provide analytics and data visualization of study habits, course performance, and upcoming tasks using JavaFX Charts or external libraries like JFreeChart.
