package dev.ujjwal.app_2_crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @InjectMocks
    public KafkaService kafkaService;

    @Mock
    public KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    public ObjectMapper objectMapper;

    @Test
    void testSendMessage() throws JsonProcessingException {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPhoneNumber("9876543210");

        String stringEmployee = "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@gmail.com\",\"phoneNumber\":\"9876543210\"}";

        when(objectMapper.writeValueAsString(employee)).thenReturn(stringEmployee);
        when(kafkaTemplate.send("topic_employee_register", "1", stringEmployee)).thenReturn(null);

        kafkaService.sendMessage(employee);

        verify(kafkaTemplate).send("topic_employee_register", "1", stringEmployee);
    }

    @Test
    void testConsumeNewEmployee() {
        String stringEmployee = "{\"id\":1,\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@gmail.com\",\"phoneNumber\":\"9876543210\"}";

        kafkaService.consumeNewEmployee(stringEmployee);

        assertNotNull(stringEmployee);
    }
}