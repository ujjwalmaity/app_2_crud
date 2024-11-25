package dev.ujjwal.app_2_crud.controller;

import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@AllArgsConstructor
@Slf4j
public class EmployeeController {

    private EmployeeService employeeService;

    private HttpServletRequest httpServletRequest;

    @PostMapping("/save")
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto employeeDto) {
        log.trace("{} {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), employeeDto);
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);
        log.trace(savedEmployee.toString());
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findEmployee(@PathVariable Long id) {
        log.trace("{} {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), id);
        EmployeeDto employee = employeeService.findEmployee(id);
        log.trace(employee.toString());
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee() {
        log.trace("{} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        List<EmployeeDto> allEmployee = employeeService.findAllEmployee();
        log.trace(allEmployee.toString());
        return new ResponseEntity<>(allEmployee, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto) {
        log.trace("{} {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), employeeDto);
        EmployeeDto savedEmployee = employeeService.updateEmployee(employeeDto);
        log.trace(savedEmployee.toString());
        return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        log.trace("{} {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), id);
        employeeService.deleteEmployee(id);
        log.trace("Deleted employee id {}", id);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
