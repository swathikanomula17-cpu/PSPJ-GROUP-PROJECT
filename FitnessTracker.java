import java.util.*;
import java.nio.file.*;

public class FitnessTracker {

    
    static class Workout {
        private String name, category;
        private int duration;
        private double calories;

        Workout(String name, String category,
                int duration, double calories) {
            this.name = name;
            this.category = category;
            this.duration = duration;
            this.calories = calories;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getDuration() { return duration; }
        public double getCalories() { return calories; }

        public String toString() {
            return name + " | " + category +
                   " | " + duration + " min | " +
                   calories + " cal";
        }
    }

    
    static double totalCalories(ArrayList<Workout> w) {
        double total = 0;

        for (Workout x : w)
            total += x.getCalories();

        return total;
    }

    
    static int count(ArrayList<Workout> w, int i) {
        if (i == w.size())
            return 0;

        return 1 + count(w, i + 1);
    }

    
    static void save(ArrayList<Workout> w) {
        try {
            ArrayList<String> data = new ArrayList<>();

            for (Workout x : w)
                data.add(x.toString());

            Files.write(
                Paths.get("workouts.txt"), data);

            System.out.println("Data saved.");
        }
        catch (Exception e) {
            System.out.println("File error.");
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        
        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter age: ");
        int age = sc.nextInt();

        System.out.print("Enter weight: ");
        double weight = sc.nextDouble();

        
        ArrayList<Workout> workouts =
                new ArrayList<>();

        int choice;

        
        do {
            System.out.println("\n--- FITNESS TRACKER ---");
            System.out.println("1. Add Workout");
            System.out.println("2. View Workouts");
            System.out.println("3. Fitness Report");
            System.out.println("4. Search Workout");
            System.out.println("5. Save");
            System.out.println("0. Exit");

            System.out.print("Choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    sc.nextLine();

                    System.out.print("Workout: ");
                    String wname = sc.nextLine();

                    System.out.print("Category: ");
                    String category = sc.nextLine();

                    System.out.print("Duration: ");
                    int duration = sc.nextInt();

                    System.out.print("Calories: ");
                    double calories = sc.nextDouble();

                    workouts.add(
                        new Workout(
                            wname, category,
                            duration, calories));

                    System.out.println("Workout added.");
                    break;

                case 2:
                    // MODULE 6: Lambda
                    workouts.forEach(
                        x -> System.out.println(x));
                    break;

                case 3:
                    // MODULE 1 + 3
                    System.out.println(
                        "Name: " + name);

                    System.out.println(
                        "Age: " + age);

                    System.out.println(
                        "Weight: " + weight + " kg");

                    System.out.println(
                        "Workouts: " +
                        count(workouts, 0));

                    System.out.println(
                        "Calories: " +
                        totalCalories(workouts));
                    break;

                case 4:
                    sc.nextLine();

                    System.out.print(
                        "Enter workout name: ");

                    String search = sc.nextLine();

                    // MODULE 5: Searching
                    for (Workout x : workouts) {
                        if (x.getName()
                             .equalsIgnoreCase(search)) {
                            System.out.println(x);
                        }
                    }
                    break;

                case 5:
                    save(workouts);
                    break;

                case 0:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println(
                        "Invalid choice.");
            }

        } while (choice != 0);

        sc.close();
    }
}