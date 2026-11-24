import java.util.*;
import java.io.*;

// College Student Management System - PS No. 1
// Mayank Bhashkar | RU-25-11735
public class Main {
    static class Student {
        String id, name, course;
        double cgpa;

        Student(String id, String name, String course, double cgpa) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.cgpa = cgpa;
        }

        public String toString() {
            return id + " | " + name + " | " + course + " | " + cgpa;
        }
    }

    static final Scanner sc = new Scanner(System.in);
    static final ArrayList<Student> students = new ArrayList<>();
    static final HashMap<String, Student> index = new HashMap<>();
    static final String FILE = "students.txt";
    static int nextId = 101;

    static void addStudent() {
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Course: ");
        String course = sc.nextLine().trim();
        System.out.print("CGPA: ");
        try {
            double cgpa = Double.parseDouble(sc.nextLine().trim());
            if (!Double.isFinite(cgpa) || cgpa < 0 || cgpa > 10) {
                System.out.println("Invalid CGPA. Enter a value from 0 to 10.");
                return;
            }
            if (name.isEmpty() || course.isEmpty() || name.contains("|") || course.contains("|")) {
                System.out.println("Invalid name or course.");
                return;
            }
            while (index.containsKey("S" + nextId)) nextId++;
            Student s = new Student("S" + nextId++, name, course, cgpa);
            students.add(s);
            index.put(s.id, s);
            System.out.println("Student added. ID: " + s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid CGPA. Enter a numeric value.");
        }
    }

    static void displayAll() {
        if (students.isEmpty()) System.out.println("No students found.");
        else for (Student s : students) System.out.println(s);
    }

    static void searchStudent() {
        System.out.print("Enter student ID or name: ");
        String key = sc.nextLine().trim();
        Student s = index.get(key.toUpperCase(Locale.ROOT));
        if (s != null) {
            System.out.println(s);
            return;
        }
        boolean found = false;
        for (Student student : students) {
            if (student.name.equalsIgnoreCase(key)) {
                System.out.println(student);
                found = true;
            }
        }
        if (!found) {
            if (key.matches("(?i)S\\d+"))
                System.out.println("Student ID " + key.toUpperCase(Locale.ROOT) + " not found.");
            else System.out.println("Student not found.");
        }
    }

    static void sortByCGPA() {
        ArrayList<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble((Student s) -> s.cgpa).reversed());
        System.out.println("Sorted list (" + sorted.size() + (sorted.size() == 1 ? " student):" : " students):"));
        for (Student s : sorted) System.out.println(s);
    }

    static void deleteStudent() {
        System.out.print("Student ID: ");
        String id = sc.nextLine().trim().toUpperCase(Locale.ROOT);
        Student s = index.remove(id);
        if (s == null) System.out.println("Student ID " + id + " not found.");
        else {
            students.remove(s);
            System.out.println("Student " + id + " deleted.");
        }
    }

    static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.startsWith("NEXT_ID=")) {
                        nextId = Math.max(nextId, Integer.parseInt(line.substring(8)));
                        continue;
                    }
                    String[] p = line.split("\\|", -1);
                    if (p.length != 4 || !p[0].matches("S\\d+")) continue;
                    double cgpa = Double.parseDouble(p[3]);
                    int number = Integer.parseInt(p[0].substring(1));
                    if (!Double.isFinite(cgpa) || cgpa < 0 || cgpa > 10 || index.containsKey(p[0])) continue;
                    Student s = new Student(p[0], p[1], p[2], cgpa);
                    students.add(s);
                    index.put(s.id, s);
                    nextId = Math.max(nextId, number + 1);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid saved record.");
                }
            }
        } catch (FileNotFoundException e) {
            // First run: start with an empty list.
        } catch (IOException e) {
            System.out.println("Error loading records: " + e.getMessage());
        }
    }

    static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            writer.write("NEXT_ID=" + nextId);
            writer.newLine();
            for (Student s : students) {
                writer.write(s.id + "|" + s.name + "|" + s.course + "|" + s.cgpa);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        loadData();
        while (true) {
            System.out.println("\n===== COLLEGE STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student\n2. Display All Students\n3. Search Student\n4. Sort by CGPA\n5. Delete Student\n6. Exit");
            System.out.print("Enter choice: ");
            if (!sc.hasNextLine()) { saveData(); return; }
            switch (sc.nextLine().trim()) {
                case "1": addStudent(); break;
                case "2": displayAll(); break;
                case "3": searchStudent(); break;
                case "4": sortByCGPA(); break;
                case "5": deleteStudent(); break;
                case "6": saveData(); System.out.println("Program closed."); return;
                default: System.out.println("Invalid menu option.");
            }
        }
    }
}
