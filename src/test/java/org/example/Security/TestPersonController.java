package org.example.Security;

import org.example.Security.models.Person;
import org.example.Security.models.PersonDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPersonController {

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public TestPersonController(MockMvc mockMvc, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }
    //_________________№1___________________________
//    Задание 1: Тестирование GET запроса для получения списка ресурсов
//    Напишите тест, который проверяет API-эндпоинт для получения списка всех ресурсов (например, пользователей). Тест должен проверять:
//    HTTP статус 200 OK
//    Корректный Content-Type (application/json)
//    Что ответ содержит непустой массив
//    Что структура каждого элемента соответствует ожидаемой
    @Test
    public void getAll_persons_sortByYear_return_listDTO() throws Exception {
        mockMvc.perform(
                        get("/api/home/getAll")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(greaterThan(1))))
                .andExpect(jsonPath("$[*].title").exists())
                .andExpect(jsonPath("$[*].year").exists());
    }
    //__________________№2____________________________
//    Задание 2: Тестирование GET запроса для получения конкретного ресурса
//    Создайте тест для проверки получения конкретного ресурса по ID. Проверьте:
//    HTTP статус 200 OK для существующего ресурса
//    HTTP статус 404 Not Found для несуществующего ресурса
//    Соответствие полученных данных ожидаемым (ID, имя и другие поля)
    @Test
    public void get_person_byValidId_return_Ok() throws Exception {
        Person person=new Person();
        person.setTitle("Jotaro Kujo");
        person.setYear(2000);
        person=personRepository.save(person);
        mockMvc.perform(
                        get("/api/home/getById")
                                .param("id", person.getId().toString()))
                .andExpect(status().isOk());
    }
    @Test
    public void get_person_valid_notExistId_return_notFound() throws Exception {
        mockMvc.perform(
                        get("/api/home/getById")
                                .param("id", "2000"))
                .andExpect(status().isNotFound());
    }
    //__________________№3____________________________
//    Задание 3: Тестирование POST запроса для создания ресурса
//    Напишите тест для проверки создания нового ресурса. Тест должен:
//    Отправлять POST запрос с валидными данными
//    Проверять HTTP статус 201 Created
//    Проверять наличие заголовка Location с URI нового ресурса
//    Убедиться, что созданный ресурс доступен по возвращенному URI для списка это новый эндпоинт
//    писать под поиск по списку,реализоал в тесте create_person_notValid_data_return_ok() т.к. там уже все эндпоинты есть
    @Test
    public void create_persons_valid_data_return_listDTO_ok() throws Exception {
        List<PersonDTO> list=new ArrayList<>();
        list.add(new PersonDTO("Naruto",2001));
        list.add(new PersonDTO("Saske",2002));
        list.add(new PersonDTO("Sakura",2003));
        list.add(new PersonDTO("Леопольд",2004));
        list.add(new PersonDTO("Добрыня",2005));
        list.add(new PersonDTO("Колобок",2006));
        mockMvc.perform(
                        post("/api/home/createMultiply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(list)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<PersonDTO> actual = mapper.readValue(json,
                            mapper.getTypeFactory().constructCollectionType(List.class, PersonDTO.class));
                    for (int i = 0; i < list.size(); i++) {
                        PersonDTO sent = list.get(i);
                        PersonDTO received = actual.get(i);
                        assertThat(sent.title(),equalTo(received.title()));
                        assertThat(sent.year(),equalTo(received.year()));
                    }
                });

    }
    //__________________№4____________________________
//    Задание 4: Тестирование валидации данных при создании ресурса
//    Разработайте тест, проверяющий валидацию данных при создании ресурса:
//    Отправка POST запроса с неполными/невалидными данными
//    Проверка HTTP статуса 400 Bad Request
//    Валидация сообщения об ошибке, указывающего на конкретные проблемные поля
    @Test
    public void create_person_notValid_data_return_badRequest() throws Exception {
        PersonDTO personDTO=new PersonDTO("Vanilin' OR 1=1--",2000);
         mockMvc.perform(
                        post("/api/home/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("$.errors.title").exists());
        System.out.println("Ошибка валидации");
        //.andDo(print());//вывод инфы о запросе
    }
    @Test
    public void create_person_notValid_data_return_ok() throws Exception {
        PersonDTO personDTO=new PersonDTO("Vanilin",2000);
        MvcResult res = mockMvc.perform(
                        post("/api/home/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(personDTO)))
                .andExpect(status().isCreated()).andReturn();
        String locationHeader = res.getResponse().getHeader("Location");
        System.out.println(locationHeader);
        mockMvc.perform(get(locationHeader))
                .andExpect(status().isOk());
    }
    //__________________№5____________________________
//    Задание 5: Тестирование PUT запроса для обновления ресурса
//    Напишите тест для проверки полного обновления существующего ресурса:
//    HTTP статус 200 OK при успешном обновлении
//    HTTP статус 404 Not Found при обновлении несуществующего ресурса
//    Проверка, что данные действительно обновились при последующем GET запросе
    @Test
    public void update_person_valid_data_return_ok() throws Exception {
        Person person=new Person();
        person.setTitle("Jotaro Kujo");
        person.setYear(2000);
        person=personRepository.save(person);
        PersonDTO personDTO=new PersonDTO("Vanilin",1978);
        mockMvc.perform(
                        put("/api/home/update")
                                .param("id", person.getId().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title",is("Vanilin")))
                .andExpect(jsonPath("$.year",is(1978)));
    }
    @Test
    public void update_person_valid_data_return_notFound() throws Exception {
        PersonDTO personDTO=new PersonDTO("Vanilin",1978);
        mockMvc.perform(
                        put("/api/home/update")
                                .param("id", "2000")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(personDTO)))
                .andExpect(status().isNotFound());
    }
}
