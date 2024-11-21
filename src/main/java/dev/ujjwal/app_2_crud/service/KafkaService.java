package dev.ujjwal.app_2_crud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ujjwal.app_2_crud.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class KafkaService {

    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;

    private static final String TOPIC = "topic_employee_register";
    private static final String GROUP_ID = "group-employee-register";

    @Async
    public void sendMessage(Employee employee) {
        try {
            String stringEmployee = objectMapper.writeValueAsString(employee);
            kafkaTemplate.send(TOPIC, employee.getId().toString(), stringEmployee);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consumeNewEmployee(String stringEmployee) {
        try {
            log.info("Employee register successfully. {}", stringEmployee);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

}
