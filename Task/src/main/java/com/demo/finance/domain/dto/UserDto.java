package com.demo.finance.domain.dto;

import com.demo.finance.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private boolean blocked;
    private Role role;
    private Long version;

    public static UserDto removePassword(UserDto userDto) {
        userDto.setPassword(null);
        return userDto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return blocked == userDto.blocked && Objects.equals(userId, userDto.userId)
                && Objects.equals(name, userDto.name) && Objects.equals(email, userDto.email)
                && Objects.equals(password, userDto.password) && Objects.equals(role, userDto.role)
                && Objects.equals(version, userDto.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, password, blocked, role, version);
    }

    @Override // password is excluded
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", blocked=" + blocked +
                ", role=" + role +
                ", version=" + version +
                '}';
    }
}