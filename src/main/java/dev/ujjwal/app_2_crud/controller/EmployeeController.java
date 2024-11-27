package dev.ujjwal.app_2_crud.controller;

import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.dto.EmployeeRegisterDto;
import dev.ujjwal.app_2_crud.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Employee")
public class EmployeeController {

    private EmployeeService employeeService;

    private HttpServletRequest httpServletRequest;

    private static final String LOG_PLACE_HOLDER_2 = "{} {}";
    private static final String LOG_PLACE_HOLDER_3 = "{} {} {}";

    @PostMapping("/save")
    @Operation(summary = "Register", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(ref = "#/components/parameters/csrf")
    })
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeRegisterDto employeeRegisterDto) {
        log.trace(LOG_PLACE_HOLDER_3, httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), employeeRegisterDto);
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeRegisterDto);
        log.trace(savedEmployee.toString());
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find")
    public ResponseEntity<EmployeeDto> findEmployee(@PathVariable Long id) {
        log.trace(LOG_PLACE_HOLDER_3, httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), id);
        EmployeeDto employee = employeeService.findEmployee(id);
        log.trace(employee.toString());
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/all")
    @Operation(summary = "Find All")
    public ResponseEntity<List<EmployeeDto>> findAllEmployee() {
        log.trace(LOG_PLACE_HOLDER_2, httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        List<EmployeeDto> allEmployee = employeeService.findAllEmployee();
        log.trace(allEmployee.toString());
        return new ResponseEntity<>(allEmployee, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "Update", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(ref = "#/components/parameters/csrf")
    })
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto) {
        log.trace(LOG_PLACE_HOLDER_3, httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), employeeDto);
        EmployeeDto savedEmployee = employeeService.updateEmployee(employeeDto);
        log.trace(savedEmployee.toString());
        return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(ref = "#/components/parameters/csrf")
    })
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        log.trace(LOG_PLACE_HOLDER_3, httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), id);
        employeeService.deleteEmployee(id);
        log.trace("Deleted employee id {}", id);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
