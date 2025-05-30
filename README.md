# course-helper
Java desktop application for students to manage class schedules and assignments. Integration with Google Calendar and AI to assist with studying and deadline reminders.

## Project Overview:
A Java desktop application designed to help students manage their coursework. The key features will include:

- Calendar Creation and Management.
- Dedicated sections for today's tasks, upcoming deadlines, plans, and study tools (flashcards, lecture refreshers...)
- Integration with Google Calendar to sync deadlines and events.
- AI-powered Chatbot to assist with studying and deadline reminders.

## Set Up Project Structure:

### Desktop Application Framework:
- Use JavaFX to build the graphical user interface (GUI) for the desktop application.
- JavaFX Scene Builder: A visual layout tool to design user interfaces quickly.
- CalendarFX: to build and manage calendars. 

### Back-End:
- Use Spring Boot for the backend logic if needed (for managing Google Calendar integration, databases, or AI processing).
- ** Alternatively, Java-based back-end logic can be implemented directly using Java Services for a pure Java desktop app. **


### Database:
- Use SQLite or H2 Database for local database storage of user data, courses, assignments, and other details.

### Authentication & Google Integration:
- Use OAuth 2.0 to allow Google authentication (signing in with Google account) and Google Calendar API for syncing data with Google Calendar.


## Creating and Managing Coursework Calendars:

### Calendar Interface (CalendarFx):
- Build the calendar using Java'as CalendarFX.
- Users can add, update, and delete course events, assignments, and exams.

### Google Calendar Sync:
- Integrate Google Calendar API for syncing coursework events.
- Use Google API Client for Java to handle authentication and API interactions with Google Calendar (bi-directional sync).



## Implementing the Study Assistance Chatbot:

### Designing the Chatbot (AI Integration):
- Use a cloud-based AI service (such as Dialogflow, IBM Watson, or Rasa) to build the chatbot.
- Chatbot will assist with reminders, studying tips, and handling study-related questions.
- Chatbot will provide personalized reminders and assistance based on users' coursework and deadlines, interacting with the Google Calendar integration.

### Integrating the Chatbot:
- Integrate the chatbot interface in the JavaFX application using a WebView component to load the chatbot's web-based interface (if using a cloud AI service).
- Alternatively, integrate via REST API calls to an external chatbot service and display the responses in the JavaFX application.


## Database Design (SQLite or H2 Database):

### Model:
- User: Stores user profile information (name, email, Google account data).
- Course: Stores course information (course name, professor, schedule).
- Assignments/Exams: Stores information about assignments and exams, including deadlines and priority.
- Chatbot Interaction: Log user-chatbot interactions for improvement (optional for study suggestions).

### Relationships:
- One User can have many Courses and Assignments.
- Use JDBC or JPA (Java Persistence API) for database interaction.


## Phase 1: 

### build different calendar views (month, week, daily) 
- month (blocking-schedule-like UI, see task details on hover), 
- week (see details about tasks, on hover see task plan)
- day (see plan of each task)

### widgets
- stats about tasks/schedule widget
- upcoming deadline widget
- fun widget (motivational quote, streak recorder, countdowns...)



TODO: 
1. create class diagram
2. design UI
3. set up project code (maven build, create pom file)



This is just a skeleton for app objectives and possible technologies to use. The learning objective is to master full-stack development utilizing Java technologies, become familiar with the full software development process, and learn to design products for specific pain points. 
