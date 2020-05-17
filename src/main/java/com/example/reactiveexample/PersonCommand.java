package com.example.reactiveexample;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonCommand {

    private String firstName;
    private String lastName;

    public PersonCommand(Person person){
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    public String sayMyName(){
        return "My name is :" + firstName + " " + lastName;
    }
}
