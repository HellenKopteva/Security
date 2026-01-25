package org.example.Security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

public record PersonDTO(//Valid в контроллере обеспечивает проерку ходящих dto
        @NotBlank
        @Size(min = 3,max = 255)
        @Pattern(regexp = "^\\s*[a-zA-Zа-яА-ЯёЁ'][a-zA-Zа-яА-ЯёЁ' -]*[a-zA-Zа-яА-ЯёЁ']?[.!?,;:]?\\s*$", message = "Only letters and spaces are allowed")
        String title,
        @NotNull
        @Range(min = 1900,max = 2025,message = "Only in range 1900-2025")
        Integer year
) {

    //делать person из personDto будет неправильно, лучше создавать БД и запрашивать оттуда, а то создадим пользоателя которого нет в БД?????
    //да и id не хочу давать dto

}
