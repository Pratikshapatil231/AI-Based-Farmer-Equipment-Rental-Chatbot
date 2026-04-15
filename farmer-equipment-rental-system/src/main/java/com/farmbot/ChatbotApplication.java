package com.farmbot;

import com.farmbot.service.ChatbotService;
import com.farmbot.ui.ConsoleColors;

import java.util.Scanner;

public class ChatbotApplication {
    public static void main(String[] args) {
        ChatbotService chatbotService = new ChatbotService();
        Scanner scanner = new Scanner(System.in);

        System.out.println(ConsoleColors.BOLD + ConsoleColors.CYAN + "AI Based Farmer Equipment Rental System" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Type your message in English. Type 'exit' to stop." + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "For complete booking process, type: book now" + ConsoleColors.RESET);

        while (true) {
            System.out.print(ConsoleColors.GREEN + "You: " + ConsoleColors.RESET);
            String input = scanner.nextLine();
            String response = chatbotService.reply(input);
            System.out.println(ConsoleColors.MAGENTA + "Bot: " + ConsoleColors.RESET + ConsoleColors.BLUE + response + ConsoleColors.RESET);

            if (input != null && (input.equalsIgnoreCase("exit")
                    || input.equalsIgnoreCase("quit"))) {
                System.out.println(ConsoleColors.CYAN + "Chat ended. Thank you!" + ConsoleColors.RESET);
                break;
            }
        }

        scanner.close();
    }
}
