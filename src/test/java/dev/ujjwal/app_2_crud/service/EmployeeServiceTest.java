package dev.ujjwal.app_2_crud.service;

import dev.ujjwal.app_2_crud.dto.EmployeeDto;
import dev.ujjwal.app_2_crud.entity.Employee;
import dev.ujjwal.app_2_crud.exception.ResourceNotFoundException;
import dev.ujjwal.app_2_crud.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeCacheService employeeCacheService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        // Before each test
    }

    @Test
    void testSaveEmployee() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("John");
        employeeDto.setLastName("Doe");
        employeeDto.setEmail("john@gmail.com");
        employeeDto.setPhoneNumber("9876543210");

        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setFirstName("John");
        savedEmployee.setLastName("Doe");
        savedEmployee.setEmail("john@gmail.com");
        savedEmployee.setPhoneNumber("9876543210");

        EmployeeDto savedEmployeeDto = new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210");

        when(modelMapper.map(employeeDto, Employee.class)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        doNothing().when(kafkaService).sendMessage(savedEmployee);
        when(modelMapper.map(savedEmployee, EmployeeDto.class)).thenReturn(savedEmployeeDto);

        EmployeeDto result = employeeService.saveEmployee(employeeDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@gmail.com", result.getEmail());
        assertEquals("9876543210", result.getPhoneNumber());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testFindEmployee_CacheHit() {
        Employee cachedEmployee = new Employee();
        cachedEmployee.setId(1L);
        cachedEmployee.setFirstName("John");
        cachedEmployee.setLastName("Doe");
        cachedEmployee.setEmail("john@gmail.com");
        cachedEmployee.setPhoneNumber("9876543210");

        when(employeeCacheService.getEmployeeFromRedis(1L)).thenReturn(cachedEmployee);
        when(modelMapper.map(cachedEmployee, EmployeeDto.class)).thenReturn(new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210"));

        EmployeeDto result = employeeService.findEmployee(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@gmail.com", result.getEmail());
        assertEquals("9876543210", result.getPhoneNumber());
        verify(employeeCacheService, times(1)).getEmployeeFromRedis(1L);
        verify(employeeRepository, never()).findById(anyLong());
    }

    @Test
    void testFindEmployee_CacheMiss() {
        when(employeeCacheService.getEmployeeFromRedis(1L)).thenReturn(null);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDto.class)).thenReturn(new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210"));

        EmployeeDto result = employeeService.findEmployee(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@gmail.com", result.getEmail());
        assertEquals("9876543210", result.getPhoneNumber());
        verify(employeeCacheService, times(1)).getEmployeeFromRedis(1L);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeCacheService, times(1)).saveEmployeeInRedis(employee);
    }

    @Test
    void testFindAllEmployee() {
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setEmail("john@gmail.com");
        employee1.setPhoneNumber("9876543210");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Smith");
        employee2.setLastName("M");
        employee2.setEmail("smith@gmail.com");
        employee2.setPhoneNumber("0123456789");

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        when(modelMapper.map(employee1, EmployeeDto.class)).thenReturn(new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210"));
        when(modelMapper.map(employee2, EmployeeDto.class)).thenReturn(new EmployeeDto(2L, "Smith", "M", "smith@gmail.com", "0123456789"));

        List<EmployeeDto> result = employeeService.findAllEmployee();

        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testUpdateEmployee() {
        EmployeeDto modifyEmployeeDto = new EmployeeDto();
        modifyEmployeeDto.setId(1L);
        modifyEmployeeDto.setFirstName("John");
        modifyEmployeeDto.setLastName("Doe");
        modifyEmployeeDto.setEmail("john@g.co");
        modifyEmployeeDto.setPhoneNumber("123");

        Employee modifyEmployee = new Employee();
        modifyEmployee.setId(1L);
        modifyEmployee.setFirstName("John");
        modifyEmployee.setLastName("Doe");
        modifyEmployee.setEmail("john@g.co");
        modifyEmployee.setPhoneNumber("123");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFirstName("John");
        employeeDto.setLastName("Doe");
        employeeDto.setEmail("john@gmail.com");
        employeeDto.setPhoneNumber("9876543210");

        when(modelMapper.map(modifyEmployeeDto, Employee.class)).thenReturn(modifyEmployee);
        when(employeeCacheService.getEmployeeFromRedis(1L)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDto.class)).thenReturn(employeeDto);

        when(employeeRepository.save(modifyEmployee)).thenReturn(modifyEmployee);
        when(modelMapper.map(modifyEmployee, EmployeeDto.class)).thenReturn(modifyEmployeeDto);

        employeeService.updateEmployee(modifyEmployeeDto);

        verify(employeeCacheService, times(1)).getEmployeeFromRedis(1L);
        verify(employeeRepository, times(1)).save(modifyEmployee);
        verify(employeeCacheService, times(1)).saveEmployeeInRedis(modifyEmployee);
    }

    @Test
    void testUpdateEmployee_WithoutId() {
        EmployeeDto modifyEmployeeDto = new EmployeeDto();
        modifyEmployeeDto.setId(null);
        modifyEmployeeDto.setFirstName("John");
        modifyEmployeeDto.setLastName("Doe");
        modifyEmployeeDto.setEmail("john@g.co");
        modifyEmployeeDto.setPhoneNumber("123");

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(modifyEmployeeDto));

        verify(employeeCacheService, never()).getEmployeeFromRedis(anyLong());
        verify(employeeRepository, never()).save(any());
        verify(employeeCacheService, times(0)).saveEmployeeInRedis(any());
    }

    @Test
    void testDeleteEmployee() {
        EmployeeDto employeeDto = new EmployeeDto(1L, "John", "Doe", "john@gmail.com", "9876543210");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        when(employeeCacheService.getEmployeeFromRedis(1L)).thenReturn(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDto.class)).thenReturn(employeeDto);

        employeeService.deleteEmployee(1L);

        verify(employeeCacheService, times(1)).deleteEmployeeFromRedis(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(employeeCacheService.getEmployeeFromRedis(1L)).thenReturn(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(1L));
        verify(employeeCacheService, never()).deleteEmployeeFromRedis(anyLong());
        verify(employeeRepository, never()).deleteById(anyLong());
    }
}