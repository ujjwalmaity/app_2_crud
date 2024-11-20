package dev.ujjwal.app_2_crud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.entity.Employee;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeCacheService {

    private RedisTemplate<String, String> redisTemplate;
    private final String REDIS_KEY_CRUD_EMPLOYEE = "CRUD_EMPLOYEE";

    private ObjectMapper objectMapper;

    // Read from Redis
    public Employee getEmployeeFromRedis(Long id) {
        try {
            String stringEmployee = (String) redisTemplate.opsForHash().get(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
            if (stringEmployee != null) return objectMapper.readValue(stringEmployee, Employee.class);
        } catch (Exception e) {
            //
        }
        return null;
    }

    // Write in Redis
    public void saveEmployeeInRedis(Employee employee) {
        try {
            String stringEmployee = objectMapper.writeValueAsString(employee);
            redisTemplate.opsForHash().put(REDIS_KEY_CRUD_EMPLOYEE, employee.getId().toString(), stringEmployee);
        } catch (Exception e) {
            //
        }
    }

    // Delete from Redis
    public void deleteEmployeeFromRedis(Long id) {
        try {
            redisTemplate.opsForHash().delete(REDIS_KEY_CRUD_EMPLOYEE, id.toString());
        } catch (Exception e) {
            //
        }
    }

}
