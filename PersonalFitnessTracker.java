import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Personal Fitness Tracker
 * Console-based Java project demonstrating Modules 1-6.
 * Run: javac PersonalFitnessTracker.java && java PersonalFitnessTracker
 */
public class PersonalFitnessTracker {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final Path DATA_FILE = Paths.get("fitness_records.csv");
    private static final String APP_NAME = "PERSONAL FITNESS TRACKER";
    private static final int DEFAULT_GOAL = 30;

    // Module 4: encapsulated object model, constructor overloading, toString().
    static class Workout implements Comparable<Workout> {
        private final int id;
        private String activity;
        private int minutes;
        private int calories;
        private LocalDate date;

        public Workout(int id, String activity, int minutes, int calories) {
            this(id, activity, minutes, calories, LocalDate.now());
        }
        public Workout(int id, String activity, int minutes, int calories, LocalDate date) {
            if (activity == null || activity.trim().isEmpty())
                throw new IllegalArgumentException("Activity cannot be empty.");
            if (minutes <= 0 || calories < 0)
                throw new IllegalArgumentException("Minutes must be positive and calories non-negative.");
            this.id = id;
            this.activity = activity.trim();
            this.minutes = minutes;
            this.calories = calories;
            this.date = Objects.requireNonNull(date, "date");
        }
        public int getId() { return id; }
        public String getActivity() { return activity; }
        public int getMinutes() { return minutes; }
        public int getCalories() { return calories; }
        public LocalDate getDate() { return date; }
        public void setActivity(String activity) {
            if (activity == null || activity.trim().isEmpty())
                throw new IllegalArgumentException("Activity cannot be empty.");
            this.activity = activity.trim();
        }
        public int compareTo(Workout other) {
            return Integer.compare(other.minutes, this.minutes); // longest first
        }
        @Override public String toString() {
            return String.format(Locale.US, "#%-3d %-12s %3d min %4d kcal %s",
                    id, activity, minutes, calories, date);
        }
        String toCsv() {
            return id + "," + activity.replace(",", " ") + "," + minutes + "," + calories + "," + date;
        }
        static Workout fromCsv(String line) {
            String[] p = line.split(",", -1);
            if (p.length != 5) throw new IllegalArgumentException("Malformed record: " + line);
            return new Workout(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]), LocalDate.parse(p[4]));
        }
    }

    // Module 4: interface + inheritance "is-a" relationship.
    interface Goal {
        boolean achieved(int minutes);
        String description();
    }
    static class DailyGoal implements Goal {
        private final int targetMinutes;
        DailyGoal(int targetMinutes) { this.targetMinutes = targetMinutes; }
        @Override public boolean achieved(int minutes) { return minutes >= targetMinutes; }
        @Override public String description() { return targetMinutes + " active minutes"; }
    }

    private static final List<Workout> workouts = new ArrayList<>();
    private static int nextId = 1;
    private static Goal dailyGoal = new DailyGoal(DEFAULT_GOAL);

    public static void main(String[] args) {
        loadRecords();
        System.out.println("====================================");
        System.out.println("       " + APP_NAME);
        System.out.println("====================================");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: addWorkout(); break;
                case 2: showWorkouts(); break;
                case 3: showSummary(); break;
                case 4: searchWorkouts(); break;
                case 5: sortWorkouts(); break;
                case 6: setGoal(); break;
                case 7: saveRecords(); break;
                case 0: saveRecords(); running = false; break;
                default: System.out.println("Invalid option. Choose 0-7.");
            }
        }
        System.out.println("Thank you for using Personal Fitness Tracker!");
    }

    private static void printMenu() {
        System.out.println("\n1. Add workout");
        System.out.println("2. View workout records");
        System.out.println("3. Fitness summary");
        System.out.println("4. Search by activity");
        System.out.println("5. Sort by duration");
        System.out.println("6. Set daily goal");
        System.out.println("7. Save records");
        System.out.println("0. Save and exit");
    }

    // Module 1: typed console input, primitive types, constants, operators, validation.
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = INPUT.nextLine().trim();
            try { return Integer.parseInt(raw); }
            catch (NumberFormatException ex) { System.out.println("Enter a whole number."); }
        }
    }
    private static String readText(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine().trim();
    }

    // Module 2: selection, loops, compound conditions.
    private static void addWorkout() {
        String activity = readText("Activity (e.g., Walking): ");
        int minutes = readInt("Duration in minutes: ");
        int calories = readInt("Calories burned (estimate): ");
        try {
            Workout w = new Workout(nextId++, activity, minutes, calories);
            workouts.add(w);
            System.out.println("Workout added: " + w);
        } catch (IllegalArgumentException ex) {
            System.out.println("Could not add workout: " + ex.getMessage());
        }
    }

    private static void showWorkouts() {
        if (workouts.isEmpty()) {
            System.out.println("No records yet. Add a workout first.");
            return;
        }
        System.out.println("\nID   ACTIVITY       DURATION   CALORIES   DATE");
        for (Workout w : workouts) System.out.println(w);
    }

    // Module 3: decomposition into methods, arrays, traversal, sum, average, recursion.
    private static void showSummary() {
        if (workouts.isEmpty()) {
            System.out.println("No data available for a summary.");
            return;
        }
        int[] minutes = new int[workouts.size()];
        int totalCalories = 0;
        for (int i = 0; i < workouts.size(); i++) {
            minutes[i] = workouts.get(i).getMinutes();
            totalCalories += workouts.get(i).getCalories();
        }
        int totalMinutes = sumRecursive(minutes, 0);
        double average = (double) totalMinutes / minutes.length;
        long sessions = workouts.stream().count(); // Module 6 stream terminal operation
        int todayMinutes = workouts.stream()
                .filter(w -> w.getDate().equals(LocalDate.now()))
                .mapToInt(Workout::getMinutes).sum();
        System.out.println("\n--------- FITNESS SUMMARY ---------");
        System.out.printf(Locale.US, "Workout sessions : %d%n", sessions);
        System.out.printf("Total active time: %d minutes%n", totalMinutes);
        System.out.printf(Locale.US, "Average session  : %.1f minutes%n", average);
        System.out.println("Estimated calories: " + totalCalories + " kcal");
        System.out.println("Today's activity : " + todayMinutes + " minutes");
        System.out.println("Daily goal       : " + dailyGoal.description());
        System.out.println("Goal status      : " + (dailyGoal.achieved(todayMinutes) ? "ACHIEVED" : "IN PROGRESS"));
    }

    private static int sumRecursive(int[] values, int index) {
        if (index >= values.length) return 0;          // base case
        return values[index] + sumRecursive(values, index + 1); // recursive case
    }

    // Module 5: String normalization, substring/case handling and search.
    private static void searchWorkouts() {
        String query = readText("Search activity: ").toLowerCase(Locale.ROOT);
        List<Workout> found = workouts.stream()
                .filter(w -> w.getActivity().toLowerCase(Locale.ROOT).contains(query))
                .collect(Collectors.toList());
        if (found.isEmpty()) System.out.println("No matching activities found.");
        else found.forEach(System.out::println);
    }

    // Module 5: Comparable ordering and Arrays.sort; Module 6: sorted stream.
    private static void sortWorkouts() {
        if (workouts.isEmpty()) {
            System.out.println("No records to sort.");
            return;
        }
        Workout[] array = workouts.toArray(new Workout[0]);
        Arrays.sort(array);
        System.out.println("\nWorkouts sorted by duration (longest first):");
        Arrays.stream(array).forEach(System.out::println);
    }

    private static void setGoal() {
        int target = readInt("Daily active-minute goal: ");
        if (target <= 0) {
            System.out.println("Goal must be greater than zero.");
            return;
        }
        dailyGoal = new DailyGoal(target);
        System.out.println("Daily goal updated to " + target + " minutes.");
    }

    // Module 5: modern Path/Files APIs, buffered line processing, malformed-row handling.
    private static void loadRecords() {
        if (!Files.exists(DATA_FILE)) return;
        try (Stream<String> lines = Files.lines(DATA_FILE)) {
            lines.forEach(line -> {
                if (line.trim().isEmpty() || line.startsWith("id,")) return;
                try {
                    Workout w = Workout.fromCsv(line);
                    workouts.add(w);
                    nextId = Math.max(nextId, w.getId() + 1);
                } catch (RuntimeException ex) {
                    System.out.println("Skipping malformed record: " + line);
                }
            });
            System.out.println("Loaded " + workouts.size() + " record(s) from " + DATA_FILE);
        } catch (IOException ex) {
            System.out.println("Could not read saved records: " + ex.getMessage());
        }
    }

    private static void saveRecords() {
        List<String> lines = new ArrayList<>();
        lines.add("id,activity,minutes,calories,date");
        for (Workout w : workouts) lines.add(w.toCsv());
        try {
            Files.write(DATA_FILE, lines);
            System.out.println("Records saved to " + DATA_FILE.toAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not save records: " + ex.getMessage());
        }
    }
}
