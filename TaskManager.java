import java.io.*;
import java.util.*;
import org.json.*;

class Task {
    private String title;
    private boolean isCompleted;
    public Task(String title) {
        this.title = title;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void markCompleted() {
        this.isCompleted = true;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("isCompleted", isCompleted);
        return json;
    }

    public static Task fromJSON(JSONObject json) {
        Task task = new Task(json.getString("title"));
        if (json.getBoolean("isCompleted")) {
            task.markCompleted();
        }
        return task;
    }
}

class TaskManager {
    private List<Task> tasks;
    private static final String FILE_NAME = "tasks.json";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks();
    }

    public void addTask(String title) {
        tasks.add(new Task(title));
        saveTasks();
    }

    public void markTaskCompleted(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            saveTasks();
        }
    }

    public void listTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println(i + 1 + ". " + task.getTitle() + (task.isCompleted() ? " [Completed]" : ""));
        }
    }

    private void saveTasks() {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            jsonArray.put(task.toJSON());
        }
        try (FileWriter file = new FileWriter(FILE_NAME)) {
            file.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                tasks.add(Task.fromJSON(jsonArray.getJSONObject(i)));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

public class TaskApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager();

        while (true) {
            System.out.println("\n1. Add Task\n2. Mark Task Completed\n3. List Tasks\n4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    manager.addTask(title);
                    break;
                case 2:
                    manager.listTasks();
                    System.out.print("Enter task number to mark completed: ");
                    int index = scanner.nextInt() - 1;
                    manager.markTaskCompleted(index);
                    break;
                case 3:
                    manager.listTasks();
                    break;
                case 4:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

