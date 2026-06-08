package com.nubiz.nutrust.initializer;

import com.nubiz.nutrust.entity.Role;
import com.nubiz.nutrust.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        String[][] initData = {
            {"ADMIN", "시스템 관리자"},
            {"COMPANY_ADMIN", "회사 관리자"},
            {"SUPPORT", "지원 담당"},
            {"CUSTOMER", "고객"}
        };

        for (String[] data : initData) {
            roleRepository.findByName(data[0]).orElseGet(
                () -> roleRepository.save(Role.builder().name(data[0]).description(data[1]).build())
            );
        }
    }
}
