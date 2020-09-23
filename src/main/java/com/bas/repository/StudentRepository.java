package com.bas.repository;

import com.bas.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StudentRepository {

    // - Singleton
    private static StudentRepository instance = null;

    private StudentRepository() {
        load();
    }

    public static StudentRepository getInstance() {
        if (null == instance)
            instance = new StudentRepository();
        return instance;
    }
    // - Singleton

    private List<Student> students = new ArrayList<>();

    private void load() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            students = objectMapper.readValue(new File(System.getProperty("user.dir") + "/students.json"), new TypeReference<List<Student>>(){});
        } catch (Exception e) {
            students = new ArrayList<>();
            System.out.println(e.getMessage());
        }
    }

    private void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(System.getProperty("user.dir") + "/students.json"), students);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Student addStudent(Student student) {
        Optional<Student> studentOpDB = getStudentById(student.getId());
        if(!studentOpDB.isPresent()) {
            students.add(student);
            save();
            return student;
        }
        return null;
    }

    public Student updateStudent(Student student) {
        Optional<Student> studentOpDB = getStudentById(student.getId());
        if(studentOpDB.isPresent()) {
            Student studentDB = studentOpDB.get();
            studentDB.setFirstName(student.getFirstName());
            studentDB.setLastName(student.getLastName());
            student.setParentsCuil(student.getParentsCuil());
            save();
        }
        return student;
    }

    public void deleteStudent(Student student) {
        Optional<Student> studentOpDB = getStudentById(student.getId());
        if(studentOpDB.isPresent()) {
            students.remove(studentOpDB.get());
            save();
        }
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public List<Student> getFilteredStudents(String filter) {
        return students
                .stream()
                .filter(student ->
                        student.getId().toLowerCase().contains(filter.toLowerCase()) ||
                        student.getFirstName().toLowerCase().contains(filter.toLowerCase()) ||
                        student.getLastName().toLowerCase().contains(filter.toLowerCase()) ||
                        student.getParentsCuil().stream().anyMatch(cuil -> cuil.toLowerCase().contains(filter.toLowerCase())))
                .collect(Collectors.toList());
    }

    public Optional<Student> getStudentById(String id) {
        return students
                .stream()
                .filter(student -> id.equals(student.getId()))
                .findAny();
    }

    public List<Student> getStudentByParentCuil(String cuil) {
        return students
                .stream()
                .filter(student ->  student.getParentsCuil().stream().anyMatch(cuil::equals))
                .collect(Collectors.toList());
    }

}
