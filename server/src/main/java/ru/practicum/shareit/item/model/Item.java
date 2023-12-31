package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
@EqualsAndHashCode(exclude = {"id", "owner", "request", "available"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String name;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;
    @OneToOne(fetch = FetchType.EAGER)
    private ItemRequest request;
}
