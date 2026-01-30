package org.example.Security.service;

import org.example.Security.models.PersonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface PersonApiInterface {//интерфейс укажет если в какойто из реализациии мы забыли переопределить метод
    ResponseEntity<PersonDTO> createUser(UserDetails userDetails, boolean isAdmin);
    //--------------------------------------------------------------------
    ResponseEntity<Void>deleteById(Long id);
   ResponseEntity<Void>deleteAll();
   //----------------------------------------------------------
   ResponseEntity<Long>getTotalCount();
   ResponseEntity<Boolean>existsById(Long id);

   }
