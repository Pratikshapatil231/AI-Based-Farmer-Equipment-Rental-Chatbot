# AI Based Farmer Equipment Rental System (Mini Project)

Java-based AI/ML mini project chatbot for farmers to get equipment rental information in English.

## Project Features
- English-only farmer chatbot
- Equipment listing and rental price query
- Rental recommendation engine (rule-based ML scoring)
- Intent classification using keyword-based NLP logic
- Console-based interactive chat
- Complete booking workflow (farmer -> equipment owner contact)
- Booking data saved in local database file (`data/bookings.csv`)

## Tech Stack
- Java 17
- Basic AI/ML logic:
  - Intent classification
  - Weighted recommendation scoring

## Folder Structure
- `src/main/java/com/farmbot/ChatbotApplication.java` - entry point
- `src/main/java/com/farmbot/model` - domain enums and entity
- `src/main/java/com/farmbot/service` - NLP and recommendation services

## How to Run
1. Install Java JDK 17.
2. Open terminal in this project folder.
3. Run these commands:
   - `javac -encoding UTF-8 -d out src/main/java/com/farmbot/model/*.java src/main/java/com/farmbot/service/*.java src/main/java/com/farmbot/ui/*.java src/main/java/com/farmbot/ChatbotApplication.java`
   - `java -cp out com.farmbot.ChatbotApplication`

## One-Click Run
- Windows CMD: double-click `run_chatbot.bat`
- PowerShell: run `.\run_chatbot.ps1`

## Example Queries
- `hello`
- `show equipment list`
- `tractor rent`
- `price of harvester`
- `book now`
- `मुझे किराए पर ट्रैक्टर चाहिए`
- `उपकरण की कीमत बताओ`
- `मला भाड्याने ट्रॅक्टर हवा आहे`
- `उपकरण यादी दाखवा`

## Complete Booking Process
1. Type `book now`
2. Enter farmer name
3. Enter farmer mobile number
4. Select equipment name
5. Enter number of rental days
6. Type `yes` to confirm booking
7. Bot shares owner mobile + farmer mobile so both can contact each other
8. Booking record is saved in `data/bookings.csv`

## Mini Project Note
This project demonstrates an educational AI/ML chatbot workflow suitable for diploma/engineering mini projects. You can extend it with:
- Spring Boot REST API
- Database (MySQL/PostgreSQL)
- WhatsApp/Telegram integration
- Real ML model for intent classification
