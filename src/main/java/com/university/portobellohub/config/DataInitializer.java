package com.university.portobellohub.config;

import com.university.portobellohub.entity.Category;
import com.university.portobellohub.entity.Role;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.ItemType;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.repository.CategoryRepository;
import com.university.portobellohub.repository.RoleRepository;
import com.university.portobellohub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Inserisce dati iniziali nel database al primo avvio (ruoli, admin di test, categorie demo).
 * Utile in sviluppo e per dimostrare le funzionalitÃƒÂ  durante l'esame.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed-admin-email:admin@portobellohub.com}") String adminEmail,
            @Value("${app.seed-admin-password:Admin123!}") String adminPassword
    ) {
        return args -> {
            seedRoles(roleRepository);
            seedAdminUser(roleRepository, userRepository, passwordEncoder, adminEmail, adminPassword);
            seedDefaultCategories(categoryRepository);
        };
    }

    private void seedRoles(RoleRepository roleRepository) {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(roleName));
            }
        }
    }

    private void seedAdminUser(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String adminEmail,
            String adminPassword
    ) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role staffRole = roleRepository.findByName(RoleName.ROLE_STAFF).orElseThrow();
        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER).orElseThrow();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setRoles(Set.of(adminRole, staffRole, customerRole));
        userRepository.save(admin);
    }

    private void seedDefaultCategories(CategoryRepository categoryRepository) {
        createCategoryIfMissing(categoryRepository, "Smartphone", "smartphone", ItemType.ELECTRONIC,
                "Telefoni usati e ricondizionati");
        createCategoryIfMissing(categoryRepository, "Laptop", "laptop", ItemType.ELECTRONIC,
                "Portatili usati");
        createCategoryIfMissing(categoryRepository, "Giacche", "giacche", ItemType.CLOTHING,
                "Giacche e cappotti");
        createCategoryIfMissing(categoryRepository, "Scarpe", "scarpe", ItemType.CLOTHING,
                "Scarpe usate in buone condizioni");
    }

    private void createCategoryIfMissing(
            CategoryRepository categoryRepository,
            String name,
            String slug,
            ItemType itemType,
            String description
    ) {
        if (categoryRepository.existsByName(name)) {
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setItemType(itemType);
        category.setDescription(description);
        categoryRepository.save(category);
    }
}
