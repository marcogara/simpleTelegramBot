package org.example;

public interface Command {
    void execute(Command command);
}

/**
 * if ("awaiting_task".equals(conversationState)) {
 *                 todoList.add(userMessage);
 *                 responseMessage = "Task added: '" + userMessage + "'";
 *                 conversationState = "none"; // Reset state
 *             } else {
 *                 if (userMessage.startsWith("/add")) {
 *                     responseMessage = "What task would you like to add?";
 *                     conversationState = "awaiting_task";
 *                 } else if (userMessage.startsWith("/list")) {
 *                     if (todoList.isEmpty()) {
 *                         responseMessage = "Your to-do list is empty.";
 *                     } else {
 *                         StringBuilder sb = new StringBuilder("Your to-do list:\n");
 *                         for (int i = 0; i < todoList.size(); i++) {
 *                             sb.append((i + 1)).append(". ").append(todoList.get(i)).append("\n");
 *                         }
 *                         responseMessage = sb.toString();
 *                     }
 *                 } else if (userMessage.startsWith("/done")) {
 *                     try {
 *                         String taskNumberStr = userMessage.substring(5).trim();
 *                         int taskNumber = Integer.parseInt(taskNumberStr);
 *                         if (taskNumber > 0 && taskNumber <= todoList.size()) {
 *                             String removedTask = todoList.remove(taskNumber - 1);
 *                             responseMessage = "Task completed: '" + removedTask + "'";
 *                         } else {
 *                             responseMessage = "Invalid task number.";
 *                         }
 *                     } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
 *                         responseMessage = "Please provide a valid task number after /done.";
 *                     }
 *
 *
 *
 *                     private final List<String> todoList = new ArrayList<>();
 *     private String conversationState = "none";
 */
