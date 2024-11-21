package dev.ujjwal.app_2_crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCacheServiceTest {

    @InjectMocks
    private EmployeeCacheService employeeCacheService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, String, String> hashOperations;

    @Mock
    private ObjectMapper objectMapper;

    private static final String REDIS_KEY_CRUD_EMPLOYEE = "CRUD_EMPLOYEE";

    @Test
    void testGetEmployeeFromRedis() {
    }

    @Test
    void testSaveEmployeeInRedis_Success() throws JsonProcessingException {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        String stringEmployee = "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@gmail.com\",\"phoneNumber\":\"9876543210\"}";
        when(objectMapper.writeValueAsString(employee)).thenReturn(stringEmployee);
        when(redisTemplate.<String, String>opsForHash()).thenReturn(hashOperations);
        // doNothing().when(hashOperations).put(REDIS_KEY_CRUD_EMPLOYEE, employee.getId().toString(), stringEmployee);

        employeeCacheService.saveEmployeeInRedis(employee);

        verify(hashOperations, times(1)).put(REDIS_KEY_CRUD_EMPLOYEE, employee.getId().toString(), stringEmployee);
    }

    @Test
    void testSaveEmployeeInRedis_ExceptionHandling() throws JsonProcessingException {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        when(objectMapper.writeValueAsString(employee)).thenThrow(new RuntimeException("Json Processing Exception"));

        assertDoesNotThrow(() -> employeeCacheService.saveEmployeeInRedis(employee));

        verify(hashOperations, never()).put(anyString(), anyString(), anyString());
    }

    @Test
    void testDeleteEmployeeFromRedis_Success() {
        Long id = 1L;

        when(redisTemplate.<String, String>opsForHash()).thenReturn(hashOperations);

        employeeCacheService.deleteEmployeeFromRedis(id);

        verify(hashOperations, times(1)).delete(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
    }

    @Test
    void testDeleteEmployeeFromRedis_ExceptionHandling() {
        Long id = 1L;

        when(redisTemplate.<String, String>opsForHash()).thenReturn(hashOperations);
        doThrow(new RuntimeException("Redis Exception")).when(hashOperations).delete(REDIS_KEY_CRUD_EMPLOYEE, id.toString());

        assertDoesNotThrow(() -> employeeCacheService.deleteEmployeeFromRedis(id));

        verify(hashOperations, times(1)).delete(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
    }
}