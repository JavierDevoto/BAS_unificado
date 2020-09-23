package com.bas.service;

import com.bas.model.Student;
import com.bas.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

public class StudentService {

    // - Singleton
    private static StudentService instance = null;

    private StudentService() {}

    public static StudentService getInstance() {
        if (null == instance)
            instance = new StudentService();
        return instance;
    }
    // - Singleton

    private final StudentRepository studentRepository = StudentRepository.getInstance();

    public Student addStudent(Student student) {
        return studentRepository.addStudent(student);
    }

    public Student updateSudent(Student student) {
        return studentRepository.updateStudent(student);
    }

    public void deleteStudent(Student student) {
        studentRepository.deleteStudent(student);
    }

    public Student getStudentById(String id) {
        Optional<Student> studentDB = studentRepository.getStudentById(id);
        if(studentDB.isPresent()) {
            return studentDB.get();
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return studentRepository.getAllStudents();
    }

    public List<Student> getFilteredStudents(String filter) {
        return studentRepository.getFilteredStudents(filter);
    }

    public String getStudentIdByParentCuil(String cuil) {
        List<Student> studentsDB = studentRepository.getStudentByParentCuil(cuil);
        if(studentsDB.isEmpty()) {
            return "";
        } else {
            String ids = "";
            for(Student student : studentsDB) {
                ids = ids + student.getId() + " - ";
            }
            return ids.substring(0, ids.length() - 3);
        }
    }

}
