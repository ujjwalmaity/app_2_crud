package dev.ujjwal.app_2_crud.service;

import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.entity.Employee;
import dev.ujjwal.app_2_crud.exception.ResourceNotFoundException;
import dev.ujjwal.app_2_crud.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private EmployeeCacheService employeeCacheService;

    private ModelMapper modelMapper;

    private final ResourceNotFoundException employeeNotFoundException = new ResourceNotFoundException("Employee not found");

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        employeeDto.setId(null);
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeDto.class);
    }

    public EmployeeDto findEmployee(Long id) {
        Employee emp = employeeCacheService.getEmployeeFromRedis(id);
        if (emp != null) return modelMapper.map(emp, EmployeeDto.class);

        Optional<Employee> opEmployee = employeeRepository.findById(id);
        if (opEmployee.isPresent()) {
            Employee employee = opEmployee.get();
            employeeCacheService.saveEmployeeInRedis(employee);
            return modelMapper.map(employee, EmployeeDto.class);
        }
        throw employeeNotFoundException;
    }

    public List<EmployeeDto> findAllEmployee() {
        List<Employee> all = employeeRepository.findAll();
        return all.stream().map(employee -> modelMapper.map(employee, EmployeeDto.class)).toList();
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getId() == null) throw new ResourceNotFoundException("Invalid input");
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        EmployeeDto emp = findEmployee(employee.getId());
        if (emp != null) {
            Employee savedEmployee = employeeRepository.save(employee);
            employeeCacheService.saveEmployeeInRedis(savedEmployee);
            return modelMapper.map(savedEmployee, EmployeeDto.class);
        }
        throw employeeNotFoundException;
    }

    public void deleteEmployee(Long id) {
        EmployeeDto emp = findEmployee(id);
        if (emp != null) {
            employeeCacheService.deleteEmployeeFromRedis(id);
            employeeRepository.deleteById(id);
            return;
        }
        throw employeeNotFoundException;
    }

}
