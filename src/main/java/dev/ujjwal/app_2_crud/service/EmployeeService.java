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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private ModelMapper modelMapper;

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        employeeDto.setId(null);
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeDto.class);
    }

    public EmployeeDto findEmployee(Long id) {
        Optional<Employee> opEmployee = employeeRepository.findById(id);
        if (opEmployee.isPresent()) {
            return modelMapper.map(opEmployee.get(), EmployeeDto.class);
        }
        throw new ResourceNotFoundException("Employee not found");
    }

    public List<EmployeeDto> findAllEmployee() {
        List<Employee> all = employeeRepository.findAll();
        return all.stream().map(employee -> modelMapper.map(employee, EmployeeDto.class)).collect(Collectors.toList());
    }

    public EmployeeDto updateEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getId() == null) throw new ResourceNotFoundException("Invalid input");
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        EmployeeDto emp = findEmployee(employee.getId());
        if (emp != null) {
            Employee savedEmployee = employeeRepository.save(employee);
            return modelMapper.map(savedEmployee, EmployeeDto.class);
        }
        throw new ResourceNotFoundException("Employee not found");
    }

    public void deleteEmployee(Long id) {
        EmployeeDto emp = findEmployee(id);
        if (emp != null) {
            employeeRepository.deleteById(id);
            return;
        }
        throw new ResourceNotFoundException("Employee not found");
    }
}