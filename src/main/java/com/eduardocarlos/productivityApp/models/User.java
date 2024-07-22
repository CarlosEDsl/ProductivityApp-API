package com.eduardocarlos.productivityApp.models;

import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = User.table_name)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    public static final String table_name = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, length = 256)
    @NotBlank
    private String email;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(min=8, max = 100)
    private String password;

    @Column(nullable = false, length = 30)
    @NotBlank
    private String name;

    @Column(length = 15)
    @Size(max=15)
    private String cell;

    @Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CollectionTable(name = "user_profile")
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles() {
        return this.profiles.stream().map(ProfileEnum::toEnum).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }

}
