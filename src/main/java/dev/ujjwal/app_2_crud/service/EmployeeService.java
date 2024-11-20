package dev.ujjwal.app_2_crud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.entity.Employee;
import dev.ujjwal.app_2_crud.exception.ResourceNotFoundException;
import dev.ujjwal.app_2_crud.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    private RedisTemplate<String, String> redisTemplate;
    private final String REDIS_KEY_CRUD_EMPLOYEE = "CRUD_EMPLOYEE";

    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;

    private final ResourceNotFoundException employeeNotFoundException = new ResourceNotFoundException("Employee not found");

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        employeeDto.setId(null);
        Employee employee = modelMapper.map(employeeDto, Employee.class);
        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeDto.class);
    }

    public EmployeeDto findEmployee(Long id) {
        Employee emp = getEmployeeFromRedis(id);
        if (emp != null) return modelMapper.map(emp, EmployeeDto.class);

        Optional<Employee> opEmployee = employeeRepository.findById(id);
        if (opEmployee.isPresent()) {
            Employee employee = opEmployee.get();
            saveEmployeeInRedis(employee);
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
            saveEmployeeInRedis(savedEmployee);
            return modelMapper.map(savedEmployee, EmployeeDto.class);
        }
        throw employeeNotFoundException;
    }

    public void deleteEmployee(Long id) {
        EmployeeDto emp = findEmployee(id);
        if (emp != null) {
            deleteEmployeeFromRedis(id);
            employeeRepository.deleteById(id);
            return;
        }
        throw employeeNotFoundException;
    }

    // Read from Redis
    private Employee getEmployeeFromRedis(Long id) {
        try {
            String stringEmployee = (String) redisTemplate.opsForHash().get(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
            if (stringEmployee != null) return objectMapper.readValue(stringEmployee, Employee.class);
        } catch (Exception e) {
            //
        }
        return null;
    }

    // Write in Redis
    private void saveEmployeeInRedis(Employee employee) {
        try {
            String stringEmployee = objectMapper.writeValueAsString(employee);
            redisTemplate.opsForHash().put(REDIS_KEY_CRUD_EMPLOYEE, employee.getId().toString(), stringEmployee);
        } catch (Exception e) {
            //
        }
    }

    // Delete from Redis
    private void deleteEmployeeFromRedis(Long id) {
        redisTemplate.opsForHash().delete(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
    }

}
