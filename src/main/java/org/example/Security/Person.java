package org.example.Security;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^\\s*[a-zA-Zа-яА-ЯёЁ'][a-zA-Zа-яА-ЯёЁ' -]*[a-zA-Zа-яА-ЯёЁ']?[.!?,;:]?\\s*$", message = "Only letters and spaces are allowed")
    @Size(min = 3,max = 255)
    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;

    @NotNull
    @Range(max = 2025,min = 1900)
    private Integer year;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public PersonDTO getDTO(){
        return new PersonDTO(title,year);
    }
}
