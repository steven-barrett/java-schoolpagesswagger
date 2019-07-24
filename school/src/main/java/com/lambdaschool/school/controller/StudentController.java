package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.ErrorDetail;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    @Autowired
    private StudentService studentService;

    // Please note there is no way to add students to course yet!

    @ApiOperation(value = "returns all Students with Paging Ability",
            responseContainer = "List")
    @ApiImplicitParams({@ApiImplicitParam(name = "page",
            dataType = "integer",
            paramType = "query",
            value = "Results page you want to retrieve (0..N)"), @ApiImplicitParam(name = "size",
            dataType = "integer",
            paramType = "query",
            value = "Number of records per page."), @ApiImplicitParam(name = "sort",
            allowMultiple = true,
            dataType = "string",
            paramType = "query",
            value = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.")})

    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(
                                            @PageableDefault(
                                                     page = 0,
                                                     size = 3)
                                                    Pageable pageable)
    {
        List<Student> myStudents = studentService.findAll(pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves a student associated with the StudentId",
            response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 201,
            message = "Student Found",
            response = Student.class), @ApiResponse(code = 404,
            message = "Student Not Found",
            response = ErrorDetail.class)})

    @GetMapping(value = "/Student/{StudentId}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentById(
            @ApiParam(value = "Student id",
                    required = true,
                    example = "1")
            @PathVariable
                    Long StudentId)
    {
        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }


    @ApiOperation(value = "Returns all Students matching a certain string",
            responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 201,
            message = "Student Found",
            response = Student.class), @ApiResponse(code = 404,
            message = "Student Not Found",
            response = ErrorDetail.class)})
    @GetMapping(value = "/student/namelike/{name}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(
            @ApiParam(value = "Part or all of the student name",
                    required = true,
                    example = "St from Steven")
            @PathVariable String name)
    {
        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }


    @ApiOperation(value = "Creates a new Student",
            notes = "The newly created student id will be sent in the location header",
            response = void.class)
    @ApiResponses(value = {@ApiResponse(code = 201,
            message = "Student Created",
            response = void.class), @ApiResponse(code = 500,
            message = "Error creating student",
            response = ErrorDetail.class)})

    @PostMapping(value = "/Student",
                 consumes = {"application/json"},
                 produces = {"application/json"})
    public ResponseEntity<?> addNewStudent( @Valid
                                            @RequestBody
                                            Student newStudent) throws URISyntaxException
    {
        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Updates an existing student",
            response = void.class)
    @ApiResponses(value = {@ApiResponse(code = 201,
            message = "Student Updated",
            response = void.class), @ApiResponse(code = 500,
            message = "Error updating student",
            response = ErrorDetail.class)})
    @PutMapping(value = "/Student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @RequestBody
                    Student updateStudent,
            @ApiParam(value = "The student id",
                    required = true,
                    example = "1")
            @PathVariable
                    long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "Deletes an existing student",
            response = void.class)
    @ApiResponses(value = {@ApiResponse(code = 201,
            message = "Student Deleted",
            response = void.class), @ApiResponse(code = 500,
            message = "Error deleting student",
            response = ErrorDetail.class)})
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @ApiParam(value = "The student id",
                    required = true,
                    example = "1")
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
