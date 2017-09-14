package edu.coursera.parallel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple wrapper class for various analytics methods.
 */
public final class StudentAnalytics {
    /**
     * Sequentially computes the average age of all actively enrolled students
     * using loops.
     *
     * @param studentArray Student data for the class.
     * @return Average age of enrolled students
     */
    static public double averageAgeOfEnrolledStudentsImperative(
            final Student[] studentArray) {
        List<Student> activeStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (s.checkIsCurrent()) {
                activeStudents.add(s);
            }
        }

        double ageSum = 0.0;
        for (Student s : activeStudents) {
            ageSum += s.getAge();
        }

        return ageSum / (double) activeStudents.size();
    }

    /**
     * TODO compute the average age of all actively enrolled students using
     * parallel streams. This should mirror the functionality of
     * averageAgeOfEnrolledStudentsImperative. This method should not use any
     * loops.
     *
     * @param studentArray Student data for the class.
     * @return Average age of enrolled students
     */
    static public double averageAgeOfEnrolledStudentsParallelStream(
            final Student[] studentArray) {
        double retVal = 0;
        try {
            retVal = Stream.of(studentArray).parallel().filter(s -> s.checkIsCurrent()).mapToDouble(s -> s.getAge()).average().getAsDouble();
        } catch (Exception e) {
            throw new UnsupportedOperationException();
        }
        return retVal;
    }

    /**
     * Sequentially computes the most common first name out of all students that
     * are no longer active in the class using loops.
     *
     * @param studentArray Student data for the class.
     * @return Most common first name of inactive students
     */
    public String mostCommonFirstNameOfInactiveStudentsImperative(
            final Student[] studentArray) {
        List<Student> inactiveStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (!s.checkIsCurrent()) {
                inactiveStudents.add(s);
            }
        }

        Map<String, Integer> nameCounts = new HashMap<String, Integer>();

        for (Student s : inactiveStudents) {
            if (nameCounts.containsKey(s.getFirstName())) {
                nameCounts.put(s.getFirstName(),
                        new Integer(nameCounts.get(s.getFirstName()) + 1));
            } else {
                nameCounts.put(s.getFirstName(), 1);
            }
        }

        String mostCommon = null;
        int mostCommonCount = -1;
        for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
            if (mostCommon == null || entry.getValue() > mostCommonCount) {
                mostCommon = entry.getKey();
                mostCommonCount = entry.getValue();
            }
        }

        return mostCommon;
    }

    /**
     * TODO compute the most common first name out of all students that are no
     * longer active in the class using parallel streams. This should mirror the
     * functionality of mostCommonFirstNameOfInactiveStudentsImperative. This
     * method should not use any loops.
     *
     * @param studentArray Student data for the class.
     * @return Most common first name of inactive students
     */
    static public String mostCommonFirstNameOfInactiveStudentsParallelStream(
            final Student[] studentArray) {
        String name = null;
        try {
            List<String> nameList =  Stream.of(studentArray).parallel().filter(s -> !s.checkIsCurrent()).map(s -> s.getFirstName()).collect(Collectors.toList());
            Map<String, Long> counted = nameList.stream().parallel().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            name = Collections.max(counted.entrySet(), (entry1, entry2) -> entry1.getValue().intValue() - entry2.getValue().intValue()).getKey();
        } catch (Exception e) {
            throw new UnsupportedOperationException();
        }
        return name;
    }

    /**
     * Sequentially computes the number of students who have failed the course
     * who are also older than 20 years old. A failing grade is anything below a
     * 65. A student has only failed the course if they have a failing grade and
     * they are not currently active.
     *
     * @param studentArray Student data for the class.
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20Imperative(
            final Student[] studentArray) {
        int count = 0;
        for (Student s : studentArray) {
            if (!s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) {
                count++;
            }
        }
        return count;
    }

    /**
     * TODO compute the number of students who have failed the course who are
     * also older than 20 years old. A failing grade is anything below a 65. A
     * student has only failed the course if they have a failing grade and they
     * are not currently active. This should mirror the functionality of
     * countNumberOfFailedStudentsOlderThan20Imperative. This method should not
     * use any loops.
     *
     * @param studentArray Student data for the class.
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20ParallelStream(
            final Student[] studentArray) {
        Long ret = null;
        try {
            ret = Arrays.stream(studentArray).parallel().filter(s -> !s.checkIsCurrent()).filter(s -> s.getAge() > 20).filter(s -> s.getGrade() < 65).count();
        } catch (Exception e) {
            throw new UnsupportedOperationException();
        }
        return ret.intValue();
    }
    public static void main(String[] args) {
        System.out.println("Start.......");
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Student s = new Student("a", "b", i, 0, false);
            list.add(s);
        }
        Student[] studentArray = new Student[list.size()];
        for (int i = 0; i < list.size(); i++) {
            studentArray[i] = list.get(i);
        }
        //System.out.println("result of sequential calculation is: " + averageAgeOfEnrolledStudentsImperative(studentArray));
        //System.out.println("result of parallel calculation is: " + averageAgeOfEnrolledStudentsParallelStream(studentArray));
        //mostCommonFirstNameOfInactiveStudentsParallelStream(studentArray);
    }
}
