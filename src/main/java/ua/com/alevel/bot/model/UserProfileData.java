package ua.com.alevel.bot.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfileData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String gender;
    @Column
    private String color;
    @Column
    private String song;

    private int age;
    private int number;
    private long chatId;


    @Override
    public String toString() {
        return String.format("Имя: %s%nВозраст: %d%nПол: %s%nЛюбимая цифра: %d%n" +
                        "Цвет: %s%nПесня: %s%n", getName(), getAge(), getGender(), getNumber(),
                getColor(), getSong());
    }
}
